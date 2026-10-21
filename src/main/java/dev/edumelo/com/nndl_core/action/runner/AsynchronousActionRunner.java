package dev.edumelo.com.nndl_core.action.runner;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.edumelo.com.nndl_core.action.Action;
import dev.edumelo.com.nndl_core.action.landmark.LandMarkWaiter;
import dev.edumelo.com.nndl_core.action.landmark.LandmarkConditionAction;
import dev.edumelo.com.nndl_core.contextAdapter.NndlContext;
import dev.edumelo.com.nndl_core.contextAdapter.NndlContextManager;
import dev.edumelo.com.nndl_core.exceptions.checked.NndlActionException;
import dev.edumelo.com.nndl_core.webdriver.IterationContent;
import dev.edumelo.com.nndl_core.webdriver.SeleniumNndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.SeleniumNndlWebDriverWaiter;

public class AsynchronousActionRunner {
	
	private static final Logger log = LoggerFactory.getLogger(AsynchronousActionRunner.class);
	
	private final SeleniumNndlWebDriver remoteWebDriver;
	private final SeleniumNndlWebDriverWaiter webDriverWait;
	private LandMarkWaiter landmarkWaiter;

	public AsynchronousActionRunner(SeleniumNndlWebDriver remoteWebDriver, SeleniumNndlWebDriverWaiter webDriverWait) {
		this.remoteWebDriver = remoteWebDriver;
		this.webDriverWait = webDriverWait;
		this.landmarkWaiter = new LandMarkWaiter(remoteWebDriver, webDriverWait);
	}

	public void run(IterationContent rootElement, Action action) {
		NndlContext context = NndlContextManager.getContext();
		CompletableFuture.runAsync(() -> NndlContextManager.runWithContext(context, () -> {
			try {
				((LandmarkConditionAction) action).runPrecedentWait(remoteWebDriver, webDriverWait,
						landmarkWaiter, rootElement);
			} catch (NndlActionException e) {
				String msg = String.format("AsynchronousActionRunner exception. Exception: %s", e.toString());
				log.error(msg);
			}
		}));
	}
	
}
