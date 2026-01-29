package lotrec.dataStructure.expression;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Comprehensive tests for ConstantExpression.
 * Tests cover construction, equality, matching, and instantiation behavior.
 */
@DisplayName("ConstantExpression")
class ConstantExpressionTest {

    private ConstantExpression constant;
    private InstanceSet instanceSet;

    @BeforeEach
    void setUp() {
        constant = new ConstantExpression("P");
        instanceSet = new InstanceSet();
    }

    @Nested
    @DisplayName("Static Constants")
    class StaticConstants {

        @Test
        @DisplayName("should return FALSUM constant")
        void shouldReturnFalsumConstant() {
            assertThat(ConstantExpression.FALSUM).isNotNull();
            assertThat(ConstantExpression.FALSUM.toString()).isEqualTo("False");
        }

        @Test
        @DisplayName("FALSUM should be a singleton constant")
        void falsumShouldBeSingleton() {
            ConstantExpression falsum1 = ConstantExpression.FALSUM;
            ConstantExpression falsum2 = ConstantExpression.FALSUM;

            assertThat(falsum1).isSameAs(falsum2);
        }
    }

    @Nested
    @DisplayName("Constructor")
    class Constructor {

        @Test
        @DisplayName("should create constant from string name")
        void shouldCreateFromString() {
            ConstantExpression expr = new ConstantExpression("MyConstant");

            assertThat(expr.toString()).isEqualTo("MyConstant");
        }

        @Test
        @DisplayName("should create constant with default name when no argument provided")
        void shouldCreateWithDefaultName() {
            ConstantExpression expr1 = new ConstantExpression();
            ConstantExpression expr2 = new ConstantExpression();

            // Each should have a unique default name
            assertThat(expr1.toString()).startsWith(ConstantExpression.DEFAULT_NAME);
            assertThat(expr2.toString()).startsWith(ConstantExpression.DEFAULT_NAME);
            assertThat(expr1.toString()).isNotEqualTo(expr2.toString());
        }
    }

    @Nested
    @DisplayName("toString")
    class ToStringTests {

        @Test
        @DisplayName("should return name as toString")
        void shouldReturnNameAsToString() {
            ConstantExpression expr = new ConstantExpression("TestName");

            assertThat(expr.toString()).isEqualTo("TestName");
        }

        @Test
        @DisplayName("should preserve case in toString")
        void shouldPreserveCaseInToString() {
            ConstantExpression expr = new ConstantExpression("MixedCase");

            assertThat(expr.toString()).isEqualTo("MixedCase");
        }
    }

    @Nested
    @DisplayName("Equality")
    class Equality {

        @Test
        @DisplayName("should equal case insensitive")
        void shouldEqualCaseInsensitive() {
            ConstantExpression upper = new ConstantExpression("PROP");
            ConstantExpression lower = new ConstantExpression("prop");
            ConstantExpression mixed = new ConstantExpression("Prop");

            assertThat(upper.equals(lower)).isTrue();
            assertThat(upper.equals(mixed)).isTrue();
            assertThat(lower.equals(mixed)).isTrue();
        }

        @Test
        @DisplayName("should not equal different constant names")
        void shouldNotEqualDifferentNames() {
            ConstantExpression expr1 = new ConstantExpression("P");
            ConstantExpression expr2 = new ConstantExpression("Q");

            assertThat(expr1.equals(expr2)).isFalse();
        }

        @Test
        @DisplayName("should not equal non-ConstantExpression objects")
        void shouldNotEqualNonConstantExpression() {
            ConstantExpression expr = new ConstantExpression("P");

            assertThat(expr.equals("P")).isFalse();
            assertThat(expr.equals(null)).isFalse();
            assertThat(expr.equals(new Object())).isFalse();
        }

        @Test
        @DisplayName("should not equal VariableExpression with same name")
        void shouldNotEqualVariableExpression() {
            ConstantExpression constExpr = new ConstantExpression("P");
            VariableExpression varExpr = new VariableExpression("P");

            assertThat(constExpr.equals(varExpr)).isFalse();
        }
    }

    @Nested
    @DisplayName("matchWith")
    class MatchWith {

        @Test
        @DisplayName("should match with same constant returning InstanceSet")
        void shouldMatchWithSameConstant() {
            ConstantExpression expr1 = new ConstantExpression("P");
            ConstantExpression expr2 = new ConstantExpression("P");

            InstanceSet result = expr1.matchWith(expr2, instanceSet);

            assertThat(result).isNotNull();
            assertThat(result).isSameAs(instanceSet);
        }

        @Test
        @DisplayName("should match with case-insensitive constant")
        void shouldMatchCaseInsensitive() {
            ConstantExpression upper = new ConstantExpression("PROP");
            ConstantExpression lower = new ConstantExpression("prop");

            InstanceSet result = upper.matchWith(lower, instanceSet);

            assertThat(result).isNotNull();
            assertThat(result).isSameAs(instanceSet);
        }

