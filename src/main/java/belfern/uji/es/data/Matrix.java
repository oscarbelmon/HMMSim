package belfern.uji.es.data;

import belfern.uji.es.hmm.Node;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Matrix<T, U, V> {
    private Map<T, Map<U, V>> matrix;

    public Matrix() {
        matrix = new HashMap<>();
    }

    public void put(T row, U column, V data) {
        if(matrix.containsKey(row) == false) {
            matrix.put(row, new HashMap<>());
        }
        matrix.get(row).put(column, data);
    }

    public V get(T row, U column) {
        if(matrix.containsKey(row) && matrix.get(row).containsKey(column))
            return matrix.get(row).get(column);
        else return null;
    }

    @Override
    public String toString() {
        return "Matrix{" +
                "matrix=" + matrix +
                '}';
    }
}
