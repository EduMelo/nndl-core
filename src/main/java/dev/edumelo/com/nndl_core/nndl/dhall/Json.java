package dev.edumelo.com.nndl_core.nndl.dhall;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import dev.edumelo.com.nndl_core.exceptions.unchecked.NndlRuntimeException;

/**
 * Parses the JSON subset emitted by {@code dhall-to-json}: objects, arrays,
 * strings, numbers, booleans and null. Objects keep their key order.
 */
public final class Json {
	private Json() {
	}

	public static Object parse(String text) {
		if (text == null) {
			throw new NndlRuntimeException("JSON text is required.");
		}
		Parser parser = new Parser(text);
		Object value = parser.parseValue();
		parser.skipWhitespace();
		if (!parser.isDone()) {
			throw new NndlRuntimeException("Unexpected trailing JSON at index " + parser.index);
		}
		return value;
	}

	private static final class Parser {
		private final String text;
		private int index;

		private Parser(String text) {
			this.text = text;
		}

		private boolean isDone() {
			return index >= text.length();
		}

		private Object parseValue() {
			skipWhitespace();
			if (isDone()) {
				throw new NndlRuntimeException("Unexpected end of JSON.");
			}
			char current = text.charAt(index);
			if (current == '{') {
				return parseObject();
			}
			if (current == '[') {
				return parseArray();
			}
			if (current == '"') {
				return parseString();
			}
			if (current == 't' || current == 'f') {
				return parseBoolean();
			}
			if (current == 'n') {
				return parseNull();
			}
			if (current == '-' || (current >= '0' && current <= '9')) {
				return parseNumber();
			}
			throw new NndlRuntimeException("Unexpected JSON character '" + current + "' at index " + index);
		}

		private Map<String, Object> parseObject() {
			expect('{');
			Map<String, Object> object = new LinkedHashMap<>();
			skipWhitespace();
			if (peek('}')) {
				index++;
				return object;
			}
			while (true) {
				skipWhitespace();
				String key = parseString();
				skipWhitespace();
				expect(':');
				object.put(key, parseValue());
				skipWhitespace();
				if (peek('}')) {
					index++;
					return object;
				}
				expect(',');
			}
		}

		private List<Object> parseArray() {
			expect('[');
			List<Object> array = new ArrayList<>();
			skipWhitespace();
			if (peek(']')) {
				index++;
				return array;
			}
			while (true) {
				array.add(parseValue());
				skipWhitespace();
				if (peek(']')) {
					index++;
					return array;
				}
				expect(',');
			}
		}

		private String parseString() {
			expect('"');
			StringBuilder builder = new StringBuilder();
			while (!isDone()) {
				char current = text.charAt(index++);
				if (current == '"') {
					return builder.toString();
				}
				if (current != '\\') {
					builder.append(current);
					continue;
				}
				if (isDone()) {
					throw new NndlRuntimeException("Unterminated JSON string escape.");
				}
				char escaped = text.charAt(index++);
				switch (escaped) {
					case '"':
					case '\\':
					case '/':
						builder.append(escaped);
						break;
					case 'b':
						builder.append('\b');
						break;
					case 'f':
						builder.append('\f');
						break;
					case 'n':
						builder.append('\n');
						break;
					case 'r':
						builder.append('\r');
						break;
					case 't':
						builder.append('\t');
						break;
					case 'u':
						if (index + 4 > text.length()) {
							throw new NndlRuntimeException("Short JSON unicode escape.");
						}
						int code = Integer.parseInt(text.substring(index, index + 4), 16);
						builder.append((char) code);
						index += 4;
						break;
					default:
						throw new NndlRuntimeException("Unknown JSON escape '\\" + escaped + "'.");
				}
			}
			throw new NndlRuntimeException("Unterminated JSON string.");
		}

		private Boolean parseBoolean() {
			if (text.startsWith("true", index)) {
				index += 4;
				return Boolean.TRUE;
			}
			if (text.startsWith("false", index)) {
				index += 5;
				return Boolean.FALSE;
			}
			throw new NndlRuntimeException("Invalid JSON boolean at index " + index);
		}

		private Object parseNull() {
			if (!text.startsWith("null", index)) {
				throw new NndlRuntimeException("Invalid JSON null at index " + index);
			}
			index += 4;
			return null;
		}

		private Number parseNumber() {
			int start = index;
			if (peek('-')) {
				index++;
			}
			if (isDone() || !isDigit(text.charAt(index))) {
				throw new NndlRuntimeException("Invalid JSON number at index " + start);
			}
			while (!isDone() && isDigit(text.charAt(index))) {
				index++;
			}
			boolean fractional = false;
			if (!isDone() && text.charAt(index) == '.') {
				fractional = true;
				index++;
				while (!isDone() && isDigit(text.charAt(index))) {
					index++;
				}
			}
			if (!isDone() && (text.charAt(index) == 'e' || text.charAt(index) == 'E')) {
				fractional = true;
				index++;
				if (!isDone() && (text.charAt(index) == '+' || text.charAt(index) == '-')) {
					index++;
				}
				while (!isDone() && isDigit(text.charAt(index))) {
					index++;
				}
			}
			String literal = text.substring(start, index);
			if (!fractional) {
				try {
					return Long.valueOf(literal);
				} catch (NumberFormatException e) {
					return Double.valueOf(literal);
				}
			}
			return Double.valueOf(literal);
		}

		private void skipWhitespace() {
			while (!isDone() && Character.isWhitespace(text.charAt(index))) {
				index++;
			}
		}

		private void expect(char expected) {
			skipWhitespace();
			if (isDone() || text.charAt(index) != expected) {
				throw new NndlRuntimeException("Expected '" + expected + "' at index " + index);
			}
			index++;
		}

		private boolean peek(char expected) {
			return !isDone() && text.charAt(index) == expected;
		}

		private boolean isDigit(char current) {
			return current >= '0' && current <= '9';
		}
	}
}
