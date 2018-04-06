package belfern.uji.es.hmm;

import belfern.uji.es.statistics.ProbabilityDensityFunction;

import java.util.*;

public class HMM<T, U> {
    Map<T, Node<T, U>> nodes;
    Map<Node<T,U>, Double> initialNodes;
//    List<Node<T,U>> viterbiPath;

    public HMM() {
        nodes = new LinkedHashMap<>();
        initialNodes = new LinkedHashMap<>();
    }

    public void addInitialNode(Node<T,U> node, double probability) {
        initialNodes.put(node, probability);
    }

    public Node<T, U> instanceNode(T id, Emitter<U> emitter) {
        Node<T, U> node = new Node<T, U>(id, emitter);
        nodes.put(id, node);
        return node;
    }

    public Edge<T, U> instanceEdge(Node<T, U> start, Node<T, U> end, ProbabilityDensityFunction pdf, double ratio) {
        Edge<T, U> edge = new Edge<T, U>(start, end, pdf);
        start.addEdge(edge, ratio);
        return edge;
    }

    public Edge<T, U> instanceEdge(String idStart, String idEnd, ProbabilityDensityFunction pdf, double ratio) {
        Node<T, U> start = nodes.get(idStart);
        Node<T, U> end = nodes.get(idEnd);
        return instanceEdge(start, end, pdf, ratio);
    }

    public List<U> generateSequence(long items) {
        List<U> sequence = new ArrayList<>();
        Node<T, U> currentNode = getInitialNode();

        for(long i = 0; i < items; i++) {
            sequence.add(currentNode.emmit());
            currentNode = currentNode.nextNode();
        }

        return sequence;
    }

    public void viterbi(List<U> symbols) {
        initializationViterbi(symbols.get(0));
        recursionViterbi(symbols);
        terminationViterbi();
    }

    void initializationViterbi(U symbol) {
//        viterbiPath = new ArrayList<>();
//        List<Commodity> commodities = new ArrayList<>();

        System.out.println("Start Itialization");
        nodes.values().stream()
                .forEach(node -> {
                    if (initialNodes.get(node) != null) {
                        node.viterbi = node.viterbiPrevious = node.emitter.getSymbolProbability(symbol) * initialNodes.get(node);
                    } else {
                        node.viterbi = node.viterbiPrevious = 0;
                    }
                    node.viterbiInit();
                    System.out.println(node.viterbi);
//                    commodities.add(new Commodity(node, node.viterbi));
//                    node.viterbiStepForward();
                });

        System.out.println("Stop Itialization");

//        viterbiPath.add(commodities.stream()
//                .max((a, b) -> Double.compare(a.viterbi, b.viterbi))
//                .get()
//                .node);

    }

//    class Commodity {
//        Node<T,U> node;
//        double viterbi;
//
//        public Commodity(Node<T, U> node, double viterbi) {
//            this.node = node;
//            this.viterbi = viterbi;
//        }
//    }

    void recursionViterbi(List<U> symbols) {
//        List<Commodity> commodities;
        for(int i = 1; i < symbols.size(); i++) {
            System.out.println("Step: " + i);
//            commodities = new ArrayList<>();
            for(Node<T, U> node: nodes.values()) {
//                commodities.add(new Commodity(node, node.viterbi(symbols.get(i))));
                node.viterbi(symbols.get(i));
            }
            for(Node<T, U> node: nodes.values()) {
                node.viterbiStepForward();
            }
//            viterbiPath.add(commodities.stream()
//                    .max((a, b) -> Double.compare(a.viterbi, b.viterbi))
//                    .get()
//                    .node);
        }

//        viterbiPath.stream()
//                .forEach(commodity -> System.out.println(commodity.id));
    }

    void terminationViterbi() {
//         return nodes.values().stream()
//                 .max((a, b) -> Double.compare(a.viterbi, b.viterbi))
//                 .get();
//        viterbiPath.stream()
//                .forEach(commodity -> System.out.print(commodity.id+","));
        Node<T,U> bestNode = nodes.values().stream()
                .max((a, b) -> Double.compare(a.viterbi, b.viterbi))
                .get();

//        bestNode.viterbiPath.stream()
//                .forEach(id -> System.out.print(id.node.id + ","));
//        System.out.println(bestNode.id);

        List<T> reverse = new ArrayList<>();
        Node<T,U> node = bestNode;
        reverse.add(node.id);
        for(int i = bestNode.viterbiPath.size(); i >0; i--) {
            node = node.viterbiPath.get(i-1).node;
            reverse.add(node.id);
        }

        for(int i = reverse.size()-1; i >= 0; i--)
            System.out.print(reverse.get(i) + ",");

//        nodes.values().stream()
//                .forEach(n -> {
//                    n.viterbiPath.stream()
//                            .forEach(id -> System.out.print(id.node.id + ","));
//                    System.out.println(bestNode.id);
//                });

    }

    public double forward(List<U> symbols) {
        initialization(symbols.get(0));
        recursion(symbols);
        return termination(symbols.get(symbols.size()-1));
    }

    void initialization(U symbol) {
        nodes.values().stream()
                .forEach(node -> {
                    if (initialNodes.get(node) != null) {
                        node.alfa = node.alfaPrevious = node.emitter.getSymbolProbability(symbol) * initialNodes.get(node);
                    } else {
                        node.alfa = node.alfaPrevious = 0;
                    }
                    node.stepForward();
                });
    }

    void recursion(List<U> symbols) {
        for(int i = 1; i < symbols.size(); i++) {
            for(Node<T, U> node: nodes.values()) {
                node.getProbabilityForSymbol(symbols.get(i));
            }
            for(Node<T, U> node: nodes.values()) {
                node.stepForward();
            }
        }
    }

    double termination(U symbol) {
        return nodes.values().stream()
                .mapToDouble(node -> node.alfa)
                .max()
                .getAsDouble();
    }

    private Node<T,U> getInitialNode() {
        double acc = 0;
        Map<Node<T,U>, Double> tmp = new LinkedHashMap<>();

        for(Map.Entry<Node<T,U>, Double> node: initialNodes.entrySet()) {
            acc += node.getValue();
            tmp.put(node.getKey(), acc);
        }

        Map<Node<T,U>, Double> tmpNormalized = new LinkedHashMap<>();

        for (Map.Entry<Node<T,U>, Double> node: tmp.entrySet()) {
            tmpNormalized.put(node.getKey(), tmp.get(node.getKey())/acc);
        }

        double probability = Math.random();
        Map.Entry<Node<T,U>, Double> entry = tmpNormalized.entrySet().stream()
                .filter(e -> e.getValue() > probability)
                .findFirst()
                .get();

        return entry.getKey();

    }
}
