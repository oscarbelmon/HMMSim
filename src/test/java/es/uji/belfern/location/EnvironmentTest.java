package es.uji.belfern.location;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnvironmentTest {
    private String trainDataFile = "train_emilio.csv";
    private String headerClassName = "label";
    private static Map<String, List<Integer>> zeroMeasures = new HashMap<>();
    private static Map<String, List<Integer>> oneMeasures = new HashMap<>();

    @BeforeAll
    static void setUp() {
        List<Integer> intensities = Arrays.asList(0, 0, 0, 0, 0);
        zeroMeasures.put("a", intensities);
        intensities = Arrays.asList(0, 0, 0, 0, 0);
        zeroMeasures.put("b", intensities);
        intensities = Arrays.asList(-87, 0, 0, -85, -85);
        zeroMeasures.put("c", intensities);
        intensities = Arrays.asList(-59, -60, -61, -59, -60);
        zeroMeasures.put("d", intensities);
        intensities = Arrays.asList(-84, -88, -86, -86, -86);
        zeroMeasures.put("e", intensities);
        intensities = Arrays.asList(-81, 0, 0, 0, 0);
        zeroMeasures.put("f", intensities);
        intensities = Arrays.asList(-80, -80, -80, -80, -80);
        zeroMeasures.put("g", intensities);
        intensities = Arrays.asList(-82, 0, 0, 0, 0);
        zeroMeasures.put("h", intensities);
        intensities = Arrays.asList(0, 0, 0, 0, 0);
        zeroMeasures.put("i", intensities);

        oneMeasures = new HashMap<>();
        intensities = Arrays.asList(0, 0, 0, 0, 0);
        oneMeasures.put("a", intensities);
        intensities = Arrays.asList(0, -89, -89, -89, -89);
        oneMeasures.put("b", intensities);
        intensities = Arrays.asList(0, 0, 0, 0, 0);
        oneMeasures.put("c", intensities);
        intensities = Arrays.asList(-60, -54, -54, -64, -68);
        oneMeasures.put("d", intensities);
        intensities = Arrays.asList(-81, 0, 0, 0, 0);
        oneMeasures.put("e", intensities);
        intensities = Arrays.asList(0, 0, 0, 0, 0);
        oneMeasures.put("f", intensities);
        intensities = Arrays.asList(0, 0, 0, 0, 0);
        oneMeasures.put("g", intensities);
        intensities = Arrays.asList(0, 0, 0, 0, 0);
        oneMeasures.put("h", intensities);
        intensities = Arrays.asList(0, 0, 0, 0, 0);
        oneMeasures.put("i", intensities);
    }

    @Test
    void environmentTest() {
        Environment environment = new Environment(trainDataFile, headerClassName);
        environment.estimateLocationProbability(zeroMeasures);
    }
}
