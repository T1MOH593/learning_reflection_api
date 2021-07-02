package hometask.task1;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Для получения данных программа должна
 * использовать только get-методы (нельзя использовать
 * значения приватных полей).
 */

public class Task2Runner {

    public static void main(String[] args) {
        Car car = new Car("Corolla", "Toyota");
        printQuery(car);
    }

    private static void printQuery(Car car) {
        String template = "INSERT INTO %s.%s (%s) VALUES (%s);";

        var tableName = Car.class.getAnnotation(Table.class).tableName();
        var schemaName = Car.class.getAnnotation(Table.class).schemaName();

        var fieldNames = Arrays.stream(Car.class.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Column.class))
                .sorted(Comparator.comparing(Field::getName))
                .map(field -> field.getAnnotation(Column.class))
                .map(Column::columnName)
                .collect(Collectors.joining(", "));

        var fieldValues = Arrays.stream(Car.class.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Column.class))
                .sorted(Comparator.comparing(Field::getName))
                .map(getGetter(car))
                .map(method -> {
                    try {
                        return method.invoke(car);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
        System.out.printf(template, tableName, schemaName, fieldNames, fieldValues);

    }

    private static Function<Field, Method> getGetter(Car car) {
        return method -> {
            try {
                return car.getClass().getMethod("get" + method.getName().substring(0, 1).toUpperCase() + method.getName().substring(1));
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                return null;
            }
        };
    }
}
