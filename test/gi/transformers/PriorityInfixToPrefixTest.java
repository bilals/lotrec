package gi.transformers;

import gi.transformers.Connector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.*;

/**
 * Characterization tests for PriorityInfixToPrefix transformer.
 * These tests capture the current behavior of infix to prefix notation conversion.
 */
@DisplayName("PriorityInfixToPrefix")
class PriorityInfixToPrefixTest {

    private PriorityInfixToPrefix transformer;

    @BeforeEach
    void setUp() throws Exception {
        ArrayList<Connector> connectors = new ArrayList<>();
        // Standard modal logic connectors with priorities
        connectors.add(new Connector("not", 1, "~_", 2));      // unary, high priority
        connectors.add(new Connector("nec", 1, "[]_", 2));     // necessity
        connectors.add(new Connector("pos", 1, "<>_", 2));     // possibility
        connectors.add(new Connector("and", 2, "_^_", 1));     // conjunction
        connectors.add(new Connector("or", 2, "_v_", 1));      // disjunction
        connectors.add(new Connector("imp", 2, "_->_", 0));    // implication, low priority

        transformer = new PriorityInfixToPrefix(connectors);
        transformer.compile();
    }

    @Nested
    @DisplayName("Simple Transformations")
    class SimpleTransformations {

        @Test
        @DisplayName("transforms single propositional variable")
        void transformsSingleVariable() throws Exception {
            String result = transformer.transform("P");

            assertThat(result).isEqualTo("P");
        }

        @Test
        @DisplayName("transforms lowercase propositional variable to uppercase")
        void transformsToUpperCase() throws Exception {
            String result = transformer.transform("p");

            assertThat(result).isEqualTo("P");
        }

        @Test
        @DisplayName("transforms parenthesized variable")
        void transformsParenthesizedVariable() throws Exception {
            String result = transformer.transform("(P)");

            assertThat(result).isEqualTo("P");
        }
    }

    @Nested
    @DisplayName("Unary Connectors")
    class UnaryConnectors {

        @Test
        @DisplayName("transforms negation")
        void transformsNegation() throws Exception {
            String result = transformer.transform("~P");

            assertThat(result).isEqualTo("(not P)");
        }

        @Test
        @DisplayName("transforms necessity (box)")
        void transformsNecessity() throws Exception {
            String result = transformer.transform("[]P");

            assertThat(result).isEqualTo("(nec P)");
        }

        @Test
        @DisplayName("transforms possibility (diamond)")
        void transformsPossibility() throws Exception {
            String result = transformer.transform("<>P");

            assertThat(result).isEqualTo("(pos P)");
        }

        @Test
        @DisplayName("transforms nested unary connectors")
        void transformsNestedUnary() throws Exception {
            String result = transformer.transform("~[]P");

            assertThat(result).isEqualTo("(not (nec P))");
        }

        @Test
        @DisplayName("transforms double negation")
        void transformsDoubleNegation() throws Exception {
            String result = transformer.transform("~~P");

            assertThat(result).isEqualTo("(not (not P))");
        }
    }

    @Nested
    @DisplayName("Binary Connectors")
    class BinaryConnectors {

        @Test
        @DisplayName("transforms conjunction")
        void transformsConjunction() throws Exception {
            String result = transformer.transform("P^Q");

            assertThat(result).isEqualTo("(and P Q)");
        }

        @Test
        @DisplayName("transforms disjunction with spaces")
        void transformsDisjunction() throws Exception {
            // Note: "PvQ" without spaces is parsed as single constant "Pvq"
            // because "v" is alphanumeric and part of the identifier
            String result = transformer.transform("P v Q");

            assertThat(result).isEqualTo("(or P Q)");
        }

        @Test
        @DisplayName("transforms implication")
        void transformsImplication() throws Exception {
            String result = transformer.transform("P->Q");

            assertThat(result).isEqualTo("(imp P Q)");
        }
    }

    @Nested
    @DisplayName("Operator Precedence")
    class OperatorPrecedence {

        @Test
        @DisplayName("implication has lower precedence than conjunction")
        void implicationLowerThanConjunction() throws Exception {
            // P ^ Q -> R should parse as (P ^ Q) -> R
            String result = transformer.transform("P^Q->R");

            assertThat(result).isEqualTo("(imp (and P Q) R)");
        }

        @Test
        @DisplayName("unary has higher precedence than binary")
        void unaryHigherThanBinary() throws Exception {
            // ~P ^ Q should parse as (~P) ^ Q
            String result = transformer.transform("~P^Q");

            assertThat(result).isEqualTo("(and (not P) Q)");
        }

        @Test
        @DisplayName("parentheses override precedence")
        void parenthesesOverridePrecedence() throws Exception {
            // P ^ (Q -> R)
            String result = transformer.transform("P^(Q->R)");

            assertThat(result).isEqualTo("(and P (imp Q R))");
        }
    }

    @Nested
    @DisplayName("Complex Expressions")
    class ComplexExpressions {

        @Test
        @DisplayName("transforms modal formula with implication")
        void transformsModalWithImplication() throws Exception {
            String result = transformer.transform("[]P->Q");

            assertThat(result).isEqualTo("(imp (nec P) Q)");
        }

        @Test
        @DisplayName("transforms complex nested modal formula")
        void transformsComplexModal() throws Exception {
            String result = transformer.transform("[](P->Q)");

            assertThat(result).isEqualTo("(nec (imp P Q))");
        }

        @Test
        @DisplayName("transforms formula with multiple operators")
        void transformsMultipleOperators() throws Exception {
            String result = transformer.transform("P^Q->~R");

            assertThat(result).isEqualTo("(imp (and P Q) (not R))");
        }

        @Test
        @DisplayName("transforms deeply nested formula")
        void transformsDeeplyNested() throws Exception {
            String result = transformer.transform("[](<>P->Q)");

            assertThat(result).isEqualTo("(nec (imp (pos P) Q))");
        }
    }

    @Nested
    @DisplayName("Whitespace Handling")
    class WhitespaceHandling {

        @Test
        @DisplayName("handles spaces around operators")
        void handlesSpacesAroundOperators() throws Exception {
            String result = transformer.transform("P -> Q");

            assertThat(result).isEqualTo("(imp P Q)");
        }

        @Test
        @DisplayName("handles spaces around parentheses")
        void handlesSpacesAroundParentheses() throws Exception {
            String result = transformer.transform("( P -> Q )");

            assertThat(result).isEqualTo("(imp P Q)");
        }
    }
}
