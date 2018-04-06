package belfern.uji.es.hmm;

import belfern.uji.es.statistics.ProbabilityDensityFunction;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
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
        Node<String, String> node = hmm.instanceNode("Primero", () -> "Uno");
        assertThat(node, notNullValue());
    }

    @Test
    public void instanceEdgeNoNullTest() {
        Node<String, String> start = hmm.instanceNode("start", () -> "Start");
        Node<String, String> end = hmm.instanceNode("end", () -> "End");
        Edge<String, String> edge = hmm.instanceEdge(start, end, ProbabilityDensityFunction.CONSTANT_PROBABILITY, 1);
        assertThat(edge, notNullValue());
    }

    @Test
    public void nextTest() {
        Node<String, String> uno = hmm.instanceNode("Uno", () -> "Uno");
        Node<String, String> dos = hmm.instanceNode("Dos", () -> "Dos");
        Node<String, String> tres = hmm.instanceNode("Tres", () -> "Tres");
        hmm.instanceEdge(uno, dos, ProbabilityDensityFunction.CONSTANT_PROBABILITY, 0.4);
        hmm.instanceEdge(uno, tres, ProbabilityDensityFunction.CONSTANT_PROBABILITY, 0.6);
        assertThat(uno.nextNode(0.5), is(tres));
    }

    @Test
    public void nextWithIdsTest() {
        Node<String, String> uno = hmm.instanceNode("Uno", () -> "Uno");
        hmm.instanceNode("Dos", () -> "Dos");
        hmm.instanceNode("Tres", () -> "Tres");
        hmm.instanceEdge("Uno", "Dos", ProbabilityDensityFunction.CONSTANT_PROBABILITY, 0.4);
        hmm.instanceEdge("Uno", "Tres", ProbabilityDensityFunction.CONSTANT_PROBABILITY, 0.6);
        assertThat(uno.nextNode(0.05), is(hmm.nodes.get("Dos")));
    }

    @Test
    public void accumulateProbabilitiesTest() {
        Node<String, String> uno = hmm.instanceNode("Uno", () -> "Uno");
        Node<String, String> dos = hmm.instanceNode("Dos", () -> "Dos");
        Node<String, String> tres = hmm.instanceNode("Tres", () -> "Tres");
        Node<String, String> cuatro = hmm.instanceNode("Cuatro", () -> "Cuatro");
        hmm.instanceEdge("Uno", "Uno", ProbabilityDensityFunction.CONSTANT_PROBABILITY, 0.1);
        hmm.instanceEdge("Uno", "Dos", ProbabilityDensityFunction.CONSTANT_PROBABILITY, 0.2);
        hmm.instanceEdge("Uno", "Tres", ProbabilityDensityFunction.CONSTANT_PROBABILITY, 0.3);
        hmm.instanceEdge("Uno", "Cuatro", ProbabilityDensityFunction.CONSTANT_PROBABILITY, 0.4);

        Node<String, String> res = uno.nextNode(0.05);
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
        Node<String, String> uno = hmm.instanceNode("Uno", () -> "Uno");
        Node<String, String> dos = hmm.instanceNode("Dos", () -> "Dos");
        hmm.instanceEdge(uno, dos, ProbabilityDensityFunction.CONSTANT_PROBABILITY, 1.0);
        hmm.instanceEdge(dos, uno, ProbabilityDensityFunction.CONSTANT_PROBABILITY, 1.0);
        hmm.addInitialNode(uno, 1);
        assertThat(hmm.generateSequence(10), is(expected));
    }

    @Test
    public void generateSequenceTest2() {
        List<String> expected = Arrays.asList("Uno", "Dos", "Uno", "Dos", "Uno", "Dos", "Uno", "Dos", "Uno", "Dos");
        Node<String, String> uno = hmm.instanceNode("Uno", () -> "Uno");
        Node<String, String> dos = hmm.instanceNode("Dos", () -> "Dos");
        hmm.instanceEdge(uno, dos, ProbabilityDensityFunction.CONSTANT_PROBABILITY, 1.0);
        hmm.instanceEdge(dos, uno, ProbabilityDensityFunction.CONSTANT_PROBABILITY, 1.0);
        hmm.addInitialNode(uno, 1);
        assertThat(hmm.generateSequence(10), is(expected));
    }

    @Test
    public void sequenceTest() {
        double uno_zero = 0.4;
        double uno_uno = 1 - uno_zero;
        double zero_uno = 0.8;
        double zero_zero = 1- zero_uno;
        double expectedRatio = zero_uno/(uno_zero + zero_uno);
        
        HMM<String, Integer> hmmInt = new HMM<>();
        Node<String, Integer> uno = hmmInt.instanceNode("uno", () -> 1);
        hmmInt.addInitialNode(uno, 1);
        Node<String, Integer> zero = hmmInt.instanceNode("zero", () -> 0);
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

    @Test
    public void sequenceTabulatedTest() {
        double uno_zero = 0.6;
        double uno_uno = 1 - uno_zero;
        double zero_uno = 0.8;
        double zero_zero = 1- zero_uno;
        double expectedRatio = zero_uno/(uno_zero + zero_uno);

        HMM<String, Integer> hmmInt = new HMM<>();
        TabulatedProbabilityEmitter<Integer> emitterOne = new TabulatedProbabilityEmitter<>();
        emitterOne.addEmission(1,100);
        TabulatedProbabilityEmitter<Integer> emitterZero = new TabulatedProbabilityEmitter<>();
        emitterZero.addEmission(0, 100);
        Node<String, Integer> uno = hmmInt.instanceNode("uno", emitterOne);
        hmmInt.addInitialNode(uno, 1);
        Node<String, Integer> zero = hmmInt.instanceNode("zero", emitterZero);
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

    @Test
    public void sequenceTabulatedTest2() {
        double uno_zero = 0.6;
        double uno_uno = 1 - uno_zero;
        double zero_uno = 0.8;
        double zero_zero = 1- zero_uno;
        double expectedRatio = zero_uno/(uno_zero + zero_uno);

        HMM<String, Integer> hmmInt = new HMM<>();
        TabulatedProbabilityEmitter<Integer> emitterOne = new TabulatedProbabilityEmitter<>();
        emitterOne.addEmission(1,100);
        TabulatedProbabilityEmitter<Integer> emitterZero = new TabulatedProbabilityEmitter<>();
        emitterZero.addEmission(0, 100);
        Node<String, Integer> uno = hmmInt.instanceNode("uno", emitterOne);
        hmmInt.addInitialNode(uno, 1);
        Node<String, Integer> zero = hmmInt.instanceNode("zero", emitterZero);
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

    @Test
    public void initializationTest() {
        Node<String, String> uno = hmm.instanceNode("Uno", () -> "Uno");
        Node<String, String> dos = hmm.instanceNode("Dos", () -> "Dos");
        hmm.instanceEdge(uno, dos, ProbabilityDensityFunction.CONSTANT_PROBABILITY, 1.0);
        hmm.instanceEdge(dos, uno, ProbabilityDensityFunction.CONSTANT_PROBABILITY, 1.0);
        hmm.addInitialNode(uno, 1);

        hmm.initialization("Uno");

        assertThat(uno, is(uno));
        assertEquals(uno, uno);

        double result = hmm.nodes.values().stream()
                .filter(node -> uno.equals(node))
                .mapToDouble(node -> node.alfa)
                .findFirst()
                .getAsDouble();

        assertEquals(result, 1.0, 0.1);

        result = hmm.nodes.values().stream()
                .filter(node -> dos.equals(node))
                .mapToDouble(node -> node.alfa)
                .findFirst()
                .getAsDouble();

        assertEquals(result, 0.0, 0.1);
    }

    @Test
    public void recursionTest() {
        HMM<String, Integer> hmmInt = new HMM<>();

        TabulatedProbabilityEmitter<String> emitterOne = new TabulatedProbabilityEmitter<>();
        emitterOne.addEmission("One",100);
        emitterOne.addEmission("Two", 0);
        TabulatedProbabilityEmitter<String> emitterTwo = new TabulatedProbabilityEmitter<>();
        emitterTwo.addEmission("One", 0);
        emitterTwo.addEmission("Two", 100);

        Node<String, String> uno = hmm.instanceNode("One", emitterOne);
        Node<String, String> dos = hmm.instanceNode("Two", emitterTwo);
        hmm.instanceEdge(uno, dos, ProbabilityDensityFunction.CONSTANT_PROBABILITY, 1.0);
        hmm.instanceEdge(dos, uno, ProbabilityDensityFunction.CONSTANT_PROBABILITY, 1.0);
        hmm.addInitialNode(uno, 1);

        hmm.initialization("One");
        hmm.recursion(Arrays.asList("One", "Two", "One", "Two", "One", "Two"));

        double[] oneForward = {1.0, 0.0, 1.0, 0.0, 1.0, 0.0};
        assertArrayEquals(oneForward, ArrayUtils.toPrimitive(uno.getAlfas().toArray(new Double[uno.getAlfas().size()])));
        double[] twoForward = {0.0, 1.0, 0.0, 1.0, 0.0, 1.0};
        assertArrayEquals(twoForward, ArrayUtils.toPrimitive(dos.getAlfas().toArray(new Double[dos.getAlfas().size()])));
    }

    @Test
    public void terminationTest() {
        TabulatedProbabilityEmitter<String> emitterOne = new TabulatedProbabilityEmitter<>();
        emitterOne.addEmission("One",100);
        emitterOne.addEmission("Two", 0);
        TabulatedProbabilityEmitter<String> emitterTwo = new TabulatedProbabilityEmitter<>();
        emitterTwo.addEmission("One", 0);
        emitterTwo.addEmission("Two", 100);

        Node<String, String> uno = hmm.instanceNode("One", emitterOne);
        Node<String, String> dos = hmm.instanceNode("Two", emitterTwo);
        hmm.instanceEdge(uno, dos, ProbabilityDensityFunction.CONSTANT_PROBABILITY, 1.0);
        hmm.instanceEdge(dos, uno, ProbabilityDensityFunction.CONSTANT_PROBABILITY, 1.0);
        hmm.addInitialNode(uno, 1);

        hmm.initialization("One");
        hmm.recursion(Arrays.asList("One", "Two", "One", "Two", "One", "Two"));
        double probability = hmm.termination("Two");

        assertEquals(1.0, probability, 0.01);

        hmm.initialization("One");
        hmm.recursion(Arrays.asList("One", "Two", "One", "Two", "One", "One"));
        probability = hmm.termination("One");

        assertEquals(0.0, probability, 0.01);
    }

    @Test
    public void forwardTest() {
        TabulatedProbabilityEmitter<String> emitterOne = new TabulatedProbabilityEmitter<>();
        emitterOne.addEmission("One",40);
        emitterOne.addEmission("Two", 60);
        TabulatedProbabilityEmitter<String> emitterTwo = new TabulatedProbabilityEmitter<>();
        emitterTwo.addEmission("One", 50);
        emitterTwo.addEmission("Two", 50);

        Node<String, String> uno = hmm.instanceNode("One", emitterOne);
        Node<String, String> dos = hmm.instanceNode("Two", emitterTwo);
        hmm.instanceEdge(uno, uno, ProbabilityDensityFunction.CONSTANT_PROBABILITY, 0.2);
        hmm.instanceEdge(uno, dos, ProbabilityDensityFunction.CONSTANT_PROBABILITY, 0.8);
        hmm.instanceEdge(dos, uno, ProbabilityDensityFunction.CONSTANT_PROBABILITY, 1.0);
        hmm.addInitialNode(uno, 0.7);
        hmm.addInitialNode(dos, 0.3);

        double probability = hmm.forward(Arrays.asList("One","Two","One","Two","One","Two","One","One","One","Two","Two"));

        double[] expectedOne = {0.28, 0.1236, 0.054688, 0.03622656, 0.01164820, 0.010092159, 0.002671085, 0.001828432, 0.0005736483, 0.0005076615, 0.0001985950};
        assertArrayEquals(expectedOne, ArrayUtils.toPrimitive(uno.getAlfas().toArray(new Double[uno.getAlfas().size()])), 0.0001);

        double[] expectedTwo = {0.15, 0.1120, 0.049440, 0.02187520, 0.01449062, 0.004659282, 0.004036864, 0.001068434, 0.0007313729, 0.0002294593, 0.0002030646};
        assertArrayEquals(expectedTwo, ArrayUtils.toPrimitive(dos.getAlfas().toArray(new Double[dos.getAlfas().size()])), 0.0001);

        String[] expected = {"One", "Two", "One", "Two", "One", "Two", "One", "Two", "One", "Two"};
        assertArrayEquals(expected, hmm.viterbi(Arrays.asList("One", "Two", "One", "Two", "Two","One","One","One","One","One")).toArray(new String[expected.length]));
    }

    @Test
    public void viterbiTest() {
        TabulatedProbabilityEmitter<String> emitterOne = new TabulatedProbabilityEmitter<>();
        emitterOne.addEmission("One",40);
        emitterOne.addEmission("Two", 60);
        TabulatedProbabilityEmitter<String> emitterTwo = new TabulatedProbabilityEmitter<>();
        emitterTwo.addEmission("One", 50);
        emitterTwo.addEmission("Two", 50);

        Node<String, String> uno = hmm.instanceNode("One", emitterOne);
        Node<String, String> dos = hmm.instanceNode("Two", emitterTwo);
        hmm.instanceEdge(uno, uno, ProbabilityDensityFunction.CONSTANT_PROBABILITY, 0.2);
        hmm.instanceEdge(uno, dos, ProbabilityDensityFunction.CONSTANT_PROBABILITY, 0.8);
        hmm.instanceEdge(dos, uno, ProbabilityDensityFunction.CONSTANT_PROBABILITY, 0.5);
        hmm.instanceEdge(dos, dos, ProbabilityDensityFunction.CONSTANT_PROBABILITY, 0.5);
        hmm.addInitialNode(uno, 0.7);
        hmm.addInitialNode(dos, 0.3);

        String[] expected = {"One", "Two", "Two", "One", "Two", "One", "Two"};
        assertArrayEquals(expected, hmm.viterbi(Arrays.asList("One", "Two", "One", "Two", "One", "Two", "One")).toArray(new String[expected.length]));
    }

    @Test
    public void viterbiTest2() {
        TabulatedProbabilityEmitter<String> emitterOne = new TabulatedProbabilityEmitter<>();
        emitterOne.addEmission("One",100);
        emitterOne.addEmission("Two", 0);
        TabulatedProbabilityEmitter<String> emitterTwo = new TabulatedProbabilityEmitter<>();
        emitterTwo.addEmission("One", 0);
        emitterTwo.addEmission("Two", 100);

        Node<String, String> uno = hmm.instanceNode("One", emitterOne);
        Node<String, String> dos = hmm.instanceNode("Two", emitterTwo);
        hmm.instanceEdge(uno, uno, ProbabilityDensityFunction.CONSTANT_PROBABILITY, 0.0);
        hmm.instanceEdge(uno, dos, ProbabilityDensityFunction.CONSTANT_PROBABILITY, 1.0);
        hmm.instanceEdge(dos, uno, ProbabilityDensityFunction.CONSTANT_PROBABILITY, 1.0);
        hmm.instanceEdge(dos, dos, ProbabilityDensityFunction.CONSTANT_PROBABILITY, 0.0);
        hmm.addInitialNode(uno, 1.0);
        hmm.addInitialNode(dos, 0.0);

//        System.out.println(hmm.viterbi(Arrays.asList("One", "One", "One", "Two", "One", "Two", "One", "One")));

        String[] expected = {"One", "One", "One", "One", "One", "One", "One", "One"};
        String[] real = hmm.viterbi(Arrays.asList("One", "One", "One", "Two", "One", "Two", "One", "One")).toArray(new String[expected.length]);

        assertArrayEquals(expected, real);
    }
    @Test
    public void viterbiTest3() {
        TabulatedProbabilityEmitter<String> emitterH = new TabulatedProbabilityEmitter<>();
        emitterH.addEmission("A",20);
        emitterH.addEmission("C", 30);
        emitterH.addEmission("G", 30);
        emitterH.addEmission("T", 20);
        TabulatedProbabilityEmitter<String> emitterL = new TabulatedProbabilityEmitter<>();
        emitterL.addEmission("A", 30);
        emitterL.addEmission("C", 20);
        emitterL.addEmission("G", 20);
        emitterL.addEmission("T", 30);

        Node<String, String> h = hmm.instanceNode("H", emitterH);
        Node<String, String> l = hmm.instanceNode("L", emitterL);
        hmm.instanceEdge(h, h, ProbabilityDensityFunction.CONSTANT_PROBABILITY, 0.5);
        hmm.instanceEdge(h, l, ProbabilityDensityFunction.CONSTANT_PROBABILITY, 0.5);
        hmm.instanceEdge(l, h, ProbabilityDensityFunction.CONSTANT_PROBABILITY, 0.4);
        hmm.instanceEdge(l, l, ProbabilityDensityFunction.CONSTANT_PROBABILITY, 0.6);
        hmm.addInitialNode(h, 0.5);
        hmm.addInitialNode(l, 0.5);

        String[] expected = {"H", "H", "H", "L", "L", "L", "L", "L", "L"};
        String[] real = hmm.viterbi(Arrays.asList("G","G","C","A","C","T","G","A","A")).toArray(new String[expected.length]);
        assertArrayEquals(expected, real);
    }

    @Test
    public void viterbiTest4() {
        TabulatedProbabilityEmitter<String> emitterHealthy = new TabulatedProbabilityEmitter();
        emitterHealthy.addEmission("Dizzy", 10);
        emitterHealthy.addEmission("Cold", 40);
        emitterHealthy.addEmission("Normal", 50);
        TabulatedProbabilityEmitter<String> emitterFever = new TabulatedProbabilityEmitter();
        emitterFever.addEmission("Dizzy", 60);
        emitterFever.addEmission("Cold", 30);
        emitterFever.addEmission("Normal", 10);

        Node<String, String> helthy = hmm.instanceNode("Healthy", emitterHealthy);
        Node<String, String> fever = hmm.instanceNode("Fever", emitterFever);

        hmm.instanceEdge(helthy, helthy, ProbabilityDensityFunction.CONSTANT_PROBABILITY, 0.7);
        hmm.instanceEdge(helthy, fever, ProbabilityDensityFunction.CONSTANT_PROBABILITY, 0.3);
        hmm.instanceEdge(fever, fever, ProbabilityDensityFunction.CONSTANT_PROBABILITY, 0.5);
        hmm.instanceEdge(fever, helthy, ProbabilityDensityFunction.CONSTANT_PROBABILITY, 0.5);
        hmm.addInitialNode(helthy, 0.6);
        hmm.addInitialNode(fever, 0.4);

        String[] expected = {"Healthy", "Fever", "Fever", "Healthy", "Healthy", "Healthy", "Healthy", "Healthy", "Healthy", "Healthy", "Fever", "Healthy", "Healthy"};
        String[] real = hmm.viterbi(Arrays.asList("Normal", "Dizzy", "Dizzy", "Cold", "Normal", "Normal", "Cold", "Normal", "Normal", "Cold", "Dizzy", "Normal", "Cold")).toArray(new String[expected.length]);
    }
}
