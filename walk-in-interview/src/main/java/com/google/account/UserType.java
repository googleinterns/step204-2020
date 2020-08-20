package com.google.account;

/** Enumeration for User Type. */
public enum UserType {
    BUSINESS("BUSINESS"),
    APPLICANT("APPLICANT");

    private final String userTypeId;

    UserType(String userTypeId) {
        this.userTypeId = userTypeId;
    }

    /** Returns the stable id representing the user type. Can be stored in database. */
    public String getUserTypeId() {
        return this.userTypeId;
    }
}
