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

import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * 
 * @author lunalobos
 * @since 1.2.9
 */
final class VisibleMetrics {
    final class MagicNumbers {

        private final Combinator combinator;
        final MagicHasher hasher;
        long[] magicNumbersArray;
        final Long[][] perfectHashMaps;
        private final FastFailLongMap map;
        private final int[] directionIndexes;
        private long[] numbers;

        public MagicNumbers(Combinator combinator, MagicHasher hasher, int capacity,
                int[] directionIndexes, long[] numbers) {
            this.combinator = combinator;
            this.hasher = hasher;
            perfectHashMaps = new Long[64][];
            map = new FastFailLongMap(capacity);
            this.directionIndexes = directionIndexes;
            this.numbers = numbers;
        }

        void calculate() {
            final Map<Integer, Long> magicNumbers = new HashMap<>();
            IntStream.range(0, 64).boxed()
                    .forEach(square -> {
                        var combinations = combinator.compute(square, directionIndexes);
                        while (true) { // don't worry about this loop, it will always converge
                            var magicNumber = numbers[square];
                            if (magicNumber < 0) {
                                magicNumber = -magicNumber;
                            }
                            map.clear();
                            var isMagic = true;
                            for (long combination : combinations) {
                                var directions = matrixUtil.queenMegamatrix[square];
                                var visible = computeVisible(square, directionIndexes, directions, 0L, combination);
                                var index = hasher.hash(combination, magicNumber, square);
                                if (map.containsKey(index)) {
                                    if (!map.get(index).equals(visible)) {
                                        isMagic = false;
                                    }
                                } else {
                                    map.put(index, visible);
                                }
                            }
                            if (isMagic) {
                                magicNumbers.put(square, magicNumber);
                                perfectHashMaps[square] = map.toLongArray();
                                break;
                            }
                        }
                    });
            magicNumbersArray = new long[magicNumbers.size()];
            for (var entry : magicNumbers.entrySet()) {
                magicNumbersArray[entry.getKey()] = entry.getValue();
            }

        }

        long visibleHashed(int square, long occupied) {
            var magic = magicNumbersArray[square];
            var hash = hasher.hash(occupied, magic, square);
            var result = perfectHashMaps[square][hash];
            return result == null ? 0L : result;
        }
    }

