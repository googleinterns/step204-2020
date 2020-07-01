package com.google.configuration;

/** Class that specify the configuration details for firestore during production phase. */
public final class ProductionFireStoreConfiguration implements FireStoreConfiguration {
    private static ProductionFireStoreConfiguration developmentFireStoreConfiguration = null;

    private static final String PROJECT_ID = "";

    private ProductionFireStoreConfiguration() {}

    public static ProductionFireStoreConfiguration getFireStoreConfiguration() {
        if (developmentFireStoreConfiguration == null) {
            developmentFireStoreConfiguration = new ProductionFireStoreConfiguration();
        }
        return developmentFireStoreConfiguration;
    }

    public String getProjectId() {
        return PROJECT_ID;
    }
}
