package practiceBeforePractice;

public class ReflectionApiExample {

    public static void main(String[] args) {
        var ivan = new User(20L, "Ivan", 24);
        Class<? extends User> aClass = ivan.getClass();
        System.out.println(ivan.getClass().getAnnotations());
    }
}
