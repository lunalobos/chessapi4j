package chessapi4j;

// singleton bean
/**
 * @author lunalobos
 *
 * @since 1.2.9
 */
class BitboardGenerator {
    private static final Logger logger = LoggerFactory.getLogger(Generator.class);
    private PawnGenerator pawnGenerator;
    private KnightGenerator knightGenerator;
    private BishopGenerator bishopGenerator;
    private RookGenerator rookGenerator;
    private QueenGenerator queenGenerator;
    private KingGenerator kingGenerator;
    private VisibleMetrics visibleMetrics;
    private GeneratorUtil generatorUtil;
    private MatrixUtil matrixUtil;

    protected BitboardGenerator(PawnGenerator pawnGenerator, KnightGenerator knightGenerator,
            BishopGenerator bishopGenerator,
            RookGenerator rookGenerator, QueenGenerator queenGenerator, KingGenerator kingGenerator,
            VisibleMetrics visibleMetrics, GeneratorUtil generatorUtil, MatrixUtil matrixUtil) {
        this.pawnGenerator = pawnGenerator;
        this.knightGenerator = knightGenerator;
        this.bishopGenerator = bishopGenerator;
        this.rookGenerator = rookGenerator;
        this.queenGenerator = queenGenerator;
        this.kingGenerator = kingGenerator;
        this.visibleMetrics = visibleMetrics;
        this.generatorUtil = generatorUtil;
        this.matrixUtil = matrixUtil;
        logger.instanciation();
    }

    private CheckInfo isInCheckWhithMask(int kingPiece, long[] bits, long whiteMoveNumeric, int[] pawnsDirections) {
        long inCheckMask = 0L;
        int checkCount = 0;
        final int kingSquare = Long.numberOfTrailingZeros(bits[kingPiece - 1]);
        long isInCheck = 0L;
        final int aux = (int) (6L & (whiteMoveNumeric << 1 | whiteMoveNumeric << 2));
        final int[] enemies = { Piece.WP.ordinal() + aux, Piece.WN.ordinal() + aux, Piece.WB.ordinal() + aux,
                Piece.WR.ordinal() + aux, Piece.WQ.ordinal() + aux, Piece.WK.ordinal() + aux };
        // pawns directions

        for (int pawnDirection : pawnsDirections) {

            final long enemyPawnDangerLocation = 1L << pawnDirection;
            final long operation = ((bits[enemies[0] - 1] & enemyPawnDangerLocation) >>> pawnDirection);
            isInCheck = isInCheck | operation;

            inCheckMask = inCheckMask | new long[] { 0L, enemyPawnDangerLocation }[(int) operation];
            checkCount += (int) operation;
        }
        // kings directions (only used in test cases)

        long kingDirectionsBits = 0L;

        for (int square : matrixUtil.kingMatrix[kingSquare]) {
            kingDirectionsBits = kingDirectionsBits | (1L << square);
        }
        final long operation = kingDirectionsBits & bits[enemies[5] - 1];
        final long operation3 = (operation >>> Long.numberOfTrailingZeros(operation));
        isInCheck = isInCheck | operation3;
        checkCount += (int) operation3;
        // knight directions

        long knightDirectionsBits;
        for (int square : matrixUtil.knightMatrix[kingSquare]) {
            knightDirectionsBits = 1L << square;

            final long operation2 = ((knightDirectionsBits & bits[enemies[1] - 1]) >>> square);
            isInCheck = isInCheck | operation2;
            inCheckMask = inCheckMask | new long[] { 0L, knightDirectionsBits }[(int) operation2];
            checkCount += (int) operation2;
        }
        // bishops directions
        final long enemyBishopsAndQuens = bits[enemies[2] - 1] | bits[enemies[4] - 1];

        for (int i = 0; i < 4; i++) {
            final long visible = visibleMetrics.visibleSquares(bits, new int[] { i },
                    kingSquare, whiteMoveNumeric);

            final long isPresent = generatorUtil.hasBitsPresent(enemyBishopsAndQuens & visible);
            isInCheck = isInCheck | isPresent;
            inCheckMask = inCheckMask | new long[] { 0L, visible }[(int) isPresent];
            checkCount += (int) isPresent;
        }

        // rooks directions
        final long enemyRooksAndQuens = bits[enemies[3] - 1] | bits[enemies[4] - 1];
        for (int i = 4; i < 8; i++) {
            final long visible = visibleMetrics.visibleSquares(bits, new int[] { i }, kingSquare, whiteMoveNumeric);

            final long isPresent = generatorUtil.hasBitsPresent(enemyRooksAndQuens & visible);
            isInCheck = isInCheck | isPresent;
            inCheckMask = inCheckMask | new long[] { 0L, visible }[(int) isPresent];
            checkCount += (int) isPresent;
        }
        return new CheckInfo(isInCheck, inCheckMask, checkCount);
    }

