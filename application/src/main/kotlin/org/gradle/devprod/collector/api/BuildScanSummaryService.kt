package org.gradle.devprod.collector.api

import org.gradle.devprod.collector.model.BuildScanSummary

interface BuildScanSummaryService {
    /**
     * @param geServer the server host, e.g. "e.grdev.net" or "ge.gradle.org"
     * @param buildScanId the build scan id, e.g. m4ey25atqqzdc
     */
    fun getSummary(geServer: String, buildScanId: String): BuildScanSummary
}
