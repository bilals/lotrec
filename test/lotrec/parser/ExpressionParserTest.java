package lotrec.parser;

import lotrec.TestFixtures;
import lotrec.dataStructure.Logic;
import lotrec.dataStructure.expression.*;
import lotrec.parser.exceptions.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Enumeration;

import static org.assertj.core.api.Assertions.*;

/**
 * Comprehensive tests for expression parsing in OldiesTokenizer.
 * Tests cover atomic expressions, compound expressions, edge cases,
 * and error handling for malformed input.
 *
 * Approximately 25+ tests covering:
 * - Constants (single letter, multi-char, keyword syntax, falsum, verum)
 * - Variables (underscore prefix, keyword syntax)
 * - Node variables (n_ prefix, keyword syntax)
 * - Unary connectors (not, nec, pos)
 * - Binary connectors (and, or, imp, equiv)
 * - Nested and deeply nested expressions
 * - Whitespace handling
 * - Comment handling
 * - Error cases
 * - Code string generation
 * - Expression equality
 * - Connector and variable detection
 */
@DisplayName("Expression Parser")
class ExpressionParserTest {

    private Logic logic;
    private OldiesTokenizer tokenizer;

    @BeforeEach
    void setUp() {
        logic = TestFixtures.createMinimalLogic();
        // Add equivalence connector for additional tests
        logic.addConnector(new Connector("equiv", 2, "_<->_"));
        tokenizer = TestFixtures.createTokenizer(logic);
    }

    @Nested
    @DisplayName("Constants")
    class Constants {

        @Test
        @DisplayName("should parse single letter constant")
        void shouldParseConstant() throws ParseException {
            Expression expr = tokenizer.parseExpression("P");

            assertThat(expr).isInstanceOf(ConstantExpression.class);
            assertThat(expr.toString()).isEqualTo("P");
        }

        @Test
        @DisplayName("should parse multi-character constant names (ABC, XYZ)")
        void shouldParseMultiCharacterNames() throws ParseException {
            Expression exprABC = tokenizer.parseExpression("ABC");
            Expression exprXYZ = tokenizer.parseExpression("XYZ");
            Expression exprProp = tokenizer.parseExpression("Proposition");

            assertThat(exprABC).isInstanceOf(ConstantExpression.class);
            assertThat(exprABC.toString()).isEqualTo("ABC");
            assertThat(exprXYZ).isInstanceOf(ConstantExpression.class);
            assertThat(exprXYZ.toString()).isEqualTo("XYZ");
            assertThat(exprProp).isInstanceOf(ConstantExpression.class);
            assertThat(exprProp.toString()).isEqualTo("Proposition");
        }

        @Test
        @DisplayName("should parse atomic propositions P, Q, R")
        void shouldParseAtomicProposition() throws ParseException {
            Expression exprP = tokenizer.parseExpression("P");
            Expression exprQ = tokenizer.parseExpression("Q");
            Expression exprR = tokenizer.parseExpression("R");

            assertThat(exprP).isInstanceOf(ConstantExpression.class);
            assertThat(exprQ).isInstanceOf(ConstantExpression.class);
            assertThat(exprR).isInstanceOf(ConstantExpression.class);
            assertThat(exprP.toString()).isEqualTo("P");
            assertThat(exprQ.toString()).isEqualTo("Q");
            assertThat(exprR.toString()).isEqualTo("R");
        }

        @Test
        @DisplayName("should parse Falsum (False constant)")
        void shouldParseFalsum() throws ParseException {
            Expression expr = tokenizer.parseExpression("False");

            assertThat(expr).isInstanceOf(ConstantExpression.class);
            assertThat(expr.toString()).isEqualTo("False");
            // Verify case-insensitive equality with FALSUM constant
            assertThat(expr).isEqualTo(ConstantExpression.FALSUM);
        }

        @Test
        @DisplayName("should parse Verum (True constant)")
        void shouldParseVerum() throws ParseException {
            Expression expr = tokenizer.parseExpression("True");

            assertThat(expr).isInstanceOf(ConstantExpression.class);
            assertThat(expr.toString()).isEqualTo("True");
        }

        @Test
        @DisplayName("should handle keyword 'constant A' syntax")
        void shouldHandleKeywordConstants() throws ParseException {
            Expression expr = tokenizer.parseExpression("constant A");

            assertThat(expr).isInstanceOf(ConstantExpression.class);
            // constant keyword converts to uppercase
            assertThat(expr.toString()).isEqualTo("A");
        }

