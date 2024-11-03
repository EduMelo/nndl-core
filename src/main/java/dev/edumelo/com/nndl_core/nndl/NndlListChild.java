package dev.edumelo.com.nndl_core.nndl;

import java.util.ArrayList;

import dev.edumelo.com.nndl_core.exceptions.NndlException;

public class NndlListChild extends ArrayList<NndlNode> implements NndlChild {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8020108680437249450L;
	
	private NndlChildType type = NndlChildType.LIST;

	@Override
	public NndlChildType getType() {
		return type;
	}

	@Override
	public void merge(NndlChild otherChild) {
		if(!(otherChild instanceof NndlListChild)) {
			throw new NndlException("NndlListChild can only be merged to a NndlListChild");
		}
		addAll((NndlListChild) otherChild);
	}
	
}
