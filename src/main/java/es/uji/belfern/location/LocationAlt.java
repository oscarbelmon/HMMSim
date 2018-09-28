package es.uji.belfern.location;

import es.uji.belfern.hmm.HMM;
import es.uji.belfern.hmm.TabulatedCSVProbabilityEmitter;

import java.io.Serializable;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class LocationAlt implements Serializable {
    private transient final String locationName;
    private final HMM<String, Integer> hmm;
    private transient final List<Integer> readings;
    private transient final List<Integer> symbols;

    public LocationAlt(final String locationName, final List<Integer> readings, int iterations) {
        this.locationName = locationName;
        this.readings = readings;
        symbols = symbols();
        hmm = createHMM(iterations);
    }

    private List<Integer> symbols() {
        return readings.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    private HMM<String, Integer> createHMM(int iterations) {
        HMM<String, Integer> hmm = new HMM<>(symbols);
        Random random = new Random(0);
        int nodes = 3;
        TabulatedCSVProbabilityEmitter emitter = new TabulatedCSVProbabilityEmitter(readings);

        // Creates each node
        for (int i = 0; i < nodes; i++) {
            hmm.instanceNode(i+"", emitter);
        }

        // Creates each edge
        String start, end;
        double[] probabilities = new double[nodes];
        double accum = 0;
        for(int i = 0; i < nodes; i++) {
            start = i + "";
            for(int j = 0; j < nodes; j++) {
                probabilities[j] = random.nextDouble();
                accum += probabilities[j];
            }
            for(int j = 0; j  < nodes; j++) {
                probabilities[j] /= accum;
            }
            for(int j = 0; j < nodes; j++) {
                end = j + "";
                hmm.instanceEdge(start, end, probabilities[j]);
            }
        }

        // Creates only one initial node
        hmm.addInitialNode("0", 1);

        HMM<String, Integer> hmmEstimated = hmm.EM(symbols, readings, iterations);

        return hmmEstimated;
    }

    double estimateLocationProbability(List<Integer> measures) {
        return hmm.forward(measures);
    }
}
