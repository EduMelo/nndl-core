package dev.edumelo.com.nndl_core.webdriver;


public class IterationContent {
	private NndlElement rootElement;
	private int count;
	
	public IterationContent(NndlElement rootElement, int count) {
		this.rootElement = rootElement;
		this.count = count;
	}

	public NndlElement getRootElement() {
		return rootElement;
	}

	public int getCount() {
		return count;
	}
	
}
