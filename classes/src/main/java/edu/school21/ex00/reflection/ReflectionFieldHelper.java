package edu.school21.ex00.reflection;

import lombok.NonNull;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public final class ReflectionFieldHelper {

    private ReflectionFieldHelper() {
    }

    public static List<Field> getAllDeclaredField(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields()).toList();
    }

    public static Class<?>[] getParametersType(List<Field> fields) {
        return fields.stream().map(Field::getType).toArray(Class[]::new);
    }

    public static void changePrivateField(@NonNull Field field, Object obj, Object value)
            throws IllegalAccessException {
        field.setAccessible(true);
        field.set(obj, value);
    }
}