    private static final Logger logger = Factory.getLogger(VisibleMetrics.class);
    private static final long[] BISHOP_NUMBERS = new long[] {
            9180746499051547339L,
            8707807373265853253L,
            6890374909820324227L,
            5627801193431221999L,
            7070276170865304959L,
            8646047479734845597L,
            6970364831972632061L,
            8392904025588649759L,
            9165100239868844527L,
            7574600961932545601L,
            8231890758063327347L,
            5253712730057210803L,
            6311715373123481983L,
            6334105248820147973L,
            7211009014984146431L,
            8889425855821077019L,
            6574226960727251671L,
            6708565959103258043L,
            5178519028952829793L,
            5879483439702805337L,
            7486612042827253013L,
            8758447185029814091L,
            7189392548657180447L,
            6780047248211824439L,
            7929108997084741009L,
            5869828385829124279L,
            4777761137253294683L,
            5054787237607967851L,
            5834892739460910721L,
            8148705127734915707L,
            8636668779999915157L,
            8766366790746870541L,
            5899194859104552961L,
            5831126227585501889L,
            5979890708839720271L,
            6599232715290823837L,
            7549227303974068249L,
            5182668415790382497L,
            8923294130810616437L,
            5276224996548832559L,
            4794425474436064603L,
            7514650417736568737L,
            6199840546671067217L,
            7870580082374679457L,
            5389076785064634397L,
            7647560584267299979L,
            5229105129703969763L,
            8298546287603928523L,
            5139758389917592841L,
            4934888716358947091L,
            6075346491543767443L,
            9035089192323316819L,
            7518307732297911763L,
            8324337169270498819L,
            5289712152947913373L,
            6886207625641520513L,
            9020848015567691011L,
            6095561483492665153L,
            5101085567079109061L,
            7753684519139727811L,
            9051213235571841283L,
            6505215318015708773L,
            6264950850225518497L,
            8846374642034972683L
    };
    private static final long[] ROOK_NUMBERS = new long[] {
            4709623878237656957L,
            6865183052646244721L,
            6376349624237580539L,
            5297447463024482657L,
            9122807865927424703L,
            8012020613347644007L,
            6228879715230563381L,
            5540515467914588591L,
            8770107568067005553L,
            8986403958761139377L,
            8131918036122205621L,
            5022165755838167137L,
            4832586688523938121L,
            5428552052160802469L,
            5831257442195200493L,
            7064005338091317367L,
            6118218228362103289L,
            8434548741746836823L,
            4801460560457390327L,
            8322046220482070471L,
            7160066332761400189L,
            5244068665286708351L,
            6471265142626309261L,
            6708231474468600607L,
            8589414748053393119L,
            9092153752976174291L,
            5880039800537319007L,
            7096385088387992291L,
            5868382921559828143L,
            5213961656025392357L,
            6646704432795100783L,
            6184809677821607387L,
            6721153792961377871L,
            5617169922917932529L,
            8617436509115982403L,
            4999461642255592667L,
            9159219035379779473L,
            8559313339296319189L,
            7351814044577528761L,
            6540225446102046473L,
            7690088775049124837L,
            5170824029536525513L,
            8409164576103292979L,
            6183299942039277269L,
            5625704011045159049L,
            5810863580383657097L,
            8702110883440362671L,
            8561948336959790603L,
            5246479419567441313L,
            7336324587000056257L,
            7987216914294478919L,
            8454347739456244987L,
            5754423180144473291L,
            4827287952960235573L,
            7546809074950217327L,
            8425978790361598091L,
            8232630225451587701L,
            8228743875403331681L,
            8940890047099034509L,
            5239080441164902853L,
            7971899346368537093L,
            8450193975376315007L,
            6116738265998620831L,
            6773585498517633529L
    };
    private final long[] opts = new long[] { 0L, 1L, 0b11L, 0b111L, 0b1111L, 0b11111L, 0b111111L, 0b1111111L };
    private final long[][][] visibleOptions = new long[64][8][];
    private final int[] trailingZeros = new int[256];
    private final MatrixUtil matrixUtil;
    private final VisibleCalculator[] calculators;
    private final MagicNumbers rookMagicNumbers;
    private final MagicNumbers bishopMagicNumbers;

    public VisibleMetrics(MatrixUtil matrixUtil) {
        var t1 = OffsetDateTime.now();
        this.matrixUtil = matrixUtil;
        fillMap();
        // magic numbers calculation
        var rookBits = 20;
        var rookSize = new Size(rookBits);
        var bishopBits = 18;
        var bishopSize = new Size(bishopBits);
        var combinator = new Combinator(matrixUtil);
        var rookHasher = new MagicHasher(rookSize.getBits(), matrixUtil.queenMegamatrix, matrixUtil.rookDirections);
        var bishopHasher = new MagicHasher(bishopSize.getBits(), matrixUtil.queenMegamatrix,
                matrixUtil.bishopDirections);

        rookMagicNumbers = new MagicNumbers(combinator, rookHasher, rookSize.getCapacity(),
                matrixUtil.rookDirections, ROOK_NUMBERS);

        bishopMagicNumbers = new MagicNumbers(combinator, bishopHasher, bishopSize.getCapacity(),
                matrixUtil.bishopDirections, BISHOP_NUMBERS);
        logger.debug("bishop magic numbers");
        bishopMagicNumbers.calculate();
        logger.debug("rook magic numbers");
        rookMagicNumbers.calculate();
        calculators = new VisibleCalculator[] {
                (sq, f, e) -> {
                    throw new IllegalArgumentException("piece must be between 1 and 12");
                },
                (sq, f, e) -> visibleSquaresWhitePawn(sq, f),
                (sq, f, e) -> visibleSquaresKnight(sq, f),
                this::visibleSquaresBishop,
                this::visibleSquaresRook,
                this::visibleSquaresQueen,
                (sq, f, e) -> visibleSquaresKing(sq, f),
                (sq, f, e) -> visibleSquaresBlackPawn(sq, f),
                (sq, f, e) -> visibleSquaresKnight(sq, f),
                this::visibleSquaresBishop,
                this::visibleSquaresRook,
                this::visibleSquaresQueen,
                (sq, f, e) -> visibleSquaresKing(sq, f)
        };
        var t2 = OffsetDateTime.now();
        logger.instantiation(t1, t2);
    }

