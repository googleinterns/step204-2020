package com.google.configuration;

/** Class that specify the configuration details for firestore during production phase. */
public final class ProductionFireStoreConfiguration implements FireStoreConfiguration {
    private static ProductionFireStoreConfiguration productionFireStoreConfiguration = null;

    private static final String PROJECT_ID = "";

    private ProductionFireStoreConfiguration() {}

    /**
     * Gets the cloud firestore configuration in production phase.
     *
     * @return Configuration details of the cloud firestore database.
     * @throws UnsupportedOperationException If the project Id is not specified yet.
     */
    public static ProductionFireStoreConfiguration getFireStoreConfiguration() throws UnsupportedOperationException {
        if (productionFireStoreConfiguration == null) {
            // The project id is not specified yet
            throw new UnsupportedOperationException("Not implemented");
            // productionFireStoreConfiguration = new ProductionFireStoreConfiguration();
        }
        return productionFireStoreConfiguration;
    }

    public String getProjectId() {
        return PROJECT_ID;
    }
}
