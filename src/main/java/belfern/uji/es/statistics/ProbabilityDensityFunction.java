package belfern.uji.es.statistics;

@FunctionalInterface
public interface ProbabilityDensityFunction {
    ProbabilityDensityFunction CONSTANT_PROBABILITY = () -> 1.0 / Math.PI;
    double density();
}
