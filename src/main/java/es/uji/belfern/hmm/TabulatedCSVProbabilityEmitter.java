package es.uji.belfern.hmm;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TabulatedCSVProbabilityEmitter<T> implements Emitter<T> {
    private List<T> data;
    private Map<T, Long> histogram;
    private double maxProbability = 0;
    private T symbolMaxProbability;

    public TabulatedCSVProbabilityEmitter(List<T> data) {
        this.data = data;
        processData();
        findMax();
    }

    private void processData() {
        histogram = data.stream()
                .collect(Collectors.toMap(e -> e, e -> 0L, (oldValud, newValue) -> newValue));

        data.stream()
                .forEach(e -> histogram.put(e, histogram.get(e)+1));
    }

    private void findMax() {
        double max = histogram.entrySet()
                .stream()
                .mapToDouble(entry -> entry.getValue())
                .max()
                .orElse(0);
        symbolMaxProbability = histogram.entrySet()
                .stream()
                .filter(entry -> entry.getValue() >= max)
                .findFirst()
                .orElse(null)
                .getKey();
        maxProbability = (max + 1.0) / (data.size() + 1.0);
    }

    @Override
    public T emmit() {
        return null;
    }

    @Override
    public double getSymbolProbability(T symbol) {
//        System.out.println(histogram);
        double result = 1.0 / (data.size() + 1.0);
        if(histogram.containsKey(symbol)) {
            result = (histogram.get(symbol) + 1.0) / (data.size() + 1.0);
        }
        return result;
    }

    @Override
    public double getMaxProbability() {
        return maxProbability;
    }

    @Override
    public T getSymbolMaxProbability() {
        return symbolMaxProbability;
    }

    @Override
    public String toString() {
        return "TabulatedCSVProbabilityEmitter{" +
                "histogram=" + histogram +
                '}';
    }
}
