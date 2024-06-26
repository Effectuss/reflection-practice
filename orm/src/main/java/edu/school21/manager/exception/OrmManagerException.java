package edu.school21.manager.exception;

public class OrmManagerException extends Exception {
    public OrmManagerException(String msg) {
        super(msg);
    }

    public OrmManagerException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
