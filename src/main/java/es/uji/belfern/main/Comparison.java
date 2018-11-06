package es.uji.belfern.main;

import es.uji.belfern.data.Matrix;
import weka.classifiers.Classifier;
import weka.classifiers.lazy.IBk;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.CSVLoader;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Comparison {
    private static String knnOptions = "-K 1 -W 0 -A \"weka.core.neighboursearch.LinearNNSearch -A \\\"weka.core.EuclideanDistance -R first-last\\\"\"";

    private Instances training;
    private Instances test;
    private IBk knn = new IBk();
    Map<String, List<Instance>> instancesMap = new HashMap<>();

    public Comparison(final String trainingFileName, final String testFileName) {
        super();
        try {
            initializeDataSets(trainingFileName, testFileName);
            initializeClassifiers();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void evaluateClassifiers(final int sampleSize, final int shift) {
//        Map<String, Integer> results = new HashMap<>();
        Instance instance;
        String estimatedClass;
        Matrix<String, String, Integer> confusion = new Matrix<>();
        for(int row = 0; row < test.classAttribute().numValues(); row++) {
            for(int column = 0; column < test.classAttribute().numValues(); column++) {
                confusion.put(test.classAttribute().value(row), test.classAttribute().value(column), 0);
            }
        }
        long total = 0, success = 0;

        List<String> locations = new ArrayList<>();
        for (int i = 0; i < test.classAttribute().numValues(); i++) {
            locations.add(test.classAttribute().value(i));
        }


        try {
            for(String location: locations) {
                List<Instance> instances = instancesMap.get(location);
                for(int i = 0; i < instances.size(); i++) {
                    instance = instances.get(i);
                    total++;
                    estimatedClass = getEstimatedClass(instance, knn);
                    if(estimatedClass.equals(location)) success++;
                    int previous = 0;
                    if (confusion.get(location, estimatedClass) != null)
                        previous = confusion.get(location, estimatedClass);
                    confusion.put(location, estimatedClass, previous + 1);

                }

            }
            System.out.println("Total:" + total + ", success: " + success + " (" + (success * 100.0 / total) + "%)");
            System.out.println(formatMatrix(confusion));
            metrics(confusion, locations);

        } catch (Exception e) {
            System.out.println("Mal");
        }
    }

    void evaluateClassifiers2(final int sampleSize, final int shift) {
//        Map<String, Integer> results = new HashMap<>();
        Instance instance;
        String estimatedClass;
        Matrix<String, String, Integer> confusion = new Matrix<>();
        for(int row = 0; row < test.classAttribute().numValues(); row++) {
            for(int column = 0; column < test.classAttribute().numValues(); column++) {
                confusion.put(test.classAttribute().value(row), test.classAttribute().value(column), 0);
            }
        }
        long total = 0, success = 0;

        List<String> locations = new ArrayList<>();
        for (int i = 0; i < test.classAttribute().numValues(); i++) {
            locations.add(test.classAttribute().value(i));
        }


        try {
            for(String location: locations) {
                List<Instance> instances = instancesMap.get(location);
                for(int i = 0; i < instances.size(); i++) {
                    instance = instances.get(i);
                    total++;
                    estimatedClass = getEstimatedClass(instance, knn);
                    if(estimatedClass.equals(location)) success++;
                    int previous = 0;
                    if (confusion.get(location, estimatedClass) != null)
                        previous = confusion.get(location, estimatedClass);
                    confusion.put(location, estimatedClass, previous + 1);

                }

            }
            System.out.println("Total:" + total + ", success: " + success + " (" + (success * 100.0 / total) + "%)");
            System.out.println(formatMatrix(confusion));
            metrics(confusion, locations);

        } catch (Exception e) {
            System.out.println("Mal");
        }
    }

    private String getEstimatedClass(final Instance instance, final Classifier classifier) throws Exception {
        int index = (int)classifier.classifyInstance(instance);
        return instance.classAttribute().value((int)index);
    }

    private String getActualClass(final Instance instance) {
        int numAttributes = test.get(0).numAttributes();
        return instance.classAttribute().value((int) instance.value(numAttributes));
    }

    private void initializeDataSets(final String trainingFileName, final String testFileName) throws IOException {
        training = csvLoader(trainingFileName);
        training.setClassIndex(training.numAttributes() - 1);
        test = csvLoader(testFileName);
        test.setClassIndex(test.numAttributes() - 1);

        for (int i = 0; i < test.classAttribute().numValues(); i++) {
            instancesMap.put(test.classAttribute().value(i), new ArrayList<>());
        }

        test.stream()
                .forEach(i -> instancesMap.get(i.classAttribute().value((int) i.value(i.numAttributes() - 1))).add(i));
    }

    private void initializeClassifiers() {
        initializeKnn();
    }

    private void initializeKnn() {
        try {
            knn.setOptions(Utils.splitOptions(knnOptions));
            knn.buildClassifier(training);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Instances csvLoader(String fileName) throws IOException {
        CSVLoader loader = new CSVLoader();
        loader.setSource(new File(fileName));
        return loader.getDataSet();
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
