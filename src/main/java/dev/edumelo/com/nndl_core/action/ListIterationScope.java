package dev.edumelo.com.nndl_core.action;

import java.util.List;

public class ListIterationScope implements LoopIterationScope {
	private List<String> list;

	public List<String> getList() {
		return list;
	}

	@Override
	public LoopIterationScopeType getType() {
		return LoopIterationScopeType.LIST;
	}

	public ListIterationScope(List<String> list) {
		this.list = list;
	}


}
