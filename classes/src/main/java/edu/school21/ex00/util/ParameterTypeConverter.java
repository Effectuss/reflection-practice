package edu.school21.ex00.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ParameterTypeConverter {

    private static final Map<String, Class<?>> typeMap = new HashMap<>();

    static {
        typeMap.put("int", int.class);
        typeMap.put("Integer", Integer.class);
        typeMap.put("long", long.class);
        typeMap.put("Long", Long.class);
        typeMap.put("double", double.class);
        typeMap.put("Double", Double.class);
        typeMap.put("boolean", boolean.class);
        typeMap.put("Boolean", Boolean.class);
        typeMap.put("String", String.class);
    }

    private ParameterTypeConverter() {
    }

    public static Class<?> convertParameterTypeSimpleNameToFullType(String parameterType) {
        return typeMap.get(parameterType);
    }

    public static Class<?>[] convertListParameterTypeSimpleNameToClassArray(List<String> parameterTypes) {
        return parameterTypes.stream()
                .map(ParameterTypeConverter::convertParameterTypeSimpleNameToFullType)
                .toArray(Class<?>[]::new);
    }
}