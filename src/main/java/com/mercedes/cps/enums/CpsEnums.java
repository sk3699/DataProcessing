package com.processing.cps.enums;

/**
 * Enums for reading the parameters.
 */
public enum CpsEnums {
    M("m"),
    C("c");

    private final String value;
    CpsEnums(final String value) {
        this.value = value;
    }

    /**
     * To return value of Enum.
     * @return
     */
    public String getValue() {
        return value;
    }
}
