package es.uji.belfern.batch;

import es.uji.belfern.statistics.Estimate;
import weka.classifiers.Classifier;
import weka.core.Instance;

import java.util.List;

public interface BatchClassifier {
//    Classifier classifier;
//
//    public BatchClassifier(Classifier classifier) {
//        this.classifier = classifier;
//    }

    public abstract Estimate estimate(final List<Instance> instances) throws Exception;
}
