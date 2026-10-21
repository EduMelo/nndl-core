package dev.edumelo.com.nndl_core.nndl.dhall;

import java.nio.file.Path;

/**
 * Typechecks a Dhall recipe with {@code dhall-to-json} and lowers the result
 * to the YAML shape consumed by {@code YamlNndlLoader}.
 */
public final class DhallNndlLoader {
	private final DhallToJson dhallToJson;
	private final LegacyNndlEncoder encoder;

	public DhallNndlLoader() {
		this(new DhallToJson(), new LegacyNndlEncoder());
	}

	public DhallNndlLoader(DhallToJson dhallToJson, LegacyNndlEncoder encoder) {
		this.dhallToJson = dhallToJson;
		this.encoder = encoder;
	}

	public EncodedNndl load(Path file) {
		String json = dhallToJson.convert(file);
		return encoder.encode(Json.parse(json));
	}
}
