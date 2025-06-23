/*
 * Copyright 2025 Miguel Angel Luna Lobos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://github.com/lunalobos/chessapi4j/blob/master/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package chessapi4j.functional;

import chessapi4j.Bitboard;
import chessapi4j.MovementException;
import chessapi4j.Piece;
import chessapi4j.Square;
import chessapi4j.Util;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.IntStream;

import static chessapi4j.Square.*;
/**
 * Instances of this class represents a chess position. This class is immutable so it is thread-safe and
 * functional friendly.
 * 
 * <p>An instance can be created in many ways: </p>
 * <ul>
 * <li>using the {@link Factory} class</li>
 * <li>using the {@link Generator} class</li>
 * <li>using the public constructor {@link #Position(String)}</li>
 * <li>using the {@link #children()} method</li>
 * <li>using the {@link #move(Move)} method</li>
 * <li>using the {@link #move(String)} method</li>
 * <li>using the {@link #sanMove(String)} method</li>
 * </ul>
 * 
 * <p>Due to the immutable nature of this class and the consequent thread-safety behavior, the
 * startpos can be reduced to a singleton instance. If you need the startpos you can just call {@link Factory#startPos()}.</p>
 * 
 * <p>The state of the position such as the check, checkmate, stalemate, lack of material or fifty moves situations can be
 * obtained using the corresponding methods.</p>
 * 
 * @author lunalobos
 * @since 1.2.9
 */
public final class Position implements Serializable {
    private static final long serialVersionUID = -1129123190893874561L;

    // bitboards for white and black pieces
    private final long[] b;
    // white moves
    private final long wm;
    // white castle kingside
    private final long wk;
    // white castle queenside
    private final long wq;
    // black castle kingside
    private final long bk;
    // black castle queenside
    private final long bq;
    // en passant (squate that can be capture using en passant rule)
    private final int ep;
    // moves counter
    private final int mc;
    // half moves counter for 50 move draw rule
    private final int hm;
    // check
    private volatile boolean c;
    // checkmate
    private volatile boolean cm;
    // stalemate
    private volatile boolean sm;
    // lack of material
    private volatile boolean lm;
    // zobrist hash
    private volatile long zobristHash;
    // hash
    private int hash;
    // move info
    private volatile MovesInfo mi;
    // children
    private final List<Tuple<Position,Move>> children = new BlockingList<>();

