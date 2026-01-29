package lotrec.dataStructure.tableau.action;

import lotrec.TestFixtures;
import lotrec.dataStructure.Logic;
import lotrec.dataStructure.tableau.Parameter;
import lotrec.dataStructure.tableau.ParameterType;
import lotrec.parser.OldiesTokenizer;
import lotrec.parser.exceptions.ParseException;
import lotrec.process.AbstractAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Vector;

import static org.assertj.core.api.Assertions.*;

/**
 * Comprehensive tests for the Action Registry in AbstractAction and action parsing.
 * Verifies all registered actions can be properly instantiated and parsed.
 */
@DisplayName("Action Registry")
class ActionRegistryTest {

    private Logic logic;
    private OldiesTokenizer tokenizer;

    @BeforeEach
    void setUp() {
        logic = TestFixtures.createMinimalLogic();
        tokenizer = TestFixtures.createTokenizer(logic);
    }

    @Nested
    @DisplayName("Registry Verification")
    class RegistryVerification {

        @Test
        @DisplayName("should have all 15 actions registered in CLASSES_KEYWORDS")
        void shouldHaveAllActionsRegistered() {
            HashMap<String, String> registry = AbstractAction.CLASSES_KEYWORDS;

            assertThat(registry).isNotNull();
            assertThat(registry).hasSize(15);

            // Verify all expected action keywords are present
            assertThat(registry).containsKeys(
                    "add",
                    "createNewNode",
                    "link",
                    "unlink",
                    "stop",
                    "mark",
                    "unmark",
                    "markExpressions",
                    "unmarkExpressions",
                    "createOneSuccessor",
                    "createOneParent",
                    "hide",
                    "kill",
                    "duplicate",
                    "merge"
            );
        }

        @Test
        @DisplayName("should map keywords to correct action class names")
        void shouldMapKeywordsToCorrectClassNames() {
            HashMap<String, String> registry = AbstractAction.CLASSES_KEYWORDS;

            assertThat(registry.get("add")).isEqualTo("AddExpressionAction");
            assertThat(registry.get("createNewNode")).isEqualTo("AddNodeAction");
            assertThat(registry.get("link")).isEqualTo("LinkAction");
            assertThat(registry.get("unlink")).isEqualTo("UnlinkAction");
            assertThat(registry.get("stop")).isEqualTo("StopStrategyAction");
            assertThat(registry.get("mark")).isEqualTo("MarkAction");
            assertThat(registry.get("unmark")).isEqualTo("UnmarkAction");
            assertThat(registry.get("markExpressions")).isEqualTo("MarkExpressionsAction");
            assertThat(registry.get("unmarkExpressions")).isEqualTo("UnmarkExpressionsAction");
            assertThat(registry.get("createOneSuccessor")).isEqualTo("AddOneSuccessorAction");
            assertThat(registry.get("createOneParent")).isEqualTo("AddOneParentAction");
            assertThat(registry.get("hide")).isEqualTo("HideAction");
            assertThat(registry.get("kill")).isEqualTo("KillAction");
            assertThat(registry.get("duplicate")).isEqualTo("DuplicateAction");
            assertThat(registry.get("merge")).isEqualTo("MergeNodeInNodeAction");
        }

        @Test
        @DisplayName("should have correct actions package path")
        void shouldHaveCorrectActionsPackagePath() {
            assertThat(AbstractAction.ACTIONS_PACKAGE)
                    .isEqualTo("lotrec.dataStructure.tableau.action.");
        }
    }

    @Nested
    @DisplayName("Action Instantiation by Keyword")
    class ActionInstantiation {

        @Test
        @DisplayName("should instantiate action classes by full qualified name")
        void shouldInstantiateActionByKeyword() throws Exception {
            String packagePath = AbstractAction.ACTIONS_PACKAGE;
            HashMap<String, String> registry = AbstractAction.CLASSES_KEYWORDS;

            // Verify that all registered classes can be loaded
            for (String keyword : registry.keySet()) {
                String className = packagePath + registry.get(keyword);
                Class<?> actionClass = Class.forName(className);

                assertThat(actionClass).isNotNull();
                assertThat(AbstractAction.class).isAssignableFrom(actionClass);
            }
        }

