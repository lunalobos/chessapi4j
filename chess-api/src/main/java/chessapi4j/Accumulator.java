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

import java.util.function.BinaryOperator;
import java.util.function.Function;

import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * @author lunalobos
 * @since 1.2.9
 */
@Data
@AllArgsConstructor
final class Accumulator<T> {

	public static <T> Accumulator<T> of(T value) {
		return new Accumulator<T>(value);
	}

	private T value;

	public void accumulate(T value, BinaryOperator<T> function) {
		this.value = function.apply(this.value, value);
	}

	public void accumulate(Accumulator<T> accumulator, BinaryOperator<T> function) {
		value = function.apply(this.value, accumulator.getValue());
	}

	public <R> Accumulator<R> map(Function<T,R> mapper){
		return of(mapper.apply(value));
	}
}