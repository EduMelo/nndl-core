package dev.edumelo.com.nndl_core.nndl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import dev.edumelo.com.nndl_core.step.Step;
import dev.edumelo.com.nndl_core.webdriver.SeleniumHubProperties;

public class Nndl {
	private Logger log = LoggerFactory.getLogger(Nndl.class);
	private static final String STEPS_TAG = "steps";
	private static final String ASYNCHRONOUS_STEPS_TAG = "asynchronousSteps";
	private static final String ENTRY_STEP_TAG = "entryStep";
	
	private String name;
	private String value;
	private List<Nndl> imports;
	private Map<String, NndlNode> nndlMap;
	private NndlNode stepsNode;
	
	public Nndl() {
	}
	
	public Nndl(String nndlFile) {
		InputStream input = this.getClass()
				.getClassLoader()
				.getResourceAsStream(nndlFile);
		
		try {		
			String yamlString = IOUtils.toString(input, "UTF-8");
			setValue(yamlString);
			loadNndlMap();
		} catch (IOException e) {
			throw new RuntimeException("It wasn't possible to load the file: "+nndlFile, e);
		}
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		if(value == null) {
			return;
		}
		this.value = value;
	}
	public List<Nndl> getImports() {
		return imports;
	}
	public void setImports(List<Nndl> imports) {
		this.imports = imports;
	}
	public void addImport(Nndl importNndl) {
		imports = imports == null ? new ArrayList<>() : imports;
		imports.add(importNndl);		
	}
	
	public void loadNndlMap() {
		log.debug("loadNndlMap. Nndl: "+toString());
		if(value == null) {
			throw new RuntimeException("NndlMap cannot be loaded because value is empty.");
		}
		Yaml yaml = new Yaml(new NndlConstructor(value));
		nndlMap = yaml.load(value);
		
		if(CollectionUtils.isNotEmpty(imports)) {
			for (Nndl nndl : imports) {
				nndl.loadNndlMap();
			}			
		}
	}
	
	public Optional<List<String>> extractImportsNames() {
		List<String> nodes = nndlMap.get("imports")
				.getListedValues()
				.orElse(new ArrayList<NndlNode>())
				.stream()
				.map(NndlNode::getScalarValue)
				.filter(n -> !n.isEmpty())
				.map(Optional::get)
				.collect(Collectors.toList());
		return Optional.ofNullable(nodes);
	}
	
	public boolean hasAsyncSteps() {
		return nndlMap.containsKey(ASYNCHRONOUS_STEPS_TAG);
	}
	
	private List<String> getAsyncStepsNames() {
		NndlNode assyncSteps = nndlMap.get(ASYNCHRONOUS_STEPS_TAG);
		if(assyncSteps == null) {
			return new ArrayList<String>();
		}
		CollectionNodeValue collectionNodeValue = (CollectionNodeValue) assyncSteps.getValue();
		return collectionNodeValue.getValue().stream()
				.map(o -> o.toString())
				.collect(Collectors.toList());
	}
	
	public NndlNode loadSteps(Map<String, String> variableSubstitutionMap) {
		//track2
		NndlNode stepsNode = (NndlNode) nndlMap.get(STEPS_TAG);
		stepsNode.setVariableSubstitutionMap(variableSubstitutionMap);
		if(imports != null) {
			imports.stream().forEach(i -> stepsNode.mergeNodes(i.loadSteps(variableSubstitutionMap)));			
		}
		this.stepsNode = stepsNode;
		return stepsNode;
	}
	
	public Map<String, Step> getAsyncStepMap(SeleniumHubProperties seleniumHubProperties) {
		List<String> asyncStepsNames = getAsyncStepsNames();
		
		return this.stepsNode
				.getListedValues()
				.get()
				.stream()
				.map(s -> new Step(seleniumHubProperties, (NndlNode) s))
				.filter(s -> asyncStepsNames.contains(s.getName()))
				.collect(Collectors.toMap(Step::getName, Function.identity()));
	}	

	public Map<String, Step> getStepMap(SeleniumHubProperties seleniumHubProperties) {
		List<String> asyncStepsNames = getAsyncStepsNames();
		
		return this.stepsNode
				.getListedValues()
				.get()
				.stream()
				.map(s -> new Step(seleniumHubProperties, (NndlNode) s))
				.filter(s -> !asyncStepsNames.contains(s.getName()))
				.collect(Collectors.toMap(Step::getName, Function.identity()));
	}
	
	public String getEntryStep() {
		NndlNode entryStepNode = (NndlNode) nndlMap.get(ENTRY_STEP_TAG);
		return ((ScalarNodeValue) entryStepNode.getValue()).getValue();
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(imports, name, value);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Nndl other = (Nndl) obj;
		return Objects.equals(imports, other.imports) && Objects.equals(name, other.name)
				&& Objects.equals(value, other.value);
	}
	
	@Override
	public String toString() {
		return "Nndl [name=" + name + ", value=" + value + ", imports=" + imports + "]";
	}
	
}
