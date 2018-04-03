package belfern.uji.es.hmm;

import java.util.ArrayList;
import java.util.List;

public class TabulatedProbabilityEmitter<T> implements Emitter<T> {
    private List<Probability<T>> probabilities = new ArrayList<>();
    private double accumulatedProbability = 0;

    public TabulatedProbabilityEmitter() {
    }

    @Override
    public T emmit() {
        double random = Math.random() * 100;
        for(Probability<T> probability: probabilities) {
            if(probability.lessThan(random)) return probability.symbol;
        }
        return probabilities.get(0).symbol;
    }

    public void addEmission(T symbol, double probability) throws IllegalArgumentException{
        if(probability > 100 || probability < 0) throw new IllegalArgumentException("Probability should a percentage between 0 and 100. Yours is: " + probability);
//        pdf.put(symbol, probability);
        accumulatedProbability += probability;
        probabilities.add(new Probability<>(symbol, accumulatedProbability));
    }

    private class Probability<T> {
        T symbol;
        double max;

        Probability(T symbol, double probability) {
            this.symbol = symbol;
            this.max = probability;
        }

        boolean lessThan(double random) {
            return random < max ? true : false;
        }
    }
}
