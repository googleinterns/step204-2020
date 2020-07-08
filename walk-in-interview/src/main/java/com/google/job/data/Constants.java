package com.google.job.data;

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

public final class Constants {
    private Constants() {}

    public static final String O_LEVEL_KEY = "o-level";
    public static final String DRIVING_LICENSE_C_KEY = "driving-license-c";

    public static final String ENGLISH = "en";

    // Map<ID, Map<languageId, localizedName>>
    public static final Map<String, Map<String, String>> REQUIREMENTS_LIST = ImmutableMap.of(
            O_LEVEL_KEY, ImmutableMap.of(ENGLISH, "O Level"),
            DRIVING_LICENSE_C_KEY, ImmutableMap.of(ENGLISH, "Category C Driving License"));
    // TODO(issue/26): add more requirements
}
