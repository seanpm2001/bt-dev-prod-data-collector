/*
 * This file is generated by jOOQ.
 */
package org.gradle.devprod.collector.persistence.generated.jooq.tables.records;


import org.gradle.devprod.collector.persistence.generated.jooq.tables.FlakyTestClass;
import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class FlakyTestClassRecord extends UpdatableRecordImpl<FlakyTestClassRecord> implements Record2<String, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.flaky_test_class.build_id</code>.
     */
    public void setBuildId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.flaky_test_class.build_id</code>.
     */
    public String getBuildId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>public.flaky_test_class.flaky_test_fqcn</code>.
     */
    public void setFlakyTestFqcn(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.flaky_test_class.flaky_test_fqcn</code>.
     */
    public String getFlakyTestFqcn() {
        return (String) get(1);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record2<String, String> key() {
        return (Record2) super.key();
    }

    // -------------------------------------------------------------------------
    // Record2 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row2<String, String> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    @Override
    public Row2<String, String> valuesRow() {
        return (Row2) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return FlakyTestClass.FLAKY_TEST_CLASS.BUILD_ID;
    }

    @Override
    public Field<String> field2() {
        return FlakyTestClass.FLAKY_TEST_CLASS.FLAKY_TEST_FQCN;
    }

    @Override
    public String component1() {
        return getBuildId();
    }

    @Override
    public String component2() {
        return getFlakyTestFqcn();
    }

    @Override
    public String value1() {
        return getBuildId();
    }

    @Override
    public String value2() {
        return getFlakyTestFqcn();
    }

    @Override
    public FlakyTestClassRecord value1(String value) {
        setBuildId(value);
        return this;
    }

    @Override
    public FlakyTestClassRecord value2(String value) {
        setFlakyTestFqcn(value);
        return this;
    }

    @Override
    public FlakyTestClassRecord values(String value1, String value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached FlakyTestClassRecord
     */
    public FlakyTestClassRecord() {
        super(FlakyTestClass.FLAKY_TEST_CLASS);
    }

    /**
     * Create a detached, initialised FlakyTestClassRecord
     */
    public FlakyTestClassRecord(String buildId, String flakyTestFqcn) {
        super(FlakyTestClass.FLAKY_TEST_CLASS);

        setBuildId(buildId);
        setFlakyTestFqcn(flakyTestFqcn);
    }
}