    // internal flags
    private volatile boolean movesInfoPresent = false;
    private volatile boolean cPresent = false;
    private volatile boolean cmPresent = false;
    private volatile boolean smPresent = false;
    private volatile boolean lmPresent = false;
    private volatile boolean zobritsPresent = false;
    private volatile boolean childrenPresent = false;
    /**
     * Creates a new instance of {@link Position} with the initial
     * position.
     */
    Position() {
        ep = -1;
        b = new long[12];
        b[Piece.WP.ordinal() - 1] = new Bitboard(A2, B2, C2, D2, E2, F2, G2, H2).getValue();
        b[Piece.WN.ordinal() - 1] = new Bitboard(B1, G1).getValue();
        b[Piece.WB.ordinal() - 1] = new Bitboard(C1, F1).getValue();
        b[Piece.WR.ordinal() - 1] = new Bitboard(A1, H1).getValue();
        b[Piece.WQ.ordinal() - 1] = new Bitboard(D1).getValue();
        b[Piece.WK.ordinal() - 1] = new Bitboard(E1).getValue();
        b[Piece.BP.ordinal() - 1] = new Bitboard(A7, B7, C7, D7, E7, F7, G7, H7).getValue();
        b[Piece.BN.ordinal() - 1] = new Bitboard(B8, G8).getValue();
        b[Piece.BB.ordinal() - 1] = new Bitboard(C8, F8).getValue();
        b[Piece.BR.ordinal() - 1] = new Bitboard(A8, H8).getValue();
        b[Piece.BQ.ordinal() - 1] = new Bitboard(D8).getValue();
        b[Piece.BK.ordinal() - 1] = new Bitboard(E8).getValue();
        wm = 1L;
        wk = 1L;
        wq = 1L;
        bk = 1L;
        bq = 1L;
        mc = 1;
        hm = 0;
        c = false;
        cPresent = true;
        cm = false;
        cmPresent = true;
        sm = false;
        smPresent = true;
        lm = false;
        lmPresent = true;
        zobristHash = Factory.initialHash;
        zobritsPresent = true;
        hash = ((Long) zobristHash).hashCode();
        var enemies = new Bitboard(A7, B7, C7, D7, E7, F7, G7, H7, B8, G8, C8, F8, A8, H8, D8, E8);
        mi = new MovesInfo(
                new Bitboard(A3, A4, B3, B4, C3, C4, D3, D4, E3, E4, F3, F4, G3, G4, H3, H4).getValue(),
                List.of(
                        new PawnMoves(Piece.WP, A2, enemies, new Bitboard(A3), new Bitboard(A4), new Bitboard(), new Bitboard()),
                        new PawnMoves(Piece.WP, B2, enemies, new Bitboard(B3), new Bitboard(B4), new Bitboard(), new Bitboard()),
                        new PawnMoves(Piece.WP, C2, enemies, new Bitboard(C3), new Bitboard(C4), new Bitboard(), new Bitboard()),
                        new PawnMoves(Piece.WP, D2, enemies, new Bitboard(D3), new Bitboard(D4), new Bitboard(), new Bitboard()),
                        new PawnMoves(Piece.WP, E2, enemies, new Bitboard(E3), new Bitboard(E4), new Bitboard(), new Bitboard()),
                        new PawnMoves(Piece.WP, F2, enemies, new Bitboard(F3), new Bitboard(F4), new Bitboard(), new Bitboard()),
                        new PawnMoves(Piece.WP, G2, enemies, new Bitboard(G3), new Bitboard(G4), new Bitboard(), new Bitboard()),
                        new PawnMoves(Piece.WP, H2, enemies, new Bitboard(H3), new Bitboard(H4), new Bitboard(), new Bitboard())
                ),
                List.of(
                        new RegularPieceMoves(Piece.WN, B1, enemies, new Bitboard(A3, C3)),
                        new RegularPieceMoves(Piece.WN, G1, enemies, new Bitboard(H3, F3))
                ),
                List.of(),
                List.of(),
                List.of(),
                new KingMoves(Piece.WK, E1, enemies, new Bitboard(), new Bitboard())
        );
        movesInfoPresent = true;
    }

