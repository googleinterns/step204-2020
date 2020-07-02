package com.google.configuration;

/** Retrieves the project configuration details. */
public final class ConfigurationFactory {
    private enum ProjectStatus {
        DEVELOPMENT,
        TEST,
        PRODUCTION
    }

    private static final ProjectStatus projectStatus = ProjectStatus.DEVELOPMENT;

    public static final FireStoreConfiguration getFireStoreConfiguration() {
        FireStoreConfiguration fireStoreConfiguration = null;
        switch (projectStatus) {
            case PRODUCTION:
                fireStoreConfiguration = ProductionFireStoreConfiguration.getFireStoreConfiguration();
                break;
            case TEST:
                fireStoreConfiguration = TestFireStoreConfiguration.getFireStoreConfiguration();
                break;
            case DEVELOPMENT:
                fireStoreConfiguration = DevelopmentFireStoreConfiguration.getFireStoreConfiguration();
                break;
        }
        return fireStoreConfiguration;
    }
}
