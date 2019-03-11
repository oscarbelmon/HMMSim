package es.uji.belfern.main;

import es.uji.belfern.data.Matrix;
import es.uji.belfern.location.Environment;
import es.uji.belfern.statistics.Estimate;
import es.uji.belfern.util.CSVReader;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;

public class Main {
    private static final String HEADER_CLASS_NAME = "label";

    public static void main(String[] args) {
        if (args[0].equals("create")) { //[1] train_file [2] model_output_file [3] HMM_states [4] model_fit_iterations [5] sample_size
            if (args.length != 6) System.out.println("Show usage.");
            else new Main().createHMM(args[1], args[2], Integer.parseInt(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5]));
        } else if (args[0].equals("evaluate")) { // [1] model_file [2] test_file [3] sample_size [4] shift_size
            if (args.length != 5) System.out.println("Show usage.");
            else if ((Integer.parseInt(args[3]) < Integer.parseInt(args[4])))
                System.out.println("Shift size can not be greater than Sample size");
            else new Main().evaluateHMM(args[1], args[2], Integer.parseInt(args[3]), Integer.parseInt(args[4]));
        } else if (args[0].equals("compare")){ // [1] model_file; [2] train_file; [3] test_file; [4] sample_size; [5] shift_size
            if (args.length != 6) System.out.println("Show usage.");
//            else if ((Integer.parseInt(args[4]) < Integer.parseInt(args[5])))
//                System.out.println("Shift size can not be greater than Sample size");
            else new Main().compareModelsTable(args[1], args[2], args[3], Integer.parseInt(args[4]), Integer.parseInt(args[5]));
        } else if (args[0].equals("all")) {
            if(args.length != 2) System.out.println("Show usage");
            else new Main().all(args[1]);
        } else {
            System.out.println("Show usage.");
        }
    }

    private void all(final String userName) {
        String hbmFileName, trainFileName, testFileName;
        for(int mode = 1; mode <= 3; mode++) {
            Map<Integer, List<Double>> table = new HashMap<>();
            List<Double> row;
            for (int size = 1; size <= 20; size++) {
                hbmFileName = userName + "_" + mode + "_2_5_" + size + ".bin";
                trainFileName = userName + "_train_" + mode + ".csv";
                testFileName = userName + "_test.csv";
                System.out.println("TRAINING DATASET: " + trainFileName);
                System.out.println("TEST DATASET: " + testFileName);
                System.out.println("SAMPLE SIZE: " + size + "    SHIFT SIZE: " + size);
                Comparison comparison = new Comparison(trainFileName, testFileName);
                row = comparison.evaluateClassifiersProbability(size, size);
                System.out.println("------------- Hidden Markov ------------");
                row.add(0, evaluateHMM(hbmFileName, testFileName, size, size));
                table.put(size, row);

            }
            System.out.println("Table: " + table);
            for(Integer i: table.keySet()) {
                System.out.print(i + ";");
                for(Double d: table.get(i)) {
                    System.out.print(d + ";");
                }
                System.out.println("");
            }

        }
        Map<Integer, List<Double>> table = new HashMap<>();
        List<Double> row;

        for (int size = 1; size <= 20; size++) {
            // emilio_1_2_5_10.bin
            hbmFileName = userName + "_2_5_" + size + ".bin";
            trainFileName = userName + "_train.csv";
            testFileName = userName + "_test.csv";
            System.out.println("TRAINING DATASET: " + trainFileName);
            System.out.println("TEST DATASET: " + testFileName);
            System.out.println("SAMPLE SIZE: " + size + "    SHIFT SIZE: " + size);
            Comparison comparison = new Comparison(trainFileName, testFileName);
            row = comparison.evaluateClassifiersProbability(size, size);
            System.out.println("------------- Hidden Markov ------------");
            row.add(0, evaluateHMM(hbmFileName, testFileName, size, size));
            table.put(size, row);

        }
        System.out.println("Table: " + table);
        for(Integer i: table.keySet()) {
            System.out.print(i + ";");
            for(Double d: table.get(i)) {
                System.out.print(d + ";");
            }
            System.out.println("");
        }

    }

    private void compareModelsTable(final String hmmFileName, final String trainFileName, final String testFileName, int size) {
        Map<Integer, List<Double>> table = new HashMap<>();
        List<Double> row;
//        for(int i = size; i <= shift; i++) {
//            row = new ArrayList<>();
            System.out.println("TRAINING DATASET: " + trainFileName);
            System.out.println("TEST DATASET: " + testFileName);
            System.out.println("SAMPLE SIZE: " + size + "    SHIFT SIZE: " + size);
            Comparison comparison = new Comparison(trainFileName, testFileName);
//            row = comparison.evaluateClassifiers(i, i);
            row = comparison.evaluateClassifiersProbability(size, size);
            System.out.println("------------- Hidden Markov ------------");
            row.add(0, evaluateHMM(hmmFileName, testFileName, size, size));
            table.put(size, row);
//        }
        System.out.println("Table: " + table);
        for(Integer i: table.keySet()) {
            System.out.print(i + ";");
            for(Double d: table.get(i)) {
                System.out.print(d + ";");
            }
            System.out.println("");
        }
    }


    private void createHMM(String trainFileName, String hmmFileName, int nodes, int iterations, int sampleSize) {
        Environment environment = new Environment(trainFileName, HEADER_CLASS_NAME, nodes, iterations, sampleSize);

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

    private double evaluateHMM(final String hmmFileName, final String testFileName, int step, int shift) {
        long total = 0, success = 0;
        try {
            Environment environment = Environment.readEnvironmentFromFile(hmmFileName);
            CSVReader csvReader = new CSVReader(testFileName, HEADER_CLASS_NAME);
            List<String> waps = csvReader.getHeaderNames();
            List<String> locations = csvReader.getLocations();
            Collections.sort(locations);
            List<String> trainLocations = environment.getLocations();
            Map<String, List<Integer>> allMeasures = new HashMap<>();
            Map<String, List<Integer>> measures = new HashMap<>();
            Matrix<String, String, Integer> confusion = new Matrix<>();
            for(String row: locations) {
                for(String column: locations) {
                    confusion.put(row, column, 0);
                }
            }
//            String estimatedLocation = "";
            Estimate estimatedLocation;
            double error = 0;
            for (String location : locations) {
                if (trainLocations.contains(location)) {
                    for (String wap : waps) {
                        allMeasures.put(wap, csvReader.getDataLocationWAP(location, wap));
                    }
                    for (int i = 0; i <= allMeasures.get(waps.get(0)).size() - step; i += shift) {
                        total++;
                        measures = new HashMap<>();
                        for (String wap : waps) {
                            measures.put(wap, allMeasures.get(wap).subList(i, i + step));
                        }
                        estimatedLocation = environment.estimateLocationProbability(measures);
                        if (estimatedLocation.label.equals(location)) {
                            success++;
                            error += estimatedLocation.probability;
                        }
                        int previous = 0;
                        if (confusion.get(location, estimatedLocation.label) != null)
                            previous = confusion.get(location, estimatedLocation.label);
                        confusion.put(location, estimatedLocation.label, previous + 1);
                    }
                }
            }
            System.out.println("Total:" + total + ", success: " + success + " (" + (success * 100.0 / total) + "%)" + " probability: " + error/success);
            System.out.println(formatMatrix(confusion));
            metrics(confusion, locations);
        } catch (IOException e) {
            System.out.println("IO Error");
        }
        return success * 100.0 / total;
    }

    private void compareModels(final String hmmFileName, final String trainFileName, final String testFileName, int step, int shift) {
        System.out.println("TRAINING DATASET: " + trainFileName);
        System.out.println("TEST DATASET: " + testFileName);
        System.out.println("SAMPLE SIZE: " + step + "    SHIFT SIZE: " + shift);
        System.out.println("------------- Hidden Markov ------------");
        evaluateHMM(hmmFileName, testFileName, step, shift);
        Comparison comparison = new Comparison(trainFileName, testFileName);
        comparison.evaluateClassifiers(step, shift);
//        comparison.evaluateClassifiersProbability(step, shift);
    }

    private void compareModelsTable(final String hmmFileName, final String trainFileName, final String testFileName, int step, int shift) {
        Map<Integer, List<Double>> table = new HashMap<>();
        List<Double> row;
        for(int i = step; i <= shift; i++) {
//            row = new ArrayList<>();
            System.out.println("TRAINING DATASET: " + trainFileName);
            System.out.println("TEST DATASET: " + testFileName);
            System.out.println("SAMPLE SIZE: " + i + "    SHIFT SIZE: " + i);
            Comparison comparison = new Comparison(trainFileName, testFileName);
//            row = comparison.evaluateClassifiers(i, i);
            row = comparison.evaluateClassifiersProbability(i, i);
            System.out.println("------------- Hidden Markov ------------");
            row.add(0, evaluateHMM(hmmFileName, testFileName, i, i));
            table.put(i, row);
        }
        System.out.println("Table: " + table);
        for(Integer i: table.keySet()) {
            System.out.print(i + ";");
            for(Double d: table.get(i)) {
                System.out.print(d + ";");
            }
            System.out.println("");
        }
    }

    private void metrics(final Matrix<String, String, Integer> confusion, final List<String> locations) {
        long tp, fn, fp, tn;
        double accuracy, precision, sensitivity, f1;
        StringBuffer sb = new StringBuffer();
        Formatter f = new Formatter(sb, Locale.US);

        f.format("%12s%12s%12s%12s%12s\n", "", "Accuracy", "Precision", "Sensitivity", "F1-score");
        for (String location : locations) {
            tp = fn = fp = tn = 0;
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
            f.format("%12s%12.3f%12.3f%12.3f%12.3f\n", location, accuracy, precision, sensitivity, f1);
        }
        System.out.println(sb.toString());
    }

    public String formatMatrix(Matrix<String, String, Integer> matrix) {
        List<String> rows = new ArrayList<>(matrix.getRows());
        Collections.sort(rows);

        StringBuffer sb = new StringBuffer();
        Formatter f = new Formatter(sb, Locale.US);

        f.format("%12s", "");
        for(String row: rows) {
            f.format("%12s", row);
        }
        f.format("\n");

        for(String row: rows) {
            f.format("%12s", row);
            for(String column: rows) {
                f.format("%12d", matrix.get(row, column));
            }
            f.format("\n");
        }

        return sb.toString();
    }

}
