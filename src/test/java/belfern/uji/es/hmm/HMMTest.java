package belfern.uji.es.hmm;

import belfern.uji.es.statistics.ProbabilityDensityFunction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.jupiter.api.Assertions.fail;

public class HMMTest {
    private static HMM hmm;

    @BeforeAll
    public static void start() {
        hmm = new HMM();
    }

    @Test
    public void nodeWithNullEmitterExceptionTest() {
        try {
            hmm.instanceNode("hola", null);
            fail("You should not see this.");
        } catch (Exception e) {
            // Nothing
        }
    }

    @Test
    public void edgeWithNullNodesExceptionTest() {
        try {
            hmm.instanceEdge(null, null, null);
            fail("You should not see this.");
        } catch (Exception e) {
            // Nothing
        }
    }

    @Test
    public void instanceNodeNotNullTest() {
        Node node = hmm.instanceNode("Primero", () -> 1.0);
        assertThat(node, notNullValue());
    }

    @Test
    public void instanceEdgeNoNullTest() {
        Node start = hmm.instanceNode("start", () -> 0);
        Node end = hmm.instanceNode("end", () -> 1);
        Edge edge = hmm.instanceEdge(start, end, ProbabilityDensityFunction.CONSTANT_PROBABILITY);
        assertThat(edge, notNullValue());
    }

    @Test
    public void initialEdgeNotNullExceptionTest() {
        try {
            hmm.setInitialNode(null);
        } catch (Exception e) {
            // Nothing
        }
    }
}
