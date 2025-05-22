package chessapi4j;

import java.util.Arrays;
import java.util.stream.IntStream;

public class LackOfMaterialMetrics {
    private final Integer[] pieceMask = new Integer[] { 1 << Piece.EMPTY.ordinal(), 1 << Piece.WP.ordinal(),
            1 << Piece.WN.ordinal(), 1 << Piece.WB.ordinal(), 1 << Piece.WR.ordinal(), 1 << Piece.WQ.ordinal(),
            1 << Piece.WK.ordinal(), 1 << Piece.BP.ordinal(), 1 << Piece.BN.ordinal(), 1 << Piece.BB.ordinal(),
            1 << Piece.BR.ordinal(), 1 << Piece.BQ.ordinal(), 1 << Piece.BK.ordinal(), };
    private final Integer[] material = new Integer[] {
            pieceMask[Piece.EMPTY.ordinal()] | pieceMask[Piece.WK.ordinal()] | pieceMask[Piece.BK.ordinal()], // K k
            pieceMask[Piece.WN.ordinal()] | pieceMask[Piece.BN.ordinal()] | pieceMask[Piece.WK.ordinal()]
                    | pieceMask[Piece.BK.ordinal()], // KN kn
            pieceMask[Piece.WN.ordinal()] | pieceMask[Piece.WK.ordinal()] | pieceMask[Piece.BK.ordinal()], // KN k
            pieceMask[Piece.BN.ordinal()] | pieceMask[Piece.WK.ordinal()] | pieceMask[Piece.BK.ordinal()], // K kn
            pieceMask[Piece.WN.ordinal()] | pieceMask[Piece.BB.ordinal()] | pieceMask[Piece.WK.ordinal()]
                    | pieceMask[Piece.BK.ordinal()], // KN kb
            pieceMask[Piece.WB.ordinal()] | pieceMask[Piece.BN.ordinal()] | pieceMask[Piece.WK.ordinal()]
                    | pieceMask[Piece.BK.ordinal()], // KB kn
            pieceMask[Piece.WB.ordinal()] | pieceMask[Piece.BB.ordinal()] | pieceMask[Piece.WK.ordinal()]
                    | pieceMask[Piece.BK.ordinal()], // KB kb
            pieceMask[Piece.WB.ordinal()] | pieceMask[Piece.WK.ordinal()] | pieceMask[Piece.BK.ordinal()], // KB k
            pieceMask[Piece.BB.ordinal()] | pieceMask[Piece.WK.ordinal()] | pieceMask[Piece.BK.ordinal()] // K kb
    };
    private final IntFunction[] bitCountFunctions = new IntFunction[] { i -> 0, i -> i,
            i -> 1 << Piece.values().length, i -> 1 << Piece.values().length, i -> 1 << Piece.values().length,
            i -> 1 << Piece.values().length, i -> 1 << Piece.values().length, i -> 1 << Piece.values().length,
            i -> 1 << Piece.values().length, i -> 1 << Piece.values().length, i -> 1 << Piece.values().length,
            i -> 1 << Piece.values().length };
    private final Integer[] inverse = new Integer[] { 1, 0 };

    public boolean isLackOfMaterial(long[] bitboards) {
        int m = IntStream.range(1, Piece.values().length)
                .map(i -> bitCountFunctions[Long.bitCount(bitboards[i - 1])].apply(pieceMask[i]))
                .reduce(0, (a, b) -> a | b);
        return Arrays.stream(material).mapToInt(i -> i).map(i -> inverse[Integer.signum(i ^ m)]).reduce(0,
                (a, b) -> a | b) == 1;
    }
}
