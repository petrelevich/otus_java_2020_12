package ru.otus.dataprocessor;

import ru.otus.model.Measurement;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProcessorAggregator implements Processor {

    @Override
    public Map<String, Double> process(List<Measurement> data) {
        //группирует выходящий список по name, при этом суммирует поля value

        return data.stream()
                .collect(Collectors.groupingBy(Measurement::getName, Collectors.summingDouble(Measurement::getValue)));
    }
}