    private void fillMap() {
        trailingZeros[0] = 7;
        for (int i = 1; i < 0b10000000; i++) {
            trailingZeros[i] = Integer.numberOfTrailingZeros(i);
        }
        for (int square = 0; square < 64; square++) {
            for (int directionIndex = 0; directionIndex < 8; directionIndex++) {
                int[] direction = matrixUtil.queenMegamatrix[square][directionIndex];
                long[] options = new long[8];
                for (int optionIndex = 0; optionIndex < 8; optionIndex++) {
                    long image = opts[optionIndex];
                    long visible = 0L;
                    int counter = 0;
                    for (int sq : direction) {
                        visible |= ((image & (1L << counter)) >>> counter) << sq;
                        counter++;
                    }
                    options[optionIndex] = visible;
                }
                visibleOptions[square][directionIndex] = options;
            }
        }

    }

    long computeVisible(int square, int[] directionsIndexes, int[][] directions, long friends,
            long enemies) {
        long moves = 0L;
        for (int index : directionsIndexes) {
            moves = moves
                    | getVisible(square, index, directions[index], friends, enemies);
        }
        return moves;
    }

    long getVisible(int square, int index, int[] direction, long friends, long enemies) {
        // space transformation: board -> direction
        int fimage = 0;
        int eimage = 0;
        int counter = 0;
        for (int sq : direction) {
            fimage |= (int) (((friends & (1L << sq)) >>> sq) << counter);
            eimage |= (int) (((enemies & (1L << sq)) >>> sq) << (counter + 1));
            counter++;
        }
        // image for direction space with bit population always <= 7
        final int image = (fimage | eimage) & 0b1111111;
        // trailing zeros count and visible bitboard selection
        return visibleOptions[square][index][trailingZeros[image]];
    }

    long visibleSquares(long[] bits, int[] directionsIndexes, int square, long whiteMoveNumeric) {
        long moves = 0L;
        final long black = bits[6] | bits[7] | bits[8] | bits[9] | bits[10] | bits[11];
        final long white = bits[0] | bits[1] | bits[2] | bits[3] | bits[4] | bits[5];
        long friends;
        long enemies;
        if (whiteMoveNumeric == 1L) {
            friends = white;
            enemies = black;
        } else {
            friends = black;
            enemies = white;
        }
        for (int index : directionsIndexes) {
            moves = moves
                    | getVisible(square, index, matrixUtil.queenMegamatrix[square][index], friends, enemies);
        }
        return moves;
    }

    long visibleSquaresWhitePawn(int square, long friends) {
        return matrixUtil.whitePawnCaptureMoves[square] & ~friends;
    }

    long visibleSquaresBlackPawn(int square, long friends) {
        return matrixUtil.blackPawnCaptureMoves[square] & ~friends;
    }

    long visibleSquaresKnight(int square, long friends) {
        return matrixUtil.knightMoves[square] & ~friends;
    }

    long visibleSquaresBishop(int square, long friends, long enemies) {
        return bishopMagicNumbers.visibleHashed(square, friends | enemies) & ~friends;
    }

    long visibleSquaresRook(int square, long friends, long enemies) {
        return rookMagicNumbers.visibleHashed(square, friends | enemies) & ~friends;
    }

