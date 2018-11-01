package es.uji.belfern.main;

import es.uji.belfern.data.Matrix;
import es.uji.belfern.location.Environment;
import es.uji.belfern.util.CSVReader;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

public class Main {
    private static final String HEADER_CLASS_NAME = "label";

    public static void main(String[] args) {
        if (args[0].equals("create")) { //[1] train_file [2] model_output_file [3] HMM_states [4] model_fit_iterations
            if (args.length != 5) System.out.println("Show usage.");
            else new Main().createHMM(args[1], args[2], Integer.parseInt(args[3]), Integer.parseInt(args[4]));
        } else if (args[0].equals("evaluate")) { // [1] model_output_file [2] test_file [3] sample_size [4] shift_size
            if (args.length != 5) System.out.println("Show usage.");
            if ((Integer.parseInt(args[3]) < Integer.parseInt(args[4]))) System.out.println("Shift size can not be greater than Sample size");
            else new Main().evaluateHMM(args[1], args[2], Integer.parseInt(args[3]), Integer.parseInt(args[4]));
        } else {
            System.out.println("Show usage.");
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

    private void evaluateHMM(final String hmmFileName, final String testFileName, int step, int shift) {
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
            long total = 0, success = 0;
            String estimatedLocation = "";
            for (String location : locations) {
                if (trainLocations.contains(location)) {
                    for (String wap : waps) {
                        allMeasures.put(wap, csvReader.getDataLocationWAP(location, wap));
                    }
                    for (int i = 0; i < allMeasures.get(waps.get(0)).size() - step; i += shift) {
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
            System.out.println("Total:" + total + ", success: " + success + " (" + (success * 100.0 / total) + "%)");
            System.out.println(formatMatrix(confusion));
            metrics(confusion, locations);
        } catch (IOException e) {
            System.out.println("IO Error");
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
