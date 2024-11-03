package dev.edumelo.com.nndl_core.nndl;

import java.util.Objects;

public class ScalarNodeValue extends NodeValue {
	private String value;
	
	public ScalarNodeValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ScalarNodeValue other = (ScalarNodeValue) obj;
		return Objects.equals(value, other.value);
	}

	@Override
	public String toString() {
		return "ScalarNodeValue [value=" + value + "]";
	}

}
