package chessapi4j;

class ZobristHasherFactory {
    private static final LongProvider longProvider = new LongProvider();
    private static final ZobristHasher zobristHasher = new ZobristHasher(longProvider);

    public static ZobristHasher instance() {
        return zobristHasher;
    }
}
