package dev.edumelo.com.nndl_core.nndl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

import dev.edumelo.com.nndl_core.contextAdapter.ThreadLocalManager;
import dev.edumelo.com.nndl_core.exceptions.unchecked.NndlParserRuntimeException;

public class YamlNndlNode extends NndlNode {
	private String name;
	private NodeValue value;
	private Mark start;
    private Mark end;
    private List<String> lines;
    private String parentNodeName;
    private NndlChild children;
    private Map<String, String> variableSubstitutionMap;
    private String nndlName;
	
    public YamlNndlNode(String name, Node node, Mark start, Mark end, String parentNodeName, List<String> lines,
    		String nndlName) {
    	this.name = name;
    	this.start = start;
		this.end = end;
		this.parentNodeName = parentNodeName;
		this.lines = lines;
		this.nndlName = nndlName;
		
		if (node instanceof ScalarNode) {
            value = new ScalarNodeValue(((ScalarNode) node).getValue());
        } else if (node instanceof MappingNode) {
            value = new MappingNodeValue(((MappingNode) node).getValue());
            if(children == null) {
            	children = new NndlMapChild();            	
            }
            constructChildren((MappingNode) node);
        } else if (node instanceof SequenceNode) {
            value = new CollectionNodeValue(((SequenceNode) node).getValue());
            if(children == null) {
            	children = new NndlListChild();
            }
            constructChildren((SequenceNode) node);
        } else {
            throw new NndlParserRuntimeException("node type not recognized. Node: " + node, this);
        }
	}
    
    public YamlNndlNode(String name, Map<String, NndlNode> childrenMap, String nndlName) {
    	if(childrenMap == null || childrenMap.isEmpty()) {
    		throw new RuntimeException("childrenMap should have entries. childrenMap: " + childrenMap);
    	}
    	NndlNode firstChild = childrenMap.values().iterator().next();
    	NndlNode lastValue = null;

    	List<String> lines = new ArrayList<String>();
        for (NndlNode entry : childrenMap.values()) {
        	lines.addAll(entry.getLines());
        	lastValue = entry;
        }
    	
    	this.name = name;
    	this.start = firstChild.getStart();
		this.end = lastValue.getEnd();
		this.parentNodeName = "";
		this.lines = lines;
		this.nndlName = nndlName;
    	
		if(children == null) {
        	children = new NndlMapChild();            	
        }
		
        constructChildren(childrenMap);
    }
    
    public String getName() {
    	return name;
    }
	
	public String getParentNodeName() {
		return parentNodeName;
	}
	public void setParentNodeName(String parentNodeName) {
		this.parentNodeName = parentNodeName;
	}

	private void constructChildren(Map<String, NndlNode> mappingNode) {
        for (Entry<String, NndlNode> tuple : mappingNode.entrySet()) {
            String key = tuple.getKey();
            ((NndlMapChild) children).put(key, tuple.getValue());
        }
    }

	private void constructChildren(MappingNode mappingNode) {
        for (NodeTuple tuple : mappingNode.getValue()) {
        	String parentNodeName = this.parentNodeName.replace(":", "");
            String key = ((ScalarNode) tuple.getKeyNode()).getValue();
            Node childNode = tuple.getValueNode();
            Mark childStart = childNode.getStartMark();
            Mark childEnd = childNode.getEndMark();
            int startIndex = start.getLine();
            int childStartIndex = childStart.getLine()-startIndex;
            int childEndIndex = childEnd.getLine()-startIndex;
            List<String> childlines = lines.subList(childStartIndex, childEndIndex);

            NndlNode child = new YamlNndlNode(key, childNode, childStart, childEnd, parentNodeName, childlines,
            		nndlName);
            ((NndlMapChild) children).put(key, child);
        }
    }
	
