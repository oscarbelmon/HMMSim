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

    public List<T> getRows() {
        return new ArrayList<>(matrix.keySet());
    }

    public long size() {
        long rows = matrix.keySet().size();
        System.out.println(matrix.keySet());
        if (matrix.keySet().iterator().hasNext()) {
            T element = matrix.keySet().iterator().next();
            long columns = matrix.get(element).size();
            System.out.println("Hola");
            return rows * columns;
        } else return 0;
    }

    @Override
    public String toString() {
        List<T> rows = new ArrayList<>(matrix.keySet());
        List<U> columns = new ArrayList<>(matrix.get(rows.get(0)).keySet());


        StringBuffer sb = new StringBuffer();
        Formatter f = new Formatter(sb, Locale.US);

        f.format("%12s", "");
        for(T row: rows) {
            f.format("%12s", row);
        }
        f.format("\n");

        for(T row: rows) {
            f.format("%12s", row);
            for(U column: columns) {
                f.format("%12d", get(row, column));
            }
            f.format("\n");
        }

        return sb.toString();
    }
}