        @Test
        @DisplayName("should convert lowercase constant keyword value to uppercase")
        void shouldConvertConstantKeywordToUppercase() throws ParseException {
            Expression expr = tokenizer.parseExpression("constant myconst");

            assertThat(expr).isInstanceOf(ConstantExpression.class);
            // constant keyword converts to uppercase
            assertThat(expr.toString()).isEqualTo("MYCONST");
        }
    }

    @Nested
    @DisplayName("Variables")
    class Variables {

        @Test
        @DisplayName("should parse variable with underscore prefix (_var)")
        void shouldParseVariable() throws ParseException {
            Expression expr = tokenizer.parseExpression("_var");

            assertThat(expr).isInstanceOf(VariableExpression.class);
            assertThat(expr.toString()).isEqualTo("var");
        }

        @Test
        @DisplayName("should parse variable with single character name (_A)")
        void shouldParseVariableSingleChar() throws ParseException {
            Expression expr = tokenizer.parseExpression("_A");

            assertThat(expr).isInstanceOf(VariableExpression.class);
            assertThat(expr.toString()).isEqualTo("A");
        }

        @Test
        @DisplayName("should handle keyword 'variable' syntax")
        void shouldHandleKeywordVariable() throws ParseException {
            Expression expr = tokenizer.parseExpression("variable myvar");

            assertThat(expr).isInstanceOf(VariableExpression.class);
            assertThat(expr.toString()).isEqualTo("myvar");
        }
    }

    @Nested
    @DisplayName("Node Variables")
    class NodeVariables {

        @Test
        @DisplayName("should parse node variable with n_ prefix (n_var)")
        void shouldParseNodeVariable() throws ParseException {
            Expression expr = tokenizer.parseExpression("n_var");

            assertThat(expr).isInstanceOf(VariableNodeExpression.class);
            assertThat(expr.toString()).isEqualTo("var");
        }

        @Test
        @DisplayName("should parse node variable with uppercase name (n_Node)")
        void shouldParseNodeVariableUppercase() throws ParseException {
            Expression expr = tokenizer.parseExpression("n_Node");

            assertThat(expr).isInstanceOf(VariableNodeExpression.class);
            assertThat(expr.toString()).isEqualTo("Node");
        }

        @Test
        @DisplayName("should handle keyword 'nodeVariable' syntax")
        void shouldHandleKeywordNodeVariable() throws ParseException {
            Expression expr = tokenizer.parseExpression("nodeVariable mynode");

            assertThat(expr).isInstanceOf(VariableNodeExpression.class);
            assertThat(expr.toString()).isEqualTo("mynode");
        }
    }

    @Nested
    @DisplayName("Unary Connectors")
    class UnaryConnectors {

        @Test
        @DisplayName("should parse unary connector (not P)")
        void shouldParseUnaryConnector() throws ParseException {
            Expression expr = tokenizer.parseExpression("not P");

            assertThat(expr).isInstanceOf(ExpressionWithSubExpressions.class);
            ExpressionWithSubExpressions compound = (ExpressionWithSubExpressions) expr;
            assertThat(compound.getConnector().getName()).isEqualTo("not");
            assertThat(compound.getConnector().getArity()).isEqualTo(1);

            // Verify sub-expression
            Enumeration subExprs = compound.getSubExpressions();
            assertThat(subExprs.hasMoreElements()).isTrue();
            Expression subExpr = (Expression) subExprs.nextElement();
            assertThat(subExpr).isInstanceOf(ConstantExpression.class);
            assertThat(subExpr.toString()).isEqualTo("P");
        }

        @Test
        @DisplayName("should parse negation (not P) with correct output format")
        void shouldParseNegation() throws ParseException {
            Expression expr = tokenizer.parseExpression("not P");

            assertThat(expr).isInstanceOf(ExpressionWithSubExpressions.class);
            ExpressionWithSubExpressions compound = (ExpressionWithSubExpressions) expr;
            assertThat(compound.getConnector().getName()).isEqualTo("not");
            // Verify output format
            assertThat(expr.toString()).isEqualTo("~P");
        }

