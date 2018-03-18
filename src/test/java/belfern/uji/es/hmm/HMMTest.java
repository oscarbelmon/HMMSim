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
            hmm.instanceEdge(null, "", null, 1);
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
        Edge edge = hmm.instanceEdge(start, end, ProbabilityDensityFunction.CONSTANT_PROBABILITY, 1);
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

    @Test
    public void nextTest() {
        Node<String> uno = hmm.instanceNode("Uno", () -> "Uno");
        Node<String > dos = hmm.instanceNode("Dos", () -> "Dos");
        Node<String> tres = hmm.instanceNode("Tres", () -> "Tres");
        hmm.instanceEdge(uno, dos, ProbabilityDensityFunction.CONSTANT_PROBABILITY, 0.4);
        hmm.instanceEdge(uno, tres, ProbabilityDensityFunction.CONSTANT_PROBABILITY, 0.6);
        assertThat(uno.nextNode(0.5), is(tres));
    }

    @Test
    public void nextWithIdsTest() {
        Node<String> uno = hmm.instanceNode("Uno", () -> "Uno");
        hmm.instanceNode("Dos", () -> "Dos");
        hmm.instanceNode("Tres", () -> "Tres");
        hmm.instanceEdge("Uno", "Dos", ProbabilityDensityFunction.CONSTANT_PROBABILITY, 0.4);
        hmm.instanceEdge("Uno", "Tres", ProbabilityDensityFunction.CONSTANT_PROBABILITY, 0.6);
        assertThat(uno.nextNode(0.05), is(hmm.nodes.get("Tres")));
    }

    @Test
    public void accumulateProbabilitiesTest() {
        Node<String> uno = hmm.instanceNode("Uno", () -> "Uno");
        Node<String> dos = hmm.instanceNode("Dos", () -> "Dos");
        Node<String> tres = hmm.instanceNode("Tres", () -> "Tres");
        Node<String> cuatro = hmm.instanceNode("Cuatro", () -> "Cuatro");
        hmm.instanceEdge("Uno", "Uno", ProbabilityDensityFunction.CONSTANT_PROBABILITY, 0.1);
        hmm.instanceEdge("Uno", "Dos", ProbabilityDensityFunction.CONSTANT_PROBABILITY, 0.2);
        hmm.instanceEdge("Uno", "Tres", ProbabilityDensityFunction.CONSTANT_PROBABILITY, 0.3);
        hmm.instanceEdge("Uno", "Cuatro", ProbabilityDensityFunction.CONSTANT_PROBABILITY, 0.4);

        Node res = uno.nextNode(0.05);
        assertThat(res, is(uno));

        res = uno.nextNode(0.15);
        assertThat(res, is(dos));

        res = uno.nextNode(0.35);
        assertThat(res, is(tres));

        res = uno.nextNode(0.95);
        assertThat(res, is(cuatro));
    }
}
