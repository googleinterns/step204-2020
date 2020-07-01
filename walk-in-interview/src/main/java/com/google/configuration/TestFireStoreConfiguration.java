package com.google.configuration;

/** Class that specify the configuration details for firestore during testing phase. */
public final class TestFireStoreConfiguration implements FireStoreConfiguration {
    private static TestFireStoreConfiguration developmentFireStoreConfiguration = null;

    private static final String PROJECT_ID = "";

    private TestFireStoreConfiguration() {}

    public static TestFireStoreConfiguration getFireStoreConfiguration() {
        if (developmentFireStoreConfiguration == null) {
            developmentFireStoreConfiguration = new TestFireStoreConfiguration();
        }
        return developmentFireStoreConfiguration;
    }

    public String getProjectId() {
        return PROJECT_ID;
    }
}
