package es.uji.belfern.location;

import org.junit.jupiter.api.Test;

public class LocationTest {
    private final String fileName = "oscar_train.csv";
    private final String label = "label";
    private final String labelName = "Despacho";

    @Test
    void createHMMForWAPTest() {
        Location location = new Location(fileName, label, labelName);
        location.createHMMForWAP("e2:41:36:00:07:b8");
    }
}
