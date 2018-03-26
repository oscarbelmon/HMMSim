package belfern.uji.es.hmm;

public class MarkovChain<T> extends HMM<T, T> {
    public MarkovChain() {
        super();
    }

    @Override
    public Node<T, T> instanceInitialNode(T id, Emitter<T> emitter) {
//        initialNode = instanceNode(id, () -> id);
//        nodes.put(id, initialNode);
//        return initialNode;
        return super.instanceInitialNode(id, emitter);
    }

    @Override
    public Node<T, T> instanceNode(T id, Emitter<T> emitter) {
//        Node<T, T> node = new Node<T, T>(id, emitter);
//        nodes.put(id, node);
//        return node;
        return super.instanceNode(id, emitter);
    }

    public Node<T, T> instanceInitialNode(T id) {
        return instanceInitialNode(id, () -> id);
    }

    Node<T, T> instanceNode(T id) {
        return instanceNode(id, () -> id);
    }
}
