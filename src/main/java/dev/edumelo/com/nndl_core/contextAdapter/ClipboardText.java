package dev.edumelo.com.nndl_core.contextAdapter;

import dev.edumelo.com.nndl_core.ExtractDataBind;
import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.step.advice.ContinueAdvice;

public class ClipboardText implements ExtractDataBind  {
	private final Advice advice = new ContinueAdvice();
	private String text;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public Advice suggestedAdvice() {
		return advice;
	}

}
