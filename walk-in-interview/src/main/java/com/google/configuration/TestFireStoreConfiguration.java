package com.google.configuration;

import jdk.internal.jline.internal.Nullable;

/** Class that specify the configuration details for firestore during testing phase. */
public final class TestFireStoreConfiguration implements FireStoreConfiguration {
    private static final String PROJECT_ID = "";
    // TODO(issue/16): create a cloud firestore project for test phase.

    @Nullable
    private static TestFireStoreConfiguration testFireStoreConfiguration = null;

    private TestFireStoreConfiguration() {}

    /**
     * Gets the cloud firestore configuration in test phase.
     *
     * @return Configuration details of the cloud firestore database.
     */
    public static TestFireStoreConfiguration getFireStoreConfiguration() {
        if (testFireStoreConfiguration == null) {
            testFireStoreConfiguration = new TestFireStoreConfiguration();
        }
        return testFireStoreConfiguration;
    }

    @Override
    public String getProjectId() throws UnsupportedOperationException {
        if (getProjectId().isEmpty()) {
            throw new UnsupportedOperationException("Not implemented");
        }
        return PROJECT_ID;
    }
}
