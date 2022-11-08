package dev.edumelo.com.nndl_core.scroll;

import java.util.List;

import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;

public class ListScroll {
	private final SeleniumSndlWebDriver remoteWebDriver;
	private final List<String> iterationScopeList;
	private final List<ScrollObserver> scrollObservers;

	public ListScroll(SeleniumSndlWebDriver remoteWebDriver, List<String> iterationScopeList,
			List<ScrollObserver> scrollObservers) {
		this.remoteWebDriver = remoteWebDriver;
		this.iterationScopeList = iterationScopeList;
		this.scrollObservers = scrollObservers;
	}

	public void scroll(long maxLoopCount, Long limit) throws InfiniteScrollMaxLoopCountReached {
		
		mainLoop: for (int i = 0; i < iterationScopeList.size(); i++) {
			int actualLoopCount = 0;
			
			for (ScrollObserver scrollObserver : scrollObservers) {
				actualLoopCount++;
				try {					
					scrollObserver.execute(remoteWebDriver, actualLoopCount);
				} catch (ScrollException e) {
					continue mainLoop;
				}
			}
		}
		
	}
	
}
