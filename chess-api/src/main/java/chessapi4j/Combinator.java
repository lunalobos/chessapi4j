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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

//singleton bean
/**
 * @author lunalobos
 * @since 1.2.8
 */
class Combinator {
	private static final Logger logger = LoggerFactory.getLogger(Combinator.class);
	public Combinator() {
		logger.instanciation();
	}

    public Set<Long> compute(int square, int[] directionsIndexs) {
		List<Integer> list = squaresList(square, directionsIndexs);
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

    private List<Integer> squaresList(int square, int[] directionsIndexs) {
		int[][] directions = Util.QUEEN_MEGAMATRIX[square];
		List<Integer> list = new LinkedList<>();
		for (int directionIndex : directionsIndexs) {
			for (int sq : directions[directionIndex]) {
				list.add(sq);
			}
		}
		return list;
	}
}

