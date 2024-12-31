package dev.edumelo.com.nndl_core.nndl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.yaml.snakeyaml.error.Mark;

public abstract class NndlNode {
	
	/**
	 * Retrieves the start mark
	 * 
	 * @return the {@code Mark} representing the start of this node.
	 */
	public abstract Mark getStart();
	
	/**
	 * Retrieves the end mark
	 * 
	 * @return the {@code Mark} representing the end of this node.
	 */
	public abstract Mark getEnd();
	
	/**
	 * Retrieves the lines
	 * 
	 * @return the {@code List} representing the lines of this node.
	 */
	public abstract List<String> getLines();
	
	/**
	 * Retrieves the lines as a single concatenated line
	 * 
	 * @return the {@code List} representing the lines of this node.
	 */
	public abstract String getConcatenadedLines();
	
	/**
     * Retrieves the value associated with this node.
     * 
     * @return the {@code NodeValue} representing the value of this node.
     */
	public abstract NodeValue getValue();
	
	/**
     * Retrieves the child container associated with this node.
     * 
     * @return the {@code NndlChild} representing the child container of this node.
     */
	public abstract NndlChild getNndlChild();
	
	/**
	 * Retrieves if a child node with this key exists.
	 * 
	 * @param key the key of the child node to retrieve.
	 * @return a boolean value
	 */
	public abstract boolean hasValueFromChild(String key);
	
	/**
     * Retrieves a child node by its key, regardless of whether the children are stored
     * as a list or a map.
     * 
     * @param key the key of the child node to retrieve.
     * @return an {@code Optional} containing the child node if found, or empty otherwise.
     * @throws NndlParserRuntimeException if the children type cannot be determined.
     */
	public abstract Optional<NndlNode> getValueFromChild(String key);
	
	/**
     * Retrieves a child node by its key when the children are stored as a list.
     * 
     * @param key the key of the child node to retrieve.
     * @return an {@code Optional} containing the child node if found, or empty otherwise.
     */
	public abstract Optional<NndlNode> getValueFromListChild(String key);
	
	/**
     * Retrieves a child node by its key when the children are stored as a map.
     * 
     * @param key the key of the child node to retrieve.
     * @return an {@code Optional} containing the child node if found, or empty otherwise.
     */
	public abstract Optional<NndlNode> getValueFromMapChild(String key);
	
	/**
     * Retrieves the scalar value of the current node, applying variable substitution if applicable.
     * 
     * @return an {@code Optional} containing the scalar value, or empty if the node does not have a scalar value.
     */
	public abstract Optional<String> getScalarValue();
	
	/**
     * Retrieves the scalar value of a child node by its key and converts it to the specified type.
     * 
     * @param <T>   the target type of the scalar value.
     * @param key   the key of the child node.
     * @param clazz the class of the target type.
     * @return an {@code Optional} containing the converted value, or empty if conversion fails.
     */
	public abstract <T> Optional<T> getScalarValueFromChild(String key, Class<T> clazz);
	
	/**
     * Retrieves the scalar value of a child node by its key, applying variable substitution if applicable.
     * 
     * @param key the key of the child node.
     * @return an {@code Optional} containing the scalar value, or empty if not found or not scalar.
     */
	public abstract Optional<String> getScalarValueFromChild(String key);
	
	/**
     * Retrieves the children of the current node as a list.
     * 
     * @return an {@code Optional} containing a list of child nodes, or empty if there are no children.
     */
	public abstract Optional<List<NndlNode>> getListedValues();
	
	/**
     * Retrieves the children of a child node identified by a key as a list.
     * 
     * @param key the key of the child node.
     * @return an {@code Optional} containing a list of child nodes, or empty if not found.
     */
	public abstract Optional<List<NndlNode>> getListedValuesFromChild(String key);
	
	/**
	 * Retrieves the exception message of the current node
	 * 
	 * @return an {@code String} containing the exception message.
	 */
	public abstract String getExceptionMessage();
	
	/**
     * Sets the variable substitution map for this node, allowing dynamic resolution
     * of variables within scalar values.
     * 
     * @param variableSubstitutionMap the map containing variable substitutions.
     */
	public abstract void setVariableSubstitutionMap(Map<String, String> variableSubstitutionMap);
	
	/**
	 * Extracts a list of scalar values from a child node identified by a key and maps each value
	 * using the provided mapper function.
	 * 
	 * @param <T>    the type of the mapped values.
	 * @param key    the key of the child node to extract the list from.
	 * @param mapper a function to map each scalar value to the desired type.
	 * @return a {@code List<T>} containing the mapped values, or an empty list if no values are found.
	 */
	public abstract <T> List<T> extractListOfValues(String key, Function<String, T> mapper);
	
	/**
	 * Extracts a list of scalar values from a child node identified by a key.
	 *
	 * @param key the key of the child node to extract the list from.
	 * @return a {@code List<String>} containing the scalar values, or an empty list if no values are found.
	 */
	public abstract List<String> extractScalarList(String key);
    
    /**
     * Extracts a single scalar value from a child node identified by a key, or returns a default value if not found.
     * 
     * @param key          the key of the child node.
     * @param defaultValue the default value to return if the scalar value is not found.
     * @return the scalar value as a {@code String} or the default value.
     */
    public abstract String extractSingleValue(String key, String defaultValue);
    
    /**
     * Extracts an optional scalar value from a child node identified by a key.
     * 
     * @param key the key of the child node.
     * @return an {@code Optional<String>} containing the scalar value if found, or empty otherwise.
     */
    public abstract Optional<String> extractOptionalValue(String key);
    
    /**
     * Retrieves a list of child nodes for a child node identified by a key.
     * 
     * @param key the key of the child node.
     * @return a {@code List<NndlNode>} of child nodes, or an empty list if none are found.
     */
    public abstract List<NndlNode> extractChildNodes(String key);
    
    /**
     * Maps child nodes of a child node identified by a key to another type using a mapper function.
     * 
     * @param <T>    the target type to map to.
     * @param key    the key of the child node.
     * @param mapper the function to map each child node.
     * @return a {@code List<T>} containing the mapped values.
     */
    public abstract <T> List<T> mapChildNodes(String key, Function<NndlNode, T> mapper);

    /**
     * Merges the children of the current node with the children of another node.
     * 
     * @param otherNode the {@code NndlNode} whose children are to be merged with the current node's children.
     * @param <T>       the type of the child container ({@code NndlChild}).
     * @throws NullPointerException if the {@code otherNode} or its children are null.
     */
	public abstract <T> void mergeNodes(NndlNode otherNode);
	
}
