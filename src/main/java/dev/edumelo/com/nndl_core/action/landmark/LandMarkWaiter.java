package dev.edumelo.com.nndl_core.action.landmark;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.step.advice.ContinueAdvice;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriverWaiter;

public class LandMarkWaiter {
	
	private static final Logger log = LoggerFactory.getLogger(LandMarkWaiter.class);
	
	private SeleniumSndlWebDriver webDriver;
	private SeleniumSndlWebDriverWaiter webDriverWait;

	public LandMarkWaiter(SeleniumSndlWebDriver webDriver, SeleniumSndlWebDriverWaiter webDriverWait) {
		this.webDriver = webDriver;
		this.webDriverWait = webDriverWait;
	}

	public Advice wait(LandmarkConditionAggregation landmarkConditionAggregation) throws LandmarkException {
		log.debug("wait. landmarkConditionAggregation: {}", landmarkConditionAggregation);
		if(landmarkConditionAggregation.getType() == LandmarkConditionAggregationType.LANDMARK_NOT_SETTED) {
			return new ContinueAdvice();
		}
		
		List<CompletableFuture<Advice>> landmarkList = new ArrayList<>();
		List<Thread> threads = new ArrayList<Thread>();
		
		final LandmarkExceptionCapture exceptionCapture = new LandmarkExceptionCapture();
		for (Landmark landmark : landmarkConditionAggregation.getLandmarkConditions()) {
			By locator = landmark.getLocator(webDriver);
			CompletableFuture<Advice> completableFuture = new CompletableFuture<>();
			landmarkList.add(completableFuture);
			threads.add(
					new Thread(() -> {
						if(landmark instanceof LandmarkCoolDown) {
							LandmarkCoolDown cooldown = (LandmarkCoolDown) landmark;
							try {
								Thread.sleep(cooldown.getCooldown());
							} catch (InterruptedException e) {
								log.debug("Landmark CoolDown wait interrupt. Landmark: {}", landmark);
							}
						} else {
							try {
								webDriverWait.getWebDriverWaiter().withTimeout(Duration.ofSeconds(landmark.getTimeout())).until(ExpectedConditions
										.visibilityOfElementLocated(locator));							
							} catch(WebDriverException e) {
								log.debug("Landmark wait interrupt. Landmark: {}", landmark);
								exceptionCapture.setExceptionCaptured(true);
								exceptionCapture.setThrowable(e);
								exceptionCapture.setLocator(locator);
								completableFuture.complete(landmark.getLandMarkAdvice());
								return;
							}							
						}
						log.debug("Landmark properly found. Landmark: {}", landmark);
						completableFuture.complete(landmark.getLandMarkAdvice());
					})
			);
		}
		
		threads.forEach(Thread::start);

		CompletableFuture<?>[] landmarkArray = landmarkList.toArray(new CompletableFuture[0]);
		if(landmarkConditionAggregation.getType() == LandmarkConditionAggregationType.CONJUNCTION) {
			CompletableFuture.allOf(landmarkArray).join();
		} else {
			CompletableFuture.anyOf(landmarkArray).join();
		}
		
		threads.forEach(Thread::interrupt);
		
		if(exceptionCapture.isExceptionCaptured()) {
			throw new LandmarkException("Exception captured", exceptionCapture.getThrowable());
		}
		
		return landmarkList.stream()
				.filter(CompletableFuture::isDone)
				.map(this::getAdvice)
				.filter(Objects::nonNull)
				.findFirst()
				.orElseThrow();
	}
	
	private Advice getAdvice(CompletableFuture<Advice> c) {
		try {
			return c.get();
		} catch (InterruptedException | ExecutionException e) {
			log.error("Thread execution error: {}", e);
			return null;
		}
	}

}
