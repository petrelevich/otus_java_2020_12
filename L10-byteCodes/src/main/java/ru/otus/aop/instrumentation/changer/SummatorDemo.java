package ru.otus.aop.instrumentation.changer;

/*
java -javaagent:summatorDemo.jar -jar summatorDemo.jar
*/
public class SummatorDemo {
    public static void main(String[] args) {
        AnyClass anyClass = new AnyClass();
        System.out.println(anyClass.summator(30, 20));
    }
}
