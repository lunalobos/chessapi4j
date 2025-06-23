package chessapi4j.functional;


import java.util.Objects;
//bean
/**
 * This class provides a simple structure to represent a tag with a name and a
 * corresponding value, along with methods for manipulation and comparison.
 * 
 * <p>Instances of this class are immutable.</p>
 *
 * @author lunalobos
 */
public class Tag implements Comparable<Tag> {
	private final String name;
	private final String value;

	/**
	 * Creates a new Tag with the given name and value.
	 * @param name the tag name
	 * @param value the tag value
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
	 * The tag name
	 * @return the tag name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * The tag value
	 * @return the tag value
	 */
	public String getValue() {
		return this.value;
	}

	@Override
	public int compareTo(Tag o) {
		int cmp = this.name.compareTo(o.name);
        if (cmp != 0) {
            return cmp;
        }
        return this.value.compareTo(o.value);
	}
}
