package chessapi4j;

import java.util.function.Supplier;

/**
 * Factory class for {@code Generator} instances. {@code Generator} class can
 * not be directly instantiated to avoid bad practices in terms of performance.
 *
 * @author lunalobos
 *
 */
public class GeneratorFactory {

	private static Generator pseudoSingleton = new Generator();
	protected static Generator pseudoInternalSingleton = new Generator();

	/**
	 * Generator main implementation
	 *
	 * @return
	 */
	public static Generator instance() {
		return pseudoSingleton;
	}

	/**
	 * This method allows you to inject your own Generator implementation. To ensure
	 * optimal performance, avoid creating instances of Generator directly outside
	 * of this method. Instantiation and pre-processing of multiple Generator
	 * instances can negatively impact memory usage and performance.
	 *
	 * @param customFactory
	 */
	public static void setFactory(Supplier<Generator> customFactory) {
		pseudoSingleton = customFactory.get();
	}
}