        @Test
        @DisplayName("should parse necessity (nec P)")
        void shouldParseNecessity() throws ParseException {
            Expression expr = tokenizer.parseExpression("nec P");

            assertThat(expr).isInstanceOf(ExpressionWithSubExpressions.class);
            ExpressionWithSubExpressions compound = (ExpressionWithSubExpressions) expr;
            assertThat(compound.getConnector().getName()).isEqualTo("nec");
            assertThat(compound.getConnector().getArity()).isEqualTo(1);
            assertThat(expr.toString()).isEqualTo("[]P");
        }

        @Test
        @DisplayName("should parse possibility (pos P)")
        void shouldParsePossibility() throws ParseException {
            Expression expr = tokenizer.parseExpression("pos P");

            assertThat(expr).isInstanceOf(ExpressionWithSubExpressions.class);
            ExpressionWithSubExpressions compound = (ExpressionWithSubExpressions) expr;
            assertThat(compound.getConnector().getName()).isEqualTo("pos");
            assertThat(compound.getConnector().getArity()).isEqualTo(1);
            assertThat(expr.toString()).isEqualTo("<>P");
        }

        @Test
        @DisplayName("should parse modal operators with variables (nec _A, pos _B)")
        void shouldParseModalOperators() throws ParseException {
            Expression necExpr = tokenizer.parseExpression("nec _A");
            Expression posExpr = tokenizer.parseExpression("pos _B");

            assertThat(necExpr).isInstanceOf(ExpressionWithSubExpressions.class);
            assertThat(posExpr).isInstanceOf(ExpressionWithSubExpressions.class);

            ExpressionWithSubExpressions necCompound = (ExpressionWithSubExpressions) necExpr;
            ExpressionWithSubExpressions posCompound = (ExpressionWithSubExpressions) posExpr;

            assertThat(necCompound.getConnector().getName()).isEqualTo("nec");
            assertThat(posCompound.getConnector().getName()).isEqualTo("pos");

            // Verify sub-expressions are variables
            Expression necSub = (Expression) necCompound.getSubExpressions().nextElement();
            Expression posSub = (Expression) posCompound.getSubExpressions().nextElement();
            assertThat(necSub).isInstanceOf(VariableExpression.class);
            assertThat(posSub).isInstanceOf(VariableExpression.class);
        }
    }

    @Nested
    @DisplayName("Binary Connectors")
    class BinaryConnectors {

        @Test
        @DisplayName("should parse binary connector (and P Q)")
        void shouldParseBinaryConnector() throws ParseException {
            Expression expr = tokenizer.parseExpression("and P Q");

            assertThat(expr).isInstanceOf(ExpressionWithSubExpressions.class);
            ExpressionWithSubExpressions compound = (ExpressionWithSubExpressions) expr;
            assertThat(compound.getConnector().getName()).isEqualTo("and");
            assertThat(compound.getConnector().getArity()).isEqualTo(2);

            // Verify both sub-expressions
            Enumeration subExprs = compound.getSubExpressions();
            Expression first = (Expression) subExprs.nextElement();
            Expression second = (Expression) subExprs.nextElement();
            assertThat(first.toString()).isEqualTo("P");
            assertThat(second.toString()).isEqualTo("Q");
        }

        @Test
        @DisplayName("should parse conjunction (and P Q)")
        void shouldParseConjunction() throws ParseException {
            Expression expr = tokenizer.parseExpression("and P Q");

            assertThat(expr).isInstanceOf(ExpressionWithSubExpressions.class);
            ExpressionWithSubExpressions compound = (ExpressionWithSubExpressions) expr;
            assertThat(compound.getConnector().getName()).isEqualTo("and");
            assertThat(expr.toString()).isEqualTo("P&Q");
        }

        @Test
        @DisplayName("should parse disjunction (or P Q)")
        void shouldParseDisjunction() throws ParseException {
            Expression expr = tokenizer.parseExpression("or P Q");

            assertThat(expr).isInstanceOf(ExpressionWithSubExpressions.class);
            ExpressionWithSubExpressions compound = (ExpressionWithSubExpressions) expr;
            assertThat(compound.getConnector().getName()).isEqualTo("or");
            assertThat(expr.toString()).isEqualTo("P|Q");
        }

