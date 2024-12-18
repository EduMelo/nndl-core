package dev.edumelo.com.nndl_core.nndl;

import java.util.HashMap;

import dev.edumelo.com.nndl_core.exceptions.unchecked.NndlRuntimeException;

public class NndlMapChild extends HashMap<String, NndlNode> implements NndlChild {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1277801589667643387L;

	private NndlChildType type = NndlChildType.MAP;
	
	@Override
	public NndlChildType getType() {
		return type;
	}
	
	@Override
	public void merge(NndlChild otherChild) {
		if(!(otherChild instanceof NndlMapChild)) {
			throw new NndlRuntimeException("NndlMapChild can only be merged to a NndlMapChild");
		}
		putAll((NndlMapChild) otherChild);
	}

}
