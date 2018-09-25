package es.uji.belfern.location;

import es.uji.belfern.util.CSVReader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.*;
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
        storeEnvironment(environment);
    }

    @Test
    void readEnvironmentTest() throws IOException {
//        Environment environment = readEnvironmentFile();
        Environment environment = Environment.readEnvironmentFromFile("hmm.bin");
        environment.estimateLocationProbability(zeroMeasures);
        environment.estimateLocationProbability(oneMeasures);
    }

    @Test
    void allMeasuresTest() throws IOException {
//        Environment environment = readEnvironmentFile();
        Environment environment = Environment.readEnvironmentFromFile("hmm.bin");
        CSVReader csvReader = new CSVReader("test_emilio.csv", headerClassName);
        List<String> waps = csvReader.getHeaderNames();
        System.out.println("Waps: " + waps);
        List<String> locations = csvReader.getLocations();
        System.out.println("Locations: " + locations);
        Map<String, List<Integer>> allMeasures = new HashMap<>();
        Map<String, List<Integer>> measures;
        long total = 0, success = 0;
        String estimatedLocation = "";
        for(String location: locations) {
            total++;
            System.out.println("Location: " + location);
            for (String wap : waps) {
                allMeasures.put(wap, csvReader.getDataLocationWAP(location, wap));
            }
            for(int i = 0; i < 90; i++) {
//            for(int i = 0; i < allMeasures.get(waps.get(0)).size(); i++) {
                System.out.println(i + "---");
                measures = new HashMap<>();
                for(String wap: waps) {
                    measures.put(wap, allMeasures.get(wap).subList(i, i+5));
                }
                estimatedLocation = environment.estimateLocationProbability(measures);
                if(estimatedLocation.equals(location)) {
                    success++;
                }
            }
        }
        System.out.println(success*100.0/total);
    }

    private Environment readEnvironmentFile() {
        try {
            FileInputStream fis = new FileInputStream("hmm.bin");
            ObjectInputStream ois = new ObjectInputStream(fis);
            Environment environment = (Environment)ois.readObject();
            ois.close();
            fis.close();
            return environment;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void storeEnvironment(Environment environment) {
        try {
            FileOutputStream fos = new FileOutputStream("hmm.bin");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(environment);
            oos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
