package edu.school21.ex00.reflection;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

public final class ReflectionMethodHelper {

    private ReflectionMethodHelper() {
    }

    public static List<Method> getPublicMethodsExcludingToString(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> Modifier.isPublic(method.getModifiers()))
                .filter(method -> !"toString".equals(method.getName()))
                .toList();
    }
}
