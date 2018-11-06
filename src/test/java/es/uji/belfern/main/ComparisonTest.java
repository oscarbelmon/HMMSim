package es.uji.belfern.main;

import org.junit.jupiter.api.Test;

import static org.hamcrest.core.Is.*;
import static org.hamcrest.MatcherAssert.*;

public class ComparisonTest {
    @Test
    void initialiseTest() {
        Comparison comparison = new Comparison("arturo_train_1.csv", "arturo_test.csv");
        assertThat(comparison.instancesMap.get("Baño").size(), is(325));
    }

    @Test
    void evaluateClassifiersTest() {
        Comparison comparison = new Comparison("arturo_train_1.csv", "arturo_test.csv");
        comparison.evaluateClassifiers(9, 9);
    }

    @Test
    void evaluataClassifiers2Test() {
        Comparison comparison = new Comparison("arturo_train_1.csv", "arturo_test.csv");
        comparison.evaluateClassifiers2(3, 3);
    }
}
