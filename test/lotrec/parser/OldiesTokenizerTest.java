package lotrec.parser;

import lotrec.dataStructure.Logic;
import lotrec.dataStructure.expression.*;
import lotrec.dataStructure.tableau.condition.*;
import lotrec.parser.exceptions.ParseException;
import lotrec.process.AbstractAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Characterization tests for OldiesTokenizer.
 * These tests capture the current behavior of the parser to enable safe refactoring.
 */
@DisplayName("OldiesTokenizer")
class OldiesTokenizerTest {

    private Logic logic;
    private OldiesTokenizer tokenizer;

    @BeforeEach
    void setUp() {
        logic = new Logic();
        logic.setName("TestLogic");

        // Add standard modal logic connectors
        logic.addConnector(new Connector("not", 1, "~_"));
        logic.addConnector(new Connector("and", 2, "_&_"));
        logic.addConnector(new Connector("or", 2, "_|_"));
        logic.addConnector(new Connector("imp", 2, "_->_"));
        logic.addConnector(new Connector("nec", 1, "[]_"));
        logic.addConnector(new Connector("pos", 1, "<>_"));

        tokenizer = new OldiesTokenizer(logic);
        tokenizer.initializeTokenizerAndProps();
    }

    @Nested
    @DisplayName("Expression Parsing")
    class ExpressionParsing {

        @Test
        @DisplayName("parses constant expression (uppercase)")
        void parsesConstantExpression() throws ParseException {
            Expression expr = tokenizer.parseExpression("P");

            assertThat(expr).isInstanceOf(ConstantExpression.class);
            assertThat(expr.toString()).isEqualTo("P");
        }

        @Test
        @DisplayName("parses variable expression with underscore prefix")
        void parsesVariableExpression() throws ParseException {
            Expression expr = tokenizer.parseExpression("_A");

            assertThat(expr).isInstanceOf(VariableExpression.class);
            assertThat(expr.toString()).isEqualTo("A");
        }

        @Test
        @DisplayName("parses node variable expression with n_ prefix")
        void parsesNodeVariableExpression() throws ParseException {
            Expression expr = tokenizer.parseExpression("n_x");

            assertThat(expr).isInstanceOf(VariableNodeExpression.class);
        }

        @Test
        @DisplayName("parses unary connector expression")
        void parsesUnaryConnectorExpression() throws ParseException {
            Expression expr = tokenizer.parseExpression("not P");

            assertThat(expr).isInstanceOf(ExpressionWithSubExpressions.class);
            ExpressionWithSubExpressions compound = (ExpressionWithSubExpressions) expr;
            assertThat(compound.getConnector().getName()).isEqualTo("not");
        }

        @Test
        @DisplayName("parses binary connector expression")
        void parsesBinaryConnectorExpression() throws ParseException {
            Expression expr = tokenizer.parseExpression("and P Q");

            assertThat(expr).isInstanceOf(ExpressionWithSubExpressions.class);
            ExpressionWithSubExpressions compound = (ExpressionWithSubExpressions) expr;
            assertThat(compound.getConnector().getName()).isEqualTo("and");
        }

        @Test
        @DisplayName("parses nested expression")
        void parsesNestedExpression() throws ParseException {
            Expression expr = tokenizer.parseExpression("imp P not Q");

            assertThat(expr).isInstanceOf(ExpressionWithSubExpressions.class);
            ExpressionWithSubExpressions compound = (ExpressionWithSubExpressions) expr;
            assertThat(compound.getConnector().getName()).isEqualTo("imp");
        }

        @Test
        @DisplayName("parses modal necessity expression")
        void parsesModalNecessity() throws ParseException {
            Expression expr = tokenizer.parseExpression("nec P");

            assertThat(expr).isInstanceOf(ExpressionWithSubExpressions.class);
            ExpressionWithSubExpressions compound = (ExpressionWithSubExpressions) expr;
            assertThat(compound.getConnector().getName()).isEqualTo("nec");
        }

        @Test
        @DisplayName("parses modal possibility expression")
        void parsesModalPossibility() throws ParseException {
            Expression expr = tokenizer.parseExpression("pos P");

            assertThat(expr).isInstanceOf(ExpressionWithSubExpressions.class);
            ExpressionWithSubExpressions compound = (ExpressionWithSubExpressions) expr;
            assertThat(compound.getConnector().getName()).isEqualTo("pos");
        }

