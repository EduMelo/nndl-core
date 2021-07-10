package dev.edumelo.com.nndl_core.scroll;

import java.util.Collection;

import dev.edumelo.com.nndl_core.ExtractDataBind;
import dev.edumelo.com.nndl_core.step.Step;
import dev.edumelo.com.nndl_core.step.StepElement;
import dev.edumelo.com.nndl_core.step.StepRunner;
import dev.edumelo.com.nndl_core.webdriver.IterationContent;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriverWaiter;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class InfiniteScrollAdapter {
	private SeleniumSndlWebDriver webDriver;
    private SeleniumSndlWebDriverWaiter webDriverWait;
    private Step iterationStep;
    private StepRunner runner;
    private InfiniteScrollCondition infiniteScrollCondition;
    private Collection<ExtractDataBind> extractDataBindCollection;
    private IterationContent rootElement;
    private StepElement element;
	
}
