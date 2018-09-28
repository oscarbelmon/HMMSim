package es.uji.belfern.location;

import es.uji.belfern.util.CSVReader;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class EnvironmentAltTest {
    private String trainDataFile = "train_emilio_reducido_10.csv";
    private String testDataFile = "test_emilio.csv";
    private String headerClassName = "label";
    private int iterarions = 5;
    private String fileName = "train_emilio_alt_10.bin";

    @Test
    void environmentAltTest() {
        EnvironmentAlt environment = new EnvironmentAlt(trainDataFile, headerClassName, iterarions);
        environment.storeEnvironment(fileName);
    }

    @Test
    void allMeasuresTest() throws IOException {
        EnvironmentAlt environmentAlt = EnvironmentAlt.readEnvironmentFromFile(fileName);
        CSVReader csvReader = new CSVReader(testDataFile, headerClassName);

        List<String> locations = csvReader.getLocations();
        List<List<Integer>> measures;
        long total = 0, success = 0;

        for(String location: locations) {
            measures = csvReader.getDataLocationAsLists(location);
            for (List<Integer> measure: measures) {
                total++;
                if(location.equals(environmentAlt.estimateLocationProbability(measure)))
                    success++;
            }
        }

        System.out.println("Total: " + total + ", success: " + success);
        System.out.println(success*100.0/total);
    }
}
