package dev.edumelo.com.nndl_core.action.landmark;

import org.openqa.selenium.By;

import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.step.advice.ContinueAdvice;
import dev.edumelo.com.nndl_core.webdriver.NndlWebDriver;
import lombok.Data;

@Data
//XXX Retornar
//@Slf4j
public class LandmarkCoolDown implements Landmark {
	private static final String TAG = "cooldown";
	private long cooldown;
	
	/**
	 * @param cooldown The cooldown in mileseconds
	 */
	public LandmarkCoolDown(long cooldown) {
		this.cooldown = cooldown;
	}
	
	@Override
	public By getLocator(NndlWebDriver remoteWebDriver) {
		return null;
	}

	@Override
	public Advice getLandMarkAdvice() {
		return new ContinueAdvice();
	}
	
	public static Object getTag() {
		return TAG;
	}

	@Override
	public Integer getTimeout() {
		String msg = "Methdo getTimeout for LandmarkCoolDown not implemented";
		//XXX Retornar
//		log.error(msg);
		throw new RuntimeException(msg);
	}
}
