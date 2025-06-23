package chessapi4j.functional;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

final class CollectionUtil {
    public static <T> List<T> bitboardToList(
            long bitboard,
            Function<Long, T> entitiesFactory,
            Supplier<List<T>> listFactory){
        var copy = bitboard;
        var list = listFactory.get();
        while(copy != 0L){
            var lowestOneBit = copy & -copy;
            list.add(entitiesFactory.apply(lowestOneBit));
            copy &= ~lowestOneBit;
        }
        return list;
    }
    public static <T> List<T> bitboardToCollectedList(
            long bitboard,
            Function<Long, List<T>> entitiesFactory,
            Supplier<List<T>> listFactory){
        var copy = bitboard;
        var list = listFactory.get();
        while(copy != 0L){
            var lowestOneBit = copy & -copy;
            list.addAll(entitiesFactory.apply(lowestOneBit));
            copy &= ~lowestOneBit;
        }
        return list;
    }
}