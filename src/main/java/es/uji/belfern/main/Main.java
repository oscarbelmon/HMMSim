package es.uji.belfern.main;

import es.uji.belfern.data.Matrix;
import es.uji.belfern.location.Environment;
import es.uji.belfern.util.CSVReader;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    private static final String HEADER_CLASS_NAME = "label";

    public static void main(String[] args) {
        if (args[0].equals("create")) {
            if (args.length != 5) System.out.println("Show usage.");
            else new Main().createHMM(args[1], args[2], Integer.parseInt(args[3]), Integer.parseInt(args[4]));
        } else if (args[0].equals("evaluate")) {
            if (args.length != 4) System.out.println("Show usage.");
            new Main().evaluateHMM(args[1], args[2], Integer.parseInt(args[3]));
        }
    }

    private void createHMM(String trainFileName, String hmmFileName, int nodes, int iterations) {
        Environment environment = new Environment(trainFileName, HEADER_CLASS_NAME, nodes, iterations);

        try {
            FileOutputStream fos = new FileOutputStream(hmmFileName);
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

    private void evaluateHMM(final String hmmFileName, final String testFileName, int step) {
        try {
            Environment environment = Environment.readEnvironmentFromFile(hmmFileName);
//            System.out.println(environment);
            CSVReader csvReader = new CSVReader(testFileName, HEADER_CLASS_NAME);
            List<String> waps = csvReader.getHeaderNames();
            List<String> locations = csvReader.getLocations();
            List<String> trainLocations = environment.getLocations();
            Map<String, List<Integer>> allMeasures = new HashMap<>();
            Map<String, List<Integer>> measures = new HashMap<>();
            Matrix<String, String, Integer> confusion = new Matrix<>();
            long total = 0, success = 0;
            String estimatedLocation = "";
//            int step = 12;
            for (String location : locations) {
                if (trainLocations.contains(location)) {
                    for (String wap : waps) {
                        allMeasures.put(wap, csvReader.getDataLocationWAP(location, wap));
                    }
                    System.out.println("Size: " + allMeasures.get(waps.get(0)).size());
                    for (int i = 0; i < allMeasures.get(waps.get(0)).size() - step; i += 1) {
                        total++;
                        measures = new HashMap<>();
                        for (String wap : waps) {
                            measures.put(wap, allMeasures.get(wap).subList(i, i + step));
                        }
                        estimatedLocation = environment.estimateLocationProbability(measures);
                        if (estimatedLocation.equals(location)) {
                            success++;
                        }
                        int previous = 0;
                        if (confusion.get(location, estimatedLocation) != null)
                            previous = confusion.get(location, estimatedLocation);
                        confusion.put(location, estimatedLocation, previous + 1);
                    }
                }
            }
            System.out.println("Total:" + total + ", success: " + success);
            System.out.println(success * 100.0 / total);
            System.out.println(confusion);
            metrics(confusion, locations);
        } catch (IOException e) {
            System.out.println("IO Error");
        }
    }

    private void metrics(final Matrix<String, String, Integer> confusion, final List<String> locations) {
        long tp = 0, fn = 0, fp = 0, tn = 0;
        double accuracy, precision, sensitivity, f1;
        for (String location : locations) {
            if (confusion.get(location, location) != null)
                tp = confusion.get(location, location);
            for (String estimate : locations) {
                if (confusion.get(location, estimate) != null)
                    fn += confusion.get(location, estimate);
                if (confusion.get(estimate, location) != null)
                    fp += confusion.get(estimate, location);
                if (confusion.get(estimate, estimate) != null)
                    tn += confusion.get(estimate, estimate);
            }
            fn -= tp;
            fp -= tp;
            tn -= tp;
            accuracy = (double) (tp + tn) / (double) (tp + tn + fp + fn);
            precision = (double) tp / (double) (tp + fp);
            sensitivity = (double) tp / (double) (tp + fn);
            f1 = 2.0 * precision * sensitivity / (double) (precision + sensitivity);
            System.out.println(location + ": TP: " + tp + ";  FN: " + fn + ";  FP: " + fp + ";  TN: " + tn +
                    ";  accuracy: " + accuracy + ";  precision: " + precision + ";  f1-score: " + f1);
        }
    }
}
