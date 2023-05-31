package com.processing.cps.exceptions;

import java.security.InvalidParameterException;

/**
 * Custom Exception class for Invalid params in parameters file provided.
 */
public class InvalidParametersException extends InvalidParameterException {

    /**
     * Constructor being used to throw exception.
     * @param data
     */
    public InvalidParametersException(final String data) {
        super(String.format("Invalid data found in Parameters File: %s", data));
    }
}
