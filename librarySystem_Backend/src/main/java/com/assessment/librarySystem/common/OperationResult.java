package com.assessment.librarySystem.common;

/**
 * Abstract base class for operation results providing type-safe success/failure handling
 */
public abstract class OperationResult<T> {
    
    /**
     * Represents a successful operation with data and message
     */
    public static final class Success<T> extends OperationResult<T> {
        private final T data;
        private final String message;
        
        public Success(T data, String message) {
            this.data = data;
            this.message = message;
        }
        
        public T data() { return data; }
        public String message() { return message; }
    }
    
    /**
     * Represents a failed operation with error details
     */
    public static final class Failure<T> extends OperationResult<T> {
        private final String error;
        private final String code;
        
        public Failure(String error, String code) {
            this.error = error;
            this.code = code;
        }
        
        public String error() { return error; }
        public String code() { return code; }
    }
    
    /**
     * Check if the operation was successful
     */
    public boolean isSuccess() {
        return this instanceof Success;
    }
    
    /**
     * Check if the operation failed
     */
    public boolean isFailure() {
        return this instanceof Failure;
    }
    
    /**
     * Get the data if successful, null otherwise
     */
    public T getData() {
        if (this instanceof Success) {
            return ((Success<T>) this).data();
        }
        return null;
    }
    
    /**
     * Get the message (success message or error)
     */
    public String getMessage() {
        if (this instanceof Success) {
            return ((Success<?>) this).message();
        } else if (this instanceof Failure) {
            return ((Failure<?>) this).error();
        }
        return null;
    }
}
