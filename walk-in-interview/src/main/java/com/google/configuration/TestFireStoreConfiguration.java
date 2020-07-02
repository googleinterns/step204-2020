package com.google.configuration;

/** Class that specify the configuration details for firestore during testing phase. */
public final class TestFireStoreConfiguration implements FireStoreConfiguration {
    private static TestFireStoreConfiguration testFireStoreConfiguration = null;

    private static final String PROJECT_ID = "";

    private TestFireStoreConfiguration() {}

    /**
     * Gets the cloud firestore configuration in test phase.
     *
     * @return Configuration details of the cloud firestore database.
     * @throws UnsupportedOperationException If the project Id is not specified yet.
     */
    public static TestFireStoreConfiguration getFireStoreConfiguration() throws UnsupportedOperationException{
        if (testFireStoreConfiguration == null) {
            // The project id is not specified yet
            throw new UnsupportedOperationException("Not implemented");
            // testFireStoreConfiguration = new TestFireStoreConfiguration();
        }
        return testFireStoreConfiguration;
    }

    public String getProjectId() {
        return PROJECT_ID;
    }
}
