package belfern.uji.es.hmm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;


class TabulatedProbabilityEmitterTest {

    @Test
    void emmit() {
        TabulatedProbabilityEmitter<Integer> cpe = new TabulatedProbabilityEmitter<>();
        cpe.addEmission(0, 0.5);
        cpe.addEmission(1, 0.5);

        double accumulator = 0;
        for (int i = 0; i < 1000000; i++) {
            accumulator += cpe.emmit();
        }

        assertEquals(500000, accumulator, 1000); // Precision 0.1%
    }

    @Test
    public void emmitTest() {
        TabulatedProbabilityEmitter<String> emitterOne = new TabulatedProbabilityEmitter<>();
        emitterOne.addEmission("a", .1);
        emitterOne.addEmission("b", .4);
        emitterOne.addEmission("c", .5);

        assertThat(emitterOne.getSymbolProbability("a"), is(0.1));

        System.out.println(emitterOne);
    }
}