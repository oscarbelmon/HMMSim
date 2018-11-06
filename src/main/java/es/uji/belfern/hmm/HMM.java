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
    List<Double> coso = new ArrayList<>();
//    List<U> trainingData = null;

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
        double result = terminationForward(symbols.size());
//        if(trainingData == null) trainingData = symbols;
        return result;
    }

    void initializationForward(U symbol) {
        coso = new ArrayList<>();
        nodes.values()
                .forEach(node -> {
                    node.alfas = new ArrayList<>();
                    if (initialNodes.get(node) != null) {
                        node.alfa = node.alfaPrevious = node.emitter.getSymbolProbability(symbol) * initialNodes.get(node);
                    } else {
                        node.alfa = node.alfaPrevious = 0;
                    }
                });

        double kk = nodes.values().stream()
                .mapToDouble(node -> node.alfa)
                .sum();

        coso.add(kk);

        nodes.values()
                .forEach(node -> node.stepForward(kk));
    }

    void recursionForward(List<U> symbols) {
        for (int i = 1; i < symbols.size(); i++) {
            double kk = 0;
            for (Node<T, U> node : nodes.values()) {
                node.alfa = node.getAlfaProbabilityForSymbol(symbols.get(i));
            }

            for(Node<T, U> node: nodes.values()) {
                kk += node.alfa;
            }
            coso.add(kk);

            for (Node<T, U> node : nodes.values()) {
                node.stepForward(kk);
            }
        }
    }

    double terminationForward(int size) {
        double product = 1;

        for(int i = 0; i < size; i ++) product *= coso.get(i); // Esto se puede sustituir por una suma de logaritmos.

        return product;
    }


    public double forwardScaled(List<U> symbols) {
        initializationForward(symbols.get(0));
        recursionForward(symbols);
        double result = terminationForwardScaled(symbols.size());
        if(Double.isNaN(result)) {
            System.out.println("Otro cachis");
        }
        return result;
    }

    double terminationForwardScaled(int numSymbols) {
        double product = 1;
        for(int i = 0; i < numSymbols; i++) {
            product *= coso.get(i);
        }
        return product;
    }


    double backwardTranssitions(List<U> symbols) {
        initializationBackward(symbols.get(symbols.size() - 1), symbols.size()-1); // The last symbol
        recursionBackwardTranssitions(symbols);
        return terminationBackward();
    }

    double backwardEmissions(List<U> symbols) {
        initializationBackward(symbols.get(symbols.size() - 1), symbols.size()-1); // The last symbol
        recursionBackwardEmissions(symbols);
        return terminationBackward();
    }

    void initializationBackward(U symbol, int pos) {
        nodes.values()
                .forEach(node -> {
                    node.betas = new ArrayList<>();
                    node.beta = node.betaNext = 1;
                });

        nodes.values()
                .forEach(node -> node.stepBackward(coso.get(pos)));
    }

    void recursionBackward(List<U> symbols, int shift) {
        for (int i = symbols.size() - 1; i > 0; i--) {
            for (Node<T, U> node : nodes.values()) {
                node.beta = node.getBetaProbabilityForSymbol(symbols.get(i));
            }

            for (Node<T, U> node : nodes.values()) {
                node.stepBackward(coso.get(i - shift));
            }
        }
        for (Node<T, U> node : nodes.values()) {
            Collections.reverse(node.getBetas());
        }
    }


    void recursionBackwardTranssitions(List<U> symbols) {
        recursionBackward(symbols, 1);
    }

    void recursionBackwardEmissions(List<U> symbols) {
        recursionBackward(symbols, 0);
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
                if(Double.isNaN(matrixEmissions.get(node, symbol))) ratio = 0;
                else ratio = matrixEmissions.get(node, symbol);
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
                if(Double.isNaN(matrixA.get(start, end))) ratio = 0;
                else ratio = matrixA.get(start, end);
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
    }

    private HMM iterate(List<U> emissionSet) {
        HMM hmm = new HMM(symbols);

        setEmissionsAndNodes(emissionSet, hmm);
        setTransitions(hmm);
        setInitialNodes(hmm);

        return hmm;
    }

    // Expectation maximization algorithm
    public HMM<T, U> EM(List<U> emissionSet, List<U> observations, long iterations) {
        HMM hmm = this;
        for (int i = 0; i < iterations; i++) {
            hmm.forward(observations);
            hmm.backwardTranssitions(observations);
            hmm.estimateMatrixA(observations);
            hmm.backwardEmissions(observations);
            hmm.estimateEmissions(observations);
            hmm = hmm.iterate(emissionSet);
        }
        return hmm;
    }

    private double numEtha(Node<T, U> i, Node<T, U> j, int pos, U symbol) {
        double alfa = i.getAlfas().get(pos);
        double aij = i.getProbabilityToNode(j);
        double emission = j.emitter.getSymbolProbability(symbol);
        double beta = j.getBetas().get(pos + 1);
        double result = alfa * aij * emission * beta;

        return result;
    }

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
        double result = num / den;
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

    public void findMaxTrellis(List<U> emissionSet, int size) {
        List<Double> max = new ArrayList<>();
        for(int i = 0; i < emissionSet.size() - size; i++) {
            max.add(forward(emissionSet.subList(i, i + size)));
        }
        maxTrellisProbability = max.stream()
                .max(Double::compareTo)
                .orElse(1.0);
    }

    // -2*log L + 2 * p
    public double AIC(List<U> symbols) {
        return -2 * Math.log(probabilityOfTrainingData(symbols)) + 2 * numberOfParameters();
    }

    // -2*log L + p * Log T, T is number of observations
    public double BIC(List<U> symbols) {
        return -2 * Math.log(probabilityOfTrainingData(symbols)) + numberOfParameters() * Math.log(symbols.size());
    }

    long numberOfParameters() {
        return numberParametersEmissions() + numberParametersTransitions();
    }

    long numberParametersEmissions() {
        return nodes.values().stream()
                .mapToLong(n ->n.emitter.size())
                .sum();
    }

    long numberParametersTransitions() {
        return nodes.values().stream()
                .mapToLong(n -> n.noneTrivialEdges())
                .sum();
    }

    double probabilityOfTrainingData(List<U> symbols) {
        return forward(symbols);
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

