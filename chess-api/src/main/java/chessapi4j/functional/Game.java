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
import lombok.Synchronized;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

import chessapi4j.EcoDescriptor;
import chessapi4j.MissingECOException;
import chessapi4j.MovementException;
import chessapi4j.Side;

/**
 * The Game class represents a chess game. The class provides a structured
 * representation of a chess game and has methods to access, modify, and
 * represent the game in PGN format.
 * 
 * <p>
 * Instances of this class are not immutable until the game is over.
 * </p>
 * <p>
 * A instance can be three repetitions sensitive in diferent ways, depending on
 * the value of the repetitionsMode field. If this field is set to IGNORE,
 * the game instance will no consider repetitions. If this field is set to
 * STRICT, the game instance will consider repetitions as game over
 * condition. Finally, if this field is set to AWARE, the game instance
 * will consider repetitions but will not set the result tag and the game can
 * continue.
 * </p>
 * 
 * <p>
 * You can induce immutability by:
 * </p>
 * <ul>
 * <li>Setting the result tag.</li>
 * <li>Using the {@link #move(Move)}, {@link #move(String)} or
 * {@link #sanMove(String)}
 * methods with a ending game move wich results in checkmate, stalemate, fifty
 * moves, lack of material, or three repetitions (if repetitionsMode is set to
 * STRICT)</li>
 * </ul>
 * 
 * @author lunalobos
 */
public class Game implements Iterable<Position> {
    static final Eco eco = new Eco(new CsvParser());
    /**
     * This enum represents the possible modes for handling three repetitions.
     */
    public enum RepetitionsMode {
        /**
         * Ignore repetitions.
         */
        IGNORE,
        /**
         * Consider repetitions as game over condition.
         */
        STRICT,
        /**
         * Consider repetitions but do not set the result tag.
         */
        AWARE
    }

    /**
     * Builder for open {@code Game} instances.
     * <p>To create an open {@code Game} instance, you need to provide the tags: event, site, date, round, white, and black.</p>
     * <p>The {@code RepetitionsMode} is set to {@code RepetitionsMode.STRICT} by default, but it can be changed.</p>
     */
    public static class Builder {
        private Tag event;
        private Tag site;
        private Tag date;
        private Tag round;
        private Tag white;
        private Tag black;
        private RepetitionsMode repetitionsMode = RepetitionsMode.STRICT; // valor por defecto

         /**
         * Creates a new {@code Builder} instance.
         */
        public Builder() {}

        /**
         * Sets the event tag.
         * @param event the event tag value
         * @return this builder
         */
        public Builder event(String event) {
            this.event = new Tag("Event", event);
            return this;
        }

        /**
         * Sets the site tag.
         * @param site the site tag value
         * @return this builder
         */
        public Builder site(String site) {
            this.site = new Tag("Site", site);
            return this;
        }

        /**
         * Sets the date tag.
         * @param date the date tag value
         * @return this builder
         */
        public Builder date(String date) {
            this.date = new Tag("Date", date);
            return this;
        }

        /**
         * Sets the round tag.
         * @param round the round tag value
         * @return this builder
         */
        public Builder round(String round) {
            this.round = new Tag("Round", round);
            return this;
        }

        /**
         * Sets the white tag.
         * @param white the white tag value
         * @return this builder
         */
        public Builder white(String white) {
            this.white = new Tag("White", white);
            return this;
        }

        /**
         * Sets the black tag.
         * @param black the black tag value
         * @return this builder
         */
        public Builder black(String black) {
            this.black = new Tag("Black", black);
            return this;
        }

        /**
         * Sets the repetitions mode.
         * @param repetitionsMode the repetitions mode
         * @return this builder
         */
        public Builder repetitionsMode(RepetitionsMode repetitionsMode) {
            this.repetitionsMode = repetitionsMode;
            return this;
        }

        /**
         * Builds the {@code Game} instance.
         * @return the built {@code Game} instance
         */
        public Game build() {
            return new Game(event, site, date, round, white, black, repetitionsMode);
        }
    }

