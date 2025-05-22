package chessapi4j;

class CheckMetrics {
    private VisibleMetrics visibleMetrics;
    private InternalUtil internalUtil;

    public CheckMetrics(VisibleMetrics visibleMetrics, InternalUtil internalUtil) {
        this.visibleMetrics = visibleMetrics;
        this.internalUtil = internalUtil;
    }

    public boolean isInCheck(long[] bitboards, boolean isWhiteMove) {
        var friendsAndEnmies = internalUtil.friendsAndEnemies(bitboards, isWhiteMove);
        var friends = friendsAndEnmies[0];
        var enemies = friendsAndEnmies[1];
        var kingSquare = Long.numberOfTrailingZeros(
                bitboards[isWhiteMove ? Piece.WK.ordinal() - 1 : Piece.BK.ordinal() - 1]);
        var enemiesVisible = visibleMetrics.enemiesVisible(bitboards, friends, enemies);
        var kingVisible = visibleMetrics.visibleSquaresKing(kingSquare, friends) & (1L << kingSquare);
        return (kingVisible & enemiesVisible) != 0L;
    }

}
