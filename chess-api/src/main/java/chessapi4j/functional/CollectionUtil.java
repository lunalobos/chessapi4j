package chessapi4j.functional;

import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

final class StreamUtil {
    static Stream<Long> bitboardToStream(long bitboard){
        var spliterator = new BitboardSpliterator(bitboard);
        return StreamSupport.stream(spliterator, false);
    }
}

class BitboardSpliterator implements Spliterator<Long> {
    private long bitboard;

    public BitboardSpliterator(long bitboard) {
        this.bitboard = bitboard;
    }

    @Override
    public boolean tryAdvance(Consumer<? super Long> action) {
        if (bitboard != 0L) {
            long lsb = bitboard & -bitboard;
            bitboard &= ~lsb;
            action.accept(lsb);
            return true;
        }
        return false;
    }

    @Override
    public Spliterator<Long> trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        return Long.bitCount(bitboard);
    }

    @Override
    public int characteristics() {
        return DISTINCT | NONNULL | IMMUTABLE | SIZED | SUBSIZED;
    }
}