    /**
     * Creates a new {@code Builder} instance. This builder is used only for creating open instances.
     * @return a new {@code Builder} instance
     */
    public Builder builder(){
        return new Builder();
    }

    private class SamePositionCounter {
        @Getter
        private volatile int count;
        private volatile Position position;

        public SamePositionCounter(Position position) {
            this.position = position;
            this.count = 1;
        }

        public void update(Position position) {
            if (position.equals(this.position)) {
                this.count++;
            } else {
                this.count = 1;
            }
            this.position = position;
            checkFlag();
        }

        private void checkFlag() {
            if (count == 3) {
                switch (repetitionsMode) {
                    case IGNORE:
                        return;
                    case STRICT:
                        strict();
                        break;
                    case AWARE:
                        repetitions = true;
                        break;
                    default:
                        return;
                }
            } else {
                repetitions = false;
            }
        }

        private void strict() {
            repetitions = true;
            setResult( "1/2-1/2");
        }
    }

    private volatile Tag event;
    private volatile Tag site;
    private volatile Tag date;
    private volatile Tag round;
    private volatile Tag white;
    private volatile Tag black;
    private volatile Tag result;
    private Set<Tag> supplementalTags;
    private Deque<PGNMove> moves;
    private volatile Deque<Position> positions;
    private volatile EcoDescriptor ecoDescriptor;
    private volatile Map<String, String> tags;
    private volatile boolean isResultSet = false;
    private volatile boolean repetitions = false;
    private final RepetitionsMode repetitionsMode;
    private volatile SamePositionCounter sameWhitePositionCounter;
    private volatile SamePositionCounter sameBlackPositionCounter;
    private final Object $lock = new Object();

    /**
     * Constructs a Game immutable instance with the specified parameters.
     *
     * @param event            the event tag
     * @param site             the site tag
     * @param date             the date tag
     * @param round            the round tag
     * @param white            the white tag
     * @param black            the black tag
     * @param result           the result tag
     * @param supplementalTags the supplemental tags
     * @param moves            the collection of moves
     */
    public Game(Tag event, Tag site, Tag date, Tag round, Tag white, Tag black, Tag result, Set<Tag> supplementalTags,
            Deque<PGNMove> moves) {
        super();
        this.event = Objects.requireNonNull(event, "Event tag cannot be null.");
        this.site = Objects.requireNonNull(site, "Site tag cannot be null.");
        this.date = Objects.requireNonNull(date, "Date tag cannot be null.");
        this.round = Objects.requireNonNull(round, "Round tag cannot be null.");
        this.white = Objects.requireNonNull(white, "White tag cannot be null.");
        this.black = Objects.requireNonNull(black, "Black tag cannot be null.");
        this.result = Objects.requireNonNull(result, "Result tag cannot be null.");
        this.supplementalTags = new HashSet<>(Objects.requireNonNull(
                supplementalTags,
                "Supplemental tags cannot be null."));
        this.moves = Objects.requireNonNull(moves, "Moves cannot be null.");

        positions = createHistory(moves, supplementalTags);

        tags = new HashMap<>();
        tags.put("Event", event.getValue());
        tags.put("Site", site.getValue());
        tags.put("Date", date.getValue());
        tags.put("Round", round.getValue());
        tags.put("White", white.getValue());
        tags.put("Black", black.getValue());
        tags.put("Result", result.getValue());

        this.repetitionsMode = RepetitionsMode.IGNORE;

        isResultSet = true;

        for (Tag tag : this.supplementalTags) {
            tags.put(tag.getName(), tag.getValue());
        }
        tags = Collections.unmodifiableMap(tags);
        ((BlockingList<Position>) positions).block();
    }

