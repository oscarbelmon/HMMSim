package es.uji.belfern.hmm;

//@FunctionalInterface
public interface Emitter<T> {
    T emmit();
    default double getSymbolProbability(T symbol) {
        return 1;
    }
}