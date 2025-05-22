package chessapi4j;

class StalemateMetrics {
    private VisibleMetrics visibleMetrics;
    private BitboardGenerator bitboardGenerator;
    private InternalUtil internalUtil;

    public StalemateMetrics(VisibleMetrics visibleMetrics, BitboardGenerator bitboardGenerator, InternalUtil internalUtil) {
        this.visibleMetrics = visibleMetrics;
        this.bitboardGenerator = bitboardGenerator;
        this.internalUtil = internalUtil;
    }

    public boolean isStalemate(long[] bitboards, boolean isWhiteMove, long wk, long wq, long bk, long bq, int enPassant) {
        var friendsAndEnmies = internalUtil.friendsAndEnemies(bitboards, isWhiteMove);
        var friends = friendsAndEnmies[0];
        var enemies = friendsAndEnmies[1];
        var kingSquare = Long.numberOfTrailingZeros(
                bitboards[isWhiteMove ? Piece.WK.ordinal() - 1 : Piece.BK.ordinal() - 1]);
        var enemiesVisible = visibleMetrics.enemiesVisible(bitboards, friends, enemies);
        var kingVisible = visibleMetrics.visibleSquaresKing(kingSquare, friends);
        var isInCheck = (1L << kingSquare) & enemiesVisible ;
        var kingHasNoMoves = (kingVisible & enemiesVisible) == kingVisible;
        var legalMoves = bitboardGenerator.generateMoveInfo(bitboards, isWhiteMove? 1L : 0L, wk, wq, bk, bq, enPassant).getMoves();
        return (isInCheck == 0L) && kingHasNoMoves && (legalMoves == 0L);
    }

}
