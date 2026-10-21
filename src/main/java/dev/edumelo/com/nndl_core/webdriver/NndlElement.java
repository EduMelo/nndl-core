package dev.edumelo.com.nndl_core.webdriver;

public interface NndlElement extends NndlSearchContext {

	void click();

	void sendText(CharSequence text);

	void sendChord(String... parts);

	int getX();

	int getY();

	boolean isDisplayed();

	boolean isEnabled();

	NndlSearchContext shadowRoot();

}
