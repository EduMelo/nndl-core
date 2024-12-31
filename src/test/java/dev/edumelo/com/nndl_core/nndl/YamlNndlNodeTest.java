package dev.edumelo.com.nndl_core.nndl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

class YamlNndlNodeTest {
	private NndlNode stepNode;

    @BeforeEach
    public void setUp() {
        String yamlContent = 
                "steps:\n" +
                "  - name: closeDialog\n" +
                "    elements:\n" +
                "      - name: dialog_close_button\n" +
                "        matchExp: \"div[role='dialog'] button[aria-label]\"\n" +
                "    actions:\n" +
                "      - order: 1\n" +
                "        elementClick: dialog_close_button\n" +
                "        timeout: 200\n"+
                "    subSteps:    \n"+
                "    - name: fillElement\n"+
                "      elements:\n"+
                "        - name: $activeElement\n"+
                "        - name: aria\n"+
                "          matchExp: \"[aria-labelledby]\"\n"+
                "      actions:      \n"+
                "      - order: 1\n"+
                "        sendKey: ARROW_DOWN\n"+
                "        targetElement: $activeElement\n"+
                "        requirementStatus:\n"+
                "              type: nonRequired\n"+
                "              stepTreatment: retreat";

        Yaml yaml = new Yaml(new NndlConstructor(yamlContent));
        Map<String, NndlNode> nndlMap = yaml.load(yamlContent);

        stepNode = nndlMap.get("steps");
    }
	
	@Test
    public void testStepScalarIsAlwaysScalar() {
        Optional<String> stepName = stepNode.getScalarValueFromChild("name");

        assertTrue(stepName.isPresent(), "O nome do Step deveria estar presente");
        assertEquals("closeDialog", stepName.get(), "O nome do Step deveria ser 'closeDialog'");
    }
	
	@Test
    public void testStepListIsPresent() {
        Optional<List<NndlNode>> nodeList = stepNode.getListedValuesFromChild("elements");

        assertTrue(nodeList.isPresent(), "A lista de nós deveria estar presente");
        assertEquals(1, nodeList.get().size(), "A lista deveria conter 1 item");
    }
	
	@Test
    public void testStepMapIsPresent() {
        Optional<List<NndlNode>> nodeList = stepNode.getListedValuesFromChild("subSteps");

        assertTrue(nodeList.isPresent(), "A lista de nós deveria estar presente");
        assertEquals(1, nodeList.get().size(), "A lista deveria conter 1 item");
    }
	
	@Test
    public void testStepMapHierarchicIsPresent() {
		Optional<String> nodeValue = stepNode.getListedValuesFromChild("subSteps")
        	.map(lst -> lst.get(0))
        	.flatMap(node -> node.getValueFromChild("actions"))
        	.flatMap(node -> node.getValueFromChild("requirementStatus"))
        	.flatMap(node -> node.getScalarValueFromChild("type"));

        assertTrue(nodeValue.isPresent(), "O type do requirementStatus deveria estar presente");
        assertEquals("nonRequired", nodeValue.get(), "O type do requirementStatus deveria ser nonRequired");
    }
}
