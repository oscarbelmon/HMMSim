package es.uji.belfern.hmm;

import java.io.Serializable;

//@FunctionalInterface
public interface Emitter<T> extends Serializable {
    T emmit();
    default double getSymbolProbability(T symbol) {
        return 1;
    }
    long size();
//    double getMaxProbability();
//    T getSymbolMaxProbability();
}
