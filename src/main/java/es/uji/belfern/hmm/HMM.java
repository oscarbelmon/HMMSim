package es.uji.belfern.hmm;

import es.uji.belfern.data.Matrix;
import es.uji.belfern.statistics.ProbabilityDensityFunction;

import java.io.Serializable;
import java.util.*;

public class HMM<T, U> implements Serializable {
    final Map<T, Node<T, U>> nodes;
    private Map<Node<T, U>, Double> initialNodes;
    private Matrix<Node<T, U>, Node<T, U>, Double> ethaMatrix;
    Matrix<Node<T, U>, Node<T, U>, Double> matrixA;
    Matrix<Node<T, U>, U, Double> matrixEmissions;
    Node<T, U> nodeMax;
    List<U> symbols;

    public HMM(List<U> symbols) {
        nodes = new LinkedHashMap<>();
        initialNodes = new LinkedHashMap<>();
        ethaMatrix = new Matrix<>();
        matrixA = new Matrix<>();
        matrixEmissions = new Matrix<>();
        this.symbols = symbols;
    }

    public void addInitialNode(Node<T, U> node, double probability) {
        initialNodes.put(node, probability);
    }

    public void addInitialNode(T id, double probability) {
        initialNodes.put(nodes.get(id), probability);
    }

    public Node<T, U> instanceNode(T id, Emitter<U> emitter) {
        Node<T, U> node = new Node<>(id, emitter);
        nodes.put(id, node);
        return node;
    }

    public Edge<T, U> instanceEdge(Node<T, U> start, Node<T, U> end, ProbabilityDensityFunction pdf, double ratio) {
        Edge<T, U> edge = new Edge<>(start, end, pdf);
        start.addEdge(edge, ratio);
        return edge;
    }

    public Edge<T, U> instanceEdge(Node<T, U> start, Node<T, U> end, double ratio) {
        Edge<T, U> edge = new Edge<>(start, end, ProbabilityDensityFunction.CONSTANT_PROBABILITY);
        start.addEdge(edge, ratio);
        return edge;
    }

    public Edge<T, U> instanceEdge(String idStart, String idEnd, ProbabilityDensityFunction pdf, double ratio) {
        Node<T, U> start = nodes.get(idStart);
        Node<T, U> end = nodes.get(idEnd);
        return instanceEdge(start, end, pdf, ratio);
    }

    public Edge<T, U> instanceEdge(T idStart, T idEnd, double ratio) {
        Node<T, U> start = nodes.get(idStart);
        Node<T, U> end = nodes.get(idEnd);
        return instanceEdge(start, end, ProbabilityDensityFunction.CONSTANT_PROBABILITY, ratio);
    }

    List<U> generateSequence(long items) {
        List<U> sequence = new ArrayList<>();
        Node<T, U> currentNode = getInitialNode();

        for (long i = 0; i < items; i++) {
            sequence.add(currentNode.emmit());
            currentNode = currentNode.nextNode();
        }

        return sequence;
    }

    public List<T> viterbi(List<U> symbols) {
        initializationViterbi(symbols.get(0));
        recursionViterbi(symbols);
        return terminationViterbi();
    }

    private void initializationViterbi(U symbol) {
        for (Node<T, U> node : nodes.values()) {
            if (initialNodes.get(node) != null) {
                node.viterbi = node.viterbiPrevious = node.emitter.getSymbolProbability(symbol) * initialNodes.get(node);
            } else {
                node.viterbi = node.viterbiPrevious = 0;
            }
            node.viterbiInit();
        }
    }

    private void recursionViterbi(List<U> symbols) {
        for (int i = 1; i < symbols.size(); i++) {
            for (Node<T, U> node : nodes.values()) {
                node.viterbi(symbols.get(i));
            }
            for (Node<T, U> node : nodes.values()) {
                node.viterbiStepForward();
            }
        }
    }

    private List<T> terminationViterbi() {
        Node<T, U> bestNode = nodes.values().stream()
                .max(Comparator.comparingDouble(a -> a.viterbi))
                .get();

        List<T> reverse = new ArrayList<>();
        Node<T, U> node = bestNode;
        reverse.add(node.id);
        for (int i = bestNode.viterbiPath.size(); i > 0; i--) {
            node = node.viterbiPath.get(i - 1).node;
            reverse.add(node.id);
        }

        Collections.reverse(reverse);

        return reverse;
    }

    public double forward(List<U> symbols) {
        initializationForward(symbols.get(0));
        recursionForward(symbols);
        return terminationForward();
    }

    void initializationForward(U symbol) {
        nodes.values()//.stream()
                .forEach(node -> {
                    if (initialNodes.get(node) != null) {
                        node.alfa = node.alfaPrevious = node.emitter.getSymbolProbability(symbol) * initialNodes.get(node);
                    } else {
                        node.alfa = node.alfaPrevious = 0;
                    }
                    node.stepForward();
                });
    }

