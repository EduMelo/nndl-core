package dev.edumelo.com.nndl_core.action;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.edumelo.com.nndl_core.action.landmark.LandMarkWaiter;
import dev.edumelo.com.nndl_core.action.landmark.LandmarkConditionAction;
import dev.edumelo.com.nndl_core.webdriver.IterationContent;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriverWaiter;

public class AsynchronousActionRunner {
	
	private static final Logger log = LoggerFactory.getLogger(AsynchronousActionRunner.class);
	
	private final SeleniumSndlWebDriver remoteWebDriver;
	private final SeleniumSndlWebDriverWaiter webDriverWait;
	private LandMarkWaiter landmarkWaiter;

	public AsynchronousActionRunner(SeleniumSndlWebDriver remoteWebDriver, SeleniumSndlWebDriverWaiter webDriverWait) {
		this.remoteWebDriver = remoteWebDriver;
		this.webDriverWait = webDriverWait;
		this.landmarkWaiter = new LandMarkWaiter(remoteWebDriver, webDriverWait);
	}
	
//	@Async
//	private CompletableFuture<Void> asyncRun(Action action) {
//		((LandmarkConditionAction) action).runPrecedentWait(remoteWebDriver, webDriverWait, landmarkWaiter);
//		return CompletableFuture.completedFuture(null);
//	}

	public void run(IterationContent rootElement, Action action) {
//		CompletableFuture.supplyAsync(() -> asyncRun(action));
//		((LandmarkConditionAction) action).runPrecedentWait(remoteWebDriver, webDriverWait, landmarkWaiter);
		CompletableFuture.runAsync(() -> {
			try {
				((LandmarkConditionAction) action).runPrecedentWait(remoteWebDriver, webDriverWait, landmarkWaiter, rootElement);
			} catch (ActionException e) {
				String msg = String.format("AsynchronousActionRunner exception. Exception: %s", e.toString());
				log.error(msg);
			}
		});
	}
	
}
