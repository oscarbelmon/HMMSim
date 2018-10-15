package es.uji.belfern.data;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class Matrix<T, U, V> implements Serializable {
    private Map<T, Map<U, V>> matrix;

    public Matrix() {
        matrix = new HashMap<>();
    }

    public void put(T row, U column, V data) {
        if (matrix.containsKey(row) == false) {
            matrix.put(row, new HashMap<>());
        }
        matrix.get(row).put(column, data);
    }

    public V get(T row, U column) {
        if (matrix.containsKey(row) && matrix.get(row).containsKey(column))
            return matrix.get(row).get(column);
        else return null;
    }

//    @Override
//    public String toString() {
//        return  matrix + "";
//    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        List<T> rows = new ArrayList<>(matrix.keySet());

        for (T row : rows) {
            sb.append(row);
            sb.append(matrix.get(row));
            sb.append("\n");
        }

        return sb.toString();
    }
}
