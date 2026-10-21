package dev.edumelo.com.nndl_core.webdriver;

import java.util.List;

@FunctionalInterface
public interface NndlElementsCondition {

	List<NndlElement> apply();

}
