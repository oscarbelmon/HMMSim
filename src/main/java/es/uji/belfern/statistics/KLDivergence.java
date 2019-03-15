package es.uji.belfern.statistics;

import es.uji.belfern.hmm.TabulatedCSVProbabilityEmitter;

public class KLDivergence {
    public static final double LOG2E = 1.442695;
    private TabulatedCSVProbabilityEmitter<Integer> p;
    private TabulatedCSVProbabilityEmitter<Integer> q;

    public KLDivergence(TabulatedCSVProbabilityEmitter<Integer> p, TabulatedCSVProbabilityEmitter<Integer> q) {
        this.p = p;
        this.q = q;
    }

    public double divergence() {
        double divergence = 0;

        for(Integer symbol: p.symbols()) {
            divergence += p.getSymbolProbability(symbol) * Math.log(p.getSymbolProbabilityReal(symbol) / q.getSymbolProbabilityReal(symbol));
        }

        return LOG2E * divergence;
    }

    public double entropyP() {
        double entropy = 0;

        entropy = p.density().stream()
                .mapToDouble(e -> e * Math.log(e))
                .sum();

        return -LOG2E * entropy;
    }

    public double crossEntropy() {
        double cross = 0;

        for(Integer symbol: p.symbols()) {
            cross += p.getSymbolProbabilityReal(symbol) * Math.log(q.getSymbolProbabilityReal(symbol));
        }

        return LOG2E * cross;
    }
}
