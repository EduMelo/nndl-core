package dev.edumelo.com.nndl_core.webdriver;

import java.time.Duration;
import java.util.List;

public interface NndlWebDriverWaiter {

	NndlElement untilElement(Duration timeout, NndlElementCondition condition);

	List<NndlElement> untilElements(Duration timeout, NndlElementsCondition condition);

	void untilScriptReturns(Duration timeout, String script);

}
