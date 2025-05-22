package chessapi4j;

import java.util.function.BinaryOperator;

import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * @author lunalobos
 * @since 1.2.9
 */
@Data
@AllArgsConstructor
class Accumulator<T> {
	public static <T> Accumulator<T> of(T value) {
		return new Accumulator<T>(value);
	}

	private T value;

	public void acumulate(T value, BinaryOperator<T> function) {
		value = function.apply(this.value, value);
	}

	public void acumulate(Accumulator<T> acumulator, BinaryOperator<T> function) {
		value = function.apply(this.value, acumulator.getValue());
	}
}

