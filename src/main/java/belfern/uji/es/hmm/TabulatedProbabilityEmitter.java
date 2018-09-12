package belfern.uji.es.hmm;

import java.util.*;

public class TabulatedProbabilityEmitter<T> implements Emitter<T> {
    private final Map<T, Probability<T>> probabilities = new LinkedHashMap<>();
    private double accumulatedProbability = 0;

    public TabulatedProbabilityEmitter() {
    }

    @Override
    public T emmit() {
        double random = Math.random();
        for(Probability<T> probability: probabilities.values()) {
            if(probability.lessThan(random)) return probability.symbol;
        }
        return probabilities.get(0).symbol;
    }

    @Override
    public double getSymbolProbability(T symbol) {
        return probabilities.get(symbol).probability;
    }

    public void addEmission(T symbol, double probability) throws IllegalArgumentException{
        if(probability > 1 || probability < 0)
            throw new IllegalArgumentException("Probability should a percentage between 0 and 1. Yours is: " + probability);
        accumulatedProbability += probability;
        probabilities.put(symbol, new Probability<>(symbol, probability, accumulatedProbability));
    }

    @Override
    public String toString() {
        return "TabulatedProbabilityEmitter{" +
                "probabilities=" + probabilities +
                ", accumulatedProbability=" + accumulatedProbability +
                '}';
    }

    private class Probability<T> {
        final T symbol;
        final double probability;
        final double accumulatedProbability;

        Probability(T symbol, double probability, double accumulatedProbability) {
            this.symbol = symbol;
            this.probability = probability;
            this.accumulatedProbability = accumulatedProbability;
        }

        boolean lessThan(double random) {
            return random < accumulatedProbability;
        }

        @Override
        public String toString() {
            return "Probability{" +
                    "symbol=" + symbol +
                    ", probability=" + probability +
                    ", accumulatedProbability=" + accumulatedProbability +
                    '}';
        }
    }
}
