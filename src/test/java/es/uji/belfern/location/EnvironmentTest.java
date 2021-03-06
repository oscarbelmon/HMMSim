package es.uji.belfern.location;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import es.uji.belfern.data.Matrix;
import es.uji.belfern.util.CSVReader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class EnvironmentTest {
//    private String trainDataFile = "wifi_train.csv";
    private String trainDataFile = "wifi_train_1_sin_ordenar_100_por_habitacion.csv";
    private String testDataFile = "wifi_test.csv";
//    private String trainDataFile = "oscar_train.csv";
//    private String testDataFile = "oscar_test.csv";
    private String headerClassName = "label";
    private static Map<String, List<Integer>> zeroMeasures = new HashMap<>();
    private static Map<String, List<Integer>> oneMeasures = new HashMap<>();
//    private static String fileOutputName = "hmm_5_iteration_3_states_radom_0.bin";
    private static String fileOutputName = "wifi_1.bin";
//    private static String fileOutputName = "kktodos.bin";

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

//    @Test
//    void environmentTest() {
//        Environment environment = new Environment(trainDataFile, headerClassName);
//        storeEnvironment(environment);
//    }

    @Test
    void readEnvironmentTest() throws IOException {
//        Environment environment = readEnvironmentFile();
        Environment environment = Environment.readEnvironmentFromFile("emilio_1.bin");
        environment.estimateLocationProbability(zeroMeasures);
        environment.estimateLocationProbability(oneMeasures);
    }

    @Test
    void allMeasuresTest() throws IOException {
        Environment environment = Environment.readEnvironmentFromFile(fileOutputName);
        System.out.println(environment);
        CSVReader csvReader = new CSVReader("test_emilio.csv", headerClassName);
//        CSVReader csvReader = new CSVReader("train_emilio.csv", headerClassName);
        List<String> waps = csvReader.getHeaderNames();
        List<String> locations = csvReader.getLocations();
        List<String> trainLocations = environment.getLocations();
        Map<String, List<Integer>> allMeasures = new HashMap<>();
//        Map<String, List<Integer>> measures;
        Map<String, List<Integer>> measures = new HashMap<>();
        Matrix<String, String, Integer> confusion = new Matrix<>();
        long total = 0, success = 0;
        String estimatedLocation = "";
        int step = 12;
        for(String location: locations) {
            if(trainLocations.contains(location)) {
                for (String wap : waps) {
                    allMeasures.put(wap, csvReader.getDataLocationWAP(location, wap));
                }
                System.out.println("Size: " + allMeasures.get(waps.get(0)).size());
                for (int i = 0; i < allMeasures.get(waps.get(0)).size() - step; i += 1) {
                    total++;
//                    System.out.println(total);
                    measures = new HashMap<>();
                    for (String wap : waps) {
                        measures.put(wap, allMeasures.get(wap).subList(i, i + step));
                    }
//                    estimatedLocation = environment.estimateLocationProbability(measures);
                    if (estimatedLocation.equals(location)) {
                        success++;
                    }
                    int previous = 0;
                    if (confusion.get(location, estimatedLocation) != null)
                        previous = confusion.get(location, estimatedLocation);
                    confusion.put(location, estimatedLocation, previous + 1);
//                    for (String wap : waps) {
//                        measures.remove(wap);
//                    }
                }
            }
        }
        System.out.println("Total:" + total + ", success: " + success);
        System.out.println(success*100.0/total);
        System.out.println(confusion);
    }

    @Test
    void jsonTest() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Environment environment = Environment.readEnvironmentFromFile("hmm_50_iterations.bin");
//        System.out.println(gson.toJson(environment));
        String json = gson.toJson(environment);
        FileWriter fw = new FileWriter("coso_50_iterations.txt");
        fw.write(json);
        fw.flush();
        fw.close();
    }

    @Test
    void jsonReadTest() throws FileNotFoundException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        FileReader fr = new FileReader("coso.txt");
//        Environment environment = gson.fromJson(fr, Environment.class);
//        System.out.println(environment);
//        return environment;


        Type REVIEW_TYPE = new TypeToken<Environment>() { }.getType();
//        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new FileReader("coso.txt"));
        Environment env = gson.fromJson(reader, REVIEW_TYPE); // contains the whole reviews list
        System.out.println(env);
    }

    @Test
    void showHMMTest() throws IOException {
        Environment environment = Environment.readEnvironmentFromFile("hmm.bin");
        environment = Environment.readEnvironmentFromFile("hmm_5_iterations_2_states_random_1.bin");

    }

    @Test
    void viterbiTest() throws IOException {
        Environment environment = Environment.readEnvironmentFromFile("hmm.bin");


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
            FileOutputStream fos = new FileOutputStream(fileOutputName);
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

    @Test
    void borrar() {
        StringBuffer sb = new StringBuffer();
        Formatter f = new Formatter(sb, Locale.US);
        f.format("%.3f", 1.23456);
        f.format("%.3f", 2.3456);
        System.out.println(sb.toString());
    }
}
