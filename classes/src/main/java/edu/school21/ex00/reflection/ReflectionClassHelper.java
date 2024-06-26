package edu.school21.ex00.reflection;

import edu.school21.ex00.reflection.exception.ReflectionManagerException;
import lombok.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class ReflectionClassHelper {

    private ReflectionClassHelper() {
    }

    public static Set<Class<?>> getAllClassesFromPackage(@NonNull String packageName) {
        String path = packageName.replace('.', '/');
        Optional<InputStream> inputStream = Optional.ofNullable(
                ClassLoader.getSystemClassLoader().getResourceAsStream(path)
        );

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream.orElseThrow(
                        () -> new ReflectionManagerException("Resource not found: " + path)), StandardCharsets.UTF_8)
        );

        try (reader) {
            return reader.lines()
                    .filter(line -> line.endsWith(".class"))
                    .map(line -> getClass(line, packageName))
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            throw new ReflectionManagerException("Error reading resources from package: " + packageName, e);
        }
    }

    private static Class<?> getClass(String className, @NonNull String packageName) {
        try {
            return Class.forName(packageName + "."
                    + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            throw new ReflectionManagerException("Class not found: " + packageName + "." + className, e);
        }
    }

    public static Set<String> getClassSimpleNameSet(Set<Class<?>> classes) {
        return classes.stream().map(Class::getSimpleName).collect(Collectors.toSet());
    }

    public static Optional<Class<?>> findClassBySimpleName(Set<Class<?>> classes, String classSimpleName) {
        return classes.stream()
                .filter(clazz -> clazz.getSimpleName().equals(classSimpleName))
                .findFirst();
    }

}

