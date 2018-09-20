package es.uji.belfern.hmm;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class HMMTest {
    private HMM<String, String> hmm;

    @BeforeEach
    public void start() {
        hmm = new HMM<>();
    }

    @Test
    public void nodeWithNullEmitterExceptionTest() {
        try {
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

    @Test
    public void instanceNodeNotNullTest() {
        Node<String, String> node = hmm.instanceNode("First", () -> "One");
        assertThat(node, notNullValue());
    }

    @Test
    public void instanceEdgeNotNullTest() {
        Node<String, String> start = hmm.instanceNode("start", () -> "Start");
        Node<String, String> end = hmm.instanceNode("end", () -> "End");
        Edge<String, String> edge = hmm.instanceEdge(start, end,1);
        assertThat(edge, notNullValue());
    }

    @Test
    public void nextTest() {
        Node<String, String> one = hmm.instanceNode("One", () -> "One");
        Node<String, String> two = hmm.instanceNode("Two", () -> "Two");
        Node<String, String> three = hmm.instanceNode("Three", () -> "Three");
        hmm.instanceEdge(one, two, 0.4);
        hmm.instanceEdge(one, three, 0.6);
        assertThat(one.nextNode(0.5), is(three));
    }

    @Test
    public void nextWithIdsTest() {
        Node<String, String> one = hmm.instanceNode("One", () -> "One");
        hmm.instanceNode("Two", () -> "Two");
        hmm.instanceNode("Three", () -> "Three");
        hmm.instanceEdge("One", "Two", 0.4);
        hmm.instanceEdge("One", "Three", 0.6);
        assertThat(one.nextNode(0.05), is(hmm.nodes.get("Two")));
    }

    @Test
    public void accumulateProbabilitiesTest() {
        Node<String, String> one = hmm.instanceNode("One", () -> "One");
        Node<String, String> two = hmm.instanceNode("Two", () -> "Two");
        Node<String, String> three = hmm.instanceNode("Three", () -> "Three");
        Node<String, String> four = hmm.instanceNode("Four", () -> "Four");
        hmm.instanceEdge("One", "One", 0.1);
        hmm.instanceEdge("One", "Two", 0.2);
        hmm.instanceEdge("One", "Three", 0.3);
        hmm.instanceEdge("One", "Four", 0.4);

        Node<String, String> res = one.nextNode(0.05);
        assertThat(res, is(one));

        res = one.nextNode(0.15);
        assertThat(res, is(two));

        res = one.nextNode(0.35);
        assertThat(res, is(three));

        res = one.nextNode(0.95);
        assertThat(res, is(four));
    }

    @Test
    public void generateSequenceTest() {
        List<String> expected = Arrays.asList("One", "Two", "One", "Two", "One", "Two", "One", "Two", "One", "Two");
        Node<String, String> one = hmm.instanceNode("One", () -> "One");
        Node<String, String> two = hmm.instanceNode("Two", () -> "Two");
        hmm.instanceEdge(one, two, 1.0);
        hmm.instanceEdge(two, one, 1.0);
        hmm.addInitialNode(one, 1);
        assertThat(hmm.generateSequence(10), is(expected));
    }

    @Test
    public void generateSequenceTest2() {
        List<String> expected = Arrays.asList("One", "Two", "One", "Two", "One", "Two", "One", "Two", "One", "Two");
        Node<String, String> one = hmm.instanceNode("One", () -> "One");
        Node<String, String> two = hmm.instanceNode("Two", () -> "Two");
        hmm.instanceEdge(one, two, 1.0);
        hmm.instanceEdge(two, one, 1.0);
        hmm.addInitialNode(one, 1);
        assertThat(hmm.generateSequence(10), is(expected));
    }

    @Test
    public void sequenceTest() {
        double one_zero = 0.4;
        double one_one = 1 - one_zero;
        double zero_one = 0.8;
        double zero_zero = 1- zero_one;
        double expectedRatio = zero_one/(one_zero + zero_one);

        HMM<String, Integer> hmmInt = new HMM<>();
        Node<String, Integer> one = hmmInt.instanceNode("one", () -> 1);
        hmmInt.addInitialNode(one, 1);
        Node<String, Integer> zero = hmmInt.instanceNode("zero", () -> 0);
        hmmInt.instanceEdge(one, zero, one_zero);
        hmmInt.instanceEdge(one, one, one_one);
        hmmInt.instanceEdge(zero, one, zero_one);
        hmmInt.instanceEdge(zero, zero, zero_zero);

        List<Integer> sequence = hmmInt.generateSequence(1000000);

        double ratio = sequence.stream()
                .mapToInt(s -> s)
                .average()
                .getAsDouble();

        assertEquals(expectedRatio, ratio, 0.001);
    }

    @Test
    public void sequenceTabulatedTest() {
        double one_zero = 0.6;
        double one_one = 1 - one_zero;
        double zero_one = 0.8;
        double zero_zero = 1- zero_one;
        double expectedRatio = zero_one/(one_zero + zero_one);

        HMM<String, Integer> hmmInt = new HMM<>();
        TabulatedProbabilityEmitter<Integer> emitterOne = new TabulatedProbabilityEmitter<>();
        emitterOne.addEmission(1,1);
        TabulatedProbabilityEmitter<Integer> emitterZero = new TabulatedProbabilityEmitter<>();
        emitterZero.addEmission(0, 1);
        Node<String, Integer> one = hmmInt.instanceNode("one", emitterOne);
        hmmInt.addInitialNode(one, 1);
        Node<String, Integer> zero = hmmInt.instanceNode("zero", emitterZero);
        hmmInt.instanceEdge(one, zero, one_zero);
        hmmInt.instanceEdge(one, one, one_one);
        hmmInt.instanceEdge(zero, one, zero_one);
        hmmInt.instanceEdge(zero, zero, zero_zero);

        List<Integer> sequence = hmmInt.generateSequence(1000000);

        double ratio = sequence.stream()
                .mapToInt(s -> s)
                .average()
                .getAsDouble();

        assertEquals(expectedRatio, ratio, 0.001);
    }

    @Test
    public void sequenceTabulatedTest2() {
        double one_zero = 0.6;
        double one_one = 1 - one_zero;
        double zero_one = 0.8;
        double zero_zero = 1- zero_one;
        double expectedRatio = zero_one/(one_zero + zero_one);

        HMM<String, Integer> hmmInt = new HMM<>();
        TabulatedProbabilityEmitter<Integer> emitterOne = new TabulatedProbabilityEmitter<>();
        emitterOne.addEmission(1,1);
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

    @Test
    public void initializationTest() {
        Node<String, String> one = hmm.instanceNode("One", () -> "One");
        Node<String, String> two = hmm.instanceNode("Two", () -> "Two");
        hmm.instanceEdge(one, two, 1.0);
        hmm.instanceEdge(two, one, 1.0);
        hmm.addInitialNode(one, 1);

        hmm.initializationForward("One");

        assertThat(one, is(one));
        assertEquals(one, one);

        double result = hmm.nodes.values().stream()
                .filter(one::equals)
//                .filter(node -> one.equals(node))
                .mapToDouble(node -> node.alfa)
                .findFirst()
                .getAsDouble();

        assertEquals(result, 1.0, 0.1);

        result = hmm.nodes.values().stream()
                .filter(two::equals)
                .mapToDouble(node -> node.alfa)
                .findFirst()
                .getAsDouble();

        assertEquals(result, 0.0, 0.1);
    }

    @Test
    public void recursionTest() {
//        HMM<String, Integer> hmmInt = new HMM<>();

        TabulatedProbabilityEmitter<String> emitterOne = new TabulatedProbabilityEmitter<>();
        emitterOne.addEmission("One",1);
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
        emitterOne.addEmission("One",1);
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
        double probability = hmm.terminationForward();

        assertEquals(1.0, probability, 0.0001);

        hmm.initializationForward("One");
        hmm.recursionForward(Arrays.asList("One", "Two", "One", "Two", "One", "One"));
        probability = hmm.terminationForward();

        assertEquals(0.0, probability, 0.01);
    }

    @Test
    public void forwardTest() {
        TabulatedProbabilityEmitter<String> emitterOne = new TabulatedProbabilityEmitter<>();
        emitterOne.addEmission("One",.4);
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

        hmm.forward(Arrays.asList("One","Two","One","Two","One","Two","One","One","One","Two","Two"));

        double[] expectedOne = {0.28, 0.1236, 0.054688, 0.03622656, 0.01164820, 0.010092159, 0.002671085, 0.001828432, 0.0005736483, 0.0005076615, 0.0001985950};
        assertArrayEquals(expectedOne, ArrayUtils.toPrimitive(one.getAlfas().toArray(new Double[0])), 0.001);

        double[] expectedTwo = {0.15, 0.1120, 0.049440, 0.02187520, 0.01449062, 0.004659282, 0.004036864, 0.001068434, 0.0007313729, 0.0002294593, 0.0002030646};
        assertArrayEquals(expectedTwo, ArrayUtils.toPrimitive(two.getAlfas().toArray(new Double[0])), 0.001);

        String[] expected = {"One", "Two", "One", "Two", "One", "Two", "One", "Two", "One", "Two"};
        assertArrayEquals(expected, hmm.viterbi(Arrays.asList("One", "Two", "One", "Two", "Two","One","One","One","One","One")).toArray(new String[expected.length]));
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

        hmm.backward(Arrays.asList("a","a","b"));

        double[] expectedX = {0.202, 0.66, 1.0};
        assertArrayEquals(expectedX, ArrayUtils.toPrimitive(X.getBetas().toArray(new Double[0])), 0.001);

        double[] expectedY = {0.234, 0.34, 1.0};
        assertArrayEquals(expectedY, ArrayUtils.toPrimitive(Y.getBetas().toArray(new Double[0])), 0.001);
    }

    @Test
    public void viterbiTest() {
        TabulatedProbabilityEmitter<String> emitterOne = new TabulatedProbabilityEmitter<>();
        emitterOne.addEmission("One",.4);
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
        emitterOne.addEmission("One",1);
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
        emitterH.addEmission("A",.2);
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
        String[] real = hmm.viterbi(Arrays.asList("G","G","C","A","C","T","G","A","A")).toArray(new String[expected.length]);
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

    @Test
    public void transitionProbabilityToNode() {
        Node<String, String> one = hmm.instanceNode("One", () -> "One");
        Node<String, String> two = hmm.instanceNode("Two", () -> "Two");
        Node<String, String> three = hmm.instanceNode("Three", () -> "Three");
        hmm.instanceEdge(one, two, 0.4);
        hmm.instanceEdge(one, three, 0.6);
        assertThat(one.getProbabilityToNode(two), is(0.4));

        assertThat(one.getProbabilityToNode(one), is(0.0));
    }

    @Test
    public void matrixARandomInitializationTest() {
        Node<String, String> one = hmm.instanceNode("One", () -> "One");
        Node<String, String> two = hmm.instanceNode("Two", () -> "Two");
        Node<String, String> three = hmm.instanceNode("Three", () -> "Three");
        hmm.instanceEdge(one, one, 0.1);
        hmm.instanceEdge(one, two, 0.4);
        hmm.instanceEdge(one, three, 0.5);
        hmm.instanceEdge(two, one, 0.1);
        hmm.instanceEdge(two, two, 0.4);
        hmm.instanceEdge(two, three, 0.5);
        hmm.instanceEdge(three, one, 0.1);
        hmm.instanceEdge(three, two, 0.4);
        hmm.instanceEdge(three, three, 0.5);

        hmm.matrixARandomInitialization();

        System.out.println(hmm.matrixA);
    }

    @Test
    public void estimateMatrixATest() {
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
        hmm.backward(observations);

        hmm.estimateMatrixA(observations);
        System.out.println(hmm.matrixA);

        hmm.estimateEmissions(observations);
        System.out.println(hmm.matrixEmissions);

        System.out.println(hmm);
    }
    @Test
    public void EMTest() {
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
        HMM<String, String> hmm2 = hmm.EM(emissionSet, observations, 1000);
        System.out.println(hmm2);
    }

    @Test
    public void estimateMatrixATest2() {
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
        hmm.backward(observations);

        hmm.estimateMatrixA(observations);
        System.out.println(hmm.matrixA);

        hmm.estimateEmissions(observations);
        System.out.println(hmm.matrixEmissions);

        System.out.println(hmm);
    }

}