package es.uji.belfern.hmm;

import es.uji.belfern.location.Environment;
import es.uji.belfern.statistics.KLDivergence;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.core.Is.*;

public class HMMTest {
    private static final List<Integer> REAL = Arrays.asList(-56,-56,-59,-59,-59,-60,-58,-58,-58,-58,-58,-59,-56,-58,-59,-59,-58,-59,-59,-60,-62,-59,-59,-59,-58,-57,-56,-58,-57,-57,-57,-56,-57,-59,-55,-57,-57,-57,-57,-57,-57,-59,-60,-58,-58,-58,-58,-57,-57,-56,-57,-57,-58,-58,-59,-55,-57,-57,-55,-58,-56,-58,-57,-57,-59,-57,-57,-55,-58,-56,-57,-58,-58,-59,-56,-58,-57,-55,-58,-58,-59,-57,-57,-58,-58,-59,-59,-59,-59,-59,-58,-59,-59,-57,-56,-59,-58,-56,-56,-56,-59,-59,-58,-57,-57,-59,-59,-59,-57,-58,-58,-58,-57,-57,-57,-58,-57,-57,-57,-57,-57,-57,-59,-57,-59,-57,-57,-58,-57,-57,-57,-59,-57,-57,-58,-57,-58,-57,-56,-57,-57,-57,-57,-57,-59,-57,-57,-57,-57,-57,-58,-58,-57,-60,-58,-58,-63,-59,-57,-57,-59,-59,-59,-59,-59,-57,-58,-56,-58,-57,-57,-57,-59,-57,-59,-58,-56,-57,-57,-59,-58,-59,-57,-56,-56,-56,-59,-57,-58,-57,-59,-56,-57,-57,-58,-60,-57,-58,-57,-57,-57,-57,-57,-59,-59,-59,-56,-58,-52,-52,-59,-58,-57,-58,-57,-58,-59,-59,-59,-58,-58,-58,-59,-59,-54,-58,-58,-57,-57,-57,-55,-57,-57,-56,-56,-57,-57,-57,-59,-56,-58,-56,-57,-58,-58,-58,-58,-55,-58,-58,-58,-57,-57,-57,-57,-55,-58,-58,-57,-57,-56,-56,-57,-55,-57,-56,-58,-57,-55,-57,-57,-56,-55,-55,-56,-55,-56,-56,-58,-57,-58,-57,-59,-56,-57,-57,-55,-57,-57,-56,-57,-58,-55,-57,-57,-58,-56,-56,-58,-55,-57,-57,-59,-59,-59,-59,-59,-57,-57,-57,-57,-58,-56,-57,-57,-60,-58,-61,-60,-59,-58,-59,-60,-59,-56,-56,-56,-59,-58,-58,-58,-59,-59,-57,-60,-60,-61,-58,-57,-60,-59,-59,-58,-57,-59,-59,-59,-60,-58,-58,-57,-57,-55,-58,-58,-57,-60,-56,-59,-58,-58,-58,-58,-58,-58,-59,-59,-59,-58,-57,-58,-59,-61,-61,-59,-59,-59,-60,-58,-59,-59,-58,-58,-57,-61,-58,-57,-57,-59,-60,-58,-59,-59,-59,-59,-57,-59,-58,-56,-58,-58,-60,-60,-60,-59,-59,-60,-58,-59,-59,-60,-60,-62,-60,-59,-60,-61,-63,-63,-56,-58,-58,-59,-59,-59,-59,-58,-59,-59,-59,-59,-60,-58,-61,-61,-59,-60,-60,-59,-59,-57,-59,-59,-63,-62,-63,-60,-62,-62,-62,-61,-60,-60,-59,-59,-59,-59,-60,-58,-60,-60,-59,-60,-60,-60,-60,-58,-57,-57,-61,-61,-59,-61,-60,-60,-59,-59,-58,-58,-57,-57,-57,-59,-59,-58,-57,-59,-59,-60,-58,-58,-58,-58,-59,-58,-59,-59,-59,-58,-63,-59,-59,-59,-59,-62,-61,-58,-59,-62,-59,-62,-59,-59,-59,-60,-60,-61,-61,-60,-62,-61,-61,-59,-58,-60,-60,-59,-59,-59,-59,-59,-59,-59,-59,-59,-61,-59,-60,-60,-57,-61,-61,-60,-61,-58,-58,-59,-58,-59,-59,-58,-60,-60,-60,-58,-57,-61,-58,-58,-58,-58,-59,-57,-59,-59,-59,-59,-59,-59,-59,-59,-58,-59,-59,-59,-60,-61,-57,-58,-59,-58,-58,-61,-58,-59,-60,-60,-60,-58,-59,-59,-59,-59,-60,-60,-60,-59,-57,-60,-58,-58,-59,-59,-60,-60,-61,-59,-60,-60,-60,-60,-58,-57,-58,-58,-58,-59,-59,-59,-59,-59,-58,-61,-59,-61,-59,-57,-57,-57,-59,-59,-59,-59,-61,-60,-60,-59,-59,-59,-60,-60,-60,-63,-61,-61,-58,-59,-61,-59,-60,-56,-58,-61,-58,-59,-59,-59,-58,-58,-62,-59,-61,-59,-61,-61,-59,-59,-58,-58,-59,-57,-59,-58,-58,-58,-57,-58,-58,-59,-59,-59,-58,-58,-59,-60,-59,-59,-61,-59,-59,-60,-60,-59,-59,-62,-59,-59,-59,-59,-58,-59,-59,-60,-60,-59,-61,-61,-61,-60,-58,-58,-58,-59,-58,-58,-59,-58,-59,-59,-59,-59,-59,-59,-59,-59,-59,-59,-58,-59,-61,-58,-59,-59,-58,-59,-59,-57,-59,-58,-58,-58,-61,-59,-59,-59,-58,-59,-59,-59,-60,-59,-59,-59,-58,-59,-61,-59,-61,-59,-61,-60,-59,-58,-61,-59,-58,-59,-58,-59,-58,-59,-58,-59,-59,-60,-60,-60,-63,-58,-59,-58,-59,-60,-59,-59,-59,-57,-56,-59,-57,-54,-54,-56,-56,-58,-56,-58,-56,-57,-58,-58,-60,-58,-57,-56,-57,-59,-58,-59,-60,-59,-58,-56,-58,-57,-58,-56,-60,-59,-58,-58,-57,-59,-58,-59,-56,-56,-60,-58,-57,-58,-59,-57,-57,-59,-57,-57,-56,-57,-55,-56,-57,-57,-57,-57,-59,-58,-58,-57,-58,-56,-56,-56,-57,-56,-56,-56,-55,-56,-57,-55,-57,-59,-57,-57,-56,-59,-59,-58,-58,-60,-57,-59,-57,-57,-57,-58,-59,-59,-59,-56,-57,-58,-58,-59,-57,-57,-57,-55,-55,-55,-57,-57,-57,-57,-57,-57,-57,-58,-57,-58,-57,-59,-56,-58,-55,-56,-58,-57,-60,-57,-57,-57,-57,-57,-57,-57,-57,-57,-58,-57,-57,-56,-57,-58,-57,-56,-56,-57,-57,-56,-56,-59,-56,-57,-57,-54,-57,-57,-57,-56,-58,-56,-55,-55,-60,-57,-55,-57,-56,-57,-57,-57,-58,-57,-58,-57,-55,-55,-56,-56,-58,-59,-57,-56,-56,-55,-55,-57,-58,-57,-57,-57,-58,-57,-58,-57,-57,-56,-56,-57,-57,-57,-55,-58,-58,-58,-56,-56,-57,-59,-56,-57,-57,-58,-59,-59,-59,-59);

    private HMM<String, String> hmm;

    @BeforeEach
    public void start() {
//        hmm = new HMM<>();
    }