    void recursionForward(List<U> symbols) {
        for (int i = 1; i < symbols.size(); i++) {
            for (Node<T, U> node : nodes.values()) {
//                System.out.println(symbols.get(i));
                node.alfa = node.getAlfaProbabilityForSymbol(symbols.get(i));
            }
            for (Node<T, U> node : nodes.values()) {
                node.stepForward();
            }
        }
    }

    double terminationForward() {
        return nodes.values().stream()
                .mapToDouble(node -> node.alfa)
                .max()
                .getAsDouble();
    }

    // length is the number of symbols in the sequence
    public double sequenceWithMaxProbability(int length) {
        initializationForwardMax();
        recursionForwardMax(length);
        return terminationForwardMax();
    }

    private void initializationForwardMax() {
        nodes.values()
                .forEach(node -> {
                    if (initialNodes.get(node) != null) {
                        node.alfaMax = node.alfaPreviousMax = node.emitter.getMaxProbability() * initialNodes.get(node);
                    } else {
                        node.alfaMax = node.alfaPreviousMax = 0;
                    }
                    node.stepForwardMax(node.emitter.getSymbolMaxProbability());
                });
    }

    private void recursionForwardMax(int length) {
        class Maximum {
            U symbol;
            double probability;

            Maximum(U symbol, double probability) {
                this.symbol = symbol;
                this.probability = probability;
            }
        }
        List<Maximum> probabilitySymbols;
        double probability;
        for (int i = 1; i < length; i++) {
            probabilitySymbols = new ArrayList<>();

            for (Node<T, U> node: nodes.values()) {
                for(U symbol: symbols) {
                    probability = node.getAlfaProbabilityForSymbol(symbol);
                    probabilitySymbols.add(new Maximum(symbol, probability));
                }
                Maximum max = probabilitySymbols.stream()
                        .max((a, b) -> a.probability > b.probability? 1 : -1)
                        .get();
                node.alfaMax = node.getAlfaProbabilityForSymbol(max.symbol);
                node.maxSymbols.add(max.symbol);
            }
        }
    }

    double terminationForwardMax() {
        double max = nodes.values().stream()
                .mapToDouble(node -> node.alfaMax)
                .max()
                .getAsDouble();

        nodeMax = nodes.values().stream()
                .filter(node -> node.alfaMax >= max)
                .findFirst()
                .orElse(null);

        return max;
    }

    double backward(List<U> symbols) {
        initializationBackward(symbols.get(symbols.size() - 1)); // The last symbol
        recursionBackward(symbols);
        return terminationBackward();
    }

    void initializationBackward(U symbol) {
        nodes.values()
                .forEach(node -> {
                    node.beta = node.betaNext = 1;
                    node.stepBackward();
                });
    }

    void recursionBackward(List<U> symbols) {
        for (int i = symbols.size() - 1; i > 0; i--) {
            for (Node<T, U> node : nodes.values()) {
                node.getBetaProbabilityForSymbol(symbols.get(i));
            }
            for (Node<T, U> node : nodes.values()) {
                node.stepBackward();
            }
        }
        for (Node<T, U> node : nodes.values()) {
            Collections.reverse(node.getBetas());
        }
    }

    double terminationBackward() {
        return nodes.values().stream()
                .mapToDouble(node -> node.beta)
                .max()
                .getAsDouble();
    }

    private void setEmissionsAndNodes(List<U> emissionSet, HMM<T, U> hmm) {
        Node<T, U> node;
        TabulatedProbabilityEmitter emitter;
        for (Map.Entry<T, Node<T, U>> entry : nodes.entrySet()) {
            node = entry.getValue();
            emitter = new TabulatedProbabilityEmitter();
            for (U symbol : emissionSet) {
//                System.out.println("Emission: " + matrixEmissions.get(node, symbol));
                emitter.addEmission(symbol, matrixEmissions.get(node, symbol));
            }
            hmm.instanceNode(node.id, emitter);
        }
    }

    // Improve: each edge is set twice
    private void setTransitions(HMM<T, U> hmm) {
        Node<T, U> start, end;
        for (Map.Entry<T, Node<T, U>> entryStart : nodes.entrySet()) {
            start = entryStart.getValue();
            for (Map.Entry<T, Node<T, U>> entryEnd : nodes.entrySet()) {
                end = entryEnd.getValue();
//                System.out.println("Transition: " + matrixA.get(start, end));
                hmm.instanceEdge(start.id, end.id, matrixA.get(start, end));
            }
        }
    }

