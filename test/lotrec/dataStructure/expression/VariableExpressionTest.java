package lotrec.dataStructure.expression;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.*;

/**
 * Comprehensive tests for VariableExpression.
 * VariableExpression is a scheme variable that can match with any expression
 * and maintains bindings in an InstanceSet.
 */
@DisplayName("VariableExpression")
class VariableExpressionTest {

    private VariableExpression variable;
    private InstanceSet instanceSet;

    @BeforeEach
    void setUp() {
        variable = new VariableExpression("X");
        instanceSet = new InstanceSet();
    }

    @Nested
    @DisplayName("Binding Behavior")
    class BindingBehavior {

        @Test
        @DisplayName("should bind on first match - matchWith adds binding to InstanceSet")
        void shouldBindOnFirstMatch() {
            ConstantExpression constant = new ConstantExpression("P");

            InstanceSet result = variable.matchWith(constant, instanceSet);

            assertThat(result).isNotNull();
            assertThat(result.get(variable)).isEqualTo(constant);
        }

        @Test
        @DisplayName("should verify existing binding - consistent binding succeeds")
        void shouldVerifyExistingBinding() {
            ConstantExpression constant = new ConstantExpression("P");

            // First match creates binding
            InstanceSet afterFirstMatch = variable.matchWith(constant, instanceSet);
            assertThat(afterFirstMatch).isNotNull();

            // Second match with same expression should succeed
            InstanceSet afterSecondMatch = variable.matchWith(constant, afterFirstMatch);

            assertThat(afterSecondMatch).isNotNull();
            assertThat(afterSecondMatch.get(variable)).isEqualTo(constant);
        }

        @Test
        @DisplayName("should reject inconsistent binding - returns null")
        void shouldRejectInconsistentBinding() {
            ConstantExpression firstConstant = new ConstantExpression("P");
            ConstantExpression secondConstant = new ConstantExpression("Q");

            // First match creates binding to P
            InstanceSet afterFirstMatch = variable.matchWith(firstConstant, instanceSet);
            assertThat(afterFirstMatch).isNotNull();

            // Second match with different expression should fail
            InstanceSet afterSecondMatch = variable.matchWith(secondConstant, afterFirstMatch);

            assertThat(afterSecondMatch).isNull();
        }

        @Test
        @DisplayName("should return bound value from getInstance")
        void shouldReturnBoundFromGetInstance() {
            ConstantExpression constant = new ConstantExpression("P");
            instanceSet.put(variable, constant);

            Expression result = variable.getInstance(instanceSet);

            assertThat(result).isEqualTo(constant);
        }