        @Test
        @DisplayName("should throw ClassNotFoundException for invalid action class")
        void shouldHandleInvalidActionClass() {
            String invalidClassName = AbstractAction.ACTIONS_PACKAGE + "NonExistentAction";

            assertThatThrownBy(() -> Class.forName(invalidClassName))
                    .isInstanceOf(ClassNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Action Parsing - Basic Actions")
    class BasicActionParsing {

        @Test
        @DisplayName("should parse 'add' action with node and formula")
        void shouldParseAdd() throws ParseException {
            AbstractAction action = tokenizer.parseAction("add ?n P");

            assertThat(action).isInstanceOf(AddExpressionAction.class);
            assertThat(action.getName()).isEqualTo("add");
            assertThat(action.getParameters()).hasSize(2);
            assertThat(action.getParameters().get(0).getType()).isEqualTo(ParameterType.NODE);
            assertThat(action.getParameters().get(1).getType()).isEqualTo(ParameterType.FORMULA);
        }

        @Test
        @DisplayName("should parse 'createNewNode' action with node identifier")
        void shouldParseCreateNewNode() throws ParseException {
            AbstractAction action = tokenizer.parseAction("createNewNode ?m");

            assertThat(action).isInstanceOf(AddNodeAction.class);
            assertThat(action.getName()).isEqualTo("createNewNode");
            assertThat(action.getParameters()).hasSize(1);
            assertThat(action.getParameters().get(0).getType()).isEqualTo(ParameterType.NODE);
        }

        @Test
        @DisplayName("should parse 'link' action with two nodes and relation")
        void shouldParseLink() throws ParseException {
            AbstractAction action = tokenizer.parseAction("link ?n ?m R");

            assertThat(action).isInstanceOf(LinkAction.class);
            assertThat(action.getName()).isEqualTo("link");
            assertThat(action.getParameters()).hasSize(3);
            assertThat(action.getParameters().get(0).getType()).isEqualTo(ParameterType.NODE);
            assertThat(action.getParameters().get(1).getType()).isEqualTo(ParameterType.NODE);
            assertThat(action.getParameters().get(2).getType()).isEqualTo(ParameterType.RELATION);
        }

        @Test
        @DisplayName("should parse 'stop' action with node identifier")
        void shouldParseStop() throws ParseException {
            AbstractAction action = tokenizer.parseAction("stop ?n");

            assertThat(action).isInstanceOf(StopStrategyAction.class);
            assertThat(action.getName()).isEqualTo("stop");
            assertThat(action.getParameters()).hasSize(1);
            assertThat(action.getParameters().get(0).getType()).isEqualTo(ParameterType.NODE);
        }
    }

    @Nested
    @DisplayName("Action Parsing - Mark Actions")
    class MarkActionParsing {

        @Test
        @DisplayName("should parse 'mark' action with node and mark name")
        void shouldParseMark() throws ParseException {
            AbstractAction action = tokenizer.parseAction("mark ?n applied");

            assertThat(action).isInstanceOf(MarkAction.class);
            assertThat(action.getName()).isEqualTo("mark");
            assertThat(action.getParameters()).hasSize(2);
            assertThat(action.getParameters().get(0).getType()).isEqualTo(ParameterType.NODE);
            assertThat(action.getParameters().get(1).getType()).isEqualTo(ParameterType.MARK);
        }

        @Test
        @DisplayName("should parse 'unmark' action with node and mark name")
        void shouldParseUnmark() throws ParseException {
            AbstractAction action = tokenizer.parseAction("unmark ?n applied");

            assertThat(action).isInstanceOf(UnmarkAction.class);
            assertThat(action.getName()).isEqualTo("unmark");
            assertThat(action.getParameters()).hasSize(2);
            assertThat(action.getParameters().get(0).getType()).isEqualTo(ParameterType.NODE);
            assertThat(action.getParameters().get(1).getType()).isEqualTo(ParameterType.MARK);
        }

        @Test
        @DisplayName("should parse 'markExpressions' action with node, formula and mark")
        void shouldParseMarkExpressions() throws ParseException {
            AbstractAction action = tokenizer.parseAction("markExpressions ?n P applied");

            assertThat(action).isInstanceOf(MarkExpressionsAction.class);
            assertThat(action.getName()).isEqualTo("markExpressions");
            assertThat(action.getParameters()).hasSize(3);
            assertThat(action.getParameters().get(0).getType()).isEqualTo(ParameterType.NODE);
            assertThat(action.getParameters().get(1).getType()).isEqualTo(ParameterType.FORMULA);
            assertThat(action.getParameters().get(2).getType()).isEqualTo(ParameterType.MARK);
        }

        @Test
        @DisplayName("should parse 'unmarkExpressions' action with node, formula and mark")
        void shouldParseUnmarkExpressions() throws ParseException {
            AbstractAction action = tokenizer.parseAction("unmarkExpressions ?n P applied");

            assertThat(action).isInstanceOf(UnmarkExpressionsAction.class);
            assertThat(action.getName()).isEqualTo("unmarkExpressions");
            assertThat(action.getParameters()).hasSize(3);
            assertThat(action.getParameters().get(0).getType()).isEqualTo(ParameterType.NODE);
            assertThat(action.getParameters().get(1).getType()).isEqualTo(ParameterType.FORMULA);
            assertThat(action.getParameters().get(2).getType()).isEqualTo(ParameterType.MARK);
        }
    }

    @Nested
    @DisplayName("Action Parsing - Node Creation Actions")
    class NodeCreationActionParsing {

        @Test
        @DisplayName("should parse 'createOneSuccessor' action with two nodes and relation")
        void shouldParseCreateOneSuccessor() throws ParseException {
            AbstractAction action = tokenizer.parseAction("createOneSuccessor ?n ?m R");

            assertThat(action).isInstanceOf(AddOneSuccessorAction.class);
            assertThat(action.getName()).isEqualTo("createOneSuccessor");
            assertThat(action.getParameters()).hasSize(3);
            assertThat(action.getParameters().get(0).getType()).isEqualTo(ParameterType.NODE);
            assertThat(action.getParameters().get(1).getType()).isEqualTo(ParameterType.NODE);
            assertThat(action.getParameters().get(2).getType()).isEqualTo(ParameterType.RELATION);
        }

        @Test
        @DisplayName("should NOT parse 'createOneParent' - not implemented in parser")
        void shouldNotParseCreateOneParent() {
            // Note: createOneParent is in CLASSES_KEYWORDS but not implemented in parseAction
            // This documents the current behavior - the action class exists but cannot be parsed
            assertThatThrownBy(() -> tokenizer.parseAction("createOneParent ?n ?m R"))
                    .isInstanceOf(ParseException.class)
                    .hasMessageContaining("createOneParent");
        }
    }

    @Nested
    @DisplayName("Action Parsing - Tableau Control Actions")
    class TableauControlActionParsing {

        @Test
        @DisplayName("should parse 'hide' action with node and formula")
        void shouldParseHide() throws ParseException {
            AbstractAction action = tokenizer.parseAction("hide ?n P");

            assertThat(action).isInstanceOf(HideAction.class);
            assertThat(action.getName()).isEqualTo("hide");
            assertThat(action.getParameters()).hasSize(2);
            assertThat(action.getParameters().get(0).getType()).isEqualTo(ParameterType.NODE);
            assertThat(action.getParameters().get(1).getType()).isEqualTo(ParameterType.FORMULA);
        }

        @Test
        @DisplayName("should parse 'kill' action with node identifier")
        void shouldParseKill() throws ParseException {
            AbstractAction action = tokenizer.parseAction("kill ?n");

            assertThat(action).isInstanceOf(KillAction.class);
            assertThat(action.getName()).isEqualTo("kill");
            assertThat(action.getParameters()).hasSize(1);
            assertThat(action.getParameters().get(0).getType()).isEqualTo(ParameterType.NODE);
        }

        @Test
        @DisplayName("should parse 'duplicate' action with special syntax")
        void shouldParseDuplicate() throws ParseException {
            // duplicate has special syntax: duplicate begin node0 mark end
            AbstractAction action = tokenizer.parseAction("duplicate begin ?n copy end");

            assertThat(action).isInstanceOf(DuplicateAction.class);
            assertThat(action.getName()).isEqualTo("duplicate");
            assertThat(action.getParameters()).hasSize(1);
            assertThat(action.getParameters().get(0).getType()).isEqualTo(ParameterType.MARK);
        }
    }

    @Nested
    @DisplayName("Action Parsing - Unimplemented Actions")
    class UnimplementedActionParsing {

        @Test
        @DisplayName("should NOT parse 'unlink' - not implemented in parser")
        void shouldNotParseUnlink() {
            // Note: unlink is in CLASSES_KEYWORDS but not implemented in parseAction
            assertThatThrownBy(() -> tokenizer.parseAction("unlink ?n ?m R"))
                    .isInstanceOf(ParseException.class)
                    .hasMessageContaining("unlink");
        }

        @Test
        @DisplayName("should NOT parse 'merge' - not implemented in parser")
        void shouldNotParseMerge() {
            // Note: merge is in CLASSES_KEYWORDS but not implemented in parseAction
            assertThatThrownBy(() -> tokenizer.parseAction("merge ?n ?m"))
                    .isInstanceOf(ParseException.class)
                    .hasMessageContaining("merge");
        }
    }

    @Nested
    @DisplayName("Action Parsing - Error Handling")
    class ActionParsingErrorHandling {

        @Test
        @DisplayName("should throw ParseException for unknown action keyword")
        void shouldHandleInvalidActionKeyword() {
            assertThatThrownBy(() -> tokenizer.parseAction("unknownAction ?n"))
                    .isInstanceOf(ParseException.class);
        }

        @Test
        @DisplayName("should throw ParseException for empty action code")
        void shouldHandleEmptyActionCode() {
            assertThatThrownBy(() -> tokenizer.parseAction(""))
                    .isInstanceOf(ParseException.class);
        }

        @Test
        @DisplayName("should throw ParseException for missing parameters")
        void shouldHandleMissingParameters() {
            // 'add' requires node and formula
            assertThatThrownBy(() -> tokenizer.parseAction("add"))
                    .isInstanceOf(ParseException.class);
        }
    }

    @Nested
    @DisplayName("Action Parameter Verification")
    class ActionParameterVerification {

        @Test
        @DisplayName("should verify parameter types for add action")
        void shouldVerifyAddActionParameterTypes() throws ParseException {
            AbstractAction action = tokenizer.parseAction("add ?node1 P");

            Vector<Parameter> params = action.getParameters();
            assertThat(params.get(0).getType()).isEqualTo(ParameterType.NODE);
            assertThat(params.get(1).getType()).isEqualTo(ParameterType.FORMULA);
        }

        @Test
        @DisplayName("should verify parameter types for link action")
        void shouldVerifyLinkActionParameterTypes() throws ParseException {
            AbstractAction action = tokenizer.parseAction("link ?source ?target R");

            Vector<Parameter> params = action.getParameters();
            assertThat(params.get(0).getType()).isEqualTo(ParameterType.NODE);
            assertThat(params.get(1).getType()).isEqualTo(ParameterType.NODE);
            assertThat(params.get(2).getType()).isEqualTo(ParameterType.RELATION);
        }

        @Test
        @DisplayName("should verify parameter types for markExpressions action")
        void shouldVerifyMarkExpressionsParameterTypes() throws ParseException {
            AbstractAction action = tokenizer.parseAction("markExpressions ?n and P Q processed");

            Vector<Parameter> params = action.getParameters();
            assertThat(params.get(0).getType()).isEqualTo(ParameterType.NODE);
            assertThat(params.get(1).getType()).isEqualTo(ParameterType.FORMULA);
            assertThat(params.get(2).getType()).isEqualTo(ParameterType.MARK);
        }
    }

    @Nested
    @DisplayName("Action Code Generation")
    class ActionCodeGeneration {

        @Test
        @DisplayName("should generate code for add action")
        void shouldGenerateCodeForAddAction() throws ParseException {
            AbstractAction action = tokenizer.parseAction("add ?n P");

            String code = action.getCode();
            assertThat(code).startsWith("add");
            // Code should be "add ?n P" - node variable and formula
            assertThat(code).containsIgnoringCase("p");
        }

        @Test
        @DisplayName("should generate code for link action")
        void shouldGenerateCodeForLinkAction() throws ParseException {
            AbstractAction action = tokenizer.parseAction("link ?n ?m R");

            String code = action.getCode();
            assertThat(code).startsWith("link");
        }

        @Test
        @DisplayName("should generate code for mark action")
        void shouldGenerateCodeForMarkAction() throws ParseException {
            AbstractAction action = tokenizer.parseAction("mark ?n done");

            String code = action.getCode();
            assertThat(code).startsWith("mark");
            assertThat(code).contains("done");
        }
    }

    @Nested
    @DisplayName("Action with Complex Expressions")
    class ActionWithComplexExpressions {

        @Test
        @DisplayName("should parse add action with unary connector expression")
        void shouldParseAddWithUnaryExpression() throws ParseException {
            AbstractAction action = tokenizer.parseAction("add ?n not P");

            assertThat(action).isInstanceOf(AddExpressionAction.class);
            assertThat(action.getName()).isEqualTo("add");
        }

        @Test
        @DisplayName("should parse add action with binary connector expression")
        void shouldParseAddWithBinaryExpression() throws ParseException {
            AbstractAction action = tokenizer.parseAction("add ?n and P Q");

            assertThat(action).isInstanceOf(AddExpressionAction.class);
            assertThat(action.getName()).isEqualTo("add");
        }

        @Test
        @DisplayName("should parse add action with nested expression")
        void shouldParseAddWithNestedExpression() throws ParseException {
            AbstractAction action = tokenizer.parseAction("add ?n imp P not Q");

            assertThat(action).isInstanceOf(AddExpressionAction.class);
            assertThat(action.getName()).isEqualTo("add");
        }

        @Test
        @DisplayName("should parse add action with variable expression")
        void shouldParseAddWithVariableExpression() throws ParseException {
            AbstractAction action = tokenizer.parseAction("add ?n _A");

            assertThat(action).isInstanceOf(AddExpressionAction.class);
            assertThat(action.getName()).isEqualTo("add");
        }

        @Test
        @DisplayName("should parse createOneSuccessor with variable relation")
        void shouldParseCreateOneSuccessorWithVariableRelation() throws ParseException {
            AbstractAction action = tokenizer.parseAction("createOneSuccessor ?n ?m _R");

            assertThat(action).isInstanceOf(AddOneSuccessorAction.class);
            assertThat(action.getName()).isEqualTo("createOneSuccessor");
        }
    }
}
