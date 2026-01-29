package lotrec.dataStructure.expression;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Comprehensive tests for the Connector class.
 * Connector defines logical operators used to build compound expressions.
 */
@DisplayName("Connector")
class ConnectorTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("should create with name, arity, and output format")
        void shouldCreateWithNameArityOutput() {
            Connector connector = new Connector("and", 2, "_&_");

            assertThat(connector.getName()).isEqualTo("and");
            assertThat(connector.getArity()).isEqualTo(2);
            assertThat(connector.getOutString()).isEqualTo("_&_");
            assertThat(connector.getSpecialCharacter()).isEqualTo(Connector.DEFAULT_SPECIAL_CHARACTER);
        }

        @Test
        @DisplayName("should create default connector with auto-generated name")
        void shouldCreateDefaultConnector() {
            Connector connector1 = new Connector();
            Connector connector2 = new Connector();

            assertThat(connector1.getName()).startsWith("undefined connector");
            assertThat(connector2.getName()).startsWith("undefined connector");
            assertThat(connector1.getName()).isNotEqualTo(connector2.getName());
            assertThat(connector1.getArity()).isEqualTo(0);
            assertThat(connector1.getOutString()).isEqualTo("undefined string !");
        }
    }

    @Nested
    @DisplayName("Priority Assignment by Arity")
    class PriorityAssignmentTests {

        @Test
        @DisplayName("should assign priority by arity - unary gets priority 2")
        void shouldAssignPriorityForUnary() {
            Connector unary = new Connector("not", 1, "~_");

            assertThat(unary.getPriority()).isEqualTo(Connector.DEFAULT_UNARY_PRIORITY);
            assertThat(unary.getPriority()).isEqualTo(2);
        }

        @Test
        @DisplayName("should assign priority by arity - binary gets priority 1")
        void shouldAssignPriorityForBinary() {
            Connector binary = new Connector("and", 2, "_&_");

            assertThat(binary.getPriority()).isEqualTo(Connector.DEFAULT_BINARY_PRIORITY);
            assertThat(binary.getPriority()).isEqualTo(1);
        }

        @Test
        @DisplayName("should assign priority by arity - ternary gets priority 0")
        void shouldAssignPriorityForTernary() {
            Connector ternary = new Connector("if_then_else", 3, "if _ then _ else _");

            assertThat(ternary.getPriority()).isEqualTo(Connector.DEFAULT_TERNARY_PRIORITY);
            assertThat(ternary.getPriority()).isEqualTo(0);
        }

        @Test
        @DisplayName("should assign priority by arity - zeroary gets priority 3")
        void shouldAssignPriorityForZeroary() {
            Connector zeroary = new Connector("true", 0, "T");

            assertThat(zeroary.getPriority()).isEqualTo(Connector.DEFAULT_ZEROARY_PRIORITY);
            assertThat(zeroary.getPriority()).isEqualTo(3);
        }

        @Test
        @DisplayName("should assign undefined priority for arity > 3")
        void shouldAssignUndefinedPriorityForHighArity() {
            Connector highArity = new Connector("quad", 4, "_,_,_,_");

            assertThat(highArity.getPriority()).isEqualTo(Connector.DEFAULT_UNDEFINED_PRIORITY);
            assertThat(highArity.getPriority()).isEqualTo(4);
        }
    }

    @Nested
    @DisplayName("Equality Tests")
    class EqualityTests {

        @Test
        @DisplayName("should equal by name only")
        void shouldEqualByNameOnly() {
            Connector connector1 = new Connector("and", 2, "_&_");
            Connector connector2 = new Connector("and", 1, "different output");

            assertThat(connector1.equals(connector2)).isTrue();
        }

        @Test
        @DisplayName("should not equal when names differ")
        void shouldNotEqualWhenNamesDiffer() {
            Connector connector1 = new Connector("and", 2, "_&_");
            Connector connector2 = new Connector("or", 2, "_&_");

            assertThat(connector1.equals(connector2)).isFalse();
        }

        @Test
        @DisplayName("should handle null name comparison - throws NPE when other's name is null")
        void shouldHandleNullName() {
            Connector connector = new Connector("test", 1, "_");
            Connector other = new Connector();
            other.setName(null);

            // equals() calls other.name.equals(name), so NPE when other.name is null
            assertThatThrownBy(() -> connector.equals(other))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("should return false when comparing with non-null name to null name")
        void shouldHandleNullNameOnThis() {
            Connector connector = new Connector();
            connector.setName(null);
            Connector other = new Connector("test", 1, "_");

            // equals() calls other.name.equals(name) where name is null
            // String.equals(null) returns false, doesn't throw
            assertThat(connector.equals(other)).isFalse();
        }
    }

    @Nested
    @DisplayName("Associativity Tests")
    class AssociativityTests {

        @Test
        @DisplayName("should set associativity")
        void shouldSetAssociativity() {
            Connector connector = new Connector("and", 2, "_&_");

            // Default is associative
            assertThat(connector.isAssociative()).isTrue();

            connector.setAssociative(false);
            assertThat(connector.isAssociative()).isFalse();

            connector.setAssociative(true);
            assertThat(connector.isAssociative()).isTrue();
        }

        @Test
        @DisplayName("default constructor should set associative to true")
        void defaultConstructorShouldBeAssociative() {
            Connector connector = new Connector();

            assertThat(connector.isAssociative()).isTrue();
        }
    }

    @Nested
    @DisplayName("Special Character Tests")
    class SpecialCharacterTests {

        @Test
        @DisplayName("should set special character")
        void shouldSetSpecialCharacter() {
            Connector connector = new Connector("test", 1, "#_");

            // Default special character
            assertThat(connector.getSpecialCharacter()).isEqualTo('_');

            connector.setSpecialCharacter('#');
            assertThat(connector.getSpecialCharacter()).isEqualTo('#');
        }

        @Test
        @DisplayName("should use default special character constant")
        void shouldUseDefaultSpecialCharacter() {
            assertThat(Connector.DEFAULT_SPECIAL_CHARACTER).isEqualTo('_');

            Connector connector = new Connector("test", 1, "_");
            assertThat(connector.getSpecialCharacter()).isEqualTo(Connector.DEFAULT_SPECIAL_CHARACTER);
        }
    }

    @Nested
    @DisplayName("Output Format Tests")
    class OutputFormatTests {

        @Test
        @DisplayName("should set output format")
        void shouldSetOutputFormat() {
            Connector connector = new Connector("not", 1, "~_");

            assertThat(connector.getOutString()).isEqualTo("~_");

            connector.setOutString("NOT(_)");
            assertThat(connector.getOutString()).isEqualTo("NOT(_)");
        }

        @Test
        @DisplayName("should support various output format patterns")
        void shouldSupportVariousOutputFormats() {
            Connector prefix = new Connector("not", 1, "~_");
            assertThat(prefix.getOutString()).isEqualTo("~_");

            Connector infix = new Connector("and", 2, "_&_");
            assertThat(infix.getOutString()).isEqualTo("_&_");

            Connector bracket = new Connector("nec", 1, "[]_");
            assertThat(bracket.getOutString()).isEqualTo("[]_");
        }
    }

    @Nested
    @DisplayName("Arity-Specific Connector Tests")
    class AritySpecificTests {

        @Test
        @DisplayName("should create unary connector")
        void shouldCreateUnaryConnector() {
            Connector unary = new Connector("not", 1, "~_");

            assertThat(unary.getArity()).isEqualTo(1);
            assertThat(unary.getPriority()).isEqualTo(Connector.DEFAULT_UNARY_PRIORITY);
        }

        @Test
        @DisplayName("should create binary connector")
        void shouldCreateBinaryConnector() {
            Connector binary = new Connector("imp", 2, "_->_");

            assertThat(binary.getArity()).isEqualTo(2);
            assertThat(binary.getPriority()).isEqualTo(Connector.DEFAULT_BINARY_PRIORITY);
        }

        @Test
        @DisplayName("should create ternary connector")
        void shouldCreateTernaryConnector() {
            Connector ternary = new Connector("conditional", 3, "_ ? _ : _");

            assertThat(ternary.getArity()).isEqualTo(3);
            assertThat(ternary.getPriority()).isEqualTo(Connector.DEFAULT_TERNARY_PRIORITY);
        }

        @Test
        @DisplayName("should allow modifying arity after creation")
        void shouldAllowModifyingArity() {
            Connector connector = new Connector("test", 1, "_");

            connector.setArity(2);
            assertThat(connector.getArity()).isEqualTo(2);
            // Note: priority is NOT automatically updated when arity changes
            assertThat(connector.getPriority()).isEqualTo(Connector.DEFAULT_UNARY_PRIORITY);
        }
    }

    @Nested
    @DisplayName("Clone and Copy Tests")
    class CloneAndCopyTests {

        @Test
        @DisplayName("should clone connector by creating equivalent instance")
        void shouldCloneConnector() {
            Connector original = new Connector("and", 2, "_&_");
            original.setAssociative(false);
            original.setPriority(5);
            original.setSpecialCharacter('#');
            original.setComment("test comment");

            // Create a clone manually
            Connector clone = new Connector(original.getName(), original.getArity(), original.getOutString());
            clone.setAssociative(original.isAssociative());
            clone.setPriority(original.getPriority());
            clone.setSpecialCharacter(original.getSpecialCharacter());
            clone.setComment(original.getComment());

            assertThat(clone.getName()).isEqualTo(original.getName());
            assertThat(clone.getArity()).isEqualTo(original.getArity());
            assertThat(clone.getOutString()).isEqualTo(original.getOutString());
            assertThat(clone.isAssociative()).isEqualTo(original.isAssociative());
            assertThat(clone.getPriority()).isEqualTo(original.getPriority());
            assertThat(clone.getSpecialCharacter()).isEqualTo(original.getSpecialCharacter());
            assertThat(clone.getComment()).isEqualTo(original.getComment());
            assertThat(clone.equals(original)).isTrue();
        }
    }

    @Nested
    @DisplayName("Priority Comparison Tests")
    class PriorityComparisonTests {

        @Test
        @DisplayName("should compare by priority")
        void shouldCompareByPriority() {
            Connector ternary = new Connector("conditional", 3, "_ ? _ : _"); // priority 0
            Connector binary = new Connector("and", 2, "_&_");                // priority 1
            Connector unary = new Connector("not", 1, "~_");                  // priority 2
            Connector zeroary = new Connector("true", 0, "T");                // priority 3

            assertThat(ternary.getPriority()).isLessThan(binary.getPriority());
            assertThat(binary.getPriority()).isLessThan(unary.getPriority());
            assertThat(unary.getPriority()).isLessThan(zeroary.getPriority());

            // Lower priority number means higher precedence in expression parsing
            assertThat(ternary.getPriority()).isEqualTo(0);
            assertThat(binary.getPriority()).isEqualTo(1);
            assertThat(unary.getPriority()).isEqualTo(2);
            assertThat(zeroary.getPriority()).isEqualTo(3);
        }

        @Test
        @DisplayName("should allow manual priority override")
        void shouldAllowManualPriorityOverride() {
            Connector connector = new Connector("and", 2, "_&_");
            assertThat(connector.getPriority()).isEqualTo(1);

            connector.setPriority(10);
            assertThat(connector.getPriority()).isEqualTo(10);
        }
    }

    @Nested
    @DisplayName("Comment Tests")
    class CommentTests {

        @Test
        @DisplayName("should set and get comment")
        void shouldSetAndGetComment() {
            Connector connector = new Connector("and", 2, "_&_");

            assertThat(connector.getComment()).isNull();

            connector.setComment("Logical conjunction");
            assertThat(connector.getComment()).isEqualTo("Logical conjunction");
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("should include all properties in toString")
        void shouldIncludeAllPropertiesInToString() {
            Connector connector = new Connector("and", 2, "_&_");
            connector.setComment("test comment");

            String result = connector.toString();

            assertThat(result).contains("name:and");
            assertThat(result).contains("arity:2");
            assertThat(result).contains("output format:_&_");
            assertThat(result).contains("priority:1");
            assertThat(result).contains("associative:true");
            assertThat(result).contains("test comment");
        }
    }
}
