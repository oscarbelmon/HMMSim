package belfern.uji.es.hmm;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

public class HMMTest {
    private static HMM hmm;

    @BeforeAll
    public static void start() {
        hmm = new HMM();
    }

    @Test
    public void instanceNodeNotNullTest() {
        Node node = hmm.instanceNode("Primero", () -> 1.0);
        assertThat(node, notNullValue());
    }

    @Test
    public void instanceEdgeNoNullTest() {
        Edge edge = hmm.instanceEdge(null, null);
        assertThat(edge, notNullValue());
    }
}
