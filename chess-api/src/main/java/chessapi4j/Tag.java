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

import java.util.Objects;
//bean
/**
 * This class provides a simple structure to represent a tag with a name and a
 * corresponding value, along with methods for manipulation and comparison.
 *
 * @author lunalobos
 * @since 1.1.0
 */
public class Tag {
	private String name;
	private String value;

	/**
	 * Creates a new tag
	 * @param name the name of the tag
	 * @param value the value of the tag
	 */
	public Tag(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}

    @Override
	public int hashCode() {
		return Objects.hash(name, value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tag other = (Tag) obj;
		return Objects.equals(name, other.name) && Objects.equals(value, other.value);
	}

	@Override
	public String toString() {
		return new StringBuilder().append("[").append(name).append(" ").append('"').append(value).append('"')
				.append("]").toString();
	}

	/**
	 * Sets the name of this tag
	 * @param name the new name of this tag
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the value for this tag
	 * @param value the new value for this tag
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * The name of this tag
	 * @return the name of this tag
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * The value of this tag
	 * @return the value of this tag
	 */
	public String getValue() {
		return this.value;
	}

}
