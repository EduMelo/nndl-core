package dev.edumelo.com.nndl_core.scroll;

import java.util.List;

import dev.edumelo.com.nndl_core.step.StepRunner;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ListScroll {
	private final List<String> iterationScopeList;
	private StepRunner runner;
	private final List<ScrollObserver> scrollObservers;

	public void scroll(long maxLoopCount, Long limit) throws InfiniteScrollMaxLoopCountReached {
		
		mainLoop: for (String iterationScope : iterationScopeList) {
			int actualLoopCount = 0;
			
			for (ScrollObserver scrollObserver : scrollObservers) {
				actualLoopCount++;
				try {					
					scrollObserver.execute(actualLoopCount);
				} catch (ScrollException e) {
					continue mainLoop;
				}
			}
		}
		
	}
	
}
