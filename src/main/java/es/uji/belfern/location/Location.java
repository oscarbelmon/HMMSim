package es.uji.belfern.location;

import es.uji.belfern.hmm.HMM;
import es.uji.belfern.hmm.TabulatedCSVProbabilityEmitter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class Location implements Serializable {
    private transient final String locationName;
    private Map<String, HMM<String, Integer>> hmms = new HashMap<>();
    private transient Map<String, List<Integer>> readings;
    private final int nodes;
    private final int iterations;

    public Location(String locationName, Map<String, List<Integer>> readings, final int nodes, final int iterations) {
        this.locationName = locationName;
        this.readings = readings;
        this.nodes = nodes;
        this.iterations = iterations;
        createHMMForAllWAP();
    }

    void createHMMForAllWAP() {
        for(String wap: readings.keySet()) {
//            System.out.print("WAP: " + wap);
            hmms.put(wap, createHMMForWAP(readings.get(wap)));
        }
    }

    HMM<String, Integer> createHMMForWAP(List<Integer> wapReadings) {
        Random random = new Random(0);
//        int nodes = random.nextInt(3) + 5;
//        System.out.println(", nodes: " + nodes);
        TabulatedCSVProbabilityEmitter emitter = new TabulatedCSVProbabilityEmitter(wapReadings);
        List<Integer> symbols = wapReadings.stream()
                .distinct()
                .collect(Collectors.toList());

        HMM<String, Integer> hmm =  new HMM<>(symbols);

        for(int i = 0; i < nodes; i++) {
            hmm.instanceNode(i+"", emitter);
        }

        String start, end;
        double accum;
        double[] probabilities;
        for(int i = 0; i < nodes; i++) {
            accum = 0;
            probabilities = new double[nodes];
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

        // Creates each initial node
        probabilities = new double[nodes];
        accum = 0;
        for(int i = 0; i < nodes; i++) {
            probabilities[i] = random.nextDouble();
            accum += probabilities[i];
        }
        for(int i = 0; i  < nodes; i++) {
            probabilities[i] /= accum;
        }
        for(int i = 0; i < nodes; i++) {
            start = i + "";
            hmm.addInitialNode(start, probabilities[i]);
        }

        List<Integer> emissionSet = wapReadings.stream()
                .distinct()
                .collect(Collectors.toList());

        HMM<String, Integer> hmmEstimated = hmm.EM(emissionSet, wapReadings, iterations);
        hmmEstimated.findMaxTrellis(emissionSet, 5);
        return hmmEstimated;
    }

    double estimateLocationProbability(Map<String, List<Integer>> measures) {
        double probability = 1, max, partial;
        for(String wap: measures.keySet()) {
            if(hmms.get(wap) != null) {
                max = hmms.get(wap).maxTrellisProbability;
//                partial = hmms.get(wap).forward(measures.get(wap));
                partial = hmms.get(wap).forwardScaled(measures.get(wap));
                if(partial > max) hmms.get(wap).maxTrellisProbability = max = partial;
                probability *= partial / max;
                if(Double.isNaN(probability)){
                    System.out.println("Underflow");
                    System.exit(0);
                }
            }
        }
        return probability;
    }
}
