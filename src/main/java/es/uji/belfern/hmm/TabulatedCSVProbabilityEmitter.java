package es.uji.belfern.hmm;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TabulatedCSVProbabilityEmitter<T> implements Emitter<T> {
    private List<T> data;
    private Map<T, Long> histogram;

    public TabulatedCSVProbabilityEmitter(List<T> data) {
        this.data = data;
        processData();
    }

    private void processData() {
        histogram = data.stream()
                .collect(Collectors.toMap(e -> e, e -> 0L, (oldValud, newValue) -> newValue));

        data.stream()
                .forEach(e -> histogram.put(e, histogram.get(e)+1));
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
    public String toString() {
        return "TabulatedCSVProbabilityEmitter{" +
                "histogram=" + histogram +
                '}';
    }
}
