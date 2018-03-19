package belfern.uji.es.hmm;

import belfern.uji.es.statistics.ProbabilityDensityFunction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class HMMTest {
    private static HMM<String> hmm;

    @BeforeAll
    public static void start() {
        hmm = new HMM<>();
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
        Node<String> node = hmm.instanceNode("Primero", () -> "Uno");
        assertThat(node, notNullValue());
    }

    @Test
    public void instanceEdgeNoNullTest() {
        Node<String> start = hmm.instanceNode("start", () -> "Start");
        Node<String> end = hmm.instanceNode("end", () -> "End");
        Edge<String> edge = hmm.instanceEdge(start, end, ProbabilityDensityFunction.CONSTANT_PROBABILITY, 1);
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
        assertThat(uno.nextNode(0.05), is(hmm.nodes.get("Dos")));
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

        Node<String> res = uno.nextNode(0.05);
        assertThat(res, is(uno));

        res = uno.nextNode(0.15);
        assertThat(res, is(dos));

        res = uno.nextNode(0.35);
        assertThat(res, is(tres));

        res = uno.nextNode(0.95);
        assertThat(res, is(cuatro));
    }

    @Test
    public void generateSequenceTest() {
        List<String> expected = Arrays.asList("Uno", "Dos", "Uno", "Dos", "Uno", "Dos", "Uno", "Dos", "Uno", "Dos");
        Node<String> uno = hmm.instanceNode("Uno", () -> "Uno");
        Node<String> dos = hmm.instanceNode("Dos", () -> "Dos");
        hmm.instanceEdge(uno, dos, ProbabilityDensityFunction.CONSTANT_PROBABILITY, 1.0);
        hmm.instanceEdge(dos, uno, ProbabilityDensityFunction.CONSTANT_PROBABILITY, 1.0);
        hmm.setInitialNode(uno);
        assertThat(hmm.generateSequence(10), is(expected));
    }

    @Test
    public void sequenceTest() {
        double uno_zero = 0.4;
        double uno_uno = 1 - uno_zero;
        double zero_uno = 0.8;
        double zero_zero = 1- zero_uno;
        double expectedRatio = zero_uno/(uno_zero + zero_uno);
        
        HMM<Integer> hmmInt = new HMM<>();
        Node<Integer> uno = hmmInt.instanceInitialNode("uno", () -> 1);
        Node<Integer> zero = hmmInt.instanceNode("zero", () -> 0);
        hmmInt.instanceEdge(uno, zero, ProbabilityDensityFunction.CONSTANT_PROBABILITY, uno_zero);
        hmmInt.instanceEdge(uno, uno, ProbabilityDensityFunction.CONSTANT_PROBABILITY, uno_uno);
        hmmInt.instanceEdge(zero, uno, ProbabilityDensityFunction.CONSTANT_PROBABILITY, zero_uno);
        hmmInt.instanceEdge(zero, zero, ProbabilityDensityFunction.CONSTANT_PROBABILITY, zero_zero);

        List<Integer> sequence = hmmInt.generateSequence(1000000);

        double ratio = sequence.stream()
                .mapToInt(s -> s)
                .average()
                .getAsDouble();

        assertEquals(expectedRatio, ratio, 0.001);
    }
}
