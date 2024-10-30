package dev.edumelo.com.nndl_core;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import dev.edumelo.com.nndl_core.contextAdapter.ThreadLocalManager;

public class NndlParser {
	private static final Logger log = LoggerFactory.getLogger(NndlParser.class);
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getYamlStack(ArrayList<String> nndlStack) {
		if(CollectionUtils.isEmpty(nndlStack)) {
			throw new RuntimeException("There should have at last one item on nndlStack");
		}
		
		Yaml yaml = new Yaml();
		String yamlString = replaceTags(nndlStack.get(0), ThreadLocalManager.getVariableSubstitutionMap());
		Map<String, Object> loadedYaml = yaml.load(yamlString);
		for (int i = 1; i < nndlStack.size(); i++) {
			String yamlImportString = replaceTags(nndlStack.get(i), ThreadLocalManager.getVariableSubstitutionMap());
			Map<String, Object> importMap = yaml.load(yamlImportString);
			List<Object> originalSteps = (List<Object>) loadedYaml.get("steps");
			List<Object> importedSteps = (List<Object>) importMap.get("steps");
			originalSteps.addAll(importedSteps);
		}
		
		return loadedYaml;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getYamlStack(String yamlString) {
		Yaml yaml = new Yaml();
		Map<String, Object> loadedYaml = yaml.load(yamlString);
		List<String> imports = getImports(yamlString);
		for (String sndlImport : imports) {
			String file = getYamlString(sndlImport);
			Map<String, Object> importMap = yaml.load(file);
			List<Object> originalSteps = (List<Object>) loadedYaml.get("steps");
			List<Object> importedSteps = (List<Object>) importMap.get("steps");
			originalSteps.addAll(importedSteps);
		}
		return loadedYaml;
	}

	@SuppressWarnings("unchecked")
	public List<String> getImports(String yamlString) {
		Yaml yaml = new Yaml();
		Map<String, Object> loadedYaml = yaml.load(yamlString);
		List<String> imports = new ArrayList<>();
		Object imp = loadedYaml.get("import");
		
		if(imp instanceof String) {
			imports.add((String) imp);
		} else if(imp instanceof List) {
			imports = (List<String>) imp;
		}
		return imports;
	}
	
	public String getYamlString(String sndlFile) {
		InputStream input = this.getClass()
				.getClassLoader()
				.getResourceAsStream(sndlFile);
		
		try {		
			String yamlString = IOUtils.toString(input, "UTF-8");
			return replaceTags(yamlString, ThreadLocalManager.getVariableSubstitutionMap());
		} catch (IOException e) {
			String msg = "";
			log.error(msg);
			throw new RuntimeException(msg, e);
		}
	}
	
	private String replaceTags(String yamlString, Map<String, Object> variableSubstitutionMap) {
		StrSubstitutor ss = new StrSubstitutor(variableSubstitutionMap);		
		return ss.replace(yamlString);
	}

}