    /**
     * Creates a new instance of {@link Position} with the given FEN.
     * @param fen the fen string
     */
    public Position(String fen) {
        // parse to squares
        int[] squares = new int[64];

        String[] parts = fen.split(" ");

        String[] rows = parts[0].split("/");
        List<Character> emptyPossibilities = new LinkedList<>(
                Arrays.asList(new Character[] { '1', '2', '3', '4', '5', '6', '7', '8' }));
        for (int h = 7; h >= 0; h--) {
            char[] chars = rows[7 - h].toCharArray();

            int i = 0;// colum counter

            for (char character : chars) {

                if (emptyPossibilities.contains(character)) {
                    for (int j = 0; j < Integer.parseInt(new StringBuilder().append(character).toString()); j++) {
                        i++;
                    }
                } else {
                    switch (character) {
                        case 'P':
                            squares[h * 8 + i] = Piece.WP.ordinal();
                            break;
                        case 'N':
                            squares[h * 8 + i] = Piece.WN.ordinal();
                            break;
                        case 'B':
                            squares[h * 8 + i] = Piece.WB.ordinal();
                            break;
                        case 'R':
                            squares[h * 8 + i] = Piece.WR.ordinal();
                            break;
                        case 'Q':
                            squares[h * 8 + i] = Piece.WQ.ordinal();
                            break;
                        case 'K':
                            squares[h * 8 + i] = Piece.WK.ordinal();
                            break;
                        case 'p':
                            squares[h * 8 + i] = Piece.BP.ordinal();
                            break;
                        case 'n':
                            squares[h * 8 + i] = Piece.BN.ordinal();
                            break;
                        case 'b':
                            squares[h * 8 + i] = Piece.BB.ordinal();
                            break;
                        case 'r':
                            squares[h * 8 + i] = Piece.BR.ordinal();
                            break;
                        case 'q':
                            squares[h * 8 + i] = Piece.BQ.ordinal();
                            break;
                        case 'k':
                            squares[h * 8 + i] = Piece.BK.ordinal();
                            break;
                        default:
                            throw new IllegalArgumentException(
                                    String.format("Invalid character %c in fen string",character));
                    }
                    i++;
                }
            }
        }
        // squares to bitboards
        b = new long[12];
        for (int i = 0; i < 64; i++) {
            if (squares[i] > 0) {
                b[squares[i] - 1] = b[squares[i] - 1] | (1L << i);
            }
        }

        // side to move
        wm = parts[1].equals("w") ? 1L : 0L;

        // castle rights
        String castlePart = parts[2];
        var wk_ = 0L;
        var bk_ = 0L;
        var wq_ = 0L;
        var bq_ = 0L;
        if (!castlePart.equals("-")) {
            char[] chars = castlePart.toCharArray();
            for (char character : chars) {
                switch (character) {
                    case 'K':
                        wk_ = 1L;
                        break;
                    case 'k':
                        bk_ = 1L;
                        break;
                    case 'Q':
                        wq_ = 1L;
                        break;
                    case 'q':
                        bq_ = 1L;
                        break;
                }
            }
        }
        wk = wk_;
        bk = bk_;
        wq = wq_;
        bq = bq_;

        // en passant
        var enPassantPart = parts[3];
        if (enPassantPart.equals("-")) {
            ep = -1;
        } else {
            char[] chars = enPassantPart.toCharArray();
            var col = "" + chars[0];
            int x = Util.getColIndex(col);
            int y;
            if (Integer.parseInt("" + chars[1]) == 6)
                y = 4;
            else if (Integer.parseInt("" + chars[1]) == 3)
                y = 3;
            else
                throw new IllegalArgumentException("Invalid en passant string");

            ep = Util.getSquareIndex(x, y);
        }

        // move info
        mi = Factory.container.bitboardGenerator.generateMoveInfo(b,wm, wk, wq, bk, bq, ep);
        movesInfoPresent = true;

        // half moves
        hm = Integer.parseInt(parts[4]);
        if (hm > 50 || hm < 0) {
            throw new IllegalArgumentException("Half moves has to be over -1 and bellow 51.");
        }

        // moves counter
        mc = Integer.parseInt(parts[5]);

        // check
        c = Factory.container.checkMetrics.inCheck(b, wm) == 1L;
        cPresent = true;

        // checkmate
        cm = Factory.container.checkmateMetrics.isCheckmate(b, wm == 1L, wk, wq, bk, bq, ep, mi.getMoves());
        cmPresent = true;

        // stalemate
        sm = Factory.container.stalemateMetrics.isStalemate(b, wm == 1L, wk, wq, bk, bq, ep, mi.getMoves());
        smPresent = true;

        // lack of material
        lm = Factory.container.lackOfMaterialMetrics.isLackOfMaterial(b);
        lmPresent = true;

        // zobrist hash
        zobristHash = Factory.zobristHasher.computeZobristHash(b, cm, sm, lm, hm == 50, cm, ep);
        zobritsPresent = true;
        // hash
        hash = ((Long) zobristHash).hashCode();
    }

    /**
     * Low level constructor for internal use.
     */
    Position(long[] b, long wm, long wk, long wq, long bk, long bq, int ep, int mc, int hm) {
        this.b = b;
        this.wm = wm;
        this.wk = wk;
        this.wq = wq;
        this.bk = bk;
        this.bq = bq;
        this.ep = ep;
        this.mc = mc;
        this.hm = hm;

        // check set to true for magic performance
        c = true;

        // checkmate set to true for magic performance
        cm = true;

        // stalemate set to true for magic performance
        sm = true;

        // lack of material set to true for magic performance
        lm = true;

        
    }

    MovesInfo movesInfo(){
        if (!movesInfoPresent) {
            mi = Factory.container.bitboardGenerator.generateMoveInfo(b, wm, wk, wq, bk, bq, ep);
            movesInfoPresent = true;
        }
        return mi;
    }

    private boolean internalCheck(){
        if(movesInfoPresent && cPresent){
            return c;
        } else if(movesInfoPresent){
            c = Factory.container.checkMetrics.inCheck(b, wm) == 1L;
            cPresent = true;
            return c;
        } else {
            mi = Factory.container.bitboardGenerator.generateMoveInfo(b,wm, wk, wq, bk, bq, ep);
            movesInfoPresent = true;
            c = Factory.container.checkMetrics.inCheck(b, wm) == 1L;
            cPresent = true;
            return c;
        }
    }

