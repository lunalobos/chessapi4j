package chessapi4j;

import java.util.HashSet;
import java.util.Random;

import org.junit.jupiter.api.Test;

public class MagicSearch {
    private static final int[][][] QUEEN_MEGAMATRIX = Util.QUEEN_MEGAMATRIX;

    // buscar números mágicos

    @Test
    void search(){
        // primero hay que precomputar todos los casos posibles  de piezas visibles, es un poco choto,
        // y además son miles de casos, pero en definitiva, no son todos los enteros largos?
        for(int i = 0; i < 10000000; i+=100000){
            iteration(i);
        }
    }

    private void iteration(int n){
        var values = new HashSet<Long>();
        var random = new Random();
        var visibleMetrics = new VisibleMetrics();
        
        for(int i = 0; i < n; i++){
            int square = random.nextInt(64);
            long friends = random.nextLong();
            long enemies = random.nextLong();
            enemies = enemies & ~friends;
            var directions = QUEEN_MEGAMATRIX[square];
            values.add(visibleMetrics.computeVisible(square, new int[]{0, 1, 2, 3, 4, 5, 6, 7},
                directions, friends, enemies));
        }
        System.out.println("Cantidad de bitboards: %d\nIteraciones: %d".formatted(values.size(), n));
    }
}