        @Test
        @DisplayName("throws ParseException for unknown connector")
        void throwsForUnknownConnector() {
            assertThatThrownBy(() -> tokenizer.parseExpression("unknown P"))
                    .isInstanceOf(ParseException.class);
        }

        @Test
        @DisplayName("parses constant keyword with explicit value")
        void parsesConstantKeyword() throws ParseException {
            Expression expr = tokenizer.parseExpression("constant myconst");

            assertThat(expr).isInstanceOf(ConstantExpression.class);
            // constant keyword converts to uppercase
            assertThat(expr.toString()).isEqualTo("MYCONST");
        }

        @Test
        @DisplayName("parses variable keyword with explicit value")
        void parsesVariableKeyword() throws ParseException {
            Expression expr = tokenizer.parseExpression("variable myvar");

            assertThat(expr).isInstanceOf(VariableExpression.class);
        }
    }

    @Nested
    @DisplayName("Condition Parsing")
    class ConditionParsing {

        @Test
        @DisplayName("parses hasElement condition")
        void parsesHasElementCondition() throws ParseException {
            AbstractCondition cond = tokenizer.parseCondition("hasElement ?n P");

            assertThat(cond).isInstanceOf(ExpressionCondition.class);
            assertThat(cond.getName()).isEqualTo("hasElement");
        }

        @Test
        @DisplayName("parses hasNotElement condition")
        void parsesHasNotElementCondition() throws ParseException {
            AbstractCondition cond = tokenizer.parseCondition("hasNotElement ?n P");

            assertThat(cond).isInstanceOf(NotExpressionCondition.class);
            assertThat(cond.getName()).isEqualTo("hasNotElement");
        }

        @Test
        @DisplayName("parses isLinked condition")
        void parsesIsLinkedCondition() throws ParseException {
            AbstractCondition cond = tokenizer.parseCondition("isLinked ?n ?m R");

            assertThat(cond).isInstanceOf(LinkCondition.class);
            assertThat(cond.getName()).isEqualTo("isLinked");
        }

        @Test
        @DisplayName("parses isNotLinked condition")
        void parsesIsNotLinkedCondition() throws ParseException {
            AbstractCondition cond = tokenizer.parseCondition("isNotLinked ?n ?m R");

            assertThat(cond).isInstanceOf(NotLinkCondition.class);
            assertThat(cond.getName()).isEqualTo("isNotLinked");
        }

        @Test
        @DisplayName("parses isMarked condition")
        void parsesIsMarkedCondition() throws ParseException {
            AbstractCondition cond = tokenizer.parseCondition("isMarked ?n applied");

            assertThat(cond).isInstanceOf(MarkCondition.class);
            assertThat(cond.getName()).isEqualTo("isMarked");
        }

        @Test
        @DisplayName("parses isNotMarked condition")
        void parsesIsNotMarkedCondition() throws ParseException {
            AbstractCondition cond = tokenizer.parseCondition("isNotMarked ?n applied");

            assertThat(cond).isInstanceOf(NotMarkCondition.class);
            assertThat(cond.getName()).isEqualTo("isNotMarked");
        }

        @Test
        @DisplayName("parses areIdentical condition")
        void parsesAreIdenticalCondition() throws ParseException {
            AbstractCondition cond = tokenizer.parseCondition("areIdentical ?n ?m");

            assertThat(cond).isInstanceOf(IdenticalCondition.class);
            assertThat(cond.getName()).isEqualTo("areIdentical");
        }

        @Test
        @DisplayName("parses areNotIdentical condition")
        void parsesAreNotIdenticalCondition() throws ParseException {
            AbstractCondition cond = tokenizer.parseCondition("areNotIdentical ?n ?m");

            assertThat(cond).isInstanceOf(NotIdenticalCondition.class);
            assertThat(cond.getName()).isEqualTo("areNotIdentical");
        }

        @Test
        @DisplayName("parses isAncestor condition")
        void parsesIsAncestorCondition() throws ParseException {
            AbstractCondition cond = tokenizer.parseCondition("isAncestor ?n ?m");

            assertThat(cond).isInstanceOf(AncestorCondition.class);
            assertThat(cond.getName()).isEqualTo("isAncestor");
        }