    private boolean internalCheckmate(){
        if(movesInfoPresent && cmPresent){
            return cm;
        } else if(movesInfoPresent){
            cm = Factory.container.checkmateMetrics.isCheckmate(b, wm == 1L, wk, wq, bk, bq, ep,
                    mi.getMoves());
            cmPresent = true;
            return cm;
        } else {
            mi = Factory.container.bitboardGenerator.generateMoveInfo(b,wm, wk, wq, bk, bq, ep);
            movesInfoPresent = true;
            cm = Factory.container.checkmateMetrics.isCheckmate(b, wm == 1L, wk, wq, bk, bq, ep,
                    mi.getMoves());
            cmPresent = true;
            return cm;
        }
    }

    private boolean internalStalemate(){
        if(movesInfoPresent && smPresent){
            return sm;
        } else if(movesInfoPresent){
            sm = Factory.container.stalemateMetrics.isStalemate(b, wm == 1L, wk, wq, bk, bq, ep,
                    mi.getMoves());
            smPresent = true;
            return sm;
        } else {
            mi = Factory.container.bitboardGenerator.generateMoveInfo(b,wm, wk, wq, bk, bq, ep);
            movesInfoPresent = true;
            sm = Factory.container.stalemateMetrics.isStalemate(b, wm == 1L, wk, wq, bk, bq, ep,
                    mi.getMoves());
            smPresent = true;
            return sm;
        }
    }

    /**
     * Returns the bitboards representing this position as an array of longs.
     * The bitboards are ordered according to the {@code Piece} enum,
     * excluding any entry for empty squares for obvious reasons.
     *
     * @return an array of longs representing the bitboards for this position
     */
    public long[] bitboards() {
        return Util.copyBitboards(b);
    }

    /**
     * Side to move rights. If white moves return 1L, if black moves return 0L.
     *
     * @return the side to move rights in long representation
     */
    public long wm() {
        return wm;
    }

    /**
     * Side to move rights. If white moves return true, if black moves return false.
     *
     * @return the side to move rights in boolean representation
     */
    public boolean whiteMove() {
        return wm == 1L;
    }

    /**
     * Returns the white castle kingside right as a long, meaning 1 that white can
     * castle kingside and 0 that white can not castle kingside.
     *
     * @return the white castle kingside right as a long
     */
    public long wk() {
        return wk;
    }

    /**
     * Returns true if white can castle kingside, false otherwise.
     *
     * @return the white castle kingside right as boolean
     */
    public boolean whiteCastleKingside() {
        return wk == 1L;
    }

    /**
     * Returns the black castle kingside right as a long, meaning 1 that black can
     * castle
     * kingside and 0 that black can not castle kingside.
     *
     * @return the black castle kingside right as a long
     */
    public long bk() {
        return bk;
    }

    /**
     * Returns true if black can castle kingside, false otherwise.
     *
     * @return the black castle kingside right as boolean
     */
    public boolean blackCastleKingside() {
        return bk == 1L;
    }

    /**
     * Returns the white castle queenside right as a long, meaning 1 that white can
     * castle queenside and 0 that white can not castle queenside.
     *
     * @return the white castle queenside right as a long
     */
    public long wq() {
        return wq;
    }

    /**
     * Returns true if white can castle queenside, false otherwise.
     *
     * @return the white castle queenside right as boolean
     */
    public boolean whiteCastleQueenside() {
        return wq == 1L;
    }

    /**
     * Returns the black castle queenside right as a long, meaning 1 that black can
     * castle queenside and 0 that black can not castle queenside.
     *
     * @return the black castle queenside right as a long
     */
    public long bq() {
        return bq;
    }

    /**
     * Returns true if black can castle queenside, false otherwise.
     *
     * @return the black castle queenside right as boolean
     */
    public boolean blackCastleQueenside() {
        return bq == 1L;
    }

    /**
     * Moves counter.
     *
     * @return the moves counter
     */
    public int movesCounter() {
        return mc;
    }

    /**
     * Half moves counter.
     *
     * @return the half moves counter
     */
    public int halfMovesCounter() {
        return hm;
    }

    /**
     * En passant square. This is the square index of the pawn that can be capture
     * using en passant rule. This is not the same square that is shown in fen
     * representation.
     *
     * @return the en passant square
     */
    public int enPassantSquare() {
        return ep;
    }

    /**
     * Check boolean value, true if this position is in check, false otherwise.
     * @return the check boolean value
     */
    public boolean check(){
        return cm && internalCheck();
    }

    /**
     * Checkmate boolean value, true if checkmate, false otherwise.
     *
     * @return the checkmate boolean value
     */
    public boolean checkmate() {
        return cm && internalCheckmate();
    }

