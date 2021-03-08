package ru.otus.dataprocessor;

import com.google.gson.Gson;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class FileSerializer implements Serializer {

    private final String fileName;
    private final Gson gson;

    public FileSerializer(String fileName) {
        this.fileName = fileName;
        this.gson = new Gson();
    }

    @Override
    public void serialize(Map<String, Double> data) {
        //формирует результирующий json и сохраняет его в файл

        try {
            var keys = new ArrayList<>(data.keySet());
            keys.sort(String::compareTo);
            var sortedData = new LinkedHashMap<>();
            for (var key : keys) {
                sortedData.put(key, data.get(key));
            }
            var serialized = gson.toJson(sortedData);

            var file = new File(fileName);
            Files.writeString(file.toPath(), serialized);
        } catch (Exception ex) {
            throw new FileProcessException(ex);
        }
    }
}
