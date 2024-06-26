package edu.school21.ex00;

import edu.school21.ex00.reflection.ReflectionClassHelper;
import edu.school21.ex00.reflection.ReflectionFieldHelper;
import edu.school21.ex00.reflection.ReflectionMethodHelper;
import edu.school21.ex00.util.MethodSignatureParser;
import edu.school21.ex00.util.ParameterTypeConverter;

import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {
    private static final String LINE_SEPARATOR = "-------------------------";
    private static final String CLASSES_STRING = "Classes:";
    private static final String ENTER_CLASS_NAME = "Enter class name:";
    private static final String ERROR_CLASS_DOES_NOT_EXIST = "Class '%s.class' does not exist!";
    private static final String FIELDS = "Fields:\n";
    private static final String METHODS = "Methods:\n";
    private static final String PROMPT_MSG_CREATE_OBJ = "Let's create an object.";
    private static final String PROMPT_MSG_CHANGE_FIELD = "Enter name of the field for changing:";
    private static final String PROMPT_ENTER_PARAM_VALUE = "Enter %s value:";
    private static final String PROMPT_MSG_CALL_METHOD = "Enter name of the method for call:";

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in);
             PrintWriter printWriter = new PrintWriter(System.out, true, StandardCharsets.UTF_8)) {

            Set<Class<?>> classes = ReflectionClassHelper.getAllClassesFromPackage("edu.school21.ex00.classes");
            Set<String> classSimpleNames = ReflectionClassHelper.getClassSimpleNameSet(classes);

            displayAvailableClasses(printWriter, classSimpleNames);
            String classSimpleName = getClassName(scanner, classSimpleNames);

            Class<?> clazz = ReflectionClassHelper.findClassBySimpleName(classes, classSimpleName)
                    .orElseThrow(() -> new IllegalArgumentException(String.format(ERROR_CLASS_DOES_NOT_EXIST, classSimpleName)));

            List<Field> allDeclaredClassFields = ReflectionFieldHelper.getAllDeclaredField(clazz);
            List<Method> allPublicMethods = ReflectionMethodHelper.getPublicMethodsExcludingToString(clazz);

            displayClassDetails(printWriter, allDeclaredClassFields, allPublicMethods);

            Object instance = createObject(scanner, printWriter, clazz, allDeclaredClassFields);

            updateField(scanner, printWriter, instance);

            callMethod(scanner, printWriter, instance);

            printWriter.println(instance);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private static void displayAvailableClasses(PrintWriter printWriter, Set<String> classSimpleNames) {
        printWriter.println(CLASSES_STRING);
        classSimpleNames.forEach(printWriter::println);
        printWriter.println(LINE_SEPARATOR + "\n" + ENTER_CLASS_NAME);
    }

    private static String getClassName(Scanner scanner, Set<String> classSimpleNames) {
        String classSimpleName = scanner.nextLine();
        if (!classSimpleNames.contains(classSimpleName)) {
            throw new IllegalArgumentException(String.format(ERROR_CLASS_DOES_NOT_EXIST, classSimpleName));
        }
        return classSimpleName;
    }

    private static void displayClassDetails(PrintWriter printWriter, List<Field> fields, List<Method> methods) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(LINE_SEPARATOR).append("\n").append(FIELDS);

        for (Field field : fields) {
            stringBuilder.append(field.getType().getSimpleName()).append(" ").append(field.getName()).append(";\n");
        }

        stringBuilder.append("\n").append(METHODS);

        for (Method method : methods) {
            stringBuilder.append(method.getReturnType().getSimpleName())
                    .append(" ")
                    .append(method.getName())
                    .append(Arrays.toString(method.getParameterTypes()))
                    .append(";\n");
        }

        stringBuilder.append(LINE_SEPARATOR).append("\n").append(PROMPT_MSG_CREATE_OBJ);
        printWriter.println(stringBuilder);
    }

    private static Object createObject(Scanner scanner, PrintWriter printWriter, Class<?> clazz, List<Field> fields)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<?> constructor = clazz.getConstructor(ReflectionFieldHelper.getParametersType(fields));
        List<Object> fieldValues = new ArrayList<>();

        for (Field field : fields) {
            printWriter.println(field.getName());
            fieldValues.add(readObjectFromCmd(scanner, field.getType()));
        }

        Object instance = constructor.newInstance(fieldValues.toArray());
        printWriter.println("Object created: " + instance + "\n" + LINE_SEPARATOR + "\n" + PROMPT_MSG_CHANGE_FIELD);
        return instance;
    }

    private static void updateField(Scanner scanner, PrintWriter printWriter, Object instance)
            throws NoSuchFieldException, IllegalAccessException {
        String fieldName = scanner.nextLine();

        Field selectedField = instance.getClass().getDeclaredField(fieldName);
        printWriter.printf(PROMPT_ENTER_PARAM_VALUE + "\n", selectedField.getType().getSimpleName());

        ReflectionFieldHelper.changePrivateField(
                selectedField, instance, readObjectFromCmd(scanner, selectedField.getType())
        );

        printWriter.println("Object updated: " + instance + "\n" + LINE_SEPARATOR + "\n" + PROMPT_MSG_CALL_METHOD);
    }

    private static void callMethod(Scanner scanner, PrintWriter printWriter, Object instance)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String methodSignature = scanner.nextLine();

        String methodName = MethodSignatureParser.extractMethodName(methodSignature);
        List<String> methodsParameterTypes = MethodSignatureParser.extractParameterTypes(methodSignature);

        Class<?>[] parameterTypes = ParameterTypeConverter.convertListParameterTypeSimpleNameToClassArray(methodsParameterTypes);
        Method selectedMethod = instance.getClass().getMethod(methodName, parameterTypes);

        List<Object> methodParams = new ArrayList<>();
        for (Class<?> paramType : selectedMethod.getParameterTypes()) {
            printWriter.printf(PROMPT_ENTER_PARAM_VALUE + "\n", paramType.getSimpleName());
            printWriter.flush();
            methodParams.add(readObjectFromCmd(scanner, paramType));
        }

        Object returnValue = selectedMethod.invoke(instance, methodParams.toArray());
        if (selectedMethod.getReturnType() != void.class) {
            printWriter.println("Method returned: " + returnValue);
        }
    }

    private static Object readObjectFromCmd(Scanner scanner, Class<?> type) {
        String input = scanner.nextLine();
        if (type == boolean.class || type == Boolean.class) {
            return Boolean.parseBoolean(input);
        } else if (type == int.class || type == Integer.class) {
            return Integer.parseInt(input);
        } else if (type == double.class || type == Double.class) {
            return Double.parseDouble(input);
        } else if (type == long.class || type == Long.class) {
            return Long.parseLong(input);
        } else {
            return input;
        }
    }


}
