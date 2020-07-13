package com.google.configuration;

import javax.annotation.Nullable;

/** Class that specify the configuration details for firestore during production phase. */
public final class ProductionFireStoreConfiguration implements FireStoreConfiguration {
    private static final String PROJECT_ID = "";
    // TODO(issue/16): create a cloud firestore project for production phase.

    @Nullable
    private static ProductionFireStoreConfiguration productionFireStoreConfiguration = null;

    private ProductionFireStoreConfiguration() {}

    /**
     * Gets the cloud firestore configuration in production phase.
     *
     * @return Configuration details of the cloud firestore database.
     */
    public static ProductionFireStoreConfiguration getFireStoreConfiguration() {
        if (productionFireStoreConfiguration == null) {
            productionFireStoreConfiguration = new ProductionFireStoreConfiguration();
        }
        return productionFireStoreConfiguration;
    }

    @Override
    public String getProjectId() throws UnsupportedOperationException {
        if (PROJECT_ID.isEmpty()) {
            throw new UnsupportedOperationException("Not implemented");
        }
        return PROJECT_ID;
    }
}
