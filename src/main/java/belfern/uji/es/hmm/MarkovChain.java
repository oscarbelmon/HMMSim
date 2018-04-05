package belfern.uji.es.hmm;

public class MarkovChain<T> extends HMM<T, T> {
    public MarkovChain() {
        super();
    }

//    @Override
//    public Node<T, T> instanceInitialNode(T id, Emitter<T> emitter) {
//        return super.instanceInitialNode(id, emitter);
//    }

    @Override
    public Node<T, T> instanceNode(T id, Emitter<T> emitter) {
        return super.instanceNode(id, emitter);
    }

//    public Node<T, T> instanceInitialNode(T id) {
//        return instanceInitialNode(id, () -> id);
//    }

    Node<T, T> instanceNode(T id) {
        return instanceNode(id, () -> id);
    }
}
