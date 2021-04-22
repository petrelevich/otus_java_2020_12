package ru.otus.mainpackage.welcome;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class LocalHello {
    private static final Logger logger = LoggerFactory.getLogger(LocalHello.class);


    //!!! Вообще, PostConstruct - это плохая практика !!!
    @PostConstruct
    public void printHello() {
        logger.info("Hello from PostConstruct");

    }
}
