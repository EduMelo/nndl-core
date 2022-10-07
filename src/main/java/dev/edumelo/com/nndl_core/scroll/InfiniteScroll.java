package dev.edumelo.com.nndl_core.scroll;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;

public class InfiniteScroll {
	
	private static final Logger log = LoggerFactory.getLogger(InfiniteScroll.class);

	private final SeleniumSndlWebDriver remoteWebDriver;
	private final int scrollCount;
	private final boolean autoScrool;
	private final List<ScrollObserver> scrollObservers;
	private int maxScrollReached;
	private int maxBreakCount = 50;

	public InfiniteScroll(SeleniumSndlWebDriver remoteWebDriver, int scrollCount, boolean autoScrool,
			List<ScrollObserver> scrollObservers) {
		super();
		this.remoteWebDriver = remoteWebDriver;
		this.scrollCount = scrollCount;
		this.autoScrool = autoScrool;
		this.scrollObservers = scrollObservers;
	}

	private int getMaxScrollReached() {
		return this.maxScrollReached;
	}
	
	private void setMaxScrollReached(int scroll) {
		if(scroll > this.maxScrollReached) {
			this.maxScrollReached = scroll;			
		}
	}
	
	/**
	 * @return The current twitter user page`s body scroll Height measure in pixels
	 */
	private Long getBodyScrollSize() {
		return (Long) remoteWebDriver.getWebDriver().executeScript("return Math.max( document.body.scrollHeight, document.body.offsetHeight, document.documentElement.clientHeight, document.documentElement.scrollHeight, document.documentElement.offsetHeight )");
	}
	
	/**
	 * scrolls to a particular set of coordinates in the document
	 *
	 * @param xCoord The X coordinate to scroll
	 * @param yCoord The Y coordinate to scroll
	 */
	private void scrollBody(long xCoord, long yCoord) {
		remoteWebDriver.getWebDriver().executeScript(String.format("window.scroll(%d, %d);", xCoord, yCoord));
	}

	private int getCurrentScroll() {
		Object value = remoteWebDriver.getWebDriver().executeScript("return (window.pageYOffset || document.documentElement.scrollTop)");
		if(value != null) {
			return ((Long) value).intValue();
		}
		return 0;
	}
	
	/**
	 * Scrolls the infinite scroll to the last created element.
	 * This method will have to be called each time after the scroll load new elements
	 * @param scrollToTheLastElement 
	 */
	private void scrollToTheLastCreatedElement() {
		for (int i = 0; i < scrollCount; i++) {
			remoteWebDriver.getWebDriver().findElement(By.tagName("html")).sendKeys(Keys.chord(Keys.DOWN));
		}
	}
	
	private void bounceBack() {
		int maxScrollReached = getMaxScrollReached();
		int currentScroll = getCurrentScroll();
		
		if (currentScroll <= maxScrollReached) {
			scrollBody(0, maxScrollReached);
		}
		
		while (currentScroll < maxScrollReached) {
			remoteWebDriver.getWebDriver().findElement(By.tagName("html")).sendKeys(Keys.chord(Keys.DOWN));
			currentScroll = getCurrentScroll();
		}
		setMaxScrollReached(currentScroll);
	}

	/**
	 * Scroll the infinite scroll until it`s end.
	 * Executes the observer to each new load of elements
	 *
	 * @param maxLoopCount The maximum scrools to wait until give up
	 * @param limit The required ammount of loops to execute. If null this parameter is ignored
	 * @param conditionClass 
	 * @param scrollObservers The observers that will be execute in each scroll
	 *
	 * @return A list of following twitter users
	 * @throws InfiniteScrollMaxLoopCountReached 
	 */
	public void scroll(long maxLoopCount, Long limit) throws InfiniteScrollMaxLoopCountReached {
		log.debug("scroll: maxLoopCount: {}, scrollObservers: {}", maxLoopCount, scrollObservers);

		int actualLoopCount = 0;
		int breakCount = 0;
		int currentLimit = 0;

		long lastHeight = getBodyScrollSize();
		mainLoop: while(true) {
			actualLoopCount++;
			
			//MaxLoopCount is an exit escape for the loop and limit is for reach a limitted result count
			if(actualLoopCount > maxLoopCount) {
				String msg = String.format("The infite scroll reached the maxLoopCount: %d", maxLoopCount);
			    log.info(msg);
				throw new InfiniteScrollMaxLoopCountReached(msg);
			}
			
			if(limit != null && currentLimit >= limit) {
				String msg = String.format("Limit reached: %d", limit);
			    log.info(msg);
			    break;
			}

			for (ScrollObserver scrollObserver : scrollObservers) {
				try {
					currentLimit += scrollObserver.execute(actualLoopCount);
				} catch(ScrollContinueException e) {
					continue mainLoop;
				} catch (ScrollStopException e) {
					String msg = String.format("Infinite scroll interrupted by the following error: %s", e.getMessage());
					log.error(msg);
					throw new RuntimeException(msg, e);
				}
			}
			
			bounceBack();
			if(autoScrool) {
				scrollToTheLastCreatedElement();				
			}
			long newHeight = getBodyScrollSize();
			log.debug(String.format("Last height %d. New height %d", lastHeight, newHeight));
			if(newHeight == lastHeight) {
				breakCount++;
				if(breakCount > maxBreakCount) {
					break;
				}
			} else {
				breakCount = 0;
			}
			
			lastHeight = newHeight;
		}
	}

}
