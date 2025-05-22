package chessapi4j;

import lombok.Data;

@Data
class LegalMoveInfo{
    private long moves;
    private long pawnMoves;
    private long knightMoves;
    private long bishopMoves;
    private long rookMoves;
    private long queenMoves;
    private long kingMoves;

    public void appendPawnMove(long move){
        moves |= move;
        pawnMoves |= move;
    }

    public void appendKnightMove(long move){
        moves |= move;
        knightMoves |= move;
    }

    public void appendBishopMove(long move){
        moves |= move;
        bishopMoves |= move;
    }

    public void appendRookMove(long move){
        moves |= move;
        rookMoves |= move;
    }

    public void appendQueenMove(long move){
        moves |= move;
        queenMoves |= move;
    }

    public void appendKingMove(long move){
        moves |= move;
        kingMoves |= move;
    }
}

