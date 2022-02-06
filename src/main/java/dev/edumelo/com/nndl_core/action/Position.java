package dev.edumelo.com.nndl_core.action;

import java.util.Map;

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

	public Position(Map<String, ?> mappedPosition) {
		setX(createX(mappedPosition));
		setY(createY(mappedPosition));
	}

	private Integer createY(Map<String, ?> mappedPosition) {
		Object yValue = mappedPosition.get(Y_TAG);
		if(yValue != null) {
			return (Integer) yValue;
		}
		return null;
	}

	private Integer createX(Map<String, ?> mappedPosition) {
		Object xValue = mappedPosition.get(X_TAG);
		if(xValue != null) {
			return (Integer) xValue;
		}
		return null;
	}
}
