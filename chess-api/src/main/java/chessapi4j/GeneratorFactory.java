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

import java.util.function.Supplier;

/**
 * Factory class for {@code Generator} instances. {@code Generator} class can
 * not be directly instantiated to avoid bad practices in terms of performance.
 *
 * @author lunalobos
 * @since 1.0.0
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
