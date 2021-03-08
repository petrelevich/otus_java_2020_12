package ru.otus.dataprocessor;

import ru.otus.model.Measurement;
import java.util.List;

public class FileLoader implements Loader {

    public FileLoader(String fileName) {
    }

    @Override
    public List<Measurement> load() {
        //читает файл, парсит и возвращает результат
        return null;
    }
}