    private void setInitialNodes(HMM<T, U> hmm) {
        Node<T, U> node;
        for (Map.Entry<T, Node<T, U>> entry : nodes.entrySet()) {
            node = entry.getValue();
            hmm.addInitialNode(hmm.nodes.get(node.id), initialNodes.get(node));
        }
//        hmm.initialNodes = this.initialNodes;
    }

    private HMM iterate(List<U> emissionSet) {
        HMM hmm = new HMM(symbols);

        setEmissionsAndNodes(emissionSet, hmm);
//        setNodes(hmm);
        setTransitions(hmm);
        setInitialNodes(hmm);

        return hmm;
    }

    // Expectation maximization algorithm
    public HMM<T, U> EM(List<U> emissionSet, List<U> observations, long interations) {
        HMM hmm = this;
        for (int i = 0; i < interations; i++) {
            hmm.forward(observations);
            hmm.backward(observations);

            hmm.estimateMatrixA(observations);
//            System.out.println(hmm.matrixA);

            hmm.estimateEmissions(observations);
//            System.out.println(hmm.matrixEmissions);

            hmm = hmm.iterate(emissionSet);
        }
        return hmm;
    }

    //    private double numEtha(Node<T, U> i, Node<T, U> j, U symbol) {
    private double numEtha(Node<T, U> i, Node<T, U> j, int pos, U symbol) {
        double alfa = i.getAlfas().get(pos);
        double aij = i.getProbabilityToNode(j);
        double emission = j.emitter.getSymbolProbability(symbol);
        double beta = j.getBetas().get(pos + 1);
        double result = alfa * aij * emission * beta;

        return result;
    }

    //    private double denEtha(Node<T, U> i, Node<T, U> j, U symbol) {
    private double denEtha(Node<T, U> i, Node<T, U> j, int symbolsSize) {
        double result = 0;

        for (int pos = 0; pos < symbolsSize; pos++) {
            result += i.getAlfas().get(pos) * i.getBetas().get(pos);
        }

        return result;
    }

    // Probablity of being at state i at time t and state j at time t+1.
    // Page 15 of [1]
//    double etha(Node<T, U> i, Node<T, U> j, U symbol) {
    double etha(Node<T, U> i, Node<T, U> j, int pos, U symbol, int symbolsSize) {
//        double result = numEtha(i, j, symbol) / denEtha(i, j, symbol);
        double result = numEtha(i, j, pos, symbol) / denEtha(i, j, symbolsSize);
        ethaMatrix.put(i, j, result);
        return result;
    }

    double numeratorEstimatedA(Node<T, U> i, Node<T, U> j, List<U> symbols) {
        double result = 0;

        for (int pos = 0; pos < symbols.size() - 1; pos++) {
            result += etha(i, j, pos, symbols.get(pos + 1), symbols.size());
        }

        return result;
    }

    double denominatorEstimatedA(Node<T, U> i, List<U> symbols) {
        double result = 0;
        for (int pos = 0; pos < symbols.size() - 1; pos++) {
            for (Node<T, U> toNode : i.getNodes()) {
                result += etha(i, toNode, pos, symbols.get(pos + 1), symbols.size());
            }
        }

        return result;
    }

    // Estimates just on matrix element
    double estimateAij(Node<T, U> i, Node<T, U> j, List<U> symbols) {
        double result = numeratorEstimatedA(i, j, symbols) / denominatorEstimatedA(i, symbols);
        matrixA.put(i, j, result);
        return result;
    }

    void estimateAForNode(Node<T, U> node, List<U> symbols) {
        node.nodes.keySet().stream().
                forEach(e -> estimateAij(node, e, symbols));
    }

    // Estimates the whole matrix
    public void estimateMatrixA(List<U> symbols) {
        nodes.entrySet().stream().
                map(n -> n.getValue()).
                forEach(node -> estimateAForNode(node, symbols));
    }

    //    double numeratorGamma(Node<T, U> node, U symbol) {
    double numeratorGamma(Node<T, U> node, int pos) {
        double result = 0;
//        result = node.getAlfaProbabilityForSymbol(symbol) * node.getBetaProbabilityForSymbol(symbol);
        result = node.getAlfas().get(pos) * node.getBetas().get(pos);
        return result;
    }

    double denominatorGamma(Node<T, U> node, List<U> symbols) {
        double result = 0;
        result = denominatorEstimatedA(node, symbols);
        return result;
    }

    // Eq. 9.42 of [1]
//    double gamma(Node<T, U> node, U symbol, List<U> symbols) {
    double gamma(Node<T, U> node, int pos, List<U> symbols) {
//        return numeratorGamma(node, symbol) / denominatorGamma(node, symbols);
        return numeratorGamma(node, pos) / denominatorGamma(node, symbols);
    }

