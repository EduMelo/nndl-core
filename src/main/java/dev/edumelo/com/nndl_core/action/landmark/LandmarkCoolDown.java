package dev.edumelo.com.nndl_core.action.landmark;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.step.advice.ContinueAdvice;
import dev.edumelo.com.nndl_core.webdriver.NndlWebDriver;

public class LandmarkCoolDown implements Landmark {
	
	private static final Logger log = LoggerFactory.getLogger(LandmarkCoolDown.class);
	
	private static final String TAG = "cooldown";
	private long cooldown;
	
	public long getCooldown() {
		return cooldown;
	}
	public void setCooldown(long cooldown) {
		this.cooldown = cooldown;
	}

	/**
	 * @param cooldown The cooldown in mileseconds
	 */
	public LandmarkCoolDown(long cooldown) {
		this.cooldown = cooldown;
	}

	@Override
	public Advice getLandMarkAdvice() {
		return new ContinueAdvice();
	}
	
	public static String getTag() {
		return TAG;
	}

	@Override
	public Integer getTimeout() {
		String msg = "Methdo getTimeout for LandmarkCoolDown not implemented";
		log.error(msg);
		throw new RuntimeException(msg);
	}
	
	@Override
	public LandmarkStrategies getStrategy() {
		return null;
	}
	
	@Override
	public ExpectedCondition<WebElement> landmarkAchiveable(NndlWebDriver remoteWebDriver,
			LandmarkStrategies strategies) {
		return null;
	}
	
}
