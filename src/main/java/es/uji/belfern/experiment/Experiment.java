package es.uji.belfern.experiment;

import java.util.ArrayList;
import java.util.List;

public class Experiment {
    private final String algorithm;
    private final String trainDataSet;
    private final String testDataSet;
    private final List<SampleResult> results = new ArrayList<>();

    public static class SampleResult {
        final int index;
        final String trueClass;
        final String estimatedClass;
        final double probability;

        public SampleResult(int index, String trueClass, String estimatedClass, double probability) {
            this.index = index;
            this.trueClass = trueClass;
            this.estimatedClass = estimatedClass;
            this.probability = probability;
        }
    }

    public Experiment(String algorithm, String trainDataSet, String testDataSet) {
        this.algorithm = algorithm;
        this.trainDataSet = trainDataSet;
        this.testDataSet = testDataSet;
    }

    public void addSampleResult(final SampleResult sampleResult) {
        results.add(sampleResult);
    }
}