        @Test
        @DisplayName("should parse implication (imp P Q)")
        void shouldParseImplication() throws ParseException {
            Expression expr = tokenizer.parseExpression("imp P Q");

            assertThat(expr).isInstanceOf(ExpressionWithSubExpressions.class);
            ExpressionWithSubExpressions compound = (ExpressionWithSubExpressions) expr;
            assertThat(compound.getConnector().getName()).isEqualTo("imp");
            assertThat(expr.toString()).isEqualTo("P->Q");
        }

        @Test
        @DisplayName("should parse equivalence (equiv P Q) if connector exists")
        void shouldParseEquivalence() throws ParseException {
            Expression expr = tokenizer.parseExpression("equiv P Q");

            assertThat(expr).isInstanceOf(ExpressionWithSubExpressions.class);
            ExpressionWithSubExpressions compound = (ExpressionWithSubExpressions) expr;
            assertThat(compound.getConnector().getName()).isEqualTo("equiv");
            assertThat(expr.toString()).isEqualTo("P<->Q");
        }
    }

    @Nested
    @DisplayName("Nested Expressions")
    class NestedExpressions {

        @Test
        @DisplayName("should parse nested expression (and P not Q)")
        void shouldParseNestedExpression() throws ParseException {
            Expression expr = tokenizer.parseExpression("and P not Q");

            assertThat(expr).isInstanceOf(ExpressionWithSubExpressions.class);
            ExpressionWithSubExpressions compound = (ExpressionWithSubExpressions) expr;
            assertThat(compound.getConnector().getName()).isEqualTo("and");

            // Verify sub-expressions
            Enumeration subExprs = compound.getSubExpressions();
            Expression first = (Expression) subExprs.nextElement();
            Expression second = (Expression) subExprs.nextElement();

            assertThat(first).isInstanceOf(ConstantExpression.class);
            assertThat(first.toString()).isEqualTo("P");
            assertThat(second).isInstanceOf(ExpressionWithSubExpressions.class);
            assertThat(((ExpressionWithSubExpressions) second).getConnector().getName()).isEqualTo("not");
        }

        @Test
        @DisplayName("should parse deeply nested expression (imp and P Q not or R S)")
        void shouldParseDeeplyNested() throws ParseException {
            // imp (and P Q) (not (or R S))
            Expression expr = tokenizer.parseExpression("imp and P Q not or R S");

            assertThat(expr).isInstanceOf(ExpressionWithSubExpressions.class);
            ExpressionWithSubExpressions root = (ExpressionWithSubExpressions) expr;
            assertThat(root.getConnector().getName()).isEqualTo("imp");

            // First argument: and P Q
            Enumeration subExprs = root.getSubExpressions();
            Expression first = (Expression) subExprs.nextElement();
            assertThat(first).isInstanceOf(ExpressionWithSubExpressions.class);
            assertThat(((ExpressionWithSubExpressions) first).getConnector().getName()).isEqualTo("and");

            // Second argument: not (or R S)
            Expression second = (Expression) subExprs.nextElement();
            assertThat(second).isInstanceOf(ExpressionWithSubExpressions.class);
            assertThat(((ExpressionWithSubExpressions) second).getConnector().getName()).isEqualTo("not");

            // Inner: or R S
            Expression inner = (Expression) ((ExpressionWithSubExpressions) second).getSubExpressions().nextElement();
            assertThat(inner).isInstanceOf(ExpressionWithSubExpressions.class);
            assertThat(((ExpressionWithSubExpressions) inner).getConnector().getName()).isEqualTo("or");
        }

        @Test
        @DisplayName("should parse complex formula (imp and P Q not R)")
        void shouldParseComplexFormula() throws ParseException {
            Expression expr = tokenizer.parseExpression("imp and P Q not R");

            assertThat(expr).isInstanceOf(ExpressionWithSubExpressions.class);
            ExpressionWithSubExpressions root = (ExpressionWithSubExpressions) expr;
            assertThat(root.getConnector().getName()).isEqualTo("imp");

            Enumeration subExprs = root.getSubExpressions();

            // First: and P Q
            Expression first = (Expression) subExprs.nextElement();
            assertThat(first).isInstanceOf(ExpressionWithSubExpressions.class);
            assertThat(((ExpressionWithSubExpressions) first).getConnector().getName()).isEqualTo("and");

            // Second: not R
            Expression second = (Expression) subExprs.nextElement();
            assertThat(second).isInstanceOf(ExpressionWithSubExpressions.class);
            assertThat(((ExpressionWithSubExpressions) second).getConnector().getName()).isEqualTo("not");
        }

