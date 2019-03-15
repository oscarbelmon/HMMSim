package es.uji.belfern.hmm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TabulatedCSVProbabilityEmitter<T> implements Emitter<T> {
    private List<T> data;
    private Map<T, Long> histogram;
//    private double maxProbability = 0;
//    private T symbolMaxProbability;

    public TabulatedCSVProbabilityEmitter(List<T> data) {
        this.data = data;
        processData();
//        findMax();
    }

    public List<T> symbols() {
        return new ArrayList<>(histogram.keySet());
    }

    public List<Double> density() {
        double sum = histogram.values().stream()
                .mapToDouble(Long::doubleValue)
                .sum();

        return histogram.values().stream()
                .mapToDouble(v -> v/sum)
                .boxed()
                .collect(Collectors.toList());
    }

    private void processData() {
        histogram = data.stream()
                .collect(Collectors.toMap(e -> e, e -> 0L, (oldValud, newValue) -> newValue));

        data.stream()
                .forEach(e -> histogram.put(e, histogram.get(e)+1));
    }

//    private void findMax() {
//        double max = histogram.entrySet()
//                .stream()
//                .mapToDouble(entry -> entry.getValue())
//                .max()
//                .orElse(0);
//        symbolMaxProbability = histogram.entrySet()
//                .stream()
//                .filter(entry -> entry.getValue() >= max)
//                .findFirst()
//                .orElse(null)
//                .getKey();
//        maxProbability = (max + 1.0) / (data.size() + 1.0);
//    }

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

    public double getSymbolProbabilityReal(T symbol) {
        double result = 1.0 / (data.size());
        if(histogram.containsKey(symbol)) {
            result = ((double)histogram.get(symbol)) / (data.size());
        }
        return result;
    }

//    @Override
//    public double getMaxProbability() {
//        return maxProbability;
//    }

//    @Override
//    public T getSymbolMaxProbability() {
//        return symbolMaxProbability;
//    }


    @Override
    public long size() {
        return data.size();
    }

    @Override
    public String toString() {
        return "TabulatedCSVProbabilityEmitter{" +
                "histogram=" + histogram +
                '}';
    }
}