    /**
     * Constructs an open to modifications instance with the specified initial
     * parameters.
     * 
     * @param event           the event tag
     * @param site            the site tag
     * @param date            the date tag
     * @param round           the round tag
     * @param white           the white tag
     * @param black           the black tag
     * @param repetitionsMode the repetitions mode
     */
    public Game(Tag event, Tag site, Tag date, Tag round, Tag white, Tag black, RepetitionsMode repetitionsMode) {
        this.event = Objects.requireNonNull(event, "Event tag cannot be null.");
        this.site = Objects.requireNonNull(site, "Site tag cannot be null.");
        this.date = Objects.requireNonNull(date, "Date tag cannot be null.");
        this.round = Objects.requireNonNull(round, "Round tag cannot be null.");
        this.white = Objects.requireNonNull(white, "White tag cannot be null.");
        this.black = Objects.requireNonNull(black, "Black tag cannot be null.");
        this.repetitionsMode = Objects.requireNonNull(repetitionsMode, "Repetitions mode cannot be null.");
        positions = new ConcurrentLinkedDeque<>();
        tags = new ConcurrentHashMap<>();
        tags.put("Event", event.getValue());
        tags.put("Site", site.getValue());
        tags.put("Date", date.getValue());
        tags.put("Round", round.getValue());
        tags.put("White", white.getValue());
        tags.put("Black", black.getValue());
        positions.add(Factory.startPos);
        sameWhitePositionCounter = new SamePositionCounter(Factory.startPos);
    }

    private void checkImmutable() {
        if (isResultSet)
            throw new UnsupportedOperationException("this instance is immutable");
    }

    /**
     * Returns the repetitions flag. Positions can determinate other states by
     * itself but three repetitions rule is a game property.
     * 
     * @return the repetitions flag
     */
    public boolean repetitions() {
        return repetitions;
    }

    /**
     * Returns the result tag wrapped in an Optional. If the result tag is not set
     * the Optional will be empty.
     * 
     * @return the result tag wrapped in an Optional
     */
    public Optional<Tag> getResult() {
        return Optional.ofNullable(result);
    }

    /**
     * Sets the event tag. This method is synchronized.
     *
     * @param event the event tag to set
     */
    @Synchronized("$lock")
    public void setEvent(String event) {
        checkImmutable();
        this.event = new Tag("Event", Objects.requireNonNull(event, "Cannot set event tag to null."));
        tags.put("Event", this.event.getValue());
    }

    /**
     * Sets the site tag. This method is synchronized.
     *
     * @param site the site tag to set
     */
    @Synchronized("$lock")
    public void setSite(String site) {
        checkImmutable();
        this.site = new Tag("Site", Objects.requireNonNull(site, "Cannot set site tag to null."));
        tags.put("Site", this.site.getValue());
    }

    /**
     * Sets the date tag. This method is synchronized.
     *
     * @param date the date tag to set
     */
    @Synchronized("$lock")
    public void setDate(String date) {
        checkImmutable();
        this.date = new Tag("Date", Objects.requireNonNull(date, "Cannot set date tag to null."));
        tags.put("Date", this.date.getValue());
    }

    /**
     * Sets the round tag. This method is synchronized.
     *
     * @param round the round tag to set
     */
    @Synchronized("$lock")
    public void setRound(String round) {
        checkImmutable();
        this.round = new Tag("Round", Objects.requireNonNull(round, "Cannot set round tag to null."));
        tags.put("Round", this.round.getValue());
    }

    /**
     * Sets the white tag. This method is synchronized.
     *
     * @param white the white tag to set
     */
    @Synchronized("$lock")
    public void setWhite(String white) {
        checkImmutable();
        this.white = new Tag("White", Objects.requireNonNull(white, "Cannot set white tag to null."));
        tags.put("White", this.white.getValue());
    }

    /**
     * Sets the black tag. This method is synchronized.
     *
     * @param black the black tag to set
     */
    @Synchronized("$lock")
    public void setBlack(String black) {
        checkImmutable();
        this.black = new Tag("Black", Objects.requireNonNull(black, "Cannot set black tag to null."));
        tags.put("Black", this.black.getValue());

    }

