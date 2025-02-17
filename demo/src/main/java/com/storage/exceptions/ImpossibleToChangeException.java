package com.storage.exceptions;

import com.sun.jdi.InvalidTypeException;

public class ImpossibleToChangeException extends InvalidTypeException {
    public ImpossibleToChangeException() {}
    public ImpossibleToChangeException(String message) {
        super(message);
    }
}
