package chessapi4j;

import static chessapi4j.Square.*;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is intended to represent a position and to be inmutable.
 * Inmutability makes the class thread-safe and brings a more clear overall view
 * of a program flow making this class preferable to {@link Position}.
 * 
 * @author lunalobos
 * @since 1.2.9
 */
public final class InmutablePosition implements Serializable {
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
    // checkmate
    private final boolean cm;
    // stalemate
    private final boolean sm;
    // fifty moves
    private final boolean fm;
    // lack of material
    private final boolean lm;

    // zobrist hash
    private final long zobristHash;
    // hash
    private final int hash;

    /**
     * Creates a new instance of {@link InmutablePosition} with the initial
     * position.
     */
    public InmutablePosition() {
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
        cm = false;
        sm = false;
        fm = false;
        lm = false;
        zobristHash = ZobristHasherFactory.instance().computeZobristHash(b, cm, sm, lm, fm, cm, ep);
        hash = ((Long) zobristHash).hashCode();
    }

    /**
     * Creates a new instance of {@link InmutablePosition} with the given FEN.
     * @param fen the fen string
     */
    public InmutablePosition(String fen) {
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
                                    "Invalid character %c in fen string".formatted(character));
                    }
                    i++;
                }
            }
        }
        // squares to botboards
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
        String enPassantPart = parts[3];
        if (enPassantPart.equals("-")) {
            ep = -1;
        } else {
            char[] chars = enPassantPart.toCharArray();
            String col = "" + chars[0];
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
        // half moves
        hm = Integer.parseInt(parts[4]);
        if (hm > 50 || hm < 0) {
            throw new IllegalArgumentException("Half moves has to be over -1 and bellow 51.");
        }

        // moves counter
        mc = Integer.parseInt(parts[5]);

        // checkmate
        cm = GeneratorFactory.checkmateMetrics.isCheckmate(b, wm == 1L, wk, wq, bk, bq, ep);

        // stalemate
        sm = GeneratorFactory.stalemateMetrics.isStalemate(b, wm == 1L, wk, wq, bk, bq, ep);

        // fifty moves
        fm = hm == 50;

        // lack of material
        lm = GeneratorFactory.lackOfMaterialMetrics.isLackOfMaterial(b);

        // zobrist hash
        zobristHash = ZobristHasherFactory.instance().computeZobristHash(b, cm, sm, lm, fm, cm, ep);

        // hash
        hash = ((Long) zobristHash).hashCode();
    }

    /**
     * Low level constructor for internal use.
     */
    protected InmutablePosition(long[] b, long wm, long wk, long wq, long bk, long bq, int ep, int mc, int hm, boolean cm,
            boolean sm, boolean fm, boolean lm, long zobristHash, int hash) {
        this.b = b;
        this.wm = wm;
        this.wk = wk;
        this.wq = wq;
        this.bk = bk;
        this.bq = bq;
        this.ep = ep;
        this.mc = mc;
        this.hm = hm;
        this.cm = cm;
        this.sm = sm;
        this.fm = fm;
        this.lm = lm;
        this.zobristHash = zobristHash;
        this.hash = hash;
    }

    public long[] bitboards() {
        return b;
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
     * castle
     * kingside and 0 that white can not castle kingside.
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
     * Checkmate boolean value, true if checkmate, false otherwise.
     * 
     * @return the checkmate boolean value
     */
    public boolean checkmate() {
        return cm;
    }

    /**
     * Stalemate boolean value, true if stalemate, false otherwise.
     * 
     * @return the stalemate boolean value
     */
    public boolean stalemate() {
        return sm;
    }

    /**
     * Fifty moves boolean value, true if fifty moves, false otherwise.
     * 
     * @return the fifty moves boolean value
     */
    public boolean fiftyMoves() {
        return fm;
    }

    /**
     * Lack of material boolean value, true if lack of material, false otherwise.
     * 
     * @return the lack of material boolean value
     */
    public boolean lackOfMaterial() {
        return lm;
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
        return GeneratorFactory.internalUtil.toFen(getSquares(), wm == 1L, wk == 1L, bk == 1L, wq == 1L, bq == 1L, ep, hm, mc);
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
    public final int[] getSquares() {
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

    /**
     * Zobrist hash
     * 
     * @return the zobrist hash
     */
    public long zobristHash() {
        return zobristHash;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public String toString() {
        int[] squares = getSquares();
        StringBuilder sb = new StringBuilder();
        sb.append("\n+---+---+---+---+---+---+---+---+ \n");
        for (int i = 7; i >= 0; i--) {
            for (int j = 0; j < 8; j++) {
                switch (Piece.values()[squares[i * 8 + j]]) {
                    case WP:
                        sb.append("| P ");
                        break;
                    case WN:
                        sb.append("| N ");
                        break;
                    case WB:
                        sb.append("| B ");
                        break;
                    case WR:
                        sb.append("| R ");
                        break;
                    case WQ:
                        sb.append("| Q ");
                        break;
                    case WK:
                        sb.append("| K ");
                        break;
                    case BP:
                        sb.append("| p ");
                        break;
                    case BN:
                        sb.append("| n ");
                        break;
                    case BB:
                        sb.append("| b ");
                        break;
                    case BR:
                        sb.append("| r ");
                        break;
                    case BQ:
                        sb.append("| q ");
                        break;
                    case BK:
                        sb.append("| k ");
                        break;
                    default:
                        sb.append("|   ");
                }
                if (j == 7) {
                    int row = i + 1;
                    sb.append("| " + row + "\n");
                    sb.append("+---+---+---+---+---+---+---+---+ \n");
                }
            }
        }
        sb.append("  a   b   c   d   e   f   g   h \n");
        sb.append("Fen: " + fen());
        return sb.toString();
    }

    /**
     * Converts this inmutable position to a {@code Position} object
     * @return a position object builded from this inmutable position
     */
    @SuppressWarnings("deprecation")
    public Position toPosition() {
        var bbCopy = new long[12];
        System.arraycopy(b, 0, bbCopy, 0, 12);
        return new Position(bbCopy, ep, wm, wk, bk, wq, bq, mc, hm, cm, sm, fm, false, lm);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
			return false;
		} else if (obj == this){
			return true;
		} else if (!(obj instanceof InmutablePosition)){
			return false;
		}		
		InmutablePosition o = (InmutablePosition) obj;
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
	 *
	 */
	public Piece getPiece(Square square) {
        var pieceType = 0;
		for (int i = 0; i < 12; i++) {
			var isPiece = (b[i] & (1L << square.ordinal())) >>> square.ordinal();
			pieceType += isPiece * (i + 1);
		}
		return Piece.values()[pieceType];
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
		return Util.longToList(b[piece.ordinal() - 1]).stream().map(l -> Long.numberOfTrailingZeros(l))
				.map(i -> Square.get(i)).collect(ShiftList::new, ShiftList::add, ShiftList::addAll);

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
	public boolean isGameOver() {
		return cm || sm || fm || lm;
	}

	/**
	 * Retrieves if the current position is a forced draw.
	 * <p>
	 * A position is considered a draw if it results from stalemate, the fifty-move
	 * rule, insufficient material, or threefold repetition.
	 *
	 * @return true if the position is a draw, false otherwise
	 * 
	 * @since 1.2.6
	 */

	public boolean isDraw() {
		return sm || fm || lm;
	}
}
