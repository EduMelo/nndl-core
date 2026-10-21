package dev.edumelo.com.nndl_core.nndl.dhall;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import dev.edumelo.com.nndl_core.exceptions.unchecked.NndlRuntimeException;

/**
 * Runs the reference {@code dhall-to-json} executable. Imports are resolved
 * from the recipe file's directory, so the recipe has to be a real file.
 */
public final class DhallToJson {
	static final String INSTALL_HINT = "dhall-to-json was not found on PATH. Install the dhall-json package "
			+ "(https://github.com/dhall-lang/dhall-haskell/releases).";

	private final String command;

	public DhallToJson() {
		this("dhall-to-json");
	}

	public DhallToJson(String command) {
		this.command = command;
	}

	public String convert(Path file) {
		if (file == null) {
			throw new NndlRuntimeException("Dhall file path is required.");
		}
		ProcessBuilder builder = new ProcessBuilder(command, "--file", file.toAbsolutePath().toString());
		Process process;
		try {
			process = builder.start();
		} catch (IOException e) {
			throw new NndlRuntimeException(INSTALL_HINT, e);
		}

		StringBuilder stdout = new StringBuilder();
		StringBuilder stderr = new StringBuilder();
		Thread errorReader = new Thread(() -> drain(process.getErrorStream(), stderr), "dhall-to-json-stderr");
		errorReader.start();
		drain(process.getInputStream(), stdout);
		try {
			errorReader.join();
			boolean finished = process.waitFor(60, TimeUnit.SECONDS);
			if (!finished) {
				process.destroyForcibly();
				throw new NndlRuntimeException("dhall-to-json timed out for " + file);
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			process.destroyForcibly();
			throw new NndlRuntimeException("dhall-to-json was interrupted for " + file, e);
		}
		if (process.exitValue() != 0) {
			throw new NndlRuntimeException(
					"dhall-to-json failed for " + file + ": " + stderr.toString().trim());
		}
		return stdout.toString();
	}

	private static void drain(InputStream stream, StringBuilder into) {
		try (stream) {
			into.append(new String(stream.readAllBytes(), StandardCharsets.UTF_8));
		} catch (IOException e) {
			into.append(e.getMessage());
		}
	}
}
