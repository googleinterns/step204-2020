package com.google.configuration;

/** Retrieves the project configuration details. */
public final class ConfigurationFactory {
    private enum ProjectStatus {
        DEVELOPMENT,
        TEST,
        PRODUCTION
    }

    private static final ProjectStatus projectStatus = ProjectStatus.DEVELOPMENT;

    /**
     *
     * @return
     * @throws UnsupportedOperationException
     */
    public static final FireStoreConfiguration getFireStoreConfiguration() throws UnsupportedOperationException {
        FireStoreConfiguration fireStoreConfiguration = null;
        switch (projectStatus) {
            case PRODUCTION:
            case TEST:
                throw new UnsupportedOperationException("Not implemented");
            case DEVELOPMENT:
                fireStoreConfiguration = DevelopmentFireStoreConfiguration.getFireStoreConfiguration();
                break;
        }
        return fireStoreConfiguration;
    }
}
