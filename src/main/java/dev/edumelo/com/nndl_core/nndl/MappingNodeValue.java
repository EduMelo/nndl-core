package dev.edumelo.com.nndl_core.nndl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MappingNodeValue extends NodeValue {
	private List<Object> value;
	
	public MappingNodeValue(List<?> list) {
		this.value = new ArrayList<Object>();
		list.stream().forEach(value::add);
	}

	public List<Object> getValue() {
		return value;
	}
	public void setValue(List<Object> value) {
		this.value = value;
	}

	public void megeValue(MappingNodeValue otherCollectionNode) {
		value.addAll(otherCollectionNode.getValue());
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
		MappingNodeValue other = (MappingNodeValue) obj;
		return Objects.equals(value, other.value);
	}

	@Override
	public String toString() {
		return "MappingNodeValue [value=" + value + "]";
	}

}