    long visibleSquaresQueen(int square, long friends, long enemies) {
        return visibleSquaresBishop(square, friends, enemies) | visibleSquaresRook(square, friends, enemies);
    }

    long visibleSquaresKing(int square, long friends) {
        return matrixUtil.kingMoves[square] & ~friends;
    }

    long immediateThreats(long[] bitboards, long friends, long enemies) {
        var enemiesCopy = enemies;
        var enemiesVisible = 0L;
        while (enemiesCopy != 0L) {
            var bitboard = Long.lowestOneBit(enemiesCopy);
            enemiesCopy &= ~bitboard;
            final var square = Long.numberOfTrailingZeros(bitboard);
            var piece = 0;
            for (int j = 1; j < 13; j++) {
                piece += j * (int) (((1L << square) & bitboards[j - 1]) >>> square);
            }
            enemiesVisible |= calculators[piece].visibleSquares(square, enemies, friends);
        }
        return enemiesVisible;
    }

    long threats(long[] bitboards, long friends, long enemies, int kingSquare, long wm) {
        var enemiesVisible = 0L;
        final var visibleKing = visibleSquaresKing(kingSquare, friends);
        final var effectiveFriends = friends | (visibleKing & enemies);
        final var effectiveEnemies = enemies & ~(visibleKing & enemies);
        final var enemyPawn = 6 * (int) wm;
        final var pawnFunction = calculators[enemyPawn + 1];
        var pawnBitboard = bitboards[enemyPawn];
        while (pawnBitboard != 0L) {
            final var bitboard = pawnBitboard & -pawnBitboard;
            pawnBitboard &= ~bitboard;
            final var square = Long.numberOfTrailingZeros(bitboard);
            enemiesVisible |= pawnFunction.visibleSquares(square, effectiveEnemies, effectiveFriends);
        }
        var knightBitboard = bitboards[enemyPawn + 1];
        while (knightBitboard != 0L) {
            final var bitboard = knightBitboard & -knightBitboard;
            knightBitboard &= ~bitboard;
            final var square = Long.numberOfTrailingZeros(bitboard);
            enemiesVisible |= visibleSquaresKnight(square, effectiveEnemies);
        }
        var bishopBitboard = bitboards[enemyPawn + 2];
        while (bishopBitboard != 0L) {
            final var bitboard = bishopBitboard & -bishopBitboard;
            bishopBitboard &= ~bitboard;
            final var square = Long.numberOfTrailingZeros(bitboard);
            enemiesVisible |= visibleSquaresBishop(square, effectiveEnemies, effectiveFriends);
        }
        var rookBitboard = bitboards[enemyPawn + 3];
        while (rookBitboard != 0L) {
            final var bitboard = rookBitboard & -rookBitboard;
            rookBitboard &= ~bitboard;
            final var square = Long.numberOfTrailingZeros(bitboard);
            enemiesVisible |= visibleSquaresRook(square, effectiveEnemies, effectiveFriends);
        }
        var queenBitboard = bitboards[enemyPawn + 4];
        while (queenBitboard != 0L) {
            final var bitboard = queenBitboard & -queenBitboard;
            queenBitboard &= ~bitboard;
            final var square = Long.numberOfTrailingZeros(bitboard);
            enemiesVisible |= visibleSquaresQueen(square, effectiveEnemies, effectiveFriends);
        }
        var kingBitboard = bitboards[enemyPawn + 5];
        while (kingBitboard != 0L) {
            final var bitboard = kingBitboard & -kingBitboard;
            kingBitboard &= ~bitboard;
            final var square = Long.numberOfTrailingZeros(bitboard);
            enemiesVisible |= visibleSquaresKing(square, effectiveEnemies);
        }
        return enemiesVisible;
    }

}

/**
 * @author lunalobos
 * @since 1.2.9
 */
final class MappingException extends RuntimeException {

    public static MappingException index(int index, int capacity) {
        return new MappingException(String.format(
                "Index %d is out of range for this hashmap with capacity %d.",
                index,
                capacity));
    }