    /**
     * Sets the result tag. This method is synchronized.
     * 
     * <p>
     * After this method is called the game instance becomes immutable.
     * </p>
     *
     * @param result the result tag to set
     */
    @Synchronized("$lock")
    public void setResult(String result) {
        checkImmutable();
        this.result = new Tag("Result", Objects.requireNonNull(result, "Cannot set result tag to null."));
        tags.put("Result", this.result.getValue());
        isResultSet = true;
        ((BlockingList<Position>) positions).block();
        tags = Collections.unmodifiableMap(tags);
        moves = new BlockingList<>(moves).block();
        positions = new BlockingList<>(positions).block();
        supplementalTags = Collections.unmodifiableSet(supplementalTags);
    }

    /**
     * Sets the supplemental tags. This method is synchronized.
     *
     * @param supplementalTags the supplemental tags to set
     */
    @Synchronized("$lock")
    public void setSupplementalTags(Set<Tag> supplementalTags) {

        for (Tag tag : Objects.requireNonNull(supplementalTags, "Supplemental tags cannot be set to null.")) {
            tags.remove(tag.getName());
        }

        this.supplementalTags = new ConcurrentSkipListSet<>(supplementalTags);

        for (Tag tag : supplementalTags) {
            tags.put(tag.getName(), tag.getValue());
        }

    }

    /**
     * Adds a list of moves to the game instance. This method is synchronized.
     * 
     * @param moves the list of moves
     * @return this instance
     */
    @Synchronized("$lock")
    public Game addMoves(List<? extends Move> moves) {
        moves.forEach(this::moveInternal);
        return this;
    }

    private void checkGameOver() {
        var current = currentPosition();
        if (current.gameOver()) {
            if (current.draw()) {
                this.setResult( "1/2-1/2");
            } else {
                if (current.whiteMove()) {
                    this.setResult("0-1");
                } else {
                    this.setResult("1-0");
                }
            }
        }
    }

    private void checkRepetitions() {
        var current = currentPosition();
        if (current.whiteMove()) {
            if (sameWhitePositionCounter == null) {
                sameWhitePositionCounter = new SamePositionCounter(current);
            } else {
                sameWhitePositionCounter.update(current);
            }
        } else {
            if (sameBlackPositionCounter == null) {
                sameBlackPositionCounter = new SamePositionCounter(current);
            } else {
                sameBlackPositionCounter.update(current);
            }
        }
    }

    private void moveInternal(Move move) {
        checkImmutable();
        var current = currentPosition();
        positions.add(current.move(move));
        if (move instanceof PGNMove) {
            moves.add((PGNMove) move);
        } else {
            moves.add(new PGNMove(move.getOrigin(), move.getTarget(), move.getPromotionPiece(), current));
        }
        checkGameOver();
        checkRepetitions();
    }

    /**
     * Add the move to the game and returns this game instace. This method is
     * synchronized.
     * 
     * <p>
     * The move is executed to current position and the resulting child position
     * is added to the position's list of this game.
     * </p>
     * 
     * <p>
     * If the move makes the game over due to checkmate, stalemate,
     * fifty moves, lack of material the result tag is set and this
     * instance becomes immutable.
     * </p>
     *
     * @param move to add to the game
     * @return this game instance
     */
    @Synchronized("$lock")
    public Game move(Move move) {
        moveInternal(move);
        return this;
    }

    /**
     * Adds the move to the game. The move is expected to be in UCI Notation. This
     * method is synchronized.
     * 
     * <p>
     * The move is executed in the current position of the game and the new position
     * is added
     * to the list of positions of the game.
     * </p>
     *
     * @param move the move in UCI format
     * @return this game instance
     */
    @Synchronized("$lock")
    public Game move(String move) {
        return move(Factory.move(move, currentPosition().whiteMove()));
    }

    /**
     * Adds the move to the game. The move is expected to be in SAN Notation. This
     * method is synchronized.
     * 
     * @param move the move
     * @return this game instance
     */
    @Synchronized("$lock")
    public Game sanMove(String move) {
        var moveObj = PGNHandler.toUCI(currentPosition(), move)
                .orElseThrow(() -> new MovementException(move, currentPosition()));
        return move(moveObj);
    }

    /**
     * Returns the current position of this game.
     *
     * @return the current position
     *
     * @since 1.2.5
     */
    public Position currentPosition() {
        return positions.peekLast();
    }