        @Test
        @DisplayName("should not match with different constant returning null")
        void shouldNotMatchWithDifferentConstant() {
            ConstantExpression expr1 = new ConstantExpression("P");
            ConstantExpression expr2 = new ConstantExpression("Q");

            InstanceSet result = expr1.matchWith(expr2, instanceSet);

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should not match with VariableExpression")
        void shouldNotMatchWithVariableExpression() {
            ConstantExpression constExpr = new ConstantExpression("P");
            VariableExpression varExpr = new VariableExpression("P");

            InstanceSet result = constExpr.matchWith(varExpr, instanceSet);

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should not match with composite expression")
        void shouldNotMatchWithCompositeExpression() {
            ConstantExpression constExpr = new ConstantExpression("P");
            Connector notConnector = new Connector("not", 1, "~_");
            ExpressionWithSubExpressions composite = new ExpressionWithSubExpressions(notConnector);
            composite.setExpression(new ConstantExpression("Q"), 0);

            InstanceSet result = constExpr.matchWith(composite, instanceSet);

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should handle empty InstanceSet correctly")
        void shouldHandleEmptyInstanceSet() {
            ConstantExpression expr1 = new ConstantExpression("P");
            ConstantExpression expr2 = new ConstantExpression("P");
            InstanceSet emptySet = new InstanceSet();

            InstanceSet result = expr1.matchWith(expr2, emptySet);

            assertThat(result).isNotNull();
            assertThat(result).isSameAs(emptySet);
        }

        @Test
        @DisplayName("should preserve existing InstanceSet when matching")
        void shouldPreserveExistingInstanceSet() {
            ConstantExpression expr1 = new ConstantExpression("P");
            ConstantExpression expr2 = new ConstantExpression("P");

            // Pre-populate instance set with some data
            VariableExpression var = new VariableExpression("X");
            ConstantExpression value = new ConstantExpression("SomeValue");
            instanceSet.put(var, value);

            InstanceSet result = expr1.matchWith(expr2, instanceSet);

            assertThat(result).isNotNull();
            assertThat(result.get(var)).isEqualTo(value);
        }
    }

    @Nested
    @DisplayName("getInstance")
    class GetInstance {

        @Test
        @DisplayName("should return this from getInstance")
        void shouldReturnThisFromGetInstance() {
            ConstantExpression expr = new ConstantExpression("P");

            Expression result = expr.getInstance(instanceSet);

            assertThat(result).isSameAs(expr);
        }

        @Test
        @DisplayName("should return this regardless of InstanceSet contents")
        void shouldReturnThisRegardlessOfInstanceSetContents() {
            ConstantExpression expr = new ConstantExpression("P");
            VariableExpression var = new VariableExpression("P");
            instanceSet.put(var, new ConstantExpression("Q"));

            Expression result = expr.getInstance(instanceSet);

            assertThat(result).isSameAs(expr);
            assertThat(result.toString()).isEqualTo("P");
        }

        @Test
        @DisplayName("should return this with empty InstanceSet")
        void shouldReturnThisWithEmptyInstanceSet() {
            ConstantExpression expr = new ConstantExpression("Test");
            InstanceSet emptySet = new InstanceSet();

            Expression result = expr.getInstance(emptySet);

            assertThat(result).isSameAs(expr);
        }
    }

    @Nested
    @DisplayName("Other Methods")
    class OtherMethods {

        @Test
        @DisplayName("getCodeString should return name")
        void getCodeStringShouldReturnName() {
            ConstantExpression expr = new ConstantExpression("MyConst");

            assertThat(expr.getCodeString()).isEqualTo("MyConst");
        }

        @Test
        @DisplayName("toMSPASS should return lowercase name")
        void toMSPASSShouldReturnLowercaseName() {
            ConstantExpression expr = new ConstantExpression("PROP");

            assertThat(expr.toMSPASS()).isEqualTo("prop");
        }

        @Test
        @DisplayName("isUsed should always return false")
        void isUsedShouldAlwaysReturnFalse() {
            ConstantExpression expr = new ConstantExpression("P");
            Connector connector = new Connector("not", 1, "~_");

            assertThat(expr.isUsed(connector)).isFalse();
        }

        @Test
        @DisplayName("getVariableExpressions should return empty list")
        void getVariableExpressionsShouldReturnEmptyList() {
            ConstantExpression expr = new ConstantExpression("P");

            assertThat(expr.getVariableExpressions()).isEmpty();
        }

        @Test
        @DisplayName("getUsedConnectors should return empty list")
        void getUsedConnectorsShouldReturnEmptyList() {
            ConstantExpression expr = new ConstantExpression("P");

            assertThat(expr.getUsedConnectors()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Integration Scenarios")
    class IntegrationScenarios {

        @Test
        @DisplayName("VariableExpression should match with ConstantExpression")
        void variableExpressionShouldMatchWithConstant() {
            // This tests the reverse scenario - VariableExpression matching against ConstantExpression
            VariableExpression varExpr = new VariableExpression("X");
            ConstantExpression constExpr = new ConstantExpression("P");

            InstanceSet result = varExpr.matchWith(constExpr, instanceSet);

            assertThat(result).isNotNull();
            assertThat(result.get(varExpr)).isEqualTo(constExpr);
        }

        @Test
        @DisplayName("FALSUM should match with equal constant")
        void falsumShouldMatchWithEqualConstant() {
            ConstantExpression falseConstant = new ConstantExpression("False");

            InstanceSet result = ConstantExpression.FALSUM.matchWith(falseConstant, instanceSet);

            assertThat(result).isNotNull();
        }
    }
}
