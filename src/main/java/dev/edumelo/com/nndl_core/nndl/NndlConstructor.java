package dev.edumelo.com.nndl_core.nndl;

import java.util.HashMap;
import java.util.Map;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;

public class NndlConstructor extends Constructor {
    private final String yamlContent;

    public NndlConstructor(String yamlContent) {
        super(new LoaderOptions());
        this.yamlContent = yamlContent;
    }

    @Override
    protected Map<Object, Object> constructMapping(MappingNode node) {
        Map<Object, Object> result = new HashMap<>();
        String[] lines = yamlContent.split("\n");

        for (NodeTuple tuple : node.getValue()) {
            Node keyNode = tuple.getKeyNode();
            Node valueNode = tuple.getValueNode();

            // Verifica se a chave é um ScalarNode
            if (!(keyNode instanceof ScalarNode)) {
                throw new IllegalArgumentException("Key node must be a ScalarNode. Found: " + keyNode.getClass());
            }

            // Extrai informações de linhas
            int startLine = valueNode.getStartMark().getLine() + 1;
            int endLine = valueNode.getEndMark().getLine() + 1;
            String previousLine = startLine > 1 && startLine - 2 < lines.length ? lines[startLine - 2] : null;
            String nextLine = endLine < lines.length ? lines[endLine] : null;

            // Constrói e adiciona o NndlNode ao map
            String key = ((ScalarNode) keyNode).getValue();
            NndlNode value = new NndlNode(key, valueNode, startLine, endLine, previousLine, nextLine);
            result.put(key, value);
        }

        return result;
    }

}
