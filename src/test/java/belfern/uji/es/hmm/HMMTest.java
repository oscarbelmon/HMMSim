package belfern.uji.es.hmm;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
    public void forwardTest2() {
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

        hmm.forward(Arrays.asList("a","a","b"));
        System.out.println(X.getAlfas());
        System.out.println(Y.getAlfas());

        hmm.backward(Arrays.asList("a","a","b"));
        Collections.reverse(X.getBetas());
        Collections.reverse(Y.getBetas());
        System.out.println(X.getBetas());
        System.out.println(Y.getBetas());
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
}