        @Test
        @DisplayName("should preserve expression structure (precedence)")
        void shouldPreservePrecedence() throws ParseException {
            // In prefix notation, structure is explicit - no precedence ambiguity
            // and (or P Q) R  vs  or (and P Q) R
            Expression expr1 = tokenizer.parseExpression("and or P Q R");
            Expression expr2 = tokenizer.parseExpression("or and P Q R");

            // expr1: and (or P Q) R
            ExpressionWithSubExpressions comp1 = (ExpressionWithSubExpressions) expr1;
            assertThat(comp1.getConnector().getName()).isEqualTo("and");
            Expression sub1 = (Expression) comp1.getSubExpressions().nextElement();
            assertThat(((ExpressionWithSubExpressions) sub1).getConnector().getName()).isEqualTo("or");

            // expr2: or (and P Q) R
            ExpressionWithSubExpressions comp2 = (ExpressionWithSubExpressions) expr2;
            assertThat(comp2.getConnector().getName()).isEqualTo("or");
            Expression sub2 = (Expression) comp2.getSubExpressions().nextElement();
            assertThat(((ExpressionWithSubExpressions) sub2).getConnector().getName()).isEqualTo("and");
        }

        @Test
        @DisplayName("should handle multiple levels of modal nesting (nec pos nec P)")
        void shouldHandleModalNesting() throws ParseException {
            // nec pos nec P
            Expression expr = tokenizer.parseExpression("nec pos nec P");

            assertThat(expr).isInstanceOf(ExpressionWithSubExpressions.class);
            ExpressionWithSubExpressions level1 = (ExpressionWithSubExpressions) expr;
            assertThat(level1.getConnector().getName()).isEqualTo("nec");

            Expression level2Expr = (Expression) level1.getSubExpressions().nextElement();
            assertThat(level2Expr).isInstanceOf(ExpressionWithSubExpressions.class);
            ExpressionWithSubExpressions level2 = (ExpressionWithSubExpressions) level2Expr;
            assertThat(level2.getConnector().getName()).isEqualTo("pos");

            Expression level3Expr = (Expression) level2.getSubExpressions().nextElement();
            assertThat(level3Expr).isInstanceOf(ExpressionWithSubExpressions.class);
            ExpressionWithSubExpressions level3 = (ExpressionWithSubExpressions) level3Expr;
            assertThat(level3.getConnector().getName()).isEqualTo("nec");

            Expression innermost = (Expression) level3.getSubExpressions().nextElement();
            assertThat(innermost).isInstanceOf(ConstantExpression.class);
            assertThat(innermost.toString()).isEqualTo("P");
        }
    }

    @Nested
    @DisplayName("Whitespace Handling")
    class WhitespaceHandling {

        @Test
        @DisplayName("should parse expression with extra whitespace")
        void shouldParseWithWhitespace() throws ParseException {
            Expression expr = tokenizer.parseExpression("  and   P    Q  ");

            assertThat(expr).isInstanceOf(ExpressionWithSubExpressions.class);
            ExpressionWithSubExpressions compound = (ExpressionWithSubExpressions) expr;
            assertThat(compound.getConnector().getName()).isEqualTo("and");

            Enumeration subExprs = compound.getSubExpressions();
            assertThat(((Expression) subExprs.nextElement()).toString()).isEqualTo("P");
            assertThat(((Expression) subExprs.nextElement()).toString()).isEqualTo("Q");
        }

        @Test
        @DisplayName("should parse expression with tabs and newlines")
        void shouldParseWithTabsAndNewlines() throws ParseException {
            Expression expr = tokenizer.parseExpression("and\tP\nQ");

            assertThat(expr).isInstanceOf(ExpressionWithSubExpressions.class);
            ExpressionWithSubExpressions compound = (ExpressionWithSubExpressions) expr;
            assertThat(compound.getConnector().getName()).isEqualTo("and");
        }

        @Test
        @DisplayName("should parse expression with leading/trailing whitespace")
        void shouldParseWithLeadingTrailingWhitespace() throws ParseException {
            Expression expr = tokenizer.parseExpression("\n\t  P  \t\n");

            assertThat(expr).isInstanceOf(ConstantExpression.class);
            assertThat(expr.toString()).isEqualTo("P");
        }
    }

