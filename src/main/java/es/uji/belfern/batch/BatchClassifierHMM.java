package es.uji.belfern.batch;

import es.uji.belfern.location.Environment;
import es.uji.belfern.statistics.Estimate;
import weka.core.Instance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BatchClassifierHMM implements BatchClassifier {
    Environment environment;
    private Instance firstInstance;

    public BatchClassifierHMM(Environment environment) {
        this.environment = environment;
    }

    @Override
    public Estimate estimate(final List<Instance> instances) throws Exception {
        firstInstance = instances.get(0);
        int numberOfWaps = firstInstance.numAttributes();
        String wap;
        Map<String, List<Integer>> measures = new HashMap<>();
        List<Integer> intensitiesForWap;

        for(int i = 0; i < numberOfWaps; i++) {
            wap = attributeName(i);
            intensitiesForWap = new ArrayList<>();
            for(Instance instance: instances) {
                intensitiesForWap.add((int)instance.value(i));
            }
            measures.put(wap, intensitiesForWap);
        }


        return environment.estimateLocationProbability(measures);
    }

    private String attributeName(final int index) {
        return firstInstance.attribute(index).name();
    }
}
