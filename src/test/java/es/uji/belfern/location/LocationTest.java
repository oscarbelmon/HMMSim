package es.uji.belfern.location;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocationTest {
//    private final String fileName = "oscar_train.csv";
    private final String fileName = "train_emilio.csv";
    private final String label = "label";
//    private final String labelName = "Despacho";
    private final String labelName = "zero";

    @Test
    void createHMMForWAPTest() {
        Location location = new Location(fileName, label, labelName);
        location.createHMMForWAP("e2:41:36:00:07:b8");
    }

    @Test
    void createHMMForAllWAPTest() {
        Location location = new Location(fileName, label, labelName);
        location.createHMMForAllWAP();
    }

    @Test
    void estimateLocationProbabilityTest() {
        Location location = new Location(fileName, label, labelName);
        location.createHMMForAllWAP();

        Map<String, List<Integer>> measures = new HashMap<>();
        List<Integer> intensities = Arrays.asList(0, 0, 0, 0, 0);
        measures.put("a", intensities);
        intensities = Arrays.asList(0, 0, 0, 0, 0);
        measures.put("b", intensities);
        intensities = Arrays.asList(-87, 0, 0, -85, -85);
        measures.put("c", intensities);
        intensities = Arrays.asList(-59, -60, -61, -59, -60);
        measures.put("d", intensities);
        intensities = Arrays.asList(-84, -88, -86, -86, -86);
        measures.put("e", intensities);
        intensities = Arrays.asList(-81, 0, 0, 0, 0);
        measures.put("f", intensities);
        intensities = Arrays.asList(-80, -80, -80, -80, -80);
        measures.put("g", intensities);
        intensities = Arrays.asList(-82, 0, 0, 0, 0);
        measures.put("h", intensities);
        intensities = Arrays.asList(0, 0, 0, 0, 0);
        measures.put("i", intensities);

        location.estimateLocationProbability(measures);
    }
}
