package com.google.configuration;

/** Class that specify the configuration details for firestore during development phase. */
public final class DevelopmentFireStoreConfiguration implements FireStoreConfiguration {
    private static DevelopmentFireStoreConfiguration developmentFireStoreConfiguration = null;

    private static final String PROJECT_ID = "google.com:walk-in-interview";

    private DevelopmentFireStoreConfiguration() {}

    /**
     * Gets the cloud firestore configuration in test phase.
     *
     * @return Configuration details of the cloud firestore database.
     */
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
