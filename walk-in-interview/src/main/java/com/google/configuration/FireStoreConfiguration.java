package com.google.configuration;

/** Interface that specify the configuration details for firestore. */
public interface FireStoreConfiguration {
    /**
     * Gets the cloud firestore project id.
     *
     * @return Project Id.
     */
    String getProjectId();
}
