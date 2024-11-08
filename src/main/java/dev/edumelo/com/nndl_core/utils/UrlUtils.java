package dev.edumelo.com.nndl_core.utils;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;

public class UrlUtils {

	public static Optional<URL> createUrl(String urlString) {
		try {
			return Optional.of(new URI(urlString).toURL());
		} catch (MalformedURLException | URISyntaxException e) {
			throw new RuntimeException(String.format("Cannot create URL from the string: %s ",
					urlString));
		}
	}
}
