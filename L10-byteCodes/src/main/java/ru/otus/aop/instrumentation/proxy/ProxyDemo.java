package ru.otus.aop.instrumentation.proxy;


/*
    java -javaagent:proxyDemo.jar -jar proxyDemo.jar
*/
public class ProxyDemo {

    public static void main(String[] args) {
        MyClassImpl myClass = new MyClassImpl();
        myClass.secureAccess("Security Param");
    }
}
