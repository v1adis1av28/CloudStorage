package com.storage.exceptions;

public class PermissionDeniedException extends IllegalAccessException{
    public PermissionDeniedException(){}
    public PermissionDeniedException(String message){
        super(message);
    }
}
