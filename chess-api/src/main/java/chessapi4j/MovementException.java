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
package chessapi4j;

/**
 * It's thrown when move inconsistencies happens.
 * 
 * @author lunalobos
 * @since 1.0.0
 */
public class MovementException extends RuntimeException {
	/**
	 *
	 */
	private static final long serialVersionUID = -4964697290998435420L;

	/**
	 * Creates a new instance of {@link MovementException} with a message that
	 * indicates that the move string is invalid.
	 * @param invalidString the invalid move string
	 * @return
	 */
	public static MovementException invalidString(String invalidString){
		return new MovementException("Invalid move string: %s".formatted(invalidString));
	}
	
	/**
	 * Creates a new instance of {@link MovementException}.
	 * @param msg the exception message
	 */
	public MovementException(String msg) {
		super(msg);
	}

	/**
	 * Creates a new instance of {@link MovementException} with a message that
	 * indicates that the move is illegal for the position.
	 * 
	 * @param move the move that is illegal
	 * @param position the position where the move is illegal
	 * 
	 * @since 1.2.5
	 */
	@Deprecated
	public MovementException(Move move, Position position) {
		this(String.format("Move %s is illegal for position %s".formatted(move, position)));
	}

}
