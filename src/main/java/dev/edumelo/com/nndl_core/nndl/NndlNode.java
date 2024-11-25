package dev.edumelo.com.nndl_core.nndl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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

public class NndlNode {
	private String name;
	private NodeValue value;
	private Mark start;
    private Mark end;
    private List<String> lines;
    private String parentNodeName;
    private NndlChild children;
    private Map<String, String> variableSubstitutionMap;
	
    public NndlNode(String name, Node node, Mark start, Mark end, String parentNodeName, List<String> lines) {
    	this.name = name;
    	this.start = start;
		this.end = end;
		this.parentNodeName = parentNodeName;
		this.lines = lines;
		
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
    
    public String getName() {
    	return name;
    }
	public NodeValue getValue() {
		return value;
	}	
	public Mark getStart() {
		return start;
	}
	public Mark getEnd() {
		return end;
	}
	public String getParentNodeName() {
		return parentNodeName;
	}
	public void setParentNodeName(String parentNodeName) {
		this.parentNodeName = parentNodeName;
	}

	public List<String> getLines() {
		return lines;
	}
	public String getConcatenadedLines() {
		if(CollectionUtils.isEmpty(lines)) {
			return null;
		}
		return lines.stream().collect(Collectors.joining("\n"));
	}
	public NndlChild getChildren() {
		return children;
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

            NndlNode child = new NndlNode(key, childNode, childStart, childEnd, parentNodeName, childlines);
            ((NndlMapChild) children).put(key, child);
        }
    }
	
	private void constructChildren(SequenceNode sequenceNode) {
		for (int i = 0; i < sequenceNode.getValue().size(); i++) {
			String parentNodeName = this.parentNodeName.replace(":", "");
			Node childNode = sequenceNode.getValue().get(i);
			Mark childStart = childNode.getStartMark();
            Mark childEnd = childNode.getEndMark();
            int startIndex = start.getLine();
            int childStartIndex = childStart.getLine()-startIndex;
            int childEndIndex = childEnd.getLine()-startIndex;
            List<String> childlines = lines.subList(childStartIndex, childEndIndex);
			
			NndlNode child = new NndlNode(parentNodeName+"List"+i, childNode, childStart, childEnd, parentNodeName, childlines);
			((NndlListChild) children).add(child);			
		}
    }
	
	public <T> void mergeNodes(NndlNode otherNode) {
		children.merge(otherNode.children);
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
	
	public boolean hasValueFromChild(String key) {
		return getValueFromChild(key).isPresent();
	}
	
	public Optional<NndlNode> getValueFromChild(String key) {
		if(children instanceof NndlListChild) {
			return getValueFromListChild(key);
		} else if(children instanceof NndlMapChild) {
			return getValueFromMapChild(key);
		}
		throw new NndlParserRuntimeException("It wasn't possible to verify node children type. Children: "+children, this);
	}
	
	public Optional<NndlNode> getValueFromListChild(String key) {
		NndlListChild listChild = (NndlListChild) children;
        for (NndlNode childNode : listChild) {
        	NndlMapChild childMap = (NndlMapChild) childNode.getChildren();
        	if (childMap.containsKey(key)) {
        		return Optional.of((NndlNode) childMap.get(key));
        	}
		}
        return Optional.empty();
    }
	
	public Optional<NndlNode> getValueFromMapChild(String key) {
		NndlMapChild listMap = (NndlMapChild) children;
		return Optional.ofNullable((NndlNode) listMap.get(key));
    }
	
	public Optional<String> getScalarValue() {
        Optional<String> child = Optional.ofNullable(getValue())
        		.map(v -> ((ScalarNodeValue) v).getValue());
        return substituteVariables(child);
    }
	
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
	
	public Optional<String> getScalarValueFromChild(String key) {
		Optional<String> child = getValueFromChild(key)
				.filter(n -> n.getValue() instanceof ScalarNodeValue)
				.map(NndlNode::getValue)
				.map(v -> ((ScalarNodeValue) v).getValue());
        return substituteVariables(child);
    }
	
	@SuppressWarnings("unchecked")
	public Optional<List<NndlNode>> getListedValues() {
		return Optional.ofNullable((List<NndlNode>) getChildren());
    }
	
	@SuppressWarnings("unchecked")
	public Optional<List<NndlNode>> getListedValuesFromChild(String key) {
		return getValueFromChild(key)
                .map(v -> (List<NndlNode>) v.getChildren());
    }
	
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
		NndlNode other = (NndlNode) obj;
		return Objects.equals(children, other.children) && Objects.equals(end, other.end)
				&& Objects.equals(lines, other.lines) && Objects.equals(name, other.name)
				&& Objects.equals(parentNodeName, other.parentNodeName) && Objects.equals(start, other.start)
				&& Objects.equals(value, other.value)
				&& Objects.equals(variableSubstitutionMap, other.variableSubstitutionMap);
	}

	public String exceptionMessage() {
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

	@Override
	public String toString() {
		return "NndlNode [name=" + name + ", parentNodeName=" + parentNodeName + ", variableSubstitutionMap="
				+ variableSubstitutionMap + "]";
	}

}
