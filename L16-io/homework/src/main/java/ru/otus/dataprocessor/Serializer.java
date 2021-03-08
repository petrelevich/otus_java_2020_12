package ru.otus.dataprocessor;

import java.util.Map;

public interface Serializer {

    //сериализует в json
    void serialize(Map<String, Double> data);
}
