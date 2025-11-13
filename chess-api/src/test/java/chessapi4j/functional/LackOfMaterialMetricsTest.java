package chessapi4j.functional;

import chessapi4j.Bitboard;
import chessapi4j.Piece;
import chessapi4j.Square;
import static chessapi4j.Piece.*;
import static chessapi4j.Square.*;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


public class LackOfMaterialMetricsTest {

    @Test
    void kvk(){
        var bitboards = bitboardsOf(e(WK, E1), e(BK, E8));
        assertTrue(Factory.container.lackOfMaterialMetrics.isLackOfMaterial(bitboards));
    }

    @Test
    void knvkn(){
        var bitboards = bitboardsOf(e(WK, E1), e(BK, E8), e(WN, G1), e(BN, G8));
        assertTrue(Factory.container.lackOfMaterialMetrics.isLackOfMaterial(bitboards));
    }

    @Test
    void knvk(){
        var bitboards = bitboardsOf(e(WK, E1), e(BK, E8), e(WN, G1));
        assertTrue(Factory.container.lackOfMaterialMetrics.isLackOfMaterial(bitboards));
    }

    @Test
    void kvkn(){
        var bitboards = bitboardsOf(e(WK, E1), e(BK, E8), e(BN, G8));
        assertTrue(Factory.container.lackOfMaterialMetrics.isLackOfMaterial(bitboards));
    }

    @Test
    void kbvkb(){
        var bitboards = bitboardsOf(e(WK, E1), e(BK, E8), e(WB, F1), e(BB, F8));
        assertTrue(Factory.container.lackOfMaterialMetrics.isLackOfMaterial(bitboards));
    }

    @Test
    void kbvk(){
        var bitboards = bitboardsOf(e(WK, E1), e(BK, E8), e(WB, F1));
        assertTrue(Factory.container.lackOfMaterialMetrics.isLackOfMaterial(bitboards));
    }

    @Test
    void kvkb(){
        var bitboards = bitboardsOf(e(WK, E1), e(BK, E8), e(BB, G8));
        assertTrue(Factory.container.lackOfMaterialMetrics.isLackOfMaterial(bitboards));
    }

    @Test
    void knvkb(){
        var bitboards = bitboardsOf(e(WK, E1), e(BK, E8), e(WN, G1), e(BB, F8));
        assertTrue(Factory.container.lackOfMaterialMetrics.isLackOfMaterial(bitboards));
    }

    @Test
    void kbvkn(){
        var bitboards = bitboardsOf(e(WK, E1), e(BK, E8), e(WB, F1), e(BN, G8));
        assertTrue(Factory.container.lackOfMaterialMetrics.isLackOfMaterial(bitboards));
    }

    @Test
    void kbbvk(){
        var bitboards = bitboardsOf(e(WK, E1), e(BK, E8), e(WB, F1), e(WB, C1));
        assertFalse(Factory.container.lackOfMaterialMetrics.isLackOfMaterial(bitboards));
    }

    private long[] bitboardsOf(Map.Entry<Piece, Square> ...entries){
        return Stream.of(entries)
                .reduce(new long[12], (b, entry) -> {
                    b[entry.getKey().ordinal() - 1] = b[entry.getKey().ordinal() - 1] |
                            new Bitboard(entry.getValue()).getValue();
                    return b;
                }, (b1, b2) -> {
                    var b = new long[12];
                    for(var i = 0; i < 12; i++){
                        b[i] = b1[i] | b2[i];
                    }
                    return b;
                });
    }

    private Map.Entry<Piece, Square> e(Piece piece, Square square){
        return Map.entry(piece, square);
    }
}