        @Test
        @DisplayName("parses isNewNode condition")
        void parsesIsNewNodeCondition() throws ParseException {
            AbstractCondition cond = tokenizer.parseCondition("isNewNode ?n");

            assertThat(cond).isInstanceOf(NodeCreatedCondition.class);
            assertThat(cond.getName()).isEqualTo("isNewNode");
        }

        @Test
        @DisplayName("parses hasNoSuccessor condition")
        void parsesHasNoSuccessorCondition() throws ParseException {
            AbstractCondition cond = tokenizer.parseCondition("hasNoSuccessor ?n R");

            assertThat(cond).isInstanceOf(HasNotSuccessorCondition.class);
            assertThat(cond.getName()).isEqualTo("hasNoSuccessor");
        }

        @Test
        @DisplayName("throws ParseException for unknown condition")
        void throwsForUnknownCondition() {
            assertThatThrownBy(() -> tokenizer.parseCondition("unknownCondition ?n"))
                    .isInstanceOf(ParseException.class);
        }
    }

    @Nested
    @DisplayName("Action Parsing")
    class ActionParsing {

        @Test
        @DisplayName("parses add action")
        void parsesAddAction() throws ParseException {
            AbstractAction action = tokenizer.parseAction("add ?n P");

            assertThat(action.getName()).isEqualTo("add");
        }

        @Test
        @DisplayName("parses createNewNode action")
        void parsesCreateNewNodeAction() throws ParseException {
            AbstractAction action = tokenizer.parseAction("createNewNode ?n");

            assertThat(action.getName()).isEqualTo("createNewNode");
        }

        @Test
        @DisplayName("parses link action")
        void parsesLinkAction() throws ParseException {
            AbstractAction action = tokenizer.parseAction("link ?n ?m R");

            assertThat(action.getName()).isEqualTo("link");
        }

        @Test
        @DisplayName("parses mark action")
        void parsesMarkAction() throws ParseException {
            AbstractAction action = tokenizer.parseAction("mark ?n applied");

            assertThat(action.getName()).isEqualTo("mark");
        }

        @Test
        @DisplayName("parses unmark action")
        void parsesUnmarkAction() throws ParseException {
            AbstractAction action = tokenizer.parseAction("unmark ?n applied");

            assertThat(action.getName()).isEqualTo("unmark");
        }

        @Test
        @DisplayName("parses stop action")
        void parsesStopAction() throws ParseException {
            AbstractAction action = tokenizer.parseAction("stop ?n");

            assertThat(action.getName()).isEqualTo("stop");
        }

        @Test
        @DisplayName("parses createOneSuccessor action")
        void parsesCreateOneSuccessorAction() throws ParseException {
            AbstractAction action = tokenizer.parseAction("createOneSuccessor ?n ?m R");

            assertThat(action.getName()).isEqualTo("createOneSuccessor");
        }

        @Test
        @DisplayName("parses kill action")
        void parsesKillAction() throws ParseException {
            AbstractAction action = tokenizer.parseAction("kill ?n");

            assertThat(action.getName()).isEqualTo("kill");
        }

        @Test
        @DisplayName("throws ParseException for unknown action")
        void throwsForUnknownAction() {
            assertThatThrownBy(() -> tokenizer.parseAction("unknownAction ?n"))
                    .isInstanceOf(ParseException.class);
        }
    }

    @Nested
    @DisplayName("Comment Handling")
    class CommentHandling {

        @Test
        @DisplayName("ignores line comments")
        void ignoresLineComments() throws ParseException {
            Expression expr = tokenizer.parseExpression("// This is a comment\nP");

            assertThat(expr).isInstanceOf(ConstantExpression.class);
            assertThat(expr.toString()).isEqualTo("P");
        }

        @Test
        @DisplayName("ignores block comments")
        void ignoresBlockComments() throws ParseException {
            Expression expr = tokenizer.parseExpression("/* block comment */ P");

            assertThat(expr).isInstanceOf(ConstantExpression.class);
            assertThat(expr.toString()).isEqualTo("P");
        }
    }
}