    /**
     * Returns the position at the given move.
     *
     * @param moveNumber the move number
     * @param sideToMove the side to move
     * @param after      if true, return the position after the move
     * @return the position at the given move number
     */
    public Position positionAt(int moveNumber, Side sideToMove, boolean after) {
        var index = (moveNumber - 1) * 2 + (sideToMove == Side.BLACK ? 1 : 0);
        index += (after ? 1 : 0);
        var list = new ArrayList<Position>(positions.size());
        list.addAll(positions);
        return list.get(index);
    }

    private Deque<Position> createHistory(Deque<PGNMove> moves, Set<Tag> supplementalTags) {
        Position initial = supplementalTags.stream().filter(tag -> tag.getName().equalsIgnoreCase("fen"))
                .map(tag -> new Position(tag.getValue())).findFirst().orElse(new Position());
        Iterator<PGNMove> moveIterator = moves.iterator();
        var positions = new BlockingList<Position>();
        positions.add(initial);
        Position current = initial;
        while (moveIterator.hasNext()) {
            var pgnMove = moveIterator.next();
            current = current.move(pgnMove);
            positions.add(current);
        }
        return positions;
    }

    /**
     * Calculates the hash code for the Game object.
     *
     * @return the hash code value
     */
    @Override
    public int hashCode() {
        return Objects.hash(black, date, event, moves, result, round, site, supplementalTags, white);
    }

    /**
     * Checks if this Game object is equal to another object.
     *
     * @param obj the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Game other = (Game) obj;
        return Objects.equals(black, other.black) && Objects.equals(date, other.date)
                && Objects.equals(event, other.event) && Objects.equals(moves, other.moves)
                && Objects.equals(result, other.result) && Objects.equals(round, other.round)
                && Objects.equals(site, other.site) && Objects.equals(supplementalTags, other.supplementalTags)
                && Objects.equals(white, other.white);
    }

    /**
     * The string representation is always the PGN export format representation.
     *
     * @return the string representation of this object
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("\n").append(event).append("\n").append(site).append("\n").append(date).append("\n").append(round)
                .append("\n").append(white).append("\n").append(black).append("\n").append(result).append("\n");
        for (Tag tag : supplementalTags) {
            sb.append(tag).append("\n");
        }
        sb.append("\n");
        Position position;
        List<Tag> fenTagList = supplementalTags.stream().filter(tag -> tag.getName().equals("FEN"))
                .collect(Collectors.toCollection(LinkedList::new));
        if (!fenTagList.isEmpty())
            position = new Position(fenTagList.get(0).getValue());
        else
            position = new Position();
        sb.append(movesToString(position, moves));
        sb.append(result.getValue());
        sb.append("\n");
        return sb.toString();
    }

    /**
     * Converts the moves list to a string representation in SAN Format.
     *
     * @param position the current position
     * @param moves    the moves list
     * @return the string representation of moves
     */
    private String movesToString(Position position, Deque<PGNMove> moves) {
        StringBuilder sb = new StringBuilder();

        for (PGNMove move : moves) {
            String sanMove = move.toString();
            if (position.whiteMove())
                sb.append(position.movesCounter()).append(". ");

            sb.append(sanMove).append(" ");

            if (move.getSuffixAnnotations() != null) {
                for (int suffix : move.getSuffixAnnotations()) {
                    sb.append("$").append(suffix).append(" ");
                }
            }

            if (move.getComment() != null) {
                sb.append("{").append(move.getComment()).append("} ");
            }

            if (move.getRav() != null) {
                sb.append(movesToString(position, new ArrayDeque<>(move.getRav())));
            }

            position = position.move(move);
        }

        return sb.toString();
    }

    /**
     * An iterator that iterates over the game positions.
     *
     * @return an iterator that iterates over the game positions
     *
     * @since 1.2.3
     */
    @Override
    public Iterator<Position> iterator() {
        return positions.iterator();
    }

