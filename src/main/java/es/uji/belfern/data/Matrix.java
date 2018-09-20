package es.uji.belfern.data;

import java.util.HashMap;
import java.util.Map;

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
