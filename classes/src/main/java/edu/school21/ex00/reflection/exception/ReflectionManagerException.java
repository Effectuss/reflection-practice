package edu.school21.ex00.reflection.exception;

public class ReflectionManagerException extends RuntimeException {
    public ReflectionManagerException(String msg) {
        super(msg);
    }

    public ReflectionManagerException(String msg, Throwable e) {
        super(msg, e);
    }
}