	private void constructChildren(SequenceNode sequenceNode) {
		for (int i = 0; i < sequenceNode.getValue().size(); i++) {
			String parentNodeName = this.parentNodeName.replace(":", "").trim();
			Node childNode = sequenceNode.getValue().get(i);
			Mark childStart = childNode.getStartMark();
            Mark childEnd = childNode.getEndMark();
            int startIndex = start.getLine();
            int childStartIndex = childStart.getLine()-startIndex;
            int childEndIndex = childEnd.getLine()-startIndex;
            List<String> childlines = lines.subList(childStartIndex, childEndIndex);
			
			NndlNode child = new YamlNndlNode(nndlName + "." + parentNodeName+".list"+i, childNode, childStart,
					childEnd, parentNodeName, childlines, nndlName);
			((NndlListChild) children).add(child);			
		}
    }
	
	@Override
    public NodeValue getValue() {
    	return value;
    }
	
	@Override
	public Mark getStart() {
		return start;
	}
	
	@Override
	public Mark getEnd() {
		return end;
	}
	
	@Override
	public List<String> getLines() {
		return lines;
	}
	
	@Override
	public String getConcatenadedLines() {
		if(CollectionUtils.isEmpty(lines)) {
			return null;
		}
		return lines.stream().collect(Collectors.joining("\n"));
	}
	
	@Override
	public NndlChild getNndlChild() {
		return children;
	}
	
	@Override
	public boolean hasValueFromChild(String key) {
		return getValueFromChild(key).isPresent();
	}
	
	@Override
	public Optional<NndlNode> getValueFromChild(String key) {
		if(children instanceof NndlListChild) {
			return getValueFromListChild(key);
		} else if(children instanceof NndlMapChild) {
			return getValueFromMapChild(key);
		}
		throw new NndlParserRuntimeException("It wasn't possible to verify node children type. Children: "+children, this);
	}
	
	@Override
	public Optional<NndlNode> getValueFromListChild(String key) {
		NndlListChild listChild = (NndlListChild) children;
        for (NndlNode childNode : listChild) {
        	NndlMapChild childMap = (NndlMapChild) childNode.getNndlChild();
        	if (childMap.containsKey(key)) {
        		return Optional.of((NndlNode) childMap.get(key));
        	}
		}
        return Optional.empty();
    }
	
	@Override
	public Optional<NndlNode> getValueFromMapChild(String key) {
		NndlMapChild listMap = (NndlMapChild) children;
		return Optional.ofNullable((NndlNode) listMap.get(key));
    }
	
	@Override
	public Optional<String> getScalarValue() {
        Optional<String> child = Optional.ofNullable(getValue())
        		.map(v -> ((ScalarNodeValue) v).getValue());
        return substituteVariables(child);
    }
	
	@Override
	public <T> Optional<T> getScalarValueFromChild(String key, Class<T> clazz) {
        return getScalarValueFromChild(key)
        		.flatMap(v -> {
                    try {
                        Object convertedValue;
                        if (clazz == Integer.class) {
                            convertedValue = Integer.valueOf(v);
                        } else if (clazz == Boolean.class) {
                            convertedValue = Boolean.valueOf(v);
                        } else if (clazz == Long.class) {
                            convertedValue = Long.valueOf(v);
                        } else if (clazz == Double.class) {
                            convertedValue = Double.valueOf(v);
                        } else if (clazz == String.class) {
                            convertedValue = v;
                        } else {
                            return Optional.empty();
                        }
                        return Optional.of(clazz.cast(convertedValue));
                    } catch (NumberFormatException e) {
                        return Optional.empty();
                    }
                });
    }
	
	@Override
	public Optional<String> getScalarValueFromChild(String key) {
		Optional<String> child = getValueFromChild(key)
				.filter(n -> n.getValue() instanceof ScalarNodeValue)
				.map(NndlNode::getValue)
				.map(v -> ((ScalarNodeValue) v).getValue());
        return substituteVariables(child);
    }
	
	@Override
	@SuppressWarnings("unchecked")
	public Optional<List<NndlNode>> getListedValues() {
		return Optional.ofNullable((List<NndlNode>) getNndlChild());
    }
	
