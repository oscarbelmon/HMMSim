package es.uji.belfern.statistics;

import java.io.Serializable;

@FunctionalInterface
public interface ProbabilityDensityFunction extends Serializable {
    ProbabilityDensityFunction CONSTANT_PROBABILITY = () -> 1.0 / Math.PI;
    double density();
}
