package ru.otus.singleton;

import java.util.Calendar;
import java.util.logging.Logger;

/**
 * Примеры singleton (а может и не singleton) в JDK.
 */
public class SingletonJdkDemo {
    public static void main(String[] args) {
        // Runtime
        Runtime runtime1 = Runtime.getRuntime();
        Runtime runtime2 = Runtime.getRuntime();
        System.out.println("--- Runtime ---");
        System.out.println("runtime1 = " + runtime1);
        System.out.println("runtime2 = " + runtime2);
        System.out.println("runtime1 == runtime2 -> " + (runtime1 == runtime2));

        // Logger (не совсем singleton, т.к. с параметром)
        Logger log1 = Logger.getLogger(SingletonJdkDemo.class.getName());
        Logger log2 = Logger.getLogger(SingletonJdkDemo.class.getName());

        System.out.println("--- Logger ---");
        System.out.println("log1 = " + log1);
        System.out.println("log2 = " + log2);
        System.out.println("log1 == log2 -> " + (log1 == log2));

        // Calendar (Factory, не Singleton)
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();

        System.out.println("--- Calendar ---");
        System.out.println("calendar1 == calendar2 -> " + (calendar1 == calendar2));
    }
}