	@Override
	@SuppressWarnings("unchecked")
	public Optional<List<NndlNode>> getListedValuesFromChild(String key) {
		return getValueFromChild(key)
                .map(v -> (List<NndlNode>) v.getNndlChild());
    }
	
	@Override
	public void setVariableSubstitutionMap(Map<String, String> variableSubstitutionMap) {
		this.variableSubstitutionMap = variableSubstitutionMap;
		Collection<NndlNode> childrenCollection = null;
		if(children instanceof NndlListChild) {
			childrenCollection = (NndlListChild) children;
		} else if(children instanceof NndlMapChild) {
			childrenCollection = ((NndlMapChild) children).values();
		}
		if(childrenCollection != null) {
			childrenCollection.stream()
			.forEach(n -> n.setVariableSubstitutionMap(variableSubstitutionMap));			
		}
	}
	
	@Override
	public <T> List<T> extractListOfValues(String key, Function<String, T> mapper) {
	    return this.getValueFromChild(key)
	            .flatMap(NndlNode::getListedValues)
	            .map(children -> children.stream()
	                    .map(NndlNode::getScalarValue)
	                    .flatMap(Optional::stream)
	                    .map(mapper)
	                    .collect(Collectors.toList()))
	            .orElse(List.of());
	}
	
	@Override
	public List<String> extractScalarList(String key) {
	    return extractListOfValues(key, Function.identity());
	}

	@Override
	public String extractSingleValue(String key, String defaultValue) {
		return this.getValueFromChild(key)
                .flatMap(NndlNode::getScalarValue)
                .orElse(defaultValue);
	}

	@Override
	public Optional<String> extractOptionalValue(String key) {
		return this.getValueFromChild(key)
                .flatMap(NndlNode::getScalarValue);
	}

	@Override
	public List<NndlNode> extractChildNodes(String key) {
		return this.getValueFromChild(key)
                .flatMap(NndlNode::getListedValues)
                .orElse(List.of());
	}

	@Override
	public <T> List<T> mapChildNodes(String key, Function<NndlNode, T> mapper) {
		return this.extractChildNodes(key).stream()
                .map(mapper)
                .collect(Collectors.toList());
	}
	
	@Override
	public <T> void mergeNodes(NndlNode otherNode) {
		children.merge(otherNode.getNndlChild());
	}
	
	@Override
	public String getExceptionMessage() {
		String message = "\n"+ ThreadLocalManager.retrieveNndlName() +" - lines "+(getStart().getLine()+1)+" ~ "+
				(getEnd().getLine()+1)+" :\n ";
		for (String line : lines) {
			if(StringUtils.isEmpty(line.trim())) {
				continue;
			}
			message = message.concat(line).concat("\n");
		}
		return message;
	}
	
	public Optional<String> substituteVariables(Optional<String> child) {
	    Pattern pattern = Pattern.compile("\\$\\{(\\w+)}");

	    return child.map(value -> {
	        Matcher matcher = pattern.matcher(value);
	        StringBuffer result = new StringBuffer();

	        while (matcher.find()) {
	            String key = matcher.group(1);
	            String replacement = variableSubstitutionMap.getOrDefault(key, matcher.group(0)); // Keep original if no match
	            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
	        }
	        
	        matcher.appendTail(result);
	        return result.toString();
	    });
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(children, end, lines, name, parentNodeName, start, value, variableSubstitutionMap);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		YamlNndlNode other = (YamlNndlNode) obj;
		return Objects.equals(children, other.children) && Objects.equals(end, other.end)
				&& Objects.equals(lines, other.lines) && Objects.equals(name, other.name)
				&& Objects.equals(parentNodeName, other.parentNodeName) && Objects.equals(start, other.start)
				&& Objects.equals(value, other.value)
				&& Objects.equals(variableSubstitutionMap, other.variableSubstitutionMap);
	}

	@Override
	public String toString() {
		return "NndlNode [name=" + name + ", parentNodeName=" + parentNodeName + ", variableSubstitutionMap="
				+ variableSubstitutionMap + "]";
	}

}
