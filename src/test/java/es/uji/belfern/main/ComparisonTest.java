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
        comparison.evaluateClassifiers(10, 10);
    }

    @Test
    void evaluateClassifiersEmilio2Test() {
        Comparison comparison = new Comparison("emilio_train_2.csv", "emilio_test.csv");
        comparison.evaluateClassifiers(5, 5);
    }

    @Test
    void evaluateClassifiersMarta1Test() {
        Comparison comparison = new Comparison("marta_train_1.csv", "marta_test.csv");
        comparison.evaluateClassifiers(10, 10);
    }

}
