package hometask.task1;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Создать класс Car с полями String brand и String model.
 * Создать аннотации @Table (принимает название схемы и таблицы
 * в базе данных) и @Column (принимает название колонки в таблице
 * базы данных). Пометить класс аннотацией @Table и поля
 * аннотацией @Column. Написать программу, принимающую
 * объект класс  Car c проинициализированными полями и
 * составляющую запрос "INSERT" в виде строки на основании
 * данных объекта.
 *     Пример: дан объект Car car = new Car("Toyota", "Corolla");
 *     Программа, принимающая этот объект, должна вывести в консоль строку:
 * "INSERT INTO garage.car (model, brand) VALUES ('Toyota', 'Corolla');" 
 */

public class Task1Runner {

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
                .peek(field -> field.setAccessible(true))
                .map(field -> {
                    try {
                        return String.valueOf(field.get(car));
                    } catch (IllegalAccessException e) {
                        return "";
                    }
                })
                .collect(Collectors.joining(", "));
        System.out.printf(template, tableName, schemaName, fieldNames, fieldValues);
    }
}
