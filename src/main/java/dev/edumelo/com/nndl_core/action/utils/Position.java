package dev.edumelo.com.nndl_core.action.utils;

import dev.edumelo.com.nndl_core.exceptions.NndlParserException;
import dev.edumelo.com.nndl_core.nndl.NndlNode;

public class Position {
	
	private static final String X_TAG = "x";
	private static final String Y_TAG = "y";
	private Integer x;
	private Integer y;

	public Integer getX() {
		return x;
	}
	public void setX(Integer x) {
		this.x = x;
	}
	public Integer getY() {
		return y;
	}
	public void setY(Integer y) {
		this.y = y;
	}
	public static String getxTag() {
		return X_TAG;
	}
	public static String getyTag() {
		return Y_TAG;
	}

	public Position(NndlNode mappedPosition) {
		setX(mappedPosition.getScalarValueFromChild(X_TAG, Integer.class)
				.orElseThrow(() -> new NndlParserException("Position without x value", mappedPosition)));
		setY(mappedPosition.getScalarValueFromChild(Y_TAG, Integer.class)
				.orElseThrow(() -> new NndlParserException("Position without y value", mappedPosition)));
	}
}
