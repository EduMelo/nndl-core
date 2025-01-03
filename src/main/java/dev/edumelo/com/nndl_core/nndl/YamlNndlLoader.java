package dev.edumelo.com.nndl_core.nndl;

import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class YamlNndlLoader {

	public NndlNode load(String nndlName, String manifestString) {
		Yaml yaml = new Yaml(new NndlConstructor(nndlName, manifestString));
		Map<String, NndlNode> nndlMap = yaml.load(manifestString);
		return new YamlNndlNode("root", nndlMap, nndlName);
	}
	
}
