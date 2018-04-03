package belfern.uji.es.hmm;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.core.Is.*;
import static org.hamcrest.MatcherAssert.*;

class ConstantProbabilityEmitterTest {

    @Test
    void emmit() {
        ConstantProbabilityEmitter<Integer> cpe = new ConstantProbabilityEmitter<>();
        cpe.addEmission(0, 50);
        cpe.addEmission(1, 50);

        double accum = 0;
        for (int i = 0; i < 1000000; i++) {
            accum += cpe.emmit();
        }

        assertEquals(500000, accum, 1000); // Precision 0.1%
    }
}