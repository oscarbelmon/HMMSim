package es.uji.belfern.data;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;


public class MatrixTest {
    @Test
    public void oneElementTest() {
        Matrix matrix = new Matrix<Integer, Integer, Double>();
        matrix.put(1, 1, 0.5);
        assertThat(matrix.get(1,1), is(0.5));
    }

    @Test
    public void sizeTest() {
        Matrix matrix = new Matrix<Integer, Integer, Double>();
        matrix.put(1, 1, 0.5);
        assertThat(matrix.size(), is(1L));
    }
}
