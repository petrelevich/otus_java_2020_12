package functionalstyle;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


public class ComparatorExample {

    // Описание в отдельном классе
    static class MyComparator implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            return o1.length() - o2.length();
        }
    }

    public static void main(String[] args) {
        List<String> list = Arrays.asList("AA", "BBB", "C");

        // создание инстанса
        list.sort(new MyComparator());

        // анонимный класс
        list.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.length() - o2.length();
            }
        });

        // Lambda
        list.sort((String s1, String s2) -> {
            return s1.length() - s2.length();
        });

        // Типы можно вывести
        list.sort((s1, s2) -> {
            return s1.length() - s2.length();
        });

        //В этом случае можно перейти к стандартному компоратору
        list.sort(Comparator.comparingInt(s -> s.length()));

        //Можно в компоратор передать ссылку на метод
        list.sort(Comparator.comparingInt(String::length));
    }
}
