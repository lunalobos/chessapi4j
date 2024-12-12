/*
 * Copyright 2024 Miguel Angel Luna Lobos
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
package chessapi4j;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The Game class represents a chess game. The class Provides a structured
 * representation of a chess game and has methods to access, modify, and
 * represent the game in PGN format.
 *	
 * @author lunalobos
 * @since 1.1.0
 */
public class Game implements Iterable<Position> {
	private Tag event, site, date, round, white, black, result;
	private Set<Tag> suplementalTags;
	private List<PGNMove> moves;
	private List<Position> positions;

	/**
	 * Constructs a Game object with the specified parameters.
	 *
	 * @param event           the event tag
	 * @param site            the site tag
	 * @param date            the date tag
	 * @param round           the round tag
	 * @param white           the white tag
	 * @param black           the black tag
	 * @param result          the result tag
	 * @param suplementalTags the supplemental tags
	 * @param moves           the list of moves
	 */
	public Game(Tag event, Tag site, Tag date, Tag round, Tag white, Tag black, Tag result, Set<Tag> suplementalTags,
			List<PGNMove> moves) {
		super();
		this.event = event;
		this.site = site;
		this.date = date;
		this.round = round;
		this.white = white;
		this.black = black;
		this.result = result;
		this.suplementalTags = suplementalTags;
		this.moves = moves;
		positions = createHistory(moves, suplementalTags);
	}

	/**
	 * Returns the event tag.
	 *
	 * @return the event tag
	 */
	public Tag getEvent() {
		return event;
	}

	/**
	 * Sets the event tag.
	 *
	 * @param event the event tag to set
	 */
	public void setEvent(Tag event) {
		this.event = event;
	}

	/**
	 * Returns the site tag.
	 *
	 * @return the site tag
	 */
	public Tag getSite() {
		return site;
	}

	/**
	 * Sets the site tag.
	 *
	 * @param site the site tag to set
	 */
	public void setSite(Tag site) {
		this.site = site;
	}

	/**
	 * Returns the date tag.
	 *
	 * @return the date tag
	 */
	public Tag getDate() {
		return date;
	}

	/**
	 * Sets the date tag.
	 *
	 * @param date the date tag to set
	 */
	public void setDate(Tag date) {
		this.date = date;
	}

	/**
	 * Returns the round tag.
	 *
	 * @return the round tag
	 */
	public Tag getRound() {
		return round;
	}

	/**
	 * Sets the round tag.
	 *
	 * @param round the round tag to set
	 */
	public void setRound(Tag round) {
		this.round = round;
	}

	/**
	 * Returns the white tag.
	 *
	 * @return the white tag
	 */
	public Tag getWhite() {
		return white;
	}

	/**
	 *
	 * Sets the white tag.
	 *
	 * @param white the white tag to set
	 */
	public void setWhite(Tag white) {
		this.white = white;
	}

	/**
	 * Returns the black tag.
	 *
	 * @return the black tag
	 */
	public Tag getBlack() {
		return black;
	}

	/**
	 * Sets the black tag.
	 *
	 * @param black the black tag to set
	 */
	public void setBlack(Tag black) {
		this.black = black;
	}

	/**
	 * Returns the result tag.
	 *
	 * @return the result tag
	 */
	public Tag getResult() {
		return result;
	}

	/**
	 * Sets the result tag.
	 *
	 * @param result the result tag to set
	 */
	public void setResult(Tag result) {
		this.result = result;
	}

	/**
	 * Returns the supplemental tags.
	 *
	 * @return the supplemental tags
	 */
	public Set<Tag> getSuplementalTags() {
		return suplementalTags;
	}

	/**
	 * Sets the supplemental tags.
	 *
	 * @param suplementalTags the supplemental tags to set
	 */
	public void setSuplementalTags(Set<Tag> suplementalTags) {
		this.suplementalTags = suplementalTags;
	}

	/**
	 * Returns the list of moves.
	 *
	 * @return the list of moves
	 */
	public List<PGNMove> getMoves() {
		return moves;
	}

	/**
	 * Sets the list of moves.
	 *
	 * @param moves the list of moves to set
	 */
	public void setMoves(List<PGNMove> moves) {
		this.moves = moves;
	}

