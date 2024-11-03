package dev.edumelo.com.nndl_core.nndl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CollectionNodeValue extends NodeValue {
	private List<Object> value;
	
	public CollectionNodeValue(List<?> list) {
		this.value = new ArrayList<Object>();
		list.stream().forEach(value::add);
	}

	public List<Object> getValue() {
		return value;
	}
	public void setValue(List<Object> value) {
		this.value = value;
	}

	public void megeValue(CollectionNodeValue otherCollectionNode) {
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
		CollectionNodeValue other = (CollectionNodeValue) obj;
		return Objects.equals(value, other.value);
	}

	@Override
	public String toString() {
		return "CollectionNodeValue [value=" + value + "]";
	}

}