    /**
     * Stalemate boolean value, true if stalemate, false otherwise.
     *
     * @return the stalemate boolean value
     */
    public boolean stalemate() {
        return sm && internalStalemate();
    }

    /**
     * Fifty moves boolean value, true if fifty moves, false otherwise.
     *
     * @return the fifty moves boolean value
     */
    public boolean fiftyMoves() {
        return hm == 50;
    }

    private boolean internalLackOfMaterial(){
        if (!lmPresent) {
            lm = Factory.container.lackOfMaterialMetrics.isLackOfMaterial(b);
            lmPresent = true;
        }
        return lm;
    }

    /**
     * Lack of material boolean value, true if lack of material, false otherwise.
     *
     * @return the lack of material boolean value
     */
    public boolean lackOfMaterial() {
        return lm && internalLackOfMaterial();
    }

    /**
     * Fen string representation.
     *
     * <p>
     * This method consumes computation
     * </p>
     *
     * @return the fen
     */
    public String fen() {
        return Factory.container.internalUtil.toFen(getSquares(), wm == 1L, wk == 1L,
                bk == 1L, wq == 1L, bq == 1L, ep, hm, mc);
    }

    /**
     * This method returns a 64 length array. It contains the piece value for each
     * square according to {@code Piece} ordinal values.
     * <p>
     * Each index represents a square, the value represents a piece
     * </p>
     * <p>
     *
     * {@code ---------Board---------}
     * </p>
     * <p>
     * {@code 56 57 58 59 60 61 62 63}
     * </p>
     * <p>
     * {@code 48 49 50 51 52 53 54 55}
     * </p>
     * <p>
     * {@code 40 41 42 43 44 45 46 47}
     * </p>
     * <p>
     * {@code 32 33 34 35 36 37 38 39}
     * </p>
     * <p>
     * {@code 24 25 26 27 28 29 30 31}
     * </p>
     * <p>
     * {@code 16 17 18 19 20 21 22 23}
     * </p>
     * <p>
     * {@code 08 09 10 11 12 13 14 15}
     * </p>
     * <p>
     * {@code 00 01 02 03 04 05 06 07}
     * </p>
     * <p>
     * This method consumes computation, squares array is not cached.
     * </p>
     *
     * @return the squares array
     */
    public int[] getSquares() {
        int[] squares = new int[64];
        for (int i = 0; i < 64; i++) {
            int piece = 0;
            for (int j = 1; j < 13; j++) {
                piece += j * (int) (((1L << i) & b[j - 1]) >>> i);
            }
            squares[i] = piece;
        }
        return squares;
    }

    private void internalZobristHash(){
        if (!zobritsPresent) {
            zobristHash = Factory.zobristHasher.computeZobristHash(b, cm, sm, lm, hm == 50, cm, ep);
            hash = ((Long) zobristHash).hashCode();
            zobritsPresent = true;
        }
    }

    /**
     * Zobrist hash. See 
     * <a href="https://www.chessprogramming.org/Zobrist_Hashing">Zobrist_Hashing</a>
     * @return the zobrist hash
     */
    public long zobristHash() {
        internalZobristHash();
        return zobristHash;
    }

    /**
     * For this class hashCode is identical to call {@link #zobristHash()}
     * and then apply the hash standard method to that value.
     * <pre><code>
     * var position = ...
     * var hash = ((Long)position.zobristHash()).hashCode();
     * </code></pre>
     *
     * @return the hashCode
     */
    @Override
    public int hashCode() {
        internalZobristHash();
        return hash;
    }