	/**
	 * Add the move to the game and returns the last position.
	 *
	 * @param move to add to the game
	 * @return the last position of the game
	 * @throws MovementException
	 *
	 * @since 1.2.3
	 */
	public Position addMove(Move move) throws MovementException {
		var lastPosition = positions.getLast();
		positions.add(lastPosition.childFromMove(MoveFactory.instance(move))
				.orElseThrow(() -> new MovementException(move, lastPosition)));
		if (move instanceof PGNMove) {
			moves.add((PGNMove) move);
		} else {
			moves.add(new PGNMove(move, lastPosition));
		}

		return positions.getLast();
	}

	/**
	 * Adds a move to the game. The move is expected to be in UCI Notation. The move
	 * is executed in the current position of the game and the new position is added
	 * to the list of positions of the game. The last position of the game is
	 * returned.
	 *
	 * @param move the move in UCI/SAN format
	 * @return the last position of the game
	 * @throws MovementException if the move is illegal
	 * 
	 * @since 1.2.5
	 */
	public Position addMove(String move) throws MovementException {
		return addMove(MoveFactory.instance(move, currentPosition().isWhiteMove()));
	}

	/**
	 * Returns the current position of the game.
	 *
	 * @return the current position
	 *
	 * @since 1.2.5
	 */
	public Position currentPosition() {
		return positions.getLast();
	}

	/**
	 * Returns the position at the given move.
	 *
	 * @param moveNumber the move number
	 * @param sideToMove the side to move
	 * @param after      if true, return the position after the move
	 * @return the position at the given move number
	 *
	 * @since 1.2.3
	 */
	public Position positionAt(int moveNumber, Side sideToMove, boolean after) {
		int index = (moveNumber - 1) * 2 + (sideToMove == Side.BLACK ? 1 : 0);
		return positions.get(index + (after ? 1 : 0));
	}

	private List<Position> createHistory(List<PGNMove> moves, Set<Tag> suplementalTags) {
		Position initial = suplementalTags.stream().filter(tag -> tag.getName().toLowerCase().equals("fen"))
				.map(tag -> new Position(tag.getValue())).findFirst().orElse(new Position());
		Iterator<PGNMove> moveIterator = moves.iterator();
		List<Position> positions = new LinkedList<>();
		positions.add(initial);
		Position current = initial;
		while (moveIterator.hasNext()) {
			PGNMove pgnMove = moveIterator.next();
			Move move = MoveFactory.instance(pgnMove.getOrigin(), pgnMove.getTarget(), pgnMove.getPromotionPiece());
			current = current.childFromMove(move).orElseThrow(() -> new IllegalArgumentException());
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
		return Objects.hash(black, date, event, moves, result, round, site, suplementalTags, white);
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
				&& Objects.equals(site, other.site) && Objects.equals(suplementalTags, other.suplementalTags)
				&& Objects.equals(white, other.white);
	}

	/**
	 *
	 * The string representation is always the PGN export format representation.
	 *
	 * @return the string representation of this object
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("\n").append(event).append("\n").append(site).append("\n").append(date).append("\n").append(round)
				.append("\n").append(white).append("\n").append(black).append("\n").append(result).append("\n");
		for (Tag tag : suplementalTags) {
			sb.append(tag).append("\n");
		}
		sb.append("\n");
		Position position;
		List<Tag> fenTagList = suplementalTags.stream().filter(tag -> tag.getName().equals("FEN"))
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
	 * Converts the list of moves to a string representation in SAN Format.
	 *
	 * @param position the current position
	 * @param moves    the list of moves
	 * @return the string representation of moves
	 */
	private String movesToString(Position position, List<PGNMove> moves) {
		StringBuilder sb = new StringBuilder();

		for (PGNMove move : moves) {
			String sanMove = move.toString();
			if (position.isWhiteMove())
				sb.append(position.getMovesCounter() + ". ");

			sb.append(sanMove + " ");

			if (move.getSuffixAnnotations() != null) {
				for (int suffix : move.getSuffixAnnotations()) {
					sb.append("$" + suffix + " ");
				}
			}

			if (move.getComment() != null) {
				sb.append("{" + move.getComment() + "} ");
			}

			if (move.getRav() != null) {
				sb.append(movesToString(position, move.getRav()));
			}

			position = position.childFromMove(move).orElseThrow(() -> new IllegalArgumentException("Not valid move"));
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

}