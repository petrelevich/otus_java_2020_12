package ru.otus.dataprocessor;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import ru.otus.model.Measurement;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;

public class FileLoader implements Loader {

    private final String fileName;
    private final Gson gson;

    public FileLoader(String fileName) {
        this.fileName = fileName;
        this.gson = new Gson();
    }

    @Override
    public List<Measurement> load() {
        //читает файл, парсит и возвращает результат

        try {
            var resource = getClass().getClassLoader().getResource(fileName);
            if (resource == null) {
                throw new FileProcessException("file not found");
            }
            var gsonFileAsString = Files.readString(new File(resource.toURI()).toPath());
            return gson.fromJson(gsonFileAsString, new TypeToken<List<Measurement>>() {}.getType());
        } catch (Exception ex) {
            throw new FileProcessException(ex);
        }
    }
}
