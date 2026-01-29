package lotrec.dataStructure.expression;

import lotrec.TestFixtures;
import lotrec.dataStructure.Logic;
import lotrec.parser.OldiesTokenizer;
import lotrec.parser.exceptions.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for expression matching (matchWith method) across various expression types.
 * Expression matching is fundamental to the tableau rule engine - it determines
 * when inference rules can be applied by matching patterns against formulas.
 */
@DisplayName("Expression Matching")
class ExpressionMatchingTest {

    private Logic logic;
    private OldiesTokenizer tokenizer;
    private Connector andConnector;
    private Connector orConnector;
    private Connector notConnector;
    private Connector necConnector;
    private Connector posConnector;

    @BeforeEach
    void setUp() {
        logic = TestFixtures.createMinimalLogic();
        tokenizer = TestFixtures.createTokenizer(logic);

        // Cache connectors for direct expression building
        andConnector = logic.getConnector("and");
        orConnector = logic.getConnector("or");
        notConnector = logic.getConnector("not");
        necConnector = logic.getConnector("nec");
        posConnector = logic.getConnector("pos");
    }

    /**
     * Helper method to parse expressions using the tokenizer.
     */
    private Expression parse(String formula) throws ParseException {
        return tokenizer.parseExpression(formula);
    }

    /**
     * Helper method to build an expression with sub-expressions directly.
     */
    private ExpressionWithSubExpressions buildExpr(Connector conn, Expression... subExprs) {
        ExpressionWithSubExpressions expr = new ExpressionWithSubExpressions(conn);
        for (int i = 0; i < subExprs.length; i++) {
            expr.setExpression(subExprs[i], i);
        }
        return expr;
    }

    @Nested
    @DisplayName("Structural Matching")
    class StructuralMatching {

