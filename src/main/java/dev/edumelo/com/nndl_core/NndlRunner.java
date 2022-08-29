package dev.edumelo.com.nndl_core;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import com.google.common.collect.ImmutableSet;

import dev.edumelo.com.nndl_core.contextAdapter.ContextAdapter;
import dev.edumelo.com.nndl_core.contextAdapter.ContextAdapterHandler;
import dev.edumelo.com.nndl_core.step.Step;
import dev.edumelo.com.nndl_core.step.StepRunner;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriverWaiter;

public class NndlRunner {
	
	private static final Logger log = LoggerFactory.getLogger(NndlRunner.class);
	
	private static final String ENTRY_STEP_TAG = "entryStep";
	private static final String ASYNCHRONOUS_STEPS_TAG = "asynchronousSteps";

	private final SeleniumSndlWebDriver webDriver;
	private final SeleniumSndlWebDriverWaiter webDriverWait;
	
	public NndlRunner(SeleniumSndlWebDriver webDriver, SeleniumSndlWebDriverWaiter webDriverWait) {
		this.webDriver = webDriver;
		this.webDriverWait = webDriverWait;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public Collection<ExtractDataBind> run(String sndlFile, Collection extractDataBindList,
			Map<String, Object> variableSubstitutionMap, ContextAdapter... adapters) {
		String sessionId = UUID.randomUUID().toString();
		ContextAdapterHandler.createContext(sessionId, adapters);
		
		String yamlString = getYamlString(sndlFile, variableSubstitutionMap); 
		Map<String, Object> yaml = getYamlMap(yamlString, variableSubstitutionMap);
		
		Map<String, Step> steps = instantiateSteps((List<Map<String, ?>>) yaml.get(Step.getTag()));
		Collection<String> asynchronousStepsNames = getAsynchronousStepNames(yaml);
		
		StepRunner stepRunner = new StepRunner(sessionId, webDriver, webDriverWait,
				extractDataBindList);
		if(asynchronousStepsNames != null) {
			Map<String, Step> asynchronousSteps = extractedAsynchronousSteps(steps, asynchronousStepsNames);			
			stepRunner.runAsynchronousSteps(null, asynchronousSteps);
		}
		return stepRunner.runSteps((String) yaml.get(ENTRY_STEP_TAG), steps, extractDataBindList);
	}
	
	@SuppressWarnings("unchecked")
	private Collection<String> getAsynchronousStepNames(Map<String, Object> yaml) {
		Object asynchronousStepNames = yaml.get(ASYNCHRONOUS_STEPS_TAG);
		if(asynchronousStepNames instanceof String) {
			return ImmutableSet.of((String) asynchronousStepNames);
		} else if(asynchronousStepNames instanceof List) {
			return (List<String>) asynchronousStepNames;
		}
		
		return null;
	}
	
	private Map<String, Step> extractedAsynchronousSteps(Map<String, Step> steps, Collection<String> asynchronousStepsNames) {
		Map<String, Step> asynchronousSteps = new HashMap<String, Step>();
		
		for (String stepName : asynchronousStepsNames) {
			asynchronousSteps.put(stepName, steps.remove(stepName));
		}
		
		return asynchronousSteps;
	}

	private Map<String, Step> instantiateSteps(List<Map<String, ?>> stepList) {
		return stepList.stream()
				.map(Step::new)
				.collect(Collectors.toMap(Step::getName, Function.identity()));
	}

	private String replaceTags(String yamlString, Map<String, Object> variableSubstitutionMap) {
		StrSubstitutor ss = new StrSubstitutor(variableSubstitutionMap);		
		return ss.replace(yamlString);
	}

	private String getYamlString(String sndlFile, Map<String, Object> variableSubstitutionMap) {
		InputStream input = this.getClass()
				.getClassLoader()
				.getResourceAsStream(sndlFile);
		
		try {		
			String yamlString = IOUtils.toString(input, "UTF-8");
			return replaceTags(yamlString, variableSubstitutionMap);
		} catch (IOException e) {
			String msg = "";
			log.error(msg);
			throw new RuntimeException(msg, e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, Object> getYamlMap(String yamlString, Map<String, Object> variableSubstitutionMap) {
		Yaml yaml = new Yaml();
		Map<String, Object> loadedYaml = yaml.load(yamlString);
		Object imp = loadedYaml.get("import");
		
		List<String> imports = new ArrayList<>();
		if(imp instanceof String) {
			imports.add((String) imp);
		} else if(imp instanceof List) {
			imports = (List<String>) imp;
		}
		
		for (String sndlImport : imports) {
			String file = getYamlString(sndlImport, variableSubstitutionMap);
			Map<String, Object> importMap = yaml.load(file);
			List<Object> originalSteps = (List<Object>) loadedYaml.get("steps");
			List<Object> importedSteps = (List<Object>) importMap.get("steps");
			originalSteps.addAll(importedSteps);
		}
		
		return loadedYaml;
	}

}