    private long createCheckMask(int kingSquare, long enemies, long friends, long wm, long nextWhiteMove,
            long[] bitboards) {
        final long empty = ~(enemies | friends);
        final int[] enemyRookChoice = new int[] { Piece.WR.ordinal(), Piece.BR.ordinal() };
        final int[] enemyQueenChoice = new int[] { Piece.WQ.ordinal(), Piece.BQ.ordinal() };
        final int[] enemyBishopChoice = new int[] { Piece.WB.ordinal(), Piece.BB.ordinal() };
        final int enemyRook = enemyRookChoice[(int) wm];
        final int enemyQueen = enemyQueenChoice[(int) wm];
        final int enemyBishop = enemyBishopChoice[(int) wm];
        long checkMask = 0L;
        for (int j = 0; j < 4; j++) {
            final long visibleEmptyOrFriendsRD = visibleMetrics.visibleSquares(bitboards,
                    new int[] { matrixUtil.rookDirections[j] },
                    kingSquare, nextWhiteMove);
            final long friendsRD = visibleEmptyOrFriendsRD & ~empty;
            final long[] testBitsRD = new long[12];
            System.arraycopy(bitboards, 0, testBitsRD, 0, 12);
            for (int i = 0; i < 12; i++) {
                testBitsRD[i] = testBitsRD[i] & ~friendsRD;
            }
            final long visibleEmptyOrEnemyRD = visibleMetrics.visibleSquares(testBitsRD,
                    new int[] { matrixUtil.rookDirections[j] }, kingSquare, wm);
            final long enemiesThreadsRD = visibleEmptyOrEnemyRD
                    & (bitboards[enemyRook - 1] | bitboards[enemyQueen - 1]);
            final long[] choice = new long[] { 0L, friendsRD | visibleEmptyOrEnemyRD };
            checkMask = checkMask | choice[(int) generatorUtil.hasBitsPresent(enemiesThreadsRD)];
        }
        for (int j = 0; j < 4; j++) {
            final long visibleEmptyOrFriendsBD = visibleMetrics.visibleSquares(bitboards,
                    new int[] { matrixUtil.bishopDirections[j] },
                    kingSquare, nextWhiteMove);
            final long friendsBD = visibleEmptyOrFriendsBD & ~empty;
            final long[] testBitsBD = new long[12];
            System.arraycopy(bitboards, 0, testBitsBD, 0, 12);
            for (int i = 0; i < 12; i++) {
                testBitsBD[i] = testBitsBD[i] & ~friendsBD;
            }
            final long visibleEmptyOrEnemyBD = visibleMetrics.visibleSquares(testBitsBD,
                    new int[] { matrixUtil.bishopDirections[j] },
                    kingSquare, wm);
            final long enemiesThreadsBD = visibleEmptyOrEnemyBD
                    & (bitboards[enemyBishop - 1] | bitboards[enemyQueen - 1]);
            final long[] choice = new long[] { 0L, friendsBD | visibleEmptyOrEnemyBD };
            checkMask = checkMask | choice[(int) generatorUtil.hasBitsPresent(enemiesThreadsBD)];
        }
        return checkMask;
    }

