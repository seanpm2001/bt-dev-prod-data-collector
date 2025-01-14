package org.gradle.devprod.collector.teamcity

import com.fasterxml.jackson.databind.ObjectMapper
import org.jetbrains.teamcity.rest.BuildConfigurationId
import org.jetbrains.teamcity.rest.TeamCityInstance
import org.jetbrains.teamcity.rest.TeamCityInstanceFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import java.net.URI
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class TeamcityClientService(
    @Value("${'$'}{teamcity.api.token}") private val teamCityApiToken: String,
    private val objectMapper: ObjectMapper
) {
    private val teamCityInstance: TeamCityInstance =
        TeamCityInstanceFactory.guestAuth("https://builds.gradle.org")

    private val client: WebClient = WebClient.create()

    fun loadTriggerBuilds(pipelines: List<String>, projectListProvider: (String) -> List<String>): Sequence<TeamCityBuild> =
        pipelines.flatMap { pipeline -> projectListProvider(pipeline) }
            .asSequence().flatMap { buildConfigurationId ->
                teamCityInstance
                    .builds()
                    .fromConfiguration(BuildConfigurationId(buildConfigurationId))
                    .includeCanceled()
                    .includeFailed()
                    .withAllBranches()
                    .since(Instant.now().minus(5, ChronoUnit.DAYS))
                    .all()
                    .mapNotNull { it.toTeamCityBuild() }
            }

    // The rest client has no "affectProject(id:Gradle_Master_Check)" buildLocator
    fun loadFailedBuilds(since: Instant, pipelines: List<String>, affectedBuildProvider: (String) -> String): Sequence<TeamCityBuild> =
        pipelines.asSequence().flatMap { pipeline ->
            // We have ~200 failed builds per day
            var nextPageUrl: String? = loadingFailedBuildsUrl(affectedBuildProvider(pipeline), since)
            var buildIterator: Iterator<TeamCityResponse.BuildBean> =
                emptyList<TeamCityResponse.BuildBean>().iterator()
            generateSequence {
                if (buildIterator.hasNext()) {
                    val build = buildIterator.next()
                    build.toTeamCityBuild(loadBuildScans(build.id))
                } else if (nextPageUrl == null) {
                    null
                } else {
                    val nextPage = loadFailedBuilds(nextPageUrl!!)
                    nextPageUrl = nextPage.nextHref
                    buildIterator = nextPage.build.iterator()
                    if (buildIterator.hasNext()) {
                        val build = buildIterator.next()
                        build.toTeamCityBuild(loadBuildScans(build.id))
                    } else {
                        null
                    }
                }
            }
        }

    private fun loadingFailedBuildsUrl(
        affectedProject: String,
        start: Instant,
        pageSize: Int = 100
    ): String {
        val locators =
            mapOf(
                "affectedProject" to "(id:$affectedProject)",
                "status" to "FAILURE",
                "branch" to "default:any",
                "composite" to "false",
                "sinceDate" to formatRFC822(start)
            ).entries
                .joinToString(",") { "${it.key}:${it.value}" }

        val fields =
            "nextHref,count,build(id,agent(name),buildType(id,name,projectName),failedToStart,revisions(revision(version)),branchName,status,statusText,state,queuedDate,startDate,finishDate,composite)"

        return "/app/rest/builds/?locator=$locators&fields=$fields&count=$pageSize"
    }

    private fun WebClient.RequestHeadersSpec<*>.bearerAuth(): WebClient.RequestHeadersSpec<*> =
        header("Authorization", "Bearer $teamCityApiToken")

    private fun loadBuildScans(id: Int): List<String> {
        val response: Mono<String> =
            client
                .get()
                .uri(createTeamcityUri("/app/rest/builds/id:$id/artifacts/content/.teamcity/build_scans/build_scans.txt"))
                .accept(MediaType.TEXT_PLAIN)
                .bearerAuth()
                .retrieve()
                .bodyToMono<String>()
                .onErrorResume(WebClientResponseException::class.java) {
                    if (it.statusCode.value() == 404) Mono.just("") else Mono.error(it)
                }

        return response.blockOptional().orElse("").lines().map { it.trim() }.filter { it.isNotBlank() }
    }

    private fun loadFailedBuilds(nextPageUrl: String): TeamCityResponse =
        client
            .get()
            .uri(createTeamcityUri(nextPageUrl))
            .accept(MediaType.APPLICATION_JSON)
            .bearerAuth()
            .retrieve()
            .bodyToMono<String>()
            .block()
            .let { objectMapper.readValue(it, TeamCityResponse::class.java) }

    private fun createTeamcityUri(url: String): URI =
        if (url.startsWith("http")) {
            URI.create(url)
        } else {
            val relativePath = if (url.startsWith("/")) url else "/$url"
            URI.create("https://builds.gradle.org$relativePath")
        }
}
