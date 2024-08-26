import chessapi4j.*;
import java.util.NoSuchElementException;

public class UtilExample {
    public static void main(String[] args) throws NoSuchElementException {
        // Create a valid position.
        final Position position = new Position("3q1r2/2p3kp/r2p1npQ/p1nPp3/1p2P1P1/5P2/PPP5/1NKR2NR b - - 1 17");

        // Let's use the Util class to check if white, the playing side, is in check (which it is).
        boolean inCheck = Util.isInCheck(position);
        System.out.printf("The position %s is %s check.\n", position.toFen(), inCheck ? "in" : "not in");

        // Let's check the visible squares for the white queen
        long visibleWQSquares = Util.visibleSquares(position, position.getSquares(Piece.WQ).stream().findFirst().orElseThrow());

        // Note that in this case, the visible squares do not include enemy pieces, and that's because white pieces 
        // don't move. This is a behavior of this method; only enemy pieces are visible if it's our turn.
        System.out.printf("%s\n", new Bitboard(visibleWQSquares));
    }
}