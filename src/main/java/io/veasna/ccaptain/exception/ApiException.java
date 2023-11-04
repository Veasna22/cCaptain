package io.veasna.ccaptain.exception;

/**
 * @author Veasna
 * @version 1.0
 * @license Veasna , LLC
 * @since 3/11/23 16:02
 */
public class ApiException extends RuntimeException{
    public ApiException(String message) {
        super(message);
    }
}
