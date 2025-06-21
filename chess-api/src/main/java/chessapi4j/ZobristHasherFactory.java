package chessapi4j;

import static chessapi4j.Square.*;
import static chessapi4j.Square.D2;
import static chessapi4j.Square.E2;
import static chessapi4j.Square.F2;
import static chessapi4j.Square.G2;
import static chessapi4j.Square.H2;

class ZobristHasherFactory {
    private static final LongProvider longProvider = new LongProvider();
    private static final ZobristHasher zobristHasher = new ZobristHasher(longProvider);
    static final long initialHash = zobristHasher.computeZobristHash(
            new long[]{
                    new Bitboard(A2, B2, C2, D2, E2, F2, G2, H2).getValue(),
                    new Bitboard(B1, G1).getValue(),
                    new Bitboard(C1, F1).getValue(),
                    new Bitboard(A1, H1).getValue(),
                    new Bitboard(D1).getValue(),
                    new Bitboard(E1).getValue(),
                    new Bitboard(A7, B7, C7, D7, E7, F7, G7, H7).getValue(),
                    new Bitboard(B8, G8).getValue(),
                    new Bitboard(C8, F8).getValue(),
                    new Bitboard(A8, H8).getValue(),
                    new Bitboard(D8).getValue(),
                    new Bitboard(E8).getValue()
            },
            true,
            true,
            true,
            true,
            true,
            -1
    );

    public static ZobristHasher instance() {
        return zobristHasher;
    }
}
