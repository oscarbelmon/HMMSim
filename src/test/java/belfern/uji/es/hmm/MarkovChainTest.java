package belfern.uji.es.hmm;

import belfern.uji.es.statistics.ProbabilityDensityFunction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MarkovChainTest {
    private static MarkovChain<String> hmm;

    @BeforeAll
    public static void start() {
        hmm = new MarkovChain<>();
    }

    @Test
    public void test() {
        Node<String, String> one = hmm.instanceNode("One");
        Node<String, String> two = hmm.instanceNode("Two");
        Node<String, String> three = hmm.instanceNode("Three");
        hmm.instanceEdge(one, two, ProbabilityDensityFunction.CONSTANT_PROBABILITY, 0.4);
        hmm.instanceEdge(one, three, ProbabilityDensityFunction.CONSTANT_PROBABILITY, 0.6);
        assertThat(one.nextNode(0.5), is(three));
    }

    @Test
    public void nextWithIdsTest() {
        Node<String, String> uno = hmm.instanceNode("One");
        hmm.instanceNode("Two");
        hmm.instanceNode("Three");
        hmm.instanceEdge("One", "Two", ProbabilityDensityFunction.CONSTANT_PROBABILITY, 0.4);
        hmm.instanceEdge("One", "Three", ProbabilityDensityFunction.CONSTANT_PROBABILITY, 0.6);
        assertThat(uno.nextNode(0.05), is(hmm.nodes.get("Two")));
    }

    @Test
    public void accumulateProbabilitiesTest() {
        Node<String, String> uno = hmm.instanceNode("One");
        Node<String, String> dos = hmm.instanceNode("Two");
        Node<String, String> three = hmm.instanceNode("Three");
        Node<String, String> four = hmm.instanceNode("Four");
        hmm.instanceEdge("One", "One", ProbabilityDensityFunction.CONSTANT_PROBABILITY, 0.1);
        hmm.instanceEdge("One", "Two", ProbabilityDensityFunction.CONSTANT_PROBABILITY, 0.2);
        hmm.instanceEdge("One", "Three", ProbabilityDensityFunction.CONSTANT_PROBABILITY, 0.3);
        hmm.instanceEdge("One", "Four", ProbabilityDensityFunction.CONSTANT_PROBABILITY, 0.4);

        Node<String, String> res = uno.nextNode(0.05);
        assertThat(res, is(uno));

        res = uno.nextNode(0.15);
        assertThat(res, is(dos));

        res = uno.nextNode(0.35);
        assertThat(res, is(three));

        res = uno.nextNode(0.95);
        assertThat(res, is(four));
    }

    @Test
    public void generateSequenceTest() {
        List<String> expected = Arrays.asList("One", "Two", "One", "Two", "One", "Two", "One", "Two", "One", "Two");
        Node<String, String> uno = hmm.instanceNode("One");
        Node<String, String> dos = hmm.instanceNode("Two");
        hmm.instanceEdge(uno, dos, ProbabilityDensityFunction.CONSTANT_PROBABILITY, 1.0);
        hmm.instanceEdge(dos, uno, ProbabilityDensityFunction.CONSTANT_PROBABILITY, 1.0);
//        hmm.setInitialNode(uno);
        hmm.addInitialNode(uno, 1);
        assertThat(hmm.generateSequence(10), is(expected));
    }
}