    double numeratorEmissionProbability(Node<T, U> node, U symbol, List<U> symbols) {
        double result = 0;

        for (int pos = 0; pos < symbols.size(); pos++) {
            if (symbol.equals(symbols.get(pos))) {
                result += gamma(node, pos, symbols);
            }
        }

        return result;
    }

    double denominatorEmissionProbability(Node<T, U> node, List<U> symbols) {
        double result = 0;

        for (int pos = 0; pos < symbols.size(); pos++) {
            result += gamma(node, pos, symbols);
        }

        return result;
    }

    double emissionProbabilityNodeSymbol(Node<T, U> node, U symbol, List<U> symbols) {
        double result = 0;

        result = numeratorEmissionProbability(node, symbol, symbols) / denominatorEmissionProbability(node, symbols);

        return result;
    }

    void estimateEmissions(List<U> symbols) {
        double emissionProbability = 0;
        for (Map.Entry<T, Node<T, U>> entry : nodes.entrySet()) {
            Node<T, U> node = entry.getValue();
            for (U symbol : symbols) {
                emissionProbability = emissionProbabilityNodeSymbol(node, symbol, symbols);
                matrixEmissions.put(node, symbol, emissionProbability);
            }
        }
    }

    void matrixARandomInitialization() {
        for (Map.Entry<T, Node<T, U>> entry : nodes.entrySet()) {
            int size = entry.getValue().nodes.size();
            double sum = 0;
            double[] random = new double[size];
            for (int i = 0; i < size; i++) {
                random[i] = Math.random();
                sum += random[i];
            }
            for (int i = 0; i < size; i++) {
                random[i] /= sum;
            }
            int i = 0;
            for (Node<T, U> toNode : entry.getValue().getNodes()) {
                matrixA.put(entry.getValue(), toNode, random[i++]);
            }
        }
    }

    @Override
    public String toString() {
        return "HMM{" +
                "nodes=" + nodes +
                ", initialNodes=" + initialNodes() +
                '}';
    }

    private String initialNodes() {
        StringBuffer sb = new StringBuffer();

        for (Node<T, U> node : initialNodes.keySet()) {
            sb.append(node.id + "=");
            sb.append(initialNodes.get(node) + ", ");
        }

        return sb.toString();
    }

    private Node<T, U> getInitialNode() {
        double acc = 0;
        Map<Node<T, U>, Double> tmp = new LinkedHashMap<>();

        for (Map.Entry<Node<T, U>, Double> node : initialNodes.entrySet()) {
            acc += node.getValue();
            tmp.put(node.getKey(), acc);
        }

        Map<Node<T, U>, Double> tmpNormalized = new LinkedHashMap<>();

        for (Map.Entry<Node<T, U>, Double> node : tmp.entrySet()) {
            tmpNormalized.put(node.getKey(), tmp.get(node.getKey()) / acc);
        }

        double probability = Math.random();
        Map.Entry<Node<T, U>, Double> entry = tmpNormalized.entrySet().stream()
                .filter(e -> e.getValue() > probability)
                .findFirst()
                .get();

        return entry.getKey();

    }

    public double maxProbability(int sequenceLength) {
        List<U> sequence = new ArrayList<>();
        double result;
        final double maxInit = initialNodes.entrySet()
                .stream()
                .mapToDouble(entry -> entry.getValue())
                .max()
                .orElse(0);
//        System.out.println("MaxInit: " + maxInit);
        Node<T, U> current = initialNodes.entrySet()
                .stream()
                .filter(entry -> entry.getValue() >= maxInit)
                .map(entry -> entry.getKey())
                .findFirst()
                .orElse(null);
        sequence.add(current.emitter.getSymbolMaxProbability());

        double emitterProbability = current.emitter.getMaxProbability();
//        System.out.println("Emitter probability: " + emitterProbability);
        result = maxInit * emitterProbability;
//        result = 1;

        Node<T, U> next;
        for (int i = 0; i < sequenceLength - 1; i++) {
            final double partial;
            partial = current.nodes.entrySet()
                    .stream()
                    .mapToDouble(entry -> entry.getValue())
                    .max()
                    .orElse(0);
            emitterProbability = current.emitter.getMaxProbability();
            current = current.nodes.entrySet()
                    .stream()
                    .filter(entry -> entry.getValue() >= maxInit)
                    .map(entry -> entry.getKey())
                    .findFirst()
                    .orElse(null);
            sequence.add(current.emitter.getSymbolMaxProbability());
            result *= partial * emitterProbability;
        }
//        System.out.println("Max probability: " + result);
//        return result;
        return forward(sequence);
    }
}
// Bibliography
// [1] Speech and Language Processing. Daniel Jurafsky et al. (Chapter 9)