        @Test
        @DisplayName("should return null for unbound variable from getInstance")
        void shouldReturnNullForUnbound() {
            Expression result = variable.getInstance(instanceSet);

            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("Matching Behavior")
    class MatchingBehavior {

        @Test
        @DisplayName("should match any constant expression")
        void shouldMatchAnyConstantExpression() {
            ConstantExpression constant = new ConstantExpression("ANY_CONSTANT");

            InstanceSet result = variable.matchWith(constant, instanceSet);

            assertThat(result).isNotNull();
            assertThat(result.get(variable)).isEqualTo(constant);
        }

        @Test
        @DisplayName("should match another variable expression")
        void shouldMatchAnotherVariableExpression() {
            VariableExpression otherVariable = new VariableExpression("Y");

            InstanceSet result = variable.matchWith(otherVariable, instanceSet);

            assertThat(result).isNotNull();
            assertThat(result.get(variable)).isEqualTo(otherVariable);
        }

        @Test
        @DisplayName("should match compound expression with subexpressions")
        void shouldMatchCompoundExpression() {
            Connector notConnector = new Connector("not", 1, "~_");
            ExpressionWithSubExpressions compoundExpr = new ExpressionWithSubExpressions(notConnector);
            compoundExpr.setExpression(new ConstantExpression("P"), 0);

            InstanceSet result = variable.matchWith(compoundExpr, instanceSet);

            assertThat(result).isNotNull();
            assertThat(result.get(variable)).isEqualTo(compoundExpr);
        }
    }

    @Nested
    @DisplayName("Equality and Identity")
    class EqualityAndIdentity {

        @Test
        @DisplayName("should equal case-sensitive with same name")
        void shouldEqualCaseSensitive() {
            VariableExpression var1 = new VariableExpression("TestVar");
            VariableExpression var2 = new VariableExpression("TestVar");

            assertThat(var1).isEqualTo(var2);
            assertThat(var1.hashCode()).isEqualTo(var2.hashCode());
        }

        @Test
        @DisplayName("should not equal with different case")
        void shouldNotEqualDifferentCase() {
            VariableExpression var1 = new VariableExpression("TestVar");
            VariableExpression var2 = new VariableExpression("TESTVAR");

            assertThat(var1).isNotEqualTo(var2);
        }

        @Test
        @DisplayName("should not equal with different name")
        void shouldNotEqualDifferentName() {
            VariableExpression var1 = new VariableExpression("X");
            VariableExpression var2 = new VariableExpression("Y");

            assertThat(var1).isNotEqualTo(var2);
        }

        @Test
        @DisplayName("should not equal constant expression with same name")
        void shouldNotEqualConstantExpression() {
            VariableExpression varExpr = new VariableExpression("P");
            ConstantExpression constExpr = new ConstantExpression("P");

            assertThat(varExpr).isNotEqualTo(constExpr);
        }

        @Test
        @DisplayName("should identify by name property")
        void shouldIdentifyByName() {
            VariableExpression namedVar = new VariableExpression("MyVariable");

            assertThat(namedVar.toString()).isEqualTo("MyVariable");
        }
    }

    @Nested
    @DisplayName("Construction")
    class Construction {

        @Test
        @DisplayName("should create with specified name")
        void shouldCreateWithSpecifiedName() {
            VariableExpression namedVar = new VariableExpression("CustomName");

            assertThat(namedVar.toString()).isEqualTo("CustomName");
        }

        @Test
        @DisplayName("should create with default name")
        void shouldCreateWithDefaultName() {
            VariableExpression defaultVar = new VariableExpression();

            assertThat(defaultVar.toString()).isEqualTo(VariableExpression.defaultName);
        }

        @Test
        @DisplayName("should create StringSchemeVariable as scheme")
        void shouldCreateSchemeVariable() {
            StringSchemeVariable scheme = new StringSchemeVariable("testScheme");

            assertThat(scheme.toString()).isEqualTo("testScheme");
            assertThat(scheme.hashCode()).isEqualTo("testScheme".hashCode());
        }

        @Test
        @DisplayName("StringSchemeVariable equality should be case-sensitive")
        void stringSchemeVariableShouldBeCaseSensitive() {
            StringSchemeVariable scheme1 = new StringSchemeVariable("Test");
            StringSchemeVariable scheme2 = new StringSchemeVariable("Test");
            StringSchemeVariable scheme3 = new StringSchemeVariable("TEST");

            assertThat(scheme1).isEqualTo(scheme2);
            assertThat(scheme1).isNotEqualTo(scheme3);
        }
    }

    @Nested
    @DisplayName("Additional Expression Interface Methods")
    class AdditionalMethods {

        @Test
        @DisplayName("should return code string representation")
        void shouldReturnCodeString() {
            VariableExpression var = new VariableExpression("MyVar");

            String codeString = var.getCodeString();

            assertThat(codeString).isEqualTo("variable MyVar");
        }

        @Test
        @DisplayName("should convert to MSPASS lowercase format")
        void shouldConvertToMSPASS() {
            VariableExpression var = new VariableExpression("MyVar");

            String mspass = var.toMSPASS();

            assertThat(mspass).isEqualTo("myvar");
        }

        @Test
        @DisplayName("should not use any connector")
        void shouldNotUseConnector() {
            Connector testConnector = new Connector("test", 1, "_");

            boolean isUsed = variable.isUsed(testConnector);

            assertThat(isUsed).isFalse();
        }

        @Test
        @DisplayName("should return itself in variable expressions list")
        void shouldReturnItselfInVariableExpressions() {
            ArrayList<Expression> variables = variable.getVariableExpressions();

            assertThat(variables).hasSize(1);
            assertThat(variables.get(0)).isEqualTo(variable);
        }

        @Test
        @DisplayName("should return empty list for used connectors")
        void shouldReturnEmptyUsedConnectors() {
            ArrayList<Connector> connectors = variable.getUsedConnectors();

            assertThat(connectors).isEmpty();
        }
    }

    @Nested
    @DisplayName("InstanceSet Integration")
    class InstanceSetIntegration {

        @Test
        @DisplayName("should work with multiple variables in same InstanceSet")
        void shouldWorkWithMultipleVariables() {
            VariableExpression varX = new VariableExpression("X");
            VariableExpression varY = new VariableExpression("Y");
            ConstantExpression constP = new ConstantExpression("P");
            ConstantExpression constQ = new ConstantExpression("Q");

            InstanceSet result = varX.matchWith(constP, instanceSet);
            result = varY.matchWith(constQ, result);

            assertThat(result).isNotNull();
            assertThat(result.get(varX)).isEqualTo(constP);
            assertThat(result.get(varY)).isEqualTo(constQ);
        }

        @Test
        @DisplayName("should preserve immutability of original InstanceSet on plus")
        void shouldPreserveImmutabilityOnPlus() {
            ConstantExpression constant = new ConstantExpression("P");

            InstanceSet newSet = variable.matchWith(constant, instanceSet);

            // Original set should remain unchanged
            assertThat(instanceSet.get(variable)).isNull();
            // New set should have the binding
            assertThat(newSet.get(variable)).isEqualTo(constant);
        }
    }
}
