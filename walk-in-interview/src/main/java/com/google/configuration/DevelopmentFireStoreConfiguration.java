package com.google.configuration;

import jdk.internal.jline.internal.Nullable;

/** Class that specify the configuration details for firestore during development phase. */
public final class DevelopmentFireStoreConfiguration implements FireStoreConfiguration {
    private static final String PROJECT_ID = "google.com:walk-in-interview";

    @Nullable
    private static DevelopmentFireStoreConfiguration developmentFireStoreConfiguration = null;

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

    @Override
    public String getProjectId() throws UnsupportedOperationException {
        if (PROJECT_ID.isEmpty()) {
            throw new UnsupportedOperationException("Not implemented");
        }
        return PROJECT_ID;
    }
}