    public final LegalMoveInfo generateMoveInfo(long[] bitboards, long wm, long wk, long wq, long bk, long bq,
            int enPassant) {

        final int aux = (int) (6L & (wm << 1 | wm << 2));
        final long friends = bitboards[Piece.BP.ordinal() - aux - 1] | bitboards[Piece.BN.ordinal() - aux - 1]
                | bitboards[Piece.BB.ordinal() - aux - 1] | bitboards[Piece.BR.ordinal() - aux - 1]
                | bitboards[Piece.BQ.ordinal() - aux - 1] | bitboards[Piece.BK.ordinal() - aux - 1];
        final long enemies = bitboards[Piece.WP.ordinal() + aux - 1] | bitboards[Piece.WN.ordinal() + aux - 1]
                | bitboards[Piece.WB.ordinal() + aux - 1] | bitboards[Piece.WR.ordinal() + aux - 1]
                | bitboards[Piece.WQ.ordinal() + aux - 1] | bitboards[Piece.WK.ordinal() + aux - 1];

        final long nextWhiteMove = (~wm) & 1L;
        final int whiteMove = (int) wm;

        final int kingPiece = matrixUtil.kings[whiteMove];

        final int kingSquare = Long.numberOfTrailingZeros(bitboards[kingPiece - 1]);

        final int[] pawnsDirections = new int[][] { matrixUtil.blackPawnMatrix2[kingSquare],
                matrixUtil.whitePawnMatrix2[kingSquare] }[(int) wm];
        final CheckInfo info = isInCheckWhithMask(kingPiece, bitboards, wm, pawnsDirections);

        long inCheckMask = info.getInCheckMask();

        final long checkMask = createCheckMask(kingSquare, enemies, friends, wm, nextWhiteMove, bitboards);
        final long[] choice = new long[] { -1L, inCheckMask, 0L, 0L, 0L, 0L };
        inCheckMask = choice[info.getCheckCount()];

        return fillChildrenList(bitboards, friends, enemies, wm, wk, wq, bk, bq, enPassant, checkMask, inCheckMask,
                nextWhiteMove, info.getInCheck(), matrixUtil.pawns[whiteMove], kingSquare,
                matrixUtil.knights[whiteMove], matrixUtil.bishops[whiteMove], matrixUtil.rooks[whiteMove],
                matrixUtil.queens[whiteMove], kingPiece, pawnsDirections, matrixUtil.pawnMatrix1[whiteMove],
                matrixUtil.pawnMatrix2[whiteMove]);
    }

    private LegalMoveInfo fillChildrenList(long[] bitboards, long friends, long enemies, long wm, long wk, long wq,
            long bk, long bq, int enPassant, long checkMask, long inCheckMask, long nextWhiteMove, long inCheck,
            int pawnPiece, int kingSquare, int knightPiece, int bishopPiece, int rookPiece, int queenPiece,
            int kingPiece, int[] pawnsDirections, int[][] matrix1, int[][] matrix2) {
        long lb;
        long j;
        var moveInfo = new LegalMoveInfo();

        // Pawn Moves
        j = bitboards[pawnPiece - 1];
        while (j != 0L) {
            lb = j & -j;
            var pawnMoves = pawnGenerator.pawnMoves(lb, Long.numberOfTrailingZeros(lb), pawnsDirections, pawnPiece,
                    matrix1, matrix2, kingSquare, enemies, friends, enPassant, wm, bitboards, checkMask, inCheckMask,
                    nextWhiteMove);
            moveInfo.appendPawnMove(pawnMoves);

            j = j & ~lb;
        }
        // Knight Moves
        j = bitboards[knightPiece - 1];
        while (j != 0L) {
            lb = j & -j;
            var knightMoves = knightGenerator.knightMoves(lb, Long.numberOfTrailingZeros(lb), knightPiece, enemies,
                    friends, checkMask, inCheckMask);
            moveInfo.appendKnightMove(knightMoves);
            j = j & ~lb;
        }
        // Bishop Moves
        j = bitboards[bishopPiece - 1];
        while (j != 0L) {
            lb = j & -j;
            var bishopMoves = bishopGenerator.bishopMoves(lb, Long.numberOfTrailingZeros(lb), kingSquare, enemies,
                    friends, checkMask, inCheckMask);
            moveInfo.appendBishopMove(bishopMoves);
            j = j & ~lb;
        }
        // Rook Moves
        j = bitboards[rookPiece - 1];
        while (j != 0L) {
            lb = j & -j;
            var rookMoves = rookGenerator.rookMoves(lb, Long.numberOfTrailingZeros(lb), rookPiece, kingSquare, enemies,
                    friends, checkMask, inCheckMask);
            moveInfo.appendRookMove(rookMoves);
            j = j & ~lb;
        }
        // Queen Moves
        j = bitboards[queenPiece - 1];
        while (j != 0L) {
            lb = j & -j;
            var queenMoves = queenGenerator.queenMoves(lb, Long.numberOfTrailingZeros(lb), queenPiece, kingSquare,
                    friends, enemies, checkMask, inCheckMask);
            moveInfo.appendQueenMove(queenMoves);
            j = j & ~lb;
        }
        // King Moves
        j = bitboards[kingPiece - 1];
        while (j != 0L) {
            lb = j & -j;
            var kingMoves = kingGenerator.kingMoves(Long.numberOfTrailingZeros(lb), kingPiece, enemies, friends,
                    inCheck, bitboards, wm == 1L, wk, wq, bk, bq);
            moveInfo.appendKingMove(kingMoves);
            j = j & ~lb;
        }
        return moveInfo;
    }

}
