package edu.school21.ex00.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MethodSignatureParser {

    private MethodSignatureParser() {

    }

    public static String extractMethodName(String methodSignature) {
        Pattern pattern = Pattern.compile("^(\\w+)\\s*\\(");
        Matcher matcher = pattern.matcher(methodSignature);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            throw new IllegalArgumentException("Invalid method signature format.");
        }
    }

    public static List<String> extractParameterTypes(String methodSignature) {
        Pattern pattern = Pattern.compile("\\((.*)\\)");
        Matcher matcher = pattern.matcher(methodSignature);

        if (matcher.find()) {
            String parameters = matcher.group(1);
            return parseParameters(parameters);
        } else {
            throw new IllegalArgumentException("Invalid method signature format.");
        }
    }

    private static List<String> parseParameters(String parameters) {
        List<String> parameterTypes = new ArrayList<>();

        if (!parameters.isEmpty()) {
            String[] paramsArray = parameters.split("\\s*,\\s*");
            parameterTypes.addAll(Arrays.asList(paramsArray));
        }

        return parameterTypes;
    }

}
