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
    List<Maximum> maximums = new ArrayList<>();
    public double maxTrellisProbability;

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
        if(Double.isNaN(ratio)) {
            System.out.println("Error");
        }
        Edge<T, U> edge = new Edge<>(start, end, pdf);
        start.addEdge(edge, ratio);
        return edge;
    }

    public Edge<T, U> instanceEdge(Node<T, U> start, Node<T, U> end, double ratio) {
        if(Double.isNaN(ratio)) {
            System.out.println("Error");
        }
        Edge<T, U> edge = new Edge<>(start, end, ProbabilityDensityFunction.CONSTANT_PROBABILITY);
        start.addEdge(edge, ratio);
        return edge;
    }

    public Edge<T, U> instanceEdge(String idStart, String idEnd, ProbabilityDensityFunction pdf, double ratio) {
        if(Double.isNaN(ratio)) {
            System.out.println("Error");
        }
        Node<T, U> start = nodes.get(idStart);
        Node<T, U> end = nodes.get(idEnd);
        return instanceEdge(start, end, pdf, ratio);
    }

    public Edge<T, U> instanceEdge(T idStart, T idEnd, double ratio) {
        if(Double.isNaN(ratio)) {
            System.out.println("Error");
        }
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
        double result = terminationForward();
        if(Double.isNaN(result)) {
            System.out.println("Otro cachis");
        }
        return result;
    }

    void initializationForward(U symbol) {
        nodes.values()
                .forEach(node -> {
                    if (initialNodes.get(node) != null) {
                        node.alfas = new ArrayList<>();
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
//                System.out.println("Node.alfa: " + node.alfa);
            }
            for (Node<T, U> node : nodes.values()) {
                node.stepForward();
            }
        }
    }

    double terminationForward() {
        return nodes.values().stream()
                .mapToDouble(node -> node.alfa)
                .sum();
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
                .sum();
    }

    private void setEmissionsAndNodes(List<U> emissionSet, HMM<T, U> hmm) {
        Node<T, U> node;
        TabulatedProbabilityEmitter emitter;
        double ratio;
        for (Map.Entry<T, Node<T, U>> entry : nodes.entrySet()) {
            node = entry.getValue();
            emitter = new TabulatedProbabilityEmitter();
            for (U symbol : emissionSet) {
//                System.out.println("Emission: " + matrixEmissions.get(node, symbol));
                if(Double.isNaN(matrixEmissions.get(node, symbol))) ratio = 0;
                else ratio = matrixEmissions.get(node, symbol);
//                emitter.addEmission(symbol, matrixEmissions.get(node, symbol));
                emitter.addEmission(symbol, ratio);
            }
            hmm.instanceNode(node.id, emitter);
        }
    }

    // Improve: each edge is set twice
    private void setTransitions(HMM<T, U> hmm) {
        Node<T, U> start, end;
        double ratio;
        for (Map.Entry<T, Node<T, U>> entryStart : nodes.entrySet()) {
            start = entryStart.getValue();
            for (Map.Entry<T, Node<T, U>> entryEnd : nodes.entrySet()) {
                end = entryEnd.getValue();
//                System.out.println("Transition: " + matrixA.get(start, end));
                if(Double.isNaN(matrixA.get(start, end))) ratio = 0;
                else ratio = matrixA.get(start, end);
//                hmm.instanceEdge(start.id, end.id, matrixA.get(start, end));
                hmm.instanceEdge(start.id, end.id, ratio);
            }
        }
    }

    private void setInitialNodes(HMM<T, U> hmm) {
        Node<T, U> node;
        for (Map.Entry<T, Node<T, U>> entry : nodes.entrySet()) {
            node = entry.getValue();
            if (initialNodes.get(node) != null)
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
    public HMM<T, U> EM(List<U> emissionSet, List<U> observations, long iterations) {
        HMM hmm = this;
        for (int i = 0; i < iterations; i++) {
//            System.out.println("Iteration: " + i);
//            System.out.println("Forward.");
            hmm.forward(observations);
//            System.out.println("Backward.");
            hmm.backward(observations);

//            System.out.println("Matrix estimation.");
            hmm.estimateMatrixA(observations);
//            System.out.println(hmm.matrixA);

//            System.out.println("Emitions estimation");
            hmm.estimateEmissions(observations);
//            System.out.println(hmm.matrixEmissions);

//            System.out.println("Iterate.");
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

//        if(result == 0) {
//            System.out.println("Alfa: " + alfa + ", aif: " + aij + ", emission: " + emission + ", beta: " + beta);
//        }

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
    double etha(Node<T, U> i, Node<T, U> j, int pos, U symbol, int symbolsSize) {
        double num = numEtha(i, j, pos, symbol);
        double den = denEtha(i, j, symbolsSize);
//        double result = numEtha(i, j, pos, symbol) / denEtha(i, j, symbolsSize);
        double result = num / den;
//        System.out.println("Num: " + num + ", den: " + den + ", res: " + result);
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

    double numeratorGamma(Node<T, U> node, int pos) {
        return node.getAlfas().get(pos) * node.getBetas().get(pos);
    }

    double denominatorGamma(Node<T, U> node, List<U> symbols) {
        return denominatorEstimatedA(node, symbols);
    }

    double numeratorEmissionProbability(Node<T, U> node, U symbol, List<U> symbols) {
        double result = 0;

        double denominator = denominatorGamma(node, symbols);
        for (int pos = 0; pos < symbols.size(); pos++) {
            if (symbol.equals(symbols.get(pos))) {
                result += numeratorGamma(node, pos);
            }
        }
        result /= denominator;

        return result;
    }

    double denominatorEmissionProbability(Node<T, U> node, List<U> symbols) {
        double result = 0;

        for (int pos = 0; pos < symbols.size(); pos++) {
            result += numeratorGamma(node, pos);
        }
        result /=  denominatorGamma(node, symbols);

        return result;
    }

    void estimateEmissions(List<U> symbols) {
        double emissionProbability = 0;
        for (Map.Entry<T, Node<T, U>> entry : nodes.entrySet()) {
            Node<T, U> node = entry.getValue();
            double denominator = denominatorEmissionProbability(node, symbols);
            for (U symbol : symbols) {
                emissionProbability = numeratorEmissionProbability(node, symbol, symbols) / denominator;
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

    public void findMaxTrellis(List<U> emissionSet, int size) {
        List<Double> max = new ArrayList<>();
        for(int i = 0; i < emissionSet.size() - size; i++) {
            max.add(forward(emissionSet.subList(i, i + size)));
        }
        maxTrellisProbability = max.stream()
                .max(Double::compareTo)
                .orElse(1.0);
    }

    class Maximum {
        U symbol;
        double probability;

        Maximum(U symbol, double probability) {
            this.symbol = symbol;
            this.probability = probability;
        }

        @Override
        public String toString() {
            return "{"
                    + symbol +
                    ", " + probability +
                    '}';
        }
    }

}
// Bibliography
// [1] Speech and Language Processing. Daniel Jurafsky et al. (Chapter 9)