    @Nested
    @DisplayName("Parentheses Handling")
    class ParenthesesHandling {

        @Test
        @DisplayName("should handle parentheses in output string based on priority")
        void shouldHandleParentheses() throws ParseException {
            // When nested expression has lower priority, parentheses are added in toString
            Expression expr = tokenizer.parseExpression("not and P Q");

            // The 'not' has higher priority than 'and', so parentheses should appear
            // in the string representation
            assertThat(expr).isInstanceOf(ExpressionWithSubExpressions.class);
            String output = expr.toString();
            // Output should be ~(P&Q) because 'and' has lower priority than 'not'
            assertThat(output).isEqualTo("~(P&Q)");
        }

        @Test
        @DisplayName("should not add unnecessary parentheses for same priority")
        void shouldNotAddUnnecessaryParentheses() throws ParseException {
            // For associative connectors with same priority
            Expression expr = tokenizer.parseExpression("and P Q");

            assertThat(expr.toString()).isEqualTo("P&Q");
        }
    }

    @Nested
    @DisplayName("Error Handling")
    class ErrorHandling {

        @Test
        @DisplayName("should reject malformed expression (unknown connector)")
        void shouldRejectMalformedExpression() {
            assertThatThrownBy(() -> tokenizer.parseExpression("unknown P"))
                    .isInstanceOf(ParseException.class)
                    .hasMessageContaining("unknown");
        }

        @Test
        @DisplayName("should reject empty expression")
        void shouldRejectEmptyExpression() {
            assertThatThrownBy(() -> tokenizer.parseExpression(""))
                    .isInstanceOf(ParseException.class);
        }

        @Test
        @DisplayName("should reject expression with only whitespace")
        void shouldRejectWhitespaceOnlyExpression() {
            assertThatThrownBy(() -> tokenizer.parseExpression("   "))
                    .isInstanceOf(ParseException.class);
        }

        @Test
        @DisplayName("should reject incomplete unary expression")
        void shouldRejectIncompleteUnary() {
            assertThatThrownBy(() -> tokenizer.parseExpression("not"))
                    .isInstanceOf(ParseException.class);
        }

        @Test
        @DisplayName("should reject incomplete binary expression")
        void shouldRejectIncompleteBinary() {
            assertThatThrownBy(() -> tokenizer.parseExpression("and P"))
                    .isInstanceOf(ParseException.class);
        }

        @Test
        @DisplayName("should reject lowercase starting token as unknown connector")
        void shouldRejectLowercaseUnknownConnector() {
            assertThatThrownBy(() -> tokenizer.parseExpression("lowercase"))
                    .isInstanceOf(ParseException.class);
        }
    }

    @Nested
    @DisplayName("Code String Generation")
    class CodeStringGeneration {

        @Test
        @DisplayName("should generate correct code string for constant")
        void shouldGenerateCodeStringForConstant() throws ParseException {
            Expression expr = tokenizer.parseExpression("P");

            assertThat(expr.getCodeString()).isEqualTo("P");
        }

        @Test
        @DisplayName("should generate correct code string for variable")
        void shouldGenerateCodeStringForVariable() throws ParseException {
            Expression expr = tokenizer.parseExpression("_A");

            assertThat(expr.getCodeString()).isEqualTo("variable A");
        }

        @Test
        @DisplayName("should generate correct code string for compound expression")
        void shouldGenerateCodeStringForCompound() throws ParseException {
            Expression expr = tokenizer.parseExpression("and P Q");

            assertThat(expr.getCodeString()).isEqualTo("and P Q");
        }

        @Test
        @DisplayName("should generate correct code string for nested expression")
        void shouldGenerateCodeStringForNested() throws ParseException {
            Expression expr = tokenizer.parseExpression("imp and P Q not R");

            assertThat(expr.getCodeString()).isEqualTo("imp and P Q not R");
        }
    }

    @Nested
    @DisplayName("Expression Equality")
    class ExpressionEquality {

        @Test
        @DisplayName("should recognize equal constant expressions")
        void shouldRecognizeEqualConstants() throws ParseException {
            Expression expr1 = tokenizer.parseExpression("P");
            Expression expr2 = tokenizer.parseExpression("P");

            assertThat(expr1).isEqualTo(expr2);
        }

