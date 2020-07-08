package com.google.job.data;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public enum Requirement {
    // TODO(issue/26): add more requirements
    O_LEVEL("o-level", ImmutableMap.of("en", "O Level")),
    ENGLISH("language-english", ImmutableMap.of("en", "English")),
    DRIVING_LICENSE_C("driving-license-c", ImmutableMap.of("en", "Category C Driving License"));

    private final String requirementId;
    private final Map<String, String> localizedNameByLanguage;

    Requirement(String requirementId, Map<String, String> localizedNameByLanguage){
        this.requirementId = requirementId;
        this.localizedNameByLanguage = localizedNameByLanguage;
    }

    /**
     * Gets the id string.
     *
     * @return Id string.
     */
    public String getRequirementId() {
        return requirementId;
    }

    /**
     * Gets the localized requirement name with the specified version of language.
     *
     * @param language Language version to be displayed.
     * @return Localized name of the requirement.
     */
    public String getLocalizedName(String language) {
        return localizedNameByLanguage.get(language);
    }
}
