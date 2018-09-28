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

    public Location(String locationName, Map<String, List<Integer>> readings) {
        this.locationName = locationName;
        this.readings = readings;
        createHMMForAllWAP();
    }

    void createHMMForAllWAP() {
        for(String wap: readings.keySet()) {
            System.out.print("WAP: " + wap);
            hmms.put(wap, createHMMForWAP(readings.get(wap)));
        }
    }

    HMM<String, Integer> createHMMForWAP(List<Integer> wapReadings) {
        Random random = new Random(0);
//        int nodes = random.nextInt(3) + 2;
        int nodes = 3;
        System.out.println(", nodes: " + nodes);
        TabulatedCSVProbabilityEmitter emitter = new TabulatedCSVProbabilityEmitter(wapReadings);
        List<Integer> symbols = wapReadings.stream()
                .distinct()
                .collect(Collectors.toList());

        HMM<String, Integer> hmm =  new HMM<>(symbols);

        // Creates each node
        for(int i = 0; i < nodes; i++) {
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

        // Creates each initial node
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

        int iterations = 5;

        HMM<String, Integer> hmmEstimated = hmm.EM(emissionSet, wapReadings, iterations);
        return hmmEstimated;
    }

    double estimateLocationProbability(Map<String, List<Integer>> measures) {
        double probability = 1;
        for(String wap: measures.keySet()) {
            if(hmms.get(wap) != null) {
                probability *= hmms.get(wap).forward(measures.get(wap));
            }
        }
        return probability;
    }
}