        @Test
        @DisplayName("should recognize equal compound expressions")
        void shouldRecognizeEqualCompounds() throws ParseException {
            Expression expr1 = tokenizer.parseExpression("and P Q");
            Expression expr2 = tokenizer.parseExpression("and P Q");

            assertThat(expr1).isEqualTo(expr2);
        }

        @Test
        @DisplayName("should distinguish different expressions")
        void shouldDistinguishDifferentExpressions() throws ParseException {
            Expression expr1 = tokenizer.parseExpression("and P Q");
            Expression expr2 = tokenizer.parseExpression("or P Q");

            assertThat(expr1).isNotEqualTo(expr2);
        }

        @Test
        @DisplayName("should distinguish expressions with different arguments")
        void shouldDistinguishDifferentArguments() throws ParseException {
            Expression expr1 = tokenizer.parseExpression("and P Q");
            Expression expr2 = tokenizer.parseExpression("and P R");

            assertThat(expr1).isNotEqualTo(expr2);
        }
    }

    @Nested
    @DisplayName("Comment Handling")
    class CommentHandling {

        @Test
        @DisplayName("should ignore line comments in expression")
        void shouldIgnoreLineComments() throws ParseException {
            Expression expr = tokenizer.parseExpression("// comment\nP");

            assertThat(expr).isInstanceOf(ConstantExpression.class);
            assertThat(expr.toString()).isEqualTo("P");
        }

        @Test
        @DisplayName("should ignore block comments in expression")
        void shouldIgnoreBlockComments() throws ParseException {
            Expression expr = tokenizer.parseExpression("/* block */ and P Q");

            assertThat(expr).isInstanceOf(ExpressionWithSubExpressions.class);
            ExpressionWithSubExpressions compound = (ExpressionWithSubExpressions) expr;
            assertThat(compound.getConnector().getName()).isEqualTo("and");
        }

        @Test
        @DisplayName("should handle comments between tokens")
        void shouldHandleCommentsBetweenTokens() throws ParseException {
            Expression expr = tokenizer.parseExpression("and /* comment */ P Q");

            assertThat(expr).isInstanceOf(ExpressionWithSubExpressions.class);
            ExpressionWithSubExpressions compound = (ExpressionWithSubExpressions) expr;
            assertThat(compound.getConnector().getName()).isEqualTo("and");
        }
    }

    @Nested
    @DisplayName("Connector Usage Detection")
    class ConnectorUsageDetection {

        @Test
        @DisplayName("should detect connector usage in expression")
        void shouldDetectConnectorUsage() throws ParseException {
            Expression expr = tokenizer.parseExpression("and P not Q");
            Connector notConnector = logic.getConnector("not");
            Connector andConnector = logic.getConnector("and");
            Connector orConnector = logic.getConnector("or");

            assertThat(expr.isUsed(notConnector)).isTrue();
            assertThat(expr.isUsed(andConnector)).isTrue();
            assertThat(expr.isUsed(orConnector)).isFalse();
        }

        @Test
        @DisplayName("should return all used connectors")
        void shouldReturnAllUsedConnectors() throws ParseException {
            Expression expr = tokenizer.parseExpression("imp and P Q nec R");

            assertThat(expr.getUsedConnectors())
                    .extracting(Connector::getName)
                    .containsExactly("imp", "and", "nec");
        }
    }

    @Nested
    @DisplayName("Variable Expression Detection")
    class VariableExpressionDetection {

        @Test
        @DisplayName("should return empty list for constant expression")
        void shouldReturnEmptyForConstant() throws ParseException {
            Expression expr = tokenizer.parseExpression("P");

            assertThat(expr.getVariableExpressions()).isEmpty();
        }

        @Test
        @DisplayName("should return variable from simple variable expression")
        void shouldReturnVariableFromSimple() throws ParseException {
            Expression expr = tokenizer.parseExpression("_A");

            assertThat(expr.getVariableExpressions()).hasSize(1);
            assertThat(expr.getVariableExpressions().get(0)).isInstanceOf(VariableExpression.class);
        }

        @Test
        @DisplayName("should return all variables from compound expression")
        void shouldReturnAllVariablesFromCompound() throws ParseException {
            Expression expr = tokenizer.parseExpression("and _A _B");

            assertThat(expr.getVariableExpressions()).hasSize(2);
        }
    }
}