    private MappingException(String message) {
        super(message);
    }
}

final class FastFailLongMap {
    private final Long[] map;

    public FastFailLongMap(int capacity) {
        map = new Long[capacity];
    }

    public void put(int index, long value) {
        if (index < 0 || index >= map.length)
            throw MappingException.index(index, map.length);
        map[index] = value;
    }

    public Long[] toLongArray() {
        var result = new Long[map.length];
        System.arraycopy(map, 0, result, 0, map.length);
        return result;
    }

    public Long get(int index) {
        if (index < 0 || index >= map.length)
            return null;
        else
            return map[index];
    }

    public boolean containsKey(int index) {
        if (index < 0 || index >= map.length)
            return false;
        else
            return map[index] != null;
    }

    public void clear() {
        Arrays.fill(map, null);
    }
}

final class Combinator {
    private static final Logger logger = Factory.getLogger(Combinator.class);
    private final MatrixUtil matrixUtil;

    public Combinator(MatrixUtil matrixUtil) {
        this.matrixUtil = matrixUtil;
        logger.instantiation();
    }

    public Set<Long> compute(int square, int[] directionsIndexes) {
        List<Integer> list = squaresList(square, directionsIndexes);
        var combinationsSize = 1 << list.size();
        Set<Long> combinationList = new HashSet<>();
        for (var combination = 0; combination < combinationsSize; combination++) {
            var bitIterator = new BitIterator(combination);
            var listIterator = list.iterator();
            var bitboard = 0L;
            while (bitIterator.hasNext() && listIterator.hasNext()) {
                bitboard |= (((long) bitIterator.next()) << listIterator.next());
            }
            combinationList.add(bitboard);
        }
        return combinationList;
    }

    private List<Integer> squaresList(int square, int[] directionsIndexes) {
        int[][] directions = matrixUtil.queenMegamatrix[square];
        List<Integer> list = new LinkedList<>();
        for (int directionIndex : directionsIndexes) {
            for (int sq : directions[directionIndex]) {
                list.add(sq);
            }
        }
        return list;
    }
}

final class BitIterator {

    private final int bits;
    private int pointer = 0;
    private final int bitsLength;

    public BitIterator(int bits) {
        this.bits = bits;
        bitsLength = 32 - Integer.numberOfLeadingZeros(bits);
    }

    public boolean hasNext() {
        return pointer < bitsLength;
    }

    public Integer next() {
        var currentPointer = pointer;
        pointer++;
        return (bits & (1 << currentPointer)) >>> currentPointer;
    }

}

final class MagicHasher {
    private static final Logger logger = Factory.getLogger(MagicHasher.class);
    private final int indexBits;
    private final long[] maskMatrix;

    public MagicHasher(int indexBits, int[][][] queenMatrix, int[] directions) {
        var t1 = OffsetDateTime.now();
        this.indexBits = indexBits;
        maskMatrix = createMaskMatrix(queenMatrix, directions);
        var t2 = OffsetDateTime.now();
        logger.instantiation(t1, t2);
    }

    private long[] createMaskMatrix(int[][][] queenMatrix, int[] directions) {
        var maskMatrix = new long[64];
        for (int square = 0; square < 64; square++) {
            var mask = 0L;
            for (int directionIndex : directions) {
                for (int squareIndex : queenMatrix[square][directionIndex]) {
                    mask |= 1L << squareIndex;
                }
            }
            maskMatrix[square] = mask;
        }
        return maskMatrix;
    }

    public int hash(long occupied, long magic, int square) {
        var mask = maskMatrix[square];
        var blockers = occupied & mask;
        return (int) ((blockers * magic) >>> (64 - indexBits));
    }
}

@Getter
final class Size {
    private final int bits;
    private final int capacity;

    public Size(int bits) {
        this.bits = bits;
        this.capacity = 1 << bits;
    }
}

interface VisibleCalculator {
    long visibleSquares(int square, long friends, long enemies);
}