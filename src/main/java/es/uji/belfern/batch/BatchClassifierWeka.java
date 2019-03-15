package es.uji.belfern.batch;

import es.uji.belfern.statistics.Estimate;
import weka.classifiers.Classifier;
import weka.core.Instance;

import java.util.Arrays;
import java.util.List;

public class BatchClassifierWeka implements BatchClassifier {
    Classifier classifier;

    public BatchClassifierWeka(Classifier classifier) {
        this.classifier = classifier;
    }

    @Override
    public Estimate estimate(final List<Instance> instances) throws Exception {
        int numClasses = instances.get(0).numClasses();

        double[] accumulated = new double[numClasses];
        for (int i = 0; i < accumulated.length; i++) accumulated[i] = 0;
        double[] distribution;
        for(Instance instance: instances) {
            distribution = classifier.distributionForInstance(instance);
            for(int i = 0; i < numClasses; i++) {
                accumulated[i] += distribution[i];
            }
        }
        int index = 0;
        double max = 0;
        for(int i = 0; i < numClasses; i++) {
            if(accumulated[i] > max) {
                max = accumulated[i];
                index = i;
            }
        }

//        Arrays.stream(accumulated)
//                .forEach(e -> System.out.println(e));

        return new Estimate(instances.get(0).classAttribute().value(index), max/instances.size());
    }
}
