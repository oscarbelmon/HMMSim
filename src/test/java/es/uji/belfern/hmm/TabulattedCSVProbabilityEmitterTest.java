package es.uji.belfern.hmm;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

public class TabulattedCSVProbabilityEmitterTest {
    @Test
    public void getSymbolProbabilityTest() {
        TabulatedCSVProbabilityEmitter<String> emitter = new TabulatedCSVProbabilityEmitter(Arrays.asList("1", "1", "2"));
        assertThat(emitter.getSymbolProbability("1"), is(0.75));
    }
}