        @Test
        @DisplayName("should match structurally when composite expressions have same connector")
        void shouldMatchStructurally() throws ParseException {
            // Pattern: and P Q
            Expression pattern = parse("and P Q");
            // Target: and P Q (same structure)
            Expression target = parse("and P Q");

            InstanceSet result = pattern.matchWith(target, new InstanceSet());

            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("should reject match when connectors differ")
        void shouldRejectDifferentConnector() throws ParseException {
            // Pattern: and P Q
            Expression pattern = parse("and P Q");
            // Target: or P Q (different connector)
            Expression target = parse("or P Q");

            InstanceSet result = pattern.matchWith(target, new InstanceSet());

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should reject match when arities differ")
        void shouldRejectArityMismatch() {
            // Create expressions with connectors of different arity
            // Pattern: unary not
            Expression pattern = buildExpr(notConnector, new ConstantExpression("P"));

            // Target: binary and - different arity
            Expression target = buildExpr(andConnector,
                    new ConstantExpression("P"),
                    new ConstantExpression("Q"));

            InstanceSet result = pattern.matchWith(target, new InstanceSet());

            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("Variable Binding")
    class VariableBinding {

        @Test
        @DisplayName("should match recursively with variables binding to sub-expressions")
        void shouldMatchRecursivelyWithVariables() throws ParseException {
            // Pattern with variable: and _A _B (variables bind to any expression)
            Expression pattern = parse("and _A _B");
            // Target: and P Q
            Expression target = parse("and P Q");

            InstanceSet result = pattern.matchWith(target, new InstanceSet());

            assertThat(result).isNotNull();
            // Variables A and B should be bound
            VariableExpression varA = new VariableExpression("A");
            VariableExpression varB = new VariableExpression("B");
            assertThat(result.get(varA)).isNotNull();
            assertThat(result.get(varB)).isNotNull();
        }

        @Test
        @DisplayName("should instantiate expression with bindings from InstanceSet")
        void shouldInstantiateWithBindings() throws ParseException {
            // Create a pattern with variables
            Expression pattern = parse("and _A _B");

            // Create bindings
            InstanceSet bindings = new InstanceSet();
            VariableExpression varA = new VariableExpression("A");
            VariableExpression varB = new VariableExpression("B");
            Expression exprP = parse("P");
            Expression exprQ = parse("Q");
            bindings.put(varA, exprP);
            bindings.put(varB, exprQ);

            // Get instance should substitute variables
            Expression instance = pattern.getInstance(bindings);

            assertThat(instance).isNotNull();
            assertThat(instance.toString()).contains("P");
            assertThat(instance.toString()).contains("Q");
        }

        @Test
        @DisplayName("should match with multiple variables binding correctly")
        void shouldMatchWithMultipleVariables() throws ParseException {
            // Pattern: and _X or _Y _Z
            Expression pattern = parse("and _X or _Y _Z");
            // Target: and P or Q R
            Expression target = parse("and P or Q R");

            InstanceSet result = pattern.matchWith(target, new InstanceSet());

            assertThat(result).isNotNull();
            VariableExpression varX = new VariableExpression("X");
            VariableExpression varY = new VariableExpression("Y");
            VariableExpression varZ = new VariableExpression("Z");
            assertThat(result.get(varX)).isNotNull();
            assertThat(result.get(varY)).isNotNull();
            assertThat(result.get(varZ)).isNotNull();
        }

        @Test
        @DisplayName("should preserve existing bindings across match")
        void shouldPreserveBindingsAcrossMatch() throws ParseException {
            // Start with pre-existing bindings
            InstanceSet existing = new InstanceSet();
            VariableExpression varA = new VariableExpression("A");
            Expression exprP = parse("P");
            existing.put(varA, exprP);

            // Pattern using same variable should match if binding is consistent
            Expression pattern = parse("not _A");
            Expression target = parse("not P");

            InstanceSet result = pattern.matchWith(target, existing);

            assertThat(result).isNotNull();
            // Original binding should be preserved
            assertThat(result.get(varA)).isEqualTo(exprP);
        }

        @Test
        @DisplayName("should fail when same variable binds to different expressions")
        void shouldFailInconsistentVariableBinding() throws ParseException {
            // Start with pre-existing binding: A -> P
            InstanceSet existing = new InstanceSet();
            VariableExpression varA = new VariableExpression("A");
            Expression exprP = parse("P");
            existing.put(varA, exprP);

            // Pattern tries to bind A to Q (inconsistent)
            Expression pattern = parse("not _A");
            Expression target = parse("not Q"); // Different from P

            InstanceSet result = pattern.matchWith(target, existing);

            // Should fail because A is already bound to P but target has Q
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("Nested Expression Matching")
    class NestedExpressionMatching {

        @Test
        @DisplayName("should match deeply nested expressions")
        void shouldMatchNestedExpressions() throws ParseException {
            // Pattern: and not _A or _B _C
            Expression pattern = parse("and not _A or _B _C");
            // Target: and not P or Q R
            Expression target = parse("and not P or Q R");

            InstanceSet result = pattern.matchWith(target, new InstanceSet());

            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("should handle very deep nesting")
        void shouldHandleDeepNesting() throws ParseException {
            // Build deeply nested: not not not not P
            Expression pattern = parse("not not not not _A");
            Expression target = parse("not not not not P");

            InstanceSet result = pattern.matchWith(target, new InstanceSet());

            assertThat(result).isNotNull();
            VariableExpression varA = new VariableExpression("A");
            assertThat(result.get(varA)).isNotNull();
        }

        @Test
        @DisplayName("should fail deep nesting match with structural difference")
        void shouldFailDeepNestingWithStructuralDifference() throws ParseException {
            // Pattern: and P and Q R (right-associative)
            Expression pattern = parse("and P and Q R");
            // Target: and and P Q R (left-associative - different structure)
            Expression target = parse("and and P Q R");

            InstanceSet result = pattern.matchWith(target, new InstanceSet());

            // These have different tree structures
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("Modal Expression Matching")
    class ModalExpressionMatching {

        @Test
        @DisplayName("should match necessity (box) expressions")
        void shouldMatchModalExpressions() throws ParseException {
            // Pattern: nec _A
            Expression pattern = parse("nec _A");
            // Target: nec P
            Expression target = parse("nec P");

            InstanceSet result = pattern.matchWith(target, new InstanceSet());

            assertThat(result).isNotNull();
            VariableExpression varA = new VariableExpression("A");
            assertThat(result.get(varA)).isNotNull();
        }

        @Test
        @DisplayName("should match possibility (diamond) expressions")
        void shouldMatchPossibilityExpressions() throws ParseException {
            // Pattern: pos _A
            Expression pattern = parse("pos _A");
            // Target: pos P
            Expression target = parse("pos P");

            InstanceSet result = pattern.matchWith(target, new InstanceSet());

            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("should distinguish between nec and pos")
        void shouldDistinguishNecAndPos() throws ParseException {
            // Pattern: nec _A
            Expression pattern = parse("nec _A");
            // Target: pos P (different modal operator)
            Expression target = parse("pos P");

            InstanceSet result = pattern.matchWith(target, new InstanceSet());

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should match nested modal expressions")
        void shouldMatchNestedModalExpressions() throws ParseException {
            // Pattern: nec pos _A
            Expression pattern = parse("nec pos _A");
            // Target: nec pos P
            Expression target = parse("nec pos P");

            InstanceSet result = pattern.matchWith(target, new InstanceSet());

            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("Constant Expression Matching")
    class ConstantExpressionMatching {

        @Test
        @DisplayName("should match identical constants")
        void shouldMatchIdenticalConstants() {
            ConstantExpression const1 = new ConstantExpression("P");
            ConstantExpression const2 = new ConstantExpression("P");

            InstanceSet result = const1.matchWith(const2, new InstanceSet());

            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("should return null when constants differ")
        void shouldReturnNullOnMismatch() {
            ConstantExpression const1 = new ConstantExpression("P");
            ConstantExpression const2 = new ConstantExpression("Q");

            InstanceSet result = const1.matchWith(const2, new InstanceSet());

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should match constants case-insensitively")
        void shouldMatchConstantsCaseInsensitive() {
            ConstantExpression lower = new ConstantExpression("p");
            ConstantExpression upper = new ConstantExpression("P");

            // ConstantExpression.equals uses equalsIgnoreCase
            InstanceSet result = lower.matchWith(upper, new InstanceSet());

            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("InstanceSet Operations")
    class InstanceSetOperations {

        @Test
        @DisplayName("should create new InstanceSet")
        void shouldCreateNewInstanceSet() {
            InstanceSet set = new InstanceSet();

            assertThat(set).isNotNull();
            assertThat(set.getTable()).isEmpty();
        }

        @Test
        @DisplayName("should add and retrieve bindings")
        void shouldAddAndRetrieveBindings() {
            InstanceSet set = new InstanceSet();
            VariableExpression var = new VariableExpression("X");
            ConstantExpression constant = new ConstantExpression("value");

            set.put(var, constant);

            assertThat(set.get(var)).isEqualTo(constant);
        }

        @Test
        @DisplayName("should create extended set with plus method")
        void shouldCreateExtendedSetWithPlus() {
            InstanceSet original = new InstanceSet();
            VariableExpression var = new VariableExpression("Y");
            ConstantExpression constant = new ConstantExpression("value");

            InstanceSet extended = original.plus(var, constant);

            assertThat(extended).isNotNull();
            assertThat(extended.get(var)).isEqualTo(constant);
            // Original should not be modified
            assertThat(original.get(var)).isNull();
        }

        @Test
        @DisplayName("should return same set when binding already exists with same value")
        void shouldReturnSameSetForExistingBinding() {
            InstanceSet set = new InstanceSet();
            VariableExpression var = new VariableExpression("Z");
            ConstantExpression constant = new ConstantExpression("value");
            set.put(var, constant);

            InstanceSet result = set.plus(var, constant);

            // Should return same instance when binding matches
            assertThat(result).isSameAs(set);
        }

        @Test
        @DisplayName("should return null when adding conflicting binding")
        void shouldReturnNullForConflictingBinding() {
            InstanceSet set = new InstanceSet();
            VariableExpression var = new VariableExpression("W");
            ConstantExpression original = new ConstantExpression("original");
            ConstantExpression conflicting = new ConstantExpression("conflicting");
            set.put(var, original);

            InstanceSet result = set.plus(var, conflicting);

            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("Variable Expression Behavior")
    class VariableExpressionBehavior {

        @Test
        @DisplayName("variable should match any expression and create binding")
        void variableShouldMatchAnyExpression() throws ParseException {
            VariableExpression var = new VariableExpression("A");
            Expression target = parse("and P Q"); // Complex expression

            InstanceSet result = var.matchWith(target, new InstanceSet());

            assertThat(result).isNotNull();
            assertThat(result.get(var)).isEqualTo(target);
        }

        @Test
        @DisplayName("variable getInstance should return bound expression")
        void variableGetInstanceShouldReturnBoundExpression() throws ParseException {
            VariableExpression var = new VariableExpression("A");
            Expression bound = parse("P");

            InstanceSet set = new InstanceSet();
            set.put(var, bound);

            Expression instance = var.getInstance(set);

            assertThat(instance).isEqualTo(bound);
        }

        @Test
        @DisplayName("variable getInstance should return null when unbound")
        void variableGetInstanceShouldReturnNullWhenUnbound() {
            VariableExpression var = new VariableExpression("A");
            InstanceSet emptySet = new InstanceSet();

            Expression instance = var.getInstance(emptySet);

            assertThat(instance).isNull();
        }
    }
}