    @Override
    public String toString() {
        return Factory.container.internalUtil.stringRepresentation(getSquares(), fen());
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        } else if (obj == this){
            return true;
        } else if (!(obj instanceof Position)){
            return false;
        }
        Position o = (Position) obj;
        long op = 0L;
        for (int i = 0; i < 12; i++) {
            op = op | (b[i] ^ o.b[i]);
        }
        op = op | (wm ^ o.wm());
        op = op | (wk ^ o.wk());
        op = op | (wq ^ o.wq());
        op = op | (bk ^ o.bk());
        op = op | (bq ^ o.bq());
        op = op | (long) (ep ^ o.ep);
        return op == 0L;
    }

    /**
     * Retrieves the {@code Piece} object that represent the piece present in the
     * given {@code Square} object given as argument
     *
     * @param square the {@code Square} object
     * @return the {@code Piece} object that represent the piece present in the
     *         given square
     */
    public Piece getPiece(Square square) {
        return IntStream.range(0, b.length).mapToObj(i -> new IndexedValue<>(i, b[i])).collect(
                        () -> Accumulator.of(0),
                        (r, e) -> {
                            var absSignum = Long.signum(e.getValue() & (1L << square.ordinal())) *
                                    Long.signum(e.getValue() & (1L << square.ordinal()));
                            var p = (e.getIndex() + 1) * absSignum;
                            r.accumulate(p, (a, b) -> a | b);
                        },
                        (a, b) -> a.accumulate(b, (x, y) -> x | y))
                .map(Piece::get).getValue();
    }

    /**
     * Retrieves the {@code Bitboard} object that represent the positions of the
     * given {@code Piece}.
     *
     * @param piece the piece to search
     * @return the {@code Bitboard} object that represent the positions of the piece
     */
    public Bitboard getBitboard(Piece piece) {
        return new Bitboard(b[piece.ordinal() - 1]);
    }

    /**
     * Retrieves a {@code List} with all the squares where is a piece like the given
     * as argument.
     *
     * @param piece the piece to search
     * @return a {@code List} with all the squares where is a piece like the given
     *         as argument
     */
    public List<Square> getSquares(Piece piece) {
        return Util.longToList(b[piece.ordinal() - 1]).stream().map(Long::numberOfTrailingZeros)
                .map(Square::get).collect(BlockingList::new, BlockingList::add, BlockingList::addAll);
    }

    /**
     * Checks if the game is over.
     * <p>
     * The game is over if it is checkmate, stalemate, fifty moves, lack of material
     * or
     * repetitions.
     *
     * @return true if the game is over, false otherwise
     */
    public boolean gameOver() {
        return checkmate() || stalemate() || fiftyMoves() || lackOfMaterial();
    }

    /**
     * Retrieves if the current position is a forced draw.
     * <p>
     * A position is considered a draw if it results from stalemate, the fifty-move
     * rule, insufficient material, or threefold repetition.
     *
     * @return true if the position is a draw, false otherwise
     */
    public boolean draw() {
        return stalemate() || fiftyMoves() || lackOfMaterial();
    }


    /**
     * Retrieves the children of the current position in a {@code List} of {@code Tuple<Position, Move>} format.
     * @return the children
     */
    public List<Tuple<Position,Move>> children(){
        if(!childrenPresent) {
            children.addAll(Factory.generator().legalMoves(this));
            childrenPresent = true;
        }
        return children;
    }

    /**
     * Checks if the given move is legal for the current position.
     * @param move the move
     * @return true if the move is legal false otherwise
     */
    public boolean isLegal(Move move){
        return this.children().stream().anyMatch(t -> t.getV2().equals(move));
    }

    /**
     * Returns the position that results from the given move.
     * 
     * <p>The object returned is a new instance of this class.</p>
     * 
     * @param move the move
     * @return the position that results from the given move
     */
    public Position move(Move move){
        return children().stream().filter(t -> t.getV2().equals(move)).findFirst().map(Tuple::getV1)
            .orElseThrow(() -> new MovementException(move, this));
    }

    /**
     * Returns the position that results from the given move in Universal Chess Interface (UCI) format.
     * @param move the move
     * @return the position that results from the given move
     */
    public Position move(String move){
        var moveObj = Factory.move(move, wm == 1L);
        return move(moveObj);
    }

    /**
     * Returns the position that results from the given move in Standard Algebraic Notation (SAN) format.
     * @param move the move
     * @return the position that results from the given move
     */
    public Position sanMove(String move){
        var moveObj = PGNHandler.toUCI(this, move).orElseThrow(
            () -> new MovementException(String.format("move %s is not valid for position %s", move, this.fen()))
        );
        return move(moveObj);
    }
}

/**
 * @author lunalobos
 * @since 1.2.9
 */
@Data
@AllArgsConstructor
final class Accumulator<T> {

    public static <T> Accumulator<T> of(T value) {
        return new Accumulator<T>(value);
    }

    private T value;

    public void accumulate(T value, BinaryOperator<T> function) {
        this.value = function.apply(this.value, value);
    }

    public void accumulate(Accumulator<T> accumulator, BinaryOperator<T> function) {
        value = function.apply(this.value, accumulator.getValue());
    }

    public <R> Accumulator<R> map(Function<T,R> mapper){
        return of(mapper.apply(value));
    }
}
