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
        switch (projectStatus) {
        case PRODUCTION:
            return ProductionFireStoreConfiguration.getFireStoreConfiguration();
        case TEST:
            return TestFireStoreConfiguration.getFireStoreConfiguration();
        default:
            return DevelopmentFireStoreConfiguration.getFireStoreConfiguration();
        }
    }
}
