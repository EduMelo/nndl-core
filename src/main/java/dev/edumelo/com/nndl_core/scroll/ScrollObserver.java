package dev.edumelo.com.nndl_core.scroll;

public interface ScrollObserver {	
	void setIterationScope(String iterationScope);
	int execute(int loopCount) throws ScrollContinueException, ScrollStopException;
}
