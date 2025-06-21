package chessapi4j.functional;

import lombok.Getter;

import java.util.Objects;
//bean
/**
 * This class provides a simple structure to represent a tag with a name and a
 * corresponding value, along with methods for manipulation and comparison.
 * 
 * Instances of this class are immutable.
 *
 * @author lunalobos
 */
@Getter
public class Tag {
	private String name;
	private String value;

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

}