    /**
     * Retrieves the ECO (Encyclopaedia of Chess Openings) descriptor for the game.
     * If the ECO code is not explicitly set, it is determined by searching the ECO
     * codes database.
     * 
     * <p>
     * Note: Due to the inherent ambiguity in ECO code classification, the returned
     * ECO may differ from those assigned by other chess platforms like ChessBase
     * or Chess.com. In testing with a sample of 521 games from a ChessBase
     * database, the accuracy was found to be just above 70%. The ECO classification
     * includes many transpositions, and no standard specification exists for
     * handling them.
     * </p>
     * <p>
     * This library employs a direct approach for determining the ECO code. Starting
     * from the first move of the game, the moves are concatenated sequentially, and
     * all combinations are checked against the ECO database. The final ECO code
     * corresponds to the longest move sequence that matches an entry in the ECO
     * database.
     * </p>
     * 
     * @return the ECO descriptor for the game or null if the game has no moves
     * 
     * @throws MissingECOException if the ECO code cannot be found. This is an
     *                             unchecked exception and should never occur unless
     *                             the object is corrupted.
     * 
     * @since 1.2.7
     */
    public EcoDescriptor getEcoDescriptor() {
        return Optional.ofNullable(ecoDescriptor).orElse(calculateEcoDescriptor());
    }

    private EcoDescriptor calculateEcoDescriptor() {
        if (moves.isEmpty())
            return null;
        var i = 0;
        var movesBuilder = new StringBuilder();
        var iterator = positions.iterator();
        var movesCopy = new ArrayDeque<>(moves);
        while (i < movesCopy.size()) {
            movesBuilder.append(movesCopy.pollFirst().toString()).append(" ");
            var position = iterator.next();
            ecoDescriptor = eco.get(movesBuilder.toString().trim()).orElse(eco.get(position).orElse(ecoDescriptor));
            i++;
        }
        if (ecoDescriptor == null)
            throw new MissingECOException(this);
        return ecoDescriptor;
    }

    /**
     * Returns the value of the tag with the given name. If the tag is not present
     * in the game, an empty Optional is returned.
     * 
     * @param tagName the name of the tag to search for
     * @return an Optional with the value of the tag if present, or an empty
     *         Optional if not present
     * 
     * @since 1.2.7
     */
    public Optional<String> getTagValue(String tagName) {
        var tagName_ = tagName.toUpperCase();
        if (tagName_.equals("ECO") && tags.get(tagName_) == null) {
            Optional.ofNullable(getEcoDescriptor()).map(EcoDescriptor::getEco)
                    .ifPresent(eco -> tags.put("ECO", eco));
        }
        return Optional.ofNullable(tags.get(tagName));
    }

    /**
     * Returns the event tag.
     *
     * @return the event tag
     */
    public Tag getEvent() {
        return this.event;
    }

    /**
     * Returns the site tag.
     *
     * @return the site tag
     */
    public Tag getSite() {
        return this.site;
    }

    /**
     * Returns the date tag.
     *
     * @return the date tag
     */
    public Tag getDate() {
        return this.date;
    }

    /**
     * Returns the round tag.
     *
     * @return the round tag
     */
    public Tag getRound() {
        return this.round;
    }

    /**
     * Returns the white tag.
     *
     * @return the white tag
     */
    public Tag getWhite() {
        return this.white;
    }

    /**
     * Returns the black tag.
     *
     * @return the black tag
     */
    public Tag getBlack() {
        return this.black;
    }

    /**
     * Returns the supplemental tags.
     *
     * @return the supplemental tags
     */
    public Set<Tag> getSupplementalTags() {
        return this.supplementalTags;
    }

    /**
     * Returns the list of moves.
     *
     * @return the list of moves
     */
    public Deque<PGNMove> getMoves() {
        return this.moves;
    }

    /**
     * The position collection of this game
     * @return the position collection of this game
     */
    public Deque<Position> getPositions() {
        return this.positions;
    }

    /**
     * Returns the tags as a map of name to value.
     * @return the tags as a map
     */
    public Map<String, String> getTags() {
        return this.tags;
    }
}


