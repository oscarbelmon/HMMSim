package belfern.uji.es.hmm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TabulatedProbabilityEmitterTest {

    @Test
    void emmit() {
        TabulatedProbabilityEmitter<Integer> cpe = new TabulatedProbabilityEmitter<>();
        cpe.addEmission(0, 50);
        cpe.addEmission(1, 50);

        double accumulator = 0;
        for (int i = 0; i < 1000000; i++) {
            accumulator += cpe.emmit();
        }

        assertEquals(500000, accumulator, 1000); // Precision 0.1%
    }
}