package com.google.configuration;

/** Class that specify the configuration details for firestore during testing phase. */
public final class DevelopmentFireStoreConfiguration implements FireStoreConfiguration {
    private static DevelopmentFireStoreConfiguration developmentFireStoreConfiguration = null;

    private static final String PROJECT_ID = "google.com:walk-in-interview";

    private DevelopmentFireStoreConfiguration() {}

    public static DevelopmentFireStoreConfiguration getFireStoreConfiguration() {
        if (developmentFireStoreConfiguration == null) {
            developmentFireStoreConfiguration = new DevelopmentFireStoreConfiguration();
        }
        return developmentFireStoreConfiguration;
    }

    public String getProjectId() {
        return PROJECT_ID;
    }
}