    @Test
    public void nodeWithNullEmitterExceptionTest() {
        try {
            hmm = new HMM(Arrays.asList(1));
            hmm.instanceNode("hello", null);
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

//    @Test
//    public void instanceNodeNotNullTest() {
//        Node<String, String> node = hmm.instanceNode("First", () -> "One");
//        assertThat(node, notNullValue());
//    }
//
//    @Test
//    public void instanceEdgeNotNullTest() {
//        Node<String, String> start = hmm.instanceNode("start", () -> "Start");
//        Node<String, String> end = hmm.instanceNode("end", () -> "End");
//        Edge<String, String> edge = hmm.instanceEdge(start, end,1);
//        assertThat(edge, notNullValue());
//    }
//
//    @Test
//    public void nextTest() {
//        Node<String, String> one = hmm.instanceNode("One", () -> "One");
//        Node<String, String> two = hmm.instanceNode("Two", () -> "Two");
//        Node<String, String> three = hmm.instanceNode("Three", () -> "Three");
//        hmm.instanceEdge(one, two, 0.4);
//        hmm.instanceEdge(one, three, 0.6);
//        assertThat(one.nextNode(0.5), is(three));
//    }
//
//    @Test
//    public void nextWithIdsTest() {
//        Node<String, String> one = hmm.instanceNode("One", () -> "One");
//        hmm.instanceNode("Two", () -> "Two");
//        hmm.instanceNode("Three", () -> "Three");
//        hmm.instanceEdge("One", "Two", 0.4);
//        hmm.instanceEdge("One", "Three", 0.6);
//        assertThat(one.nextNode(0.05), is(hmm.nodes.get("Two")));
//    }
//
//    @Test
//    public void accumulateProbabilitiesTest() {
//        Node<String, String> one = hmm.instanceNode("One", () -> "One");
//        Node<String, String> two = hmm.instanceNode("Two", () -> "Two");
//        Node<String, String> three = hmm.instanceNode("Three", () -> "Three");
//        Node<String, String> four = hmm.instanceNode("Four", () -> "Four");
//        hmm.instanceEdge("One", "One", 0.1);
//        hmm.instanceEdge("One", "Two", 0.2);
//        hmm.instanceEdge("One", "Three", 0.3);
//        hmm.instanceEdge("One", "Four", 0.4);
//
//        Node<String, String> res = one.nextNode(0.05);
//        assertThat(res, is(one));
//
//        res = one.nextNode(0.15);
//        assertThat(res, is(two));
//
//        res = one.nextNode(0.35);
//        assertThat(res, is(three));
//
//        res = one.nextNode(0.95);
//        assertThat(res, is(four));
//    }
//
//    @Test
//    public void generateSequenceTest() {
//        List<String> expected = Arrays.asList("One", "Two", "One", "Two", "One", "Two", "One", "Two", "One", "Two");
//        Node<String, String> one = hmm.instanceNode("One", () -> "One");
//        Node<String, String> two = hmm.instanceNode("Two", () -> "Two");
//        hmm.instanceEdge(one, two, 1.0);
//        hmm.instanceEdge(two, one, 1.0);
//        hmm.addInitialNode(one, 1);
//        assertThat(hmm.generateSequence(10), is(expected));
//    }
//
//    @Test
//    public void generateSequenceTest2() {
//        List<String> expected = Arrays.asList("One", "Two", "One", "Two", "One", "Two", "One", "Two", "One", "Two");
//        Node<String, String> one = hmm.instanceNode("One", () -> "One");
//        Node<String, String> two = hmm.instanceNode("Two", () -> "Two");
//        hmm.instanceEdge(one, two, 1.0);
//        hmm.instanceEdge(two, one, 1.0);
//        hmm.addInitialNode(one, 1);
//        assertThat(hmm.generateSequence(10), is(expected));
//    }
//
//    @Test
//    public void sequenceTest() {
//        double one_zero = 0.4;
//        double one_one = 1 - one_zero;
//        double zero_one = 0.8;
//        double zero_zero = 1- zero_one;
//        double expectedRatio = zero_one/(one_zero + zero_one);
//
//        HMM<String, Integer> hmmInt = new HMM<>();
//        Node<String, Integer> one = hmmInt.instanceNode("one", () -> 1);
//        hmmInt.addInitialNode(one, 1);
//        Node<String, Integer> zero = hmmInt.instanceNode("zero", () -> 0);
//        hmmInt.instanceEdge(one, zero, one_zero);
//        hmmInt.instanceEdge(one, one, one_one);
//        hmmInt.instanceEdge(zero, one, zero_one);
//        hmmInt.instanceEdge(zero, zero, zero_zero);
//
//        List<Integer> sequence = hmmInt.generateSequence(1000000);
//
//        double ratio = sequence.stream()
//                .mapToInt(s -> s)
//                .average()
//                .getAsDouble();
//
//        assertEquals(expectedRatio, ratio, 0.001);
//    }
//
//    @Test
//    public void sequenceTabulatedTest() {
//        double one_zero = 0.6;
//        double one_one = 1 - one_zero;
//        double zero_one = 0.8;
//        double zero_zero = 1- zero_one;
//        double expectedRatio = zero_one/(one_zero + zero_one);
//
//        HMM<String, Integer> hmmInt = new HMM<>();
//        TabulatedProbabilityEmitter<Integer> emitterOne = new TabulatedProbabilityEmitter<>();
//        emitterOne.addEmission(1,1);
//        TabulatedProbabilityEmitter<Integer> emitterZero = new TabulatedProbabilityEmitter<>();
//        emitterZero.addEmission(0, 1);
//        Node<String, Integer> one = hmmInt.instanceNode("one", emitterOne);
//        hmmInt.addInitialNode(one, 1);
//        Node<String, Integer> zero = hmmInt.instanceNode("zero", emitterZero);
//        hmmInt.instanceEdge(one, zero, one_zero);
//        hmmInt.instanceEdge(one, one, one_one);
//        hmmInt.instanceEdge(zero, one, zero_one);
//        hmmInt.instanceEdge(zero, zero, zero_zero);
//
//        List<Integer> sequence = hmmInt.generateSequence(1000000);
//
//        double ratio = sequence.stream()
//                .mapToInt(s -> s)
//                .average()
//                .getAsDouble();
//
//        assertEquals(expectedRatio, ratio, 0.001);
//    }

    @Test
    public void sequenceTabulatedTest2() {
        double one_zero = 0.6;
        double one_one = 1 - one_zero;
        double zero_one = 0.8;
        double zero_zero = 1 - zero_one;
        double expectedRatio = zero_one / (one_zero + zero_one);

        HMM<String, Integer> hmmInt = new HMM<>(Arrays.asList(0, 1));
        TabulatedProbabilityEmitter<Integer> emitterOne = new TabulatedProbabilityEmitter<>();
        emitterOne.addEmission(1, 1);
        TabulatedProbabilityEmitter<Integer> emitterZero = new TabulatedProbabilityEmitter<>();
        emitterZero.addEmission(0, 1);
        Node<String, Integer> one = hmmInt.instanceNode("one", emitterOne);
        hmmInt.addInitialNode(one, 1);
        Node<String, Integer> zero = hmmInt.instanceNode("zero", emitterZero);
        hmmInt.instanceEdge(one, zero, one_zero);
        hmmInt.instanceEdge(one, one, one_one);
        hmmInt.instanceEdge(zero, one, zero_one);
        hmmInt.instanceEdge(zero, zero, zero_zero);

        List<Integer> sequence = hmmInt.generateSequence(100000);

        double ratio = sequence.stream()
                .mapToInt(s -> s)
                .average()
                .getAsDouble();

        assertEquals(expectedRatio, ratio, 0.01);
    }

//    @Test
//    public void initializationTest() {
//        Node<String, String> one = hmm.instanceNode("One", () -> "One");
//        Node<String, String> two = hmm.instanceNode("Two", () -> "Two");
//        hmm.instanceEdge(one, two, 1.0);
//        hmm.instanceEdge(two, one, 1.0);
//        hmm.addInitialNode(one, 1);
//
//        hmm.initializationForward("One");
//
//        assertThat(one, is(one));
//        assertEquals(one, one);
//
//        double result = hmm.nodes.values().stream()
//                .filter(one::equals)
////                .filter(node -> one.equals(node))
//                .mapToDouble(node -> node.alfa)
//                .findFirst()
//                .getAsDouble();
//
//        assertEquals(result, 1.0, 0.1);
//
//        result = hmm.nodes.values().stream()
//                .filter(two::equals)
//                .mapToDouble(node -> node.alfa)
//                .findFirst()
//                .getAsDouble();
//
//        assertEquals(result, 0.0, 0.1);
//    }

    @Test
    public void recursionTest() {
//        HMM<String, Integer> hmmInt = new HMM<>();

        TabulatedProbabilityEmitter<String> emitterOne = new TabulatedProbabilityEmitter<>();
        emitterOne.addEmission("One", 1);
        emitterOne.addEmission("Two", 0);
        TabulatedProbabilityEmitter<String> emitterTwo = new TabulatedProbabilityEmitter<>();
        emitterTwo.addEmission("One", 0);
        emitterTwo.addEmission("Two", 1);

        Node<String, String> one = hmm.instanceNode("One", emitterOne);
        Node<String, String> two = hmm.instanceNode("Two", emitterTwo);
        hmm.instanceEdge(one, two, 1.0);
        hmm.instanceEdge(two, one, 1.0);
        hmm.addInitialNode(one, 1);

        hmm.initializationForward("One");
        hmm.recursionForward(Arrays.asList("One", "Two", "One", "Two", "One", "Two"));

        double[] oneForward = {1.0, 0.0, 1.0, 0.0, 1.0, 0.0};
        assertArrayEquals(oneForward, ArrayUtils.toPrimitive(one.getAlfas().toArray(new Double[0])));
        double[] twoForward = {0.0, 1.0, 0.0, 1.0, 0.0, 1.0};
        assertArrayEquals(twoForward, ArrayUtils.toPrimitive(two.getAlfas().toArray(new Double[0])));
    }

    @Test
    public void terminationTest() {
        TabulatedProbabilityEmitter<String> emitterOne = new TabulatedProbabilityEmitter<>();
        emitterOne.addEmission("One", 1);
        emitterOne.addEmission("Two", 0);
        TabulatedProbabilityEmitter<String> emitterTwo = new TabulatedProbabilityEmitter<>();
        emitterTwo.addEmission("One", 0);
        emitterTwo.addEmission("Two", 1);

        Node<String, String> one = hmm.instanceNode("One", emitterOne);
        Node<String, String> two = hmm.instanceNode("Two", emitterTwo);
        hmm.instanceEdge(one, two, 1.0);
        hmm.instanceEdge(two, one, 1.0);
        hmm.addInitialNode(one, 1);

        hmm.initializationForward("One");
        hmm.recursionForward(Arrays.asList("One", "Two", "One", "Two", "One", "Two"));
        double probability = hmm.terminationForward(6);

        assertEquals(1.0, probability, 0.0001);

        hmm.initializationForward("One");
        hmm.recursionForward(Arrays.asList("One", "Two", "One", "Two", "One", "One"));
        probability = hmm.terminationForward(6);

        assertEquals(0.0, probability, 0.01);
    }

    @Test
    public void forwardTest() {
        hmm = new HMM<>(Arrays.asList("One", "Two"));
        TabulatedProbabilityEmitter<String> emitterOne = new TabulatedProbabilityEmitter<>();
        emitterOne.addEmission("One", .4);
        emitterOne.addEmission("Two", .6);
        TabulatedProbabilityEmitter<String> emitterTwo = new TabulatedProbabilityEmitter<>();
        emitterTwo.addEmission("One", .5);
        emitterTwo.addEmission("Two", .5);

        Node<String, String> one = hmm.instanceNode("One", emitterOne);
        Node<String, String> two = hmm.instanceNode("Two", emitterTwo);
        hmm.instanceEdge(one, one, 0.2);
        hmm.instanceEdge(one, two, 0.8);
        hmm.instanceEdge(two, one, 1.0);
        hmm.addInitialNode(one, 0.7);
        hmm.addInitialNode(two, 0.3);

        hmm.forward(Arrays.asList("One", "Two", "One", "Two", "One", "Two", "One", "One", "One", "Two", "Two"));

        double[] expectedOne = {0.28, 0.1236, 0.054688, 0.03622656, 0.01164820, 0.010092159, 0.002671085, 0.001828432, 0.0005736483, 0.0005076615, 0.0001985950};
        assertArrayEquals(expectedOne, ArrayUtils.toPrimitive(one.getAlfas().toArray(new Double[0])), 0.001);

        double[] expectedTwo = {0.15, 0.1120, 0.049440, 0.02187520, 0.01449062, 0.004659282, 0.004036864, 0.001068434, 0.0007313729, 0.0002294593, 0.0002030646};
        assertArrayEquals(expectedTwo, ArrayUtils.toPrimitive(two.getAlfas().toArray(new Double[0])), 0.001);

        String[] expected = {"One", "Two", "One", "Two", "One", "Two", "One", "Two", "One", "Two"};
        assertArrayEquals(expected, hmm.viterbi(Arrays.asList("One", "Two", "One", "Two", "Two", "One", "One", "One", "One", "One")).toArray(new String[expected.length]));
    }

    @Test
    public void backwardTest() {
        TabulatedProbabilityEmitter<String> emitterOne = new TabulatedProbabilityEmitter<>();
        emitterOne.addEmission("a", 0.3);
        emitterOne.addEmission("b", 0.7);
        TabulatedProbabilityEmitter<String> emitterTwo = new TabulatedProbabilityEmitter<>();
        emitterTwo.addEmission("a", 0.7);
        emitterTwo.addEmission("b", 0.3);

        Node<String, String> X = hmm.instanceNode("X", emitterOne);
        Node<String, String> Y = hmm.instanceNode("Y", emitterTwo);
        hmm.instanceEdge(X, X, 0.9);
        hmm.instanceEdge(X, Y, 0.1);
        hmm.instanceEdge(Y, Y, 0.9);
        hmm.instanceEdge(Y, X, 0.1);
        hmm.addInitialNode(X, 0.3);
        hmm.addInitialNode(Y, 0.7);

        hmm.backwardTranssitions(Arrays.asList("a", "a", "b"));

        double[] expectedX = {0.202, 0.66, 1.0};
        assertArrayEquals(expectedX, ArrayUtils.toPrimitive(X.getBetas().toArray(new Double[0])), 0.001);

        double[] expectedY = {0.234, 0.34, 1.0};
        assertArrayEquals(expectedY, ArrayUtils.toPrimitive(Y.getBetas().toArray(new Double[0])), 0.001);
    }

    @Test
    public void viterbiTest() {
        TabulatedProbabilityEmitter<String> emitterOne = new TabulatedProbabilityEmitter<>();
        emitterOne.addEmission("One", .4);
        emitterOne.addEmission("Two", .6);
        TabulatedProbabilityEmitter<String> emitterTwo = new TabulatedProbabilityEmitter<>();
        emitterTwo.addEmission("One", .5);
        emitterTwo.addEmission("Two", .5);

        Node<String, String> one = hmm.instanceNode("One", emitterOne);
        Node<String, String> two = hmm.instanceNode("Two", emitterTwo);
        hmm.instanceEdge(one, one, 0.2);
        hmm.instanceEdge(one, two, 0.8);
        hmm.instanceEdge(two, one, 0.5);
        hmm.instanceEdge(two, two, 0.5);
        hmm.addInitialNode(one, 0.7);
        hmm.addInitialNode(two, 0.3);

        String[] expected = {"One", "Two", "Two", "One", "Two", "One", "Two"};
        assertArrayEquals(expected, hmm.viterbi(Arrays.asList("One", "Two", "One", "Two", "One", "Two", "One")).toArray(new String[expected.length]));
    }

    @Test
    public void viterbiTest2() {
        TabulatedProbabilityEmitter<String> emitterOne = new TabulatedProbabilityEmitter<>();
        emitterOne.addEmission("One", 1);
        emitterOne.addEmission("Two", 0);
        TabulatedProbabilityEmitter<String> emitterTwo = new TabulatedProbabilityEmitter<>();
        emitterTwo.addEmission("One", 0);
        emitterTwo.addEmission("Two", 1);

        Node<String, String> one = hmm.instanceNode("One", emitterOne);
        Node<String, String> two = hmm.instanceNode("Two", emitterTwo);
        hmm.instanceEdge(one, one, 0.0);
        hmm.instanceEdge(one, two, 1.0);
        hmm.instanceEdge(two, one, 1.0);
        hmm.instanceEdge(two, two, 0.0);
        hmm.addInitialNode(one, 1.0);
        hmm.addInitialNode(two, 0.0);

//        System.out.println(hmm.viterbi(Arrays.asList("One", "One", "One", "Two", "One", "Two", "One", "One")));

        String[] expected = {"One", "One", "One", "One", "One", "One", "One", "One"};
        String[] real = hmm.viterbi(Arrays.asList("One", "One", "One", "Two", "One", "Two", "One", "One")).toArray(new String[expected.length]);

        assertArrayEquals(expected, real);
    }

    @Test
    public void viterbiTest3() {
        TabulatedProbabilityEmitter<String> emitterH = new TabulatedProbabilityEmitter<>();
        emitterH.addEmission("A", .2);
        emitterH.addEmission("C", .3);
        emitterH.addEmission("G", .3);
        emitterH.addEmission("T", .2);
        TabulatedProbabilityEmitter<String> emitterL = new TabulatedProbabilityEmitter<>();
        emitterL.addEmission("A", .3);
        emitterL.addEmission("C", .2);
        emitterL.addEmission("G", .2);
        emitterL.addEmission("T", .3);

        Node<String, String> h = hmm.instanceNode("H", emitterH);
        Node<String, String> l = hmm.instanceNode("L", emitterL);
        hmm.instanceEdge(h, h, 0.5);
        hmm.instanceEdge(h, l, 0.5);
        hmm.instanceEdge(l, h, 0.4);
        hmm.instanceEdge(l, l, 0.6);
        hmm.addInitialNode(h, 0.5);
        hmm.addInitialNode(l, 0.5);

        String[] expected = {"H", "H", "H", "L", "L", "L", "L", "L", "L"};
        String[] real = hmm.viterbi(Arrays.asList("G", "G", "C", "A", "C", "T", "G", "A", "A")).toArray(new String[expected.length]);
        assertArrayEquals(expected, real);
    }

    @Test
    public void viterbiTest4() {
        TabulatedProbabilityEmitter<String> emitterHealthy = new TabulatedProbabilityEmitter<>();
        emitterHealthy.addEmission("Dizzy", .1);
        emitterHealthy.addEmission("Cold", .4);
        emitterHealthy.addEmission("Normal", .5);
        TabulatedProbabilityEmitter<String> emitterFever = new TabulatedProbabilityEmitter<>();
        emitterFever.addEmission("Dizzy", .6);
        emitterFever.addEmission("Cold", .3);
        emitterFever.addEmission("Normal", .1);

        Node<String, String> helthy = hmm.instanceNode("Healthy", emitterHealthy);
        Node<String, String> fever = hmm.instanceNode("Fever", emitterFever);

        hmm.instanceEdge(helthy, helthy, 0.7);
        hmm.instanceEdge(helthy, fever, 0.3);
        hmm.instanceEdge(fever, fever, 0.5);
        hmm.instanceEdge(fever, helthy, 0.5);
        hmm.addInitialNode(helthy, 0.6);
        hmm.addInitialNode(fever, 0.4);

        String[] expected = {"Healthy", "Fever", "Fever", "Healthy", "Healthy", "Healthy", "Healthy", "Healthy", "Healthy", "Healthy", "Fever", "Healthy", "Healthy"};
        String[] real = hmm.viterbi(Arrays.asList("Normal", "Dizzy", "Dizzy", "Cold", "Normal", "Normal", "Cold", "Normal", "Normal", "Cold", "Dizzy", "Normal", "Cold")).toArray(new String[expected.length]);

        assertArrayEquals(expected, real);
    }

//    @Test
//    public void transitionProbabilityToNode() {
//        Node<String, String> one = hmm.instanceNode("One", () -> "One");
//        Node<String, String> two = hmm.instanceNode("Two", () -> "Two");
//        Node<String, String> three = hmm.instanceNode("Three", () -> "Three");
//        hmm.instanceEdge(one, two, 0.4);
//        hmm.instanceEdge(one, three, 0.6);
//        assertThat(one.getProbabilityToNode(two), is(0.4));
//
//        assertThat(one.getProbabilityToNode(one), is(0.0));
//    }

//    @Test
//    public void matrixARandomInitializationTest() {
//        Node<String, String> one = hmm.instanceNode("One", () -> "One");
//        Node<String, String> two = hmm.instanceNode("Two", () -> "Two");
//        Node<String, String> three = hmm.instanceNode("Three", () -> "Three");
//        hmm.instanceEdge(one, one, 0.1);
//        hmm.instanceEdge(one, two, 0.4);
//        hmm.instanceEdge(one, three, 0.5);
//        hmm.instanceEdge(two, one, 0.1);
//        hmm.instanceEdge(two, two, 0.4);
//        hmm.instanceEdge(two, three, 0.5);
//        hmm.instanceEdge(three, one, 0.1);
//        hmm.instanceEdge(three, two, 0.4);
//        hmm.instanceEdge(three, three, 0.5);
//
//        hmm.matrixARandomInitialization();
//
//        System.out.println(hmm.matrixA);
//    }

    @Test
    public void estimateMatrixATest() {
        hmm = new HMM<>(Arrays.asList("a", "b", "c"));
        TabulatedProbabilityEmitter<String> emitterOne = new TabulatedProbabilityEmitter<>();
        emitterOne.addEmission("a", 0.1137271);
        emitterOne.addEmission("b", 0.4645561);
        emitterOne.addEmission("c", 0.4217168);

        TabulatedProbabilityEmitter<String> emitterTwo = new TabulatedProbabilityEmitter<>();
        emitterTwo.addEmission("a", 0.2991603);
        emitterTwo.addEmission("b", 0.2860711);
        emitterTwo.addEmission("c", 0.4147685);

        TabulatedProbabilityEmitter<String> emitterThree = new TabulatedProbabilityEmitter<>();
        emitterThree.addEmission("a", 0.5178120);
        emitterThree.addEmission("b", 0.2647543);
        emitterThree.addEmission("c", 0.2174337);

        Node<String, String> one = hmm.instanceNode("One", emitterOne);
        Node<String, String> two = hmm.instanceNode("Two", emitterTwo);
        Node<String, String> three = hmm.instanceNode("Three", emitterThree);

        hmm.instanceEdge(one, one, 0.09660597);
        hmm.instanceEdge(one, two, 0.3806887);
        hmm.instanceEdge(one, three, 0.5227053);
        hmm.instanceEdge(two, one, 0.23569230);
        hmm.instanceEdge(two, two, 0.2162365);
        hmm.instanceEdge(two, three, 0.5480712);
        hmm.instanceEdge(three, one, 0.58003645);
        hmm.instanceEdge(three, two, 0.2084311);
        hmm.instanceEdge(three, three, 0.2115324);

        hmm.addInitialNode(one, 0.1);
        hmm.addInitialNode(two, 0.4);
        hmm.addInitialNode(three, 0.5);

        List<String> observations = Arrays.asList("a", "c", "a", "b", "a", "a", "b", "c", "b", "b", "c", "c");
        hmm.forward(observations);
        hmm.backwardTranssitions(observations);

        hmm.estimateMatrixA(observations);
        System.out.println(hmm.matrixA);

        hmm.estimateEmissions(observations);
        System.out.println(hmm.matrixEmissions);

        System.out.println(hmm);
    }

    @Test
    public void EMTest() {
        hmm = new HMM<>(Arrays.asList("a", "b", "c"));
        TabulatedProbabilityEmitter<String> emitterOne = new TabulatedProbabilityEmitter<>();
        emitterOne.addEmission("a", 0.1137271);
        emitterOne.addEmission("b", 0.4645561);
        emitterOne.addEmission("c", 0.4217168);

        TabulatedProbabilityEmitter<String> emitterTwo = new TabulatedProbabilityEmitter<>();
        emitterTwo.addEmission("a", 0.2991603);
        emitterTwo.addEmission("b", 0.2860711);
        emitterTwo.addEmission("c", 0.4147685);

        TabulatedProbabilityEmitter<String> emitterThree = new TabulatedProbabilityEmitter<>();
        emitterThree.addEmission("a", 0.5178120);
        emitterThree.addEmission("b", 0.2647543);
        emitterThree.addEmission("c", 0.2174337);

        Node<String, String> one = hmm.instanceNode("One", emitterOne);
        Node<String, String> two = hmm.instanceNode("Two", emitterTwo);
        Node<String, String> three = hmm.instanceNode("Three", emitterThree);

        hmm.instanceEdge(one, one, 0.09660597);
        hmm.instanceEdge(one, two, 0.3806887);
        hmm.instanceEdge(one, three, 0.5227053);
        hmm.instanceEdge(two, one, 0.23569230);
        hmm.instanceEdge(two, two, 0.2162365);
        hmm.instanceEdge(two, three, 0.5480712);
        hmm.instanceEdge(three, one, 0.58003645);
        hmm.instanceEdge(three, two, 0.2084311);
        hmm.instanceEdge(three, three, 0.2115324);

        hmm.addInitialNode(one, 0.1);
        hmm.addInitialNode(two, 0.4);
        hmm.addInitialNode(three, 0.5);

//        System.out.println("Emission parameters: " + hmm.numberParametersEmissions());
//        System.out.println("Transition parameters: " + hmm.numberParametersTransitions());
//        System.out.println("Total: " + hmm.numberOfParameters());


        List<String> emissionSet = Arrays.asList("a", "b", "c");
        List<String> observations = Arrays.asList("a", "c", "a", "b", "a", "a", "b", "c", "b", "b", "c", "c");

//        System.out.println(hmm);
        HMM<String, String> hmm2 = hmm.EM(emissionSet, observations, 100);
//        System.out.println(hmm2.forwardScaled(Arrays.asList("a", "a", "b")));
//        System.out.println(hmm2.forwardScaled(Arrays.asList("a", "b", "b")));
//        System.out.println(hmm2.forwardScaled(Arrays.asList("a", "a", "b")));
//        System.out.println(hmm2.forwardScaled(Arrays.asList("b", "a", "b")));

        System.out.println(hmm2);
        System.out.println("Emission parameters: " + hmm2.numberParametersEmissions());
        System.out.println("Transition parameters: " + hmm2.numberParametersTransitions());
        System.out.println("Total: " + hmm2.numberOfParameters());
        System.out.println(hmm2.AIC(observations));
        System.out.println(hmm2.BIC(observations));
    }

    @Test
    public void estimateMatrixATest2() {
        hmm = new HMM<>(Arrays.asList("a", "b", "c"));

        TabulatedProbabilityEmitter<String> emitterOne = new TabulatedProbabilityEmitter<>();
//        emitterOne.addEmission("a", .1);
//        emitterOne.addEmission("b", .4);
//        emitterOne.addEmission("c", .5);

        emitterOne.addEmission("a", 0.1137271);
        emitterOne.addEmission("b", 0.4645561);
        emitterOne.addEmission("c", 0.4217168);

        TabulatedProbabilityEmitter<String> emitterTwo = new TabulatedProbabilityEmitter<>();
//        emitterTwo.addEmission("a", .2);
//        emitterTwo.addEmission("b", .3);
//        emitterTwo.addEmission("c", .5);

        emitterTwo.addEmission("a", 0.2991603);
        emitterTwo.addEmission("b", 0.2860711);
        emitterTwo.addEmission("c", 0.4147685);

        TabulatedProbabilityEmitter<String> emitterThree = new TabulatedProbabilityEmitter<>();
//        emitterThree.addEmission("a", .4);
//        emitterThree.addEmission("b", .3);
//        emitterThree.addEmission("c", .3);

        emitterThree.addEmission("a", 0.5178120);
        emitterThree.addEmission("b", 0.2647543);
        emitterThree.addEmission("c", 0.2174337);

        Node<String, String> one = hmm.instanceNode("One", emitterOne);
        Node<String, String> two = hmm.instanceNode("Two", emitterTwo);
        Node<String, String> three = hmm.instanceNode("Three", emitterThree);
//        hmm.instanceEdge(one, one, 0.1);
//        hmm.instanceEdge(one, two, 0.4);
//        hmm.instanceEdge(one, three, 0.5);
//        hmm.instanceEdge(two, one, 0.2);
//        hmm.instanceEdge(two, two, 0.3);
//        hmm.instanceEdge(two, three, 0.5);
//        hmm.instanceEdge(three, one, 0.6);
//        hmm.instanceEdge(three, two, 0.2);
//        hmm.instanceEdge(three, three, 0.2);

        hmm.instanceEdge(one, one, 0.09660597);
        hmm.instanceEdge(one, two, 0.3806887);
        hmm.instanceEdge(one, three, 0.5227053);
        hmm.instanceEdge(two, one, 0.23569230);
        hmm.instanceEdge(two, two, 0.2162365);
        hmm.instanceEdge(two, three, 0.5480712);
        hmm.instanceEdge(three, one, 0.58003645);
        hmm.instanceEdge(three, two, 0.2084311);
        hmm.instanceEdge(three, three, 0.2115324);

        hmm.addInitialNode(one, 0.1);
        hmm.addInitialNode(two, 0.4);
        hmm.addInitialNode(three, 0.5);

//        hmm.matrixARandomInitialization();
//        System.out.println(hmm.matrixA);

        List<String> observations = Arrays.asList("a", "c", "a", "b", "a", "a", "b", "c", "b", "b", "c", "c");
        hmm.forward(observations);
        hmm.backwardTranssitions(observations);

        hmm.estimateMatrixA(observations);
        System.out.println(hmm.matrixA);

        hmm.estimateEmissions(observations);
        System.out.println(hmm.matrixEmissions);

        System.out.println(hmm);
    }

    @Test
    void sequenceWithMaxProbabilityTest() {
        hmm = new HMM<>(Arrays.asList("a", "b", "c"));
        TabulatedProbabilityEmitter<String> emitterOne = new TabulatedProbabilityEmitter<>();
        emitterOne.addEmission("a", 0.1137271);
        emitterOne.addEmission("b", 0.4645561);
        emitterOne.addEmission("c", 0.4217168);

        TabulatedProbabilityEmitter<String> emitterTwo = new TabulatedProbabilityEmitter<>();
        emitterTwo.addEmission("a", 0.2991603);
        emitterTwo.addEmission("b", 0.2860711);
        emitterTwo.addEmission("c", 0.4147685);

        TabulatedProbabilityEmitter<String> emitterThree = new TabulatedProbabilityEmitter<>();
        emitterThree.addEmission("a", 0.5178120);
        emitterThree.addEmission("b", 0.2647543);
        emitterThree.addEmission("c", 0.2174337);

        Node<String, String> one = hmm.instanceNode("One", emitterOne);
        Node<String, String> two = hmm.instanceNode("Two", emitterTwo);
        Node<String, String> three = hmm.instanceNode("Three", emitterThree);

        hmm.instanceEdge(one, one, 0.09660597);
        hmm.instanceEdge(one, two, 0.3806887);
        hmm.instanceEdge(one, three, 0.5227053);
        hmm.instanceEdge(two, one, 0.23569230);
        hmm.instanceEdge(two, two, 0.2162365);
        hmm.instanceEdge(two, three, 0.5480712);
        hmm.instanceEdge(three, one, 0.58003645);
        hmm.instanceEdge(three, two, 0.2084311);
        hmm.instanceEdge(three, three, 0.2115324);

        hmm.addInitialNode(one, 0.1);
        hmm.addInitialNode(two, 0.4);
        hmm.addInitialNode(three, 0.5);

        List<String> emissionSet = Arrays.asList("a", "b", "c");
        List<String> observations = Arrays.asList("a", "c", "a", "b", "a", "a", "b", "c", "b", "b", "c", "c");

//        System.out.println(hmm);
        HMM<String, String> hmm2 = hmm.EM(emissionSet, observations, 10);
        System.out.println(hmm2);

        List<String> sequence = hmm2.nodeMax.maxSymbols;
        System.out.println(hmm2.nodeMax.maxSymbols);
        System.out.println(hmm2.forward(sequence));
    }

    @Test
    void initializationForwardMaxTest() {
        hmm = new HMM<>(Arrays.asList("a", "b", "c"));
        TabulatedProbabilityEmitter<String> emitterOne = new TabulatedProbabilityEmitter<>();
        emitterOne.addEmission("a", 0.1137271);
        emitterOne.addEmission("b", 0.4645561);
        emitterOne.addEmission("c", 0.4217168);

        TabulatedProbabilityEmitter<String> emitterTwo = new TabulatedProbabilityEmitter<>();
        emitterTwo.addEmission("a", 0.2991603);
        emitterTwo.addEmission("b", 0.2860711);
        emitterTwo.addEmission("c", 0.4147685);

        TabulatedProbabilityEmitter<String> emitterThree = new TabulatedProbabilityEmitter<>();
        emitterThree.addEmission("a", 0.5178120);
        emitterThree.addEmission("b", 0.2647543);
        emitterThree.addEmission("c", 0.2174337);

        Node<String, String> one = hmm.instanceNode("One", emitterOne);
        Node<String, String> two = hmm.instanceNode("Two", emitterTwo);
        Node<String, String> three = hmm.instanceNode("Three", emitterThree);

        hmm.instanceEdge(one, one, 0.09660597);
        hmm.instanceEdge(one, two, 0.3806887);
        hmm.instanceEdge(one, three, 0.5227053);
        hmm.instanceEdge(two, one, 0.23569230);
        hmm.instanceEdge(two, two, 0.2162365);
        hmm.instanceEdge(two, three, 0.5480712);
        hmm.instanceEdge(three, one, 0.58003645);
        hmm.instanceEdge(three, two, 0.2084311);
        hmm.instanceEdge(three, three, 0.2115324);

        hmm.addInitialNode(one, 0.1);
        hmm.addInitialNode(two, 0.4);
        hmm.addInitialNode(three, 0.5);

        List<String> emissionSet = Arrays.asList("a", "b", "c");
        List<String> observations = Arrays.asList("a", "c", "a", "b", "a", "a", "b", "c", "b", "b", "c", "c");

//        System.out.println(hmm);
        HMM<String, String> hmm2 = hmm.EM(emissionSet, observations, 10);
        System.out.println(hmm2);

//        System.out.println(hmm2.sequenceWithMaxProbability(5));
//        List<String> sequence = hmm2.nodeMax.maxSymbols;
//        System.out.println(hmm2.nodeMax.maxSymbols);
//        System.out.println(hmm2.forward(sequence));
    }

    @Test
    void initializationForwardMaxTest2() {
        hmm = new HMM<>(Arrays.asList("a", "b"));
        TabulatedProbabilityEmitter<String> emitterOne = new TabulatedProbabilityEmitter<>();
        emitterOne.addEmission("a", 0.6);
        emitterOne.addEmission("b", 0.4);

        TabulatedProbabilityEmitter<String> emitterTwo = new TabulatedProbabilityEmitter<>();
        emitterTwo.addEmission("a", 0.4);
        emitterTwo.addEmission("b", 0.6);

        Node<String, String> one = hmm.instanceNode("One", emitterOne);
        Node<String, String> two = hmm.instanceNode("Two", emitterTwo);

        hmm.instanceEdge(one, one, 0.7);
        hmm.instanceEdge(one, two, 0.3);
        hmm.instanceEdge(two, one, 0.2);
        hmm.instanceEdge(two, two, 0.8);

        hmm.addInitialNode(one, 0.6);
        hmm.addInitialNode(two, 0.4);
        hmm = hmm.EM(Arrays.asList("a", "b"), Arrays.asList("a", "b", "b", "a", "a", "a", "b", "a", "a", "b", "b", "b", "b", "a", "b"), 5);
        System.out.println(hmm);
//        hmm.initializationForwardScaled("a");

//        System.out.println("a --> " + hmm.forward(Arrays.asList("a")));
//        System.out.println("b --> " + hmm.forward(Arrays.asList("b")));
//
//        System.out.println("a a -->" + hmm.forward(Arrays.asList("a", "a")));
//        System.out.println("a b -->" + hmm.forward(Arrays.asList("a", "b")));
//        System.out.println("b a -->" + hmm.forward(Arrays.asList("b", "a")));
//        System.out.println("b b -->" + hmm.forward(Arrays.asList("b", "b")));

        System.out.println("a a a -->" + hmm.forwardScaled(Arrays.asList("a", "a", "a")));
        System.out.println("a a b -->" + hmm.forwardScaled(Arrays.asList("a", "a", "b")));
        System.out.println("a b a -->" + hmm.forwardScaled(Arrays.asList("a", "b", "a")));
        System.out.println("a b b -->" + hmm.forwardScaled(Arrays.asList("a", "b", "b")));
        System.out.println("b a a -->" + hmm.forwardScaled(Arrays.asList("b", "a", "a")));
        System.out.println("b a b -->" + hmm.forwardScaled(Arrays.asList("b", "a", "b")));
        System.out.println("b b a -->" + hmm.forwardScaled(Arrays.asList("b", "b", "a")));
        System.out.println("b b b -->" + hmm.forwardScaled(Arrays.asList("b", "b", "b")));
//
//        System.out.println("a a a a -->" + hmm.forward(Arrays.asList("a", "a", "a", "a")));
//        System.out.println("a a a b -->" + hmm.forward(Arrays.asList("a", "a", "a", "b")));
//        System.out.println("a a b a -->" + hmm.forward(Arrays.asList("a", "a", "b", "a")));
//        System.out.println("a a b b -->" + hmm.forward(Arrays.asList("a", "a", "b", "b")));
//        System.out.println("a b a a -->" + hmm.forward(Arrays.asList("a", "b", "a", "a")));
//        System.out.println("a b a b -->" + hmm.forward(Arrays.asList("a", "b", "a", "b")));
//        System.out.println("a b b a -->" + hmm.forward(Arrays.asList("a", "b", "b", "a")));
//        System.out.println("a b b b -->" + hmm.forward(Arrays.asList("a", "b", "b", "b")));
//        System.out.println("b a a a -->" + hmm.forward(Arrays.asList("b", "a", "a", "a")));
//        System.out.println("b a a b -->" + hmm.forward(Arrays.asList("b", "a", "a", "b")));
//        System.out.println("b a b a -->" + hmm.forward(Arrays.asList("b", "a", "b", "a")));
//        System.out.println("b a b b -->" + hmm.forward(Arrays.asList("b", "a", "b", "b")));
//        System.out.println("b b a a -->" + hmm.forward(Arrays.asList("b", "b", "a", "a")));
//        System.out.println("b b a b -->" + hmm.forward(Arrays.asList("b", "b", "a", "b")));
//        System.out.println("b b b a -->" + hmm.forward(Arrays.asList("b", "b", "b", "a")));
//        System.out.println("b b b b -->" + hmm.forward(Arrays.asList("b", "b", "b", "b")));

//        System.out.println("a a a a a -->" + hmm.forward(Arrays.asList("a", "a", "a", "a", "a")));
//        System.out.println("a a a a b -->" + hmm.forward(Arrays.asList("a", "a", "a", "a", "b")));
//        System.out.println("a a a b a -->" + hmm.forward(Arrays.asList("a", "a", "a", "b", "a")));
//        System.out.println("a a a b b -->" + hmm.forward(Arrays.asList("a", "a", "a", "b", "b")));
//        System.out.println("a a b a a -->" + hmm.forward(Arrays.asList("a", "a", "b", "a", "a")));
//        System.out.println("a a b a b -->" + hmm.forward(Arrays.asList("a", "a", "b", "a", "b")));
//        System.out.println("a a b b a -->" + hmm.forward(Arrays.asList("a", "a", "b", "b", "a")));
//        System.out.println("a a b b b -->" + hmm.forward(Arrays.asList("a", "a", "b", "b", "b")));
//        System.out.println("a b a a a -->" + hmm.forward(Arrays.asList("a", "b", "a", "a", "a")));
//        System.out.println("a b a a b -->" + hmm.forward(Arrays.asList("a", "b", "a", "a", "b")));
//        System.out.println("a b a b a -->" + hmm.forward(Arrays.asList("a", "b", "a", "b", "a")));
//        System.out.println("a b a b b -->" + hmm.forward(Arrays.asList("a", "b", "a", "b", "b")));
//        System.out.println("a b b a a -->" + hmm.forward(Arrays.asList("a", "b", "b", "a", "a")));
//        System.out.println("a b b a b -->" + hmm.forward(Arrays.asList("a", "b", "b", "a", "b")));
//        System.out.println("a b b b a -->" + hmm.forward(Arrays.asList("a", "b", "b", "b", "a")));
//        System.out.println("a b b b b -->" + hmm.forward(Arrays.asList("a", "b", "b", "b", "b")));
//        System.out.println("b a a a a -->" + hmm.forward(Arrays.asList("b", "a", "a", "a", "a")));
//        System.out.println("b a a a b -->" + hmm.forward(Arrays.asList("b", "a", "a", "a", "b")));
//        System.out.println("b a a b a -->" + hmm.forward(Arrays.asList("b", "a", "a", "b", "a")));
//        System.out.println("b a a b b -->" + hmm.forward(Arrays.asList("b", "a", "a", "b", "b")));
//        System.out.println("b a b a a -->" + hmm.forward(Arrays.asList("b", "a", "b", "a", "a")));
//        System.out.println("b a b a b -->" + hmm.forward(Arrays.asList("b", "a", "b", "a", "b")));
//        System.out.println("b a b b a -->" + hmm.forward(Arrays.asList("b", "a", "b", "b", "a")));
//        System.out.println("b a b b b -->" + hmm.forward(Arrays.asList("b", "a", "b", "b", "b")));
//        System.out.println("b b a a a -->" + hmm.forward(Arrays.asList("b", "b", "a", "a", "a")));
//        System.out.println("b b a a b -->" + hmm.forward(Arrays.asList("b", "b", "a", "a", "b")));
//        System.out.println("b b a b a -->" + hmm.forward(Arrays.asList("b", "b", "a", "b", "a")));
//        System.out.println("b b a b b -->" + hmm.forward(Arrays.asList("b", "b", "a", "b", "b")));
//        System.out.println("b b b a a -->" + hmm.forward(Arrays.asList("b", "b", "b", "a", "a")));
//        System.out.println("b b b a b -->" + hmm.forward(Arrays.asList("b", "b", "b", "a", "b")));
//        System.out.println("b b b b a -->" + hmm.forward(Arrays.asList("b", "b", "b", "b", "a")));
//        System.out.println("b b b b b -->" + hmm.forward(Arrays.asList("b", "b", "b", "b", "b")));

//        System.out.println("a a a a a a -->" + hmm.forward(Arrays.asList("a", "a", "a", "a", "a", "a")));
//        System.out.println("a a a a a b -->" + hmm.forward(Arrays.asList("a", "a", "a", "a", "a", "b")));
//        System.out.println("b b a a a a -->" + hmm.forward(Arrays.asList("b", "b", "a", "a", "a", "a")));
//        System.out.println("b b a a a b -->" + hmm.forward(Arrays.asList("b", "b", "a", "a", "a", "b")));
//
//        System.out.println("a a a a a a -->" + hmm.forward(Arrays.asList("a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a")));
//        System.out.println("a a a a a b -->" + hmm.forward(Arrays.asList("a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "b")));

    }

    @Test
    void generateTest() {
        String hmmFileName = "arturo_wap01_banyo.bin";
        try {
            Environment environment = Environment.readEnvironmentFromFile(hmmFileName);
            HMM hmm = environment.getHMMWAPLocation("WAP_0", "Baño");
//            System.out.println(hmm);
            System.out.println(hmm.generateSequence(800));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    void generate2Test() {
        String hmmFileName = "one_wap_5_iterations.bin";
        try {
            Environment environment = Environment.readEnvironmentFromFile(hmmFileName);
            HMM hmm = environment.getHMMWAPLocation("WAP_5", "label");
            System.out.println(hmm.generateSequence(1000));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void KLdivergenceTest() {
        List<Integer> sim = Arrays.asList(-59, -57, -58, -59, -61, -57, -57, -59, -57, -59, -59, -58, -58, -56, -59, -61, -58, -55, -63, -59, -57, -59, -59, -58, -57, -59, -57, -57, -59, -60, -56, -56, -61, -57, -58, -60, -60, -59, -61, -59, -56, -57, -59, -59, -59, -63, -59, -57, -59, -59, -57, -59, -61, -59, -57, -58, -57, -58, -58, -56, -57, -56, -57, -57, -60, -57, -59, -61, -58, -57, -61, -57, -59, -57, -57, -57, -58, -60, -60, -59, -59, -59, -57, -57, -60, -59, -57, -57, -60, -57, -57, -57, -60, -59, -56, -52, -57, -58, -59, -57, -59, -59, -57, -59, -57, -59, -57, -57, -57, -59, -57, -57, -58, -58, -58, -56, -58, -56, -59, -59, -57, -58, -57, -55, -57, -57, -57, -56, -57, -57, -60, -57, -57, -52, -61, -58, -60, -56, -59, -60, -57, -57, -59, -57, -55, -59, -59, -60, -58, -61, -57, -56, -58, -59, -59, -57, -62, -57, -58, -55, -57, -59, -55, -59, -61, -59, -58, -59, -58, -57, -60, -58, -57, -59, -60, -58, -57, -60, -59, -56, -58, -59, -56, -59, -57, -59, -57, -56, -60, -58, -58, -59, -58, -59, -57, -58, -58, -59, -56, -60, -56, -59, -58, -58, -59, -58, -58, -60, -58, -57, -60, -57, -59, -59, -56, -61, -57, -57, -57, -57, -59, -59, -59, -58, -57, -57, -58, -57, -60, -61, -60, -62, -54, -58, -58, -58, -59, -59, -57, -57, -56, -57, -56, -58, -58,
                -59, -56, -58, -59, -55, -59, -60, -62, -56, -58, -59, -57, -57, -57, -57, -62, -56, -61, -57, -57, -58, -59, -61, -60, -60, -57, -58, -59, -55, -59, -57, -57, -60, -57, -58, -57, -58, -59, -60, -57, -58, -57, -59, -60, -58, -57, -58, -60, -57, -59, -57, -57, -57, -58, -56, -59, -59, -59, -57, -57, -59, -58, -56, -58, -59, -58, -58, -58, -57, -59, -57, -59, -58, -59, -58, -57, -59, -59, -59, -57, -57, -56, -58, -59, -56, -62, -56, -61, -56, -59, -60, -58, -58, -57, -59, -59, -57, -61, -59, -56, -58, -57, -58, -56, -58, -58, -58, -59, -57, -59, -57, -57, -58, -58, -62, -59, -59, -59, -56, -59, -63, -59, -60, -57, -56, -59, -57, -59, -58, -61, -59, -60, -56, -56, -60, -59, -56, -56, -57, -58, -61, -59, -56, -58, -57, -57, -56, -59, -59, -59, -56, -57, -59, -56, -60, -57, -57, -57, -58, -58, -60, -55, -60, -58, -58, -59, -56, -59, -56, -59, -60, -59, -59, -59, -60, -57, -56, -58, -59, -56, -58, -57, -58, -57, -58, -60, -56, -55, -56, -59, -56, -61, -58, -59, -56, -59, -60, -59, -59, -59, -57, -58, -57, -59, -59, -59, -57, -59, -56, -57, -58, -58, -56, -58, -59, -58, -57, -56, -58, -61, -60, -57, -58, -57, -57, -55, -60, -57, -60, -61, -56, -58, -58, -57, -58, -57, -59, -58, -57, -59, -59, -56, -59, -57, -59,
                -59, -57, -58, -56, -63, -58, -60, -61, -57, -59, -57, -57, -60, -57, -61, -60, -59, -58, -57, -58, -56, -61, -59, -57, -57, -58, -57, -59, -58, -57, -58, -59, -57, -60, -59, -54, -57, -56, -59, -60, -56, -57, -58, -60, -59, -59, -57, -60, -60, -58, -57, -57, -58, -59, -57, -59, -58, -60, -59, -61, -57, -58, -60, -60, -59, -61, -59, -62, -59, -61, -58, -55, -56, -56, -59, -59, -58, -59, -57, -58, -57, -59, -57, -59, -57, -56, -59, -57, -57, -60, -58, -58, -58, -57, -57, -61, -60, -58, -57, -57, -58, -57, -60, -61, -60, -57, -57, -60, -59, -57, -57, -58, -60, -57, -58, -57, -59, -60, -57, -59, -60, -57, -56, -59, -57, -56, -58, -58, -58, -55, -59, -57, -59, -57, -56, -59, -57, -59, -59, -59, -58, -59, -61, -59, -57, -59, -57, -58, -57, -60, -58, -59, -55, -61, -59, -56, -59, -56, -59, -57, -59, -59, -59, -58, -59, -58, -58, -57, -62, -59, -58, -57, -58, -59, -59, -56, -56, -57, -58, -57, -56, -58, -58, -59, -60, -56, -57, -57, -57, -59, -58, -58, -55, -59, -59, -57, -56, -57, -57, -57, -58, -58, -58, -57, -57, -59, -58, -59, -59, -58, -57, -57, -57, -61, -58, -57, -61, -57, -57, -61, -61, -58, -57, -59, -57, -55, -59, -58, -56, -56, -58, -57, -57, -59, -57, -59, -57, -55, -57, -62, -57, -59, -60, -56, -58,
                -59, -58, -59, -59, -59, -58, -60, -57, -57, -59, -58, -58, -59, -59, -59, -55, -58, -56, -58, -60, -59, -58, -52, -58, -59, -61, -59, -63, -63, -57, -59, -56, -58, -58, -59, -59, -58, -59, -57, -58, -58, -61, -57, -59, -63, -59, -59, -59, -59, -57, -58, -59, -58, -59, -57, -57, -59, -59, -55, -60, -58, -59, -60, -60, -57, -60, -58, -60, -60, -56, -59, -57, -59, -59, -59, -56, -58, -58, -57, -57, -58, -57, -59, -59, -55, -59, -60, -55, -56, -60, -60, -62, -58, -59, -60, -57, -58, -58, -61, -60, -58, -58, -60, -59, -55, -57, -56, -56, -60, -57, -59, -58, -57, -60, -60, -55, -59, -59, -63, -59, -57, -58, -59, -55, -57, -54, -60, -59, -59, -56, -59, -58, -60, -59, -57, -59, -61, -58, -57, -57, -59, -57, -59, -59, -56, -55, -58, -57, -56, -59, -59, -58, -58, -59, -58, -60, -58, -60, -59, -56, -58, -59, -59, -57, -57, -57, -59, -57, -62, -58, -62, -58, -59, -57, -60, -56, -59, -59, -57, -57, -57, -57, -60, -57, -57, -57, -57, -57, -57, -62, -56, -59, -59, -58, -58, -60, -57, -58, -59, -58, -57, -56, -56, -57, -59, -59, -59, -57, -59, -60, -58, -55, -59, -56, -57, -60, -60, -58, -60, -56, -59, -60, -58, -57, -58, -58, -57, -56, -57, -59, -59, -60, -56, -59, -59, -58, -59, -58, -59, -61, -57, -60, -57, -57, -58,
                -57, -58, -58, -59, -58, -58, -58, -59, -58, -60, -57, -59, -61, -59, -59, -60, -57, -57, -59, -60);
        String hmmFileName = "one_wap_5_iterations.bin";
        try {
            Environment environment = Environment.readEnvironmentFromFile(hmmFileName);
            HMM hmm = environment.getHMMWAPLocation("WAP_5", "label");
            TabulatedCSVProbabilityEmitter<Integer> sim1 = new TabulatedCSVProbabilityEmitter<>(REAL);
            TabulatedCSVProbabilityEmitter<Integer> sim2 = new TabulatedCSVProbabilityEmitter<>(sim);
            KLDivergence kl = new KLDivergence(sim1, sim2);
//            System.out.println(kl.divergence());
            assertEquals(0.006880213, kl.divergence(), 0.00001);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void KLdivergence2Test() {
        String hmmFileName = "one_wap_50_iterations.bin";
        try {
            KLDivergence kl = null;
            double divergence, crossEntropy;
            StandardDeviation sd = new StandardDeviation();
            StandardDeviation sdCroosEntropy = new StandardDeviation();
            Mean mCrossEntroy = new Mean();
            Mean m = new Mean();
            Environment environment = Environment.readEnvironmentFromFile(hmmFileName);
            HMM hmm = environment.getHMMWAPLocation("WAP_5", "label");
            TabulatedCSVProbabilityEmitter<Integer> real = new TabulatedCSVProbabilityEmitter<>(REAL);
            for(int i = 0; i < 10000; i++) {
                TabulatedCSVProbabilityEmitter<Integer> sim = new TabulatedCSVProbabilityEmitter<>(hmm.generateSequence(999));
                kl = new KLDivergence(real, sim);
                divergence = kl.divergence();
                m.increment(divergence);
                sd.increment(divergence);
                crossEntropy = -kl.crossEntropy();
                mCrossEntroy.increment(crossEntropy);
                sdCroosEntropy.increment(crossEntropy);
            }

            if(kl != null) {
                System.out.println("Entropy: " + -kl.entropyP());
                System.out.println("Cross entropy: " + mCrossEntroy.getResult());
                System.out.println("Sd cross entropy: " + sdCroosEntropy.getResult());
            }
            System.out.println("Mean: " + m.getResult());
            System.out.println("Sd: " + sd.getResult()/Math.sqrt(sd.getN()));
            System.out.println(m.getResult()*100.0/-kl.entropyP());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
