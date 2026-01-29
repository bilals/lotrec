package lotrec.dataStructure.expression;

import lotrec.TestFixtures;
import lotrec.dataStructure.Logic;
import lotrec.parser.OldiesTokenizer;
import lotrec.parser.exceptions.ParseException;
import lotrec.util.CommonDuplicator;
import lotrec.util.DuplicateException;
import lotrec.util.Duplicateable;
import lotrec.util.Duplicator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Comprehensive tests for MarkedExpression.
 * Tests cover construction, marking, equality, and duplication behavior.
 */
@DisplayName("MarkedExpression")
class MarkedExpressionTest {

    private Logic logic;
    private OldiesTokenizer tokenizer;
    private Expression simpleExpression;
    private Expression compoundExpression;

    @BeforeEach
    void setUp() throws ParseException {
        logic = TestFixtures.createMinimalLogic();
        tokenizer = TestFixtures.createTokenizer(logic);

        // Create test expressions using tokenizer
        simpleExpression = tokenizer.parseExpression("P");
        compoundExpression = tokenizer.parseExpression("and P Q");
    }

    @Nested
    @DisplayName("Constructor")
    class Constructor {

        @Test
        @DisplayName("should wrap expression")
        void shouldWrapExpression() {
            MarkedExpression marked = new MarkedExpression(simpleExpression);

            assertThat(marked.expression).isSameAs(simpleExpression);
            assertThat(marked.getMarks()).isEmpty();
        }

        @Test
        @DisplayName("should wrap compound expression")
        void shouldWrapCompoundExpression() {
            MarkedExpression marked = new MarkedExpression(compoundExpression);

            assertThat(marked.expression).isSameAs(compoundExpression);
            assertThat(marked.expression.toString()).contains("P");
        }

        @Test
        @DisplayName("should have empty marks on construction")
        void shouldHaveEmptyMarksOnConstruction() {
            MarkedExpression marked = new MarkedExpression(simpleExpression);

            assertThat(marked.getMarks()).isNotNull();
            assertThat(marked.getMarks()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Mark Operations")
    class MarkOperations {

        @Test
        @DisplayName("should add mark")
        void shouldAddMark() {
            MarkedExpression marked = new MarkedExpression(simpleExpression);

            marked.mark("applied");

            assertThat(marked.getMarks()).hasSize(1);
            assertThat(marked.getMarks()).contains("applied");
        }

        @Test
        @DisplayName("should add multiple marks")
        void shouldAddMultipleMarks() {
            MarkedExpression marked = new MarkedExpression(simpleExpression);

            marked.mark("mark1");
            marked.mark("mark2");
            marked.mark("mark3");

            assertThat(marked.getMarks()).hasSize(3);
            assertThat(marked.getMarks()).contains("mark1", "mark2", "mark3");
        }

        @Test
        @DisplayName("should remove mark")
        void shouldRemoveMark() {
            MarkedExpression marked = new MarkedExpression(simpleExpression);
            marked.mark("toRemove");
            marked.mark("toKeep");

            marked.unmark("toRemove");

            assertThat(marked.getMarks()).hasSize(1);
            assertThat(marked.getMarks()).contains("toKeep");
            assertThat(marked.getMarks()).doesNotContain("toRemove");
        }

        @Test
        @DisplayName("should handle removing non-existent mark gracefully")
        void shouldHandleRemovingNonExistentMark() {
            MarkedExpression marked = new MarkedExpression(simpleExpression);
            marked.mark("existing");

            // Removing non-existent mark should not throw
            marked.unmark("nonexistent");

            assertThat(marked.getMarks()).hasSize(1);
            assertThat(marked.getMarks()).contains("existing");
        }

        @Test
        @DisplayName("should check mark presence")
        void shouldCheckMarkPresence() {
            MarkedExpression marked = new MarkedExpression(simpleExpression);
            marked.mark("present");

            assertThat(marked.isMarked("present")).isTrue();
            assertThat(marked.isMarked("absent")).isFalse();
        }

        @Test
        @DisplayName("should support object marks")
        void shouldSupportObjectMarks() {
            MarkedExpression marked = new MarkedExpression(simpleExpression);
            Object objectMark = new Object();

            marked.mark(objectMark);

            assertThat(marked.isMarked(objectMark)).isTrue();
        }
    }

    @Nested
    @DisplayName("getExpression")
    class GetExpression {

        @Test
        @DisplayName("should return wrapped expression")
        void shouldReturnWrappedExpression() {
            MarkedExpression marked = new MarkedExpression(simpleExpression);

            assertThat(marked.expression).isSameAs(simpleExpression);
            assertThat(marked.expression.toString()).isEqualTo("P");
        }

        @Test
        @DisplayName("should preserve expression after marking")
        void shouldPreserveExpressionAfterMarking() {
            MarkedExpression marked = new MarkedExpression(simpleExpression);
            marked.mark("someMark");
            marked.mark("anotherMark");

            assertThat(marked.expression).isSameAs(simpleExpression);
        }
    }

    @Nested
    @DisplayName("Duplication")
    class Duplication {

        @Test
        @DisplayName("should clone with marks")
        void shouldCloneWithMarks() {
            MarkedExpression original = new MarkedExpression(simpleExpression);
            original.mark("mark1");
            original.mark("mark2");

            Duplicator duplicator = new CommonDuplicator();
            Duplicateable duplicated = original.duplicate(duplicator);

            assertThat(duplicated).isInstanceOf(MarkedExpression.class);
            MarkedExpression cloned = (MarkedExpression) duplicated;

            assertThat(cloned.expression).isSameAs(original.expression);
            assertThat(cloned.getMarks()).hasSize(2);
            assertThat(cloned.isMarked("mark1")).isTrue();
            assertThat(cloned.isMarked("mark2")).isTrue();
        }

        @Test
        @DisplayName("should create independent clone")
        void shouldCreateIndependentClone() {
            MarkedExpression original = new MarkedExpression(simpleExpression);
            original.mark("originalMark");

            Duplicator duplicator = new CommonDuplicator();
            MarkedExpression cloned = (MarkedExpression) original.duplicate(duplicator);

            // Modify original after cloning
            original.mark("newMark");

            // Clone should not have the new mark
            assertThat(cloned.isMarked("originalMark")).isTrue();
            assertThat(cloned.isMarked("newMark")).isFalse();
        }

        @Test
        @DisplayName("should register with duplicator")
        void shouldRegisterWithDuplicator() throws DuplicateException {
            MarkedExpression original = new MarkedExpression(simpleExpression);
            CommonDuplicator duplicator = new CommonDuplicator();

            Duplicateable duplicated = original.duplicate(duplicator);

            assertThat(duplicator.hasImage(original)).isTrue();
            assertThat(duplicator.getImage(original)).isSameAs(duplicated);
        }

        @Test
        @DisplayName("copy constructor should copy marks")
        void copyConstructorShouldCopyMarks() {
            MarkedExpression original = new MarkedExpression(simpleExpression);
            original.mark("copyMe");

            MarkedExpression copy = new MarkedExpression(original);

            assertThat(copy.expression).isSameAs(original.expression);
            assertThat(copy.isMarked("copyMe")).isTrue();
        }
    }

    @Nested
    @DisplayName("Equality")
    class Equality {

        @Test
        @DisplayName("should equal with same wrapped expression and marks")
        void shouldEqualWithSameMarks() {
            MarkedExpression marked1 = new MarkedExpression(simpleExpression);
            MarkedExpression marked2 = new MarkedExpression(simpleExpression);
            marked1.mark("sameMark");
            marked2.mark("sameMark");

            // Note: MarkedExpression.equals only compares expressions, not marks
            assertThat(marked1.equals(marked2)).isTrue();
            assertThat(marked2.equals(marked1)).isTrue();
        }

        @Test
        @DisplayName("should equal with same expression regardless of marks")
        void shouldEqualWithSameExpressionRegardlessOfMarks() {
            MarkedExpression marked1 = new MarkedExpression(simpleExpression);
            MarkedExpression marked2 = new MarkedExpression(simpleExpression);
            marked1.mark("mark1");
            marked2.mark("differentMark");

            // MarkedExpression.equals only compares the wrapped expression
            assertThat(marked1.equals(marked2)).isTrue();
        }

        @Test
        @DisplayName("should not equal with different wrapped expression")
        void shouldNotEqualWithDifferentWrappedExpression() throws ParseException {
            Expression exprP = tokenizer.parseExpression("P");
            Expression exprQ = tokenizer.parseExpression("Q");

            MarkedExpression marked1 = new MarkedExpression(exprP);
            MarkedExpression marked2 = new MarkedExpression(exprQ);

            assertThat(marked1.equals(marked2)).isFalse();
        }

        @Test
        @DisplayName("should not equal non-MarkedExpression objects")
        void shouldNotEqualNonMarkedExpression() {
            MarkedExpression marked = new MarkedExpression(simpleExpression);

            assertThat(marked.equals(null)).isFalse();
            assertThat(marked.equals(simpleExpression)).isFalse();
            assertThat(marked.equals("P")).isFalse();
        }

        @Test
        @DisplayName("should have consistent hashCode for equal objects")
        void shouldHaveConsistentHashCode() {
            MarkedExpression marked1 = new MarkedExpression(simpleExpression);
            MarkedExpression marked2 = new MarkedExpression(simpleExpression);

            assertThat(marked1.hashCode()).isEqualTo(marked2.hashCode());
        }
    }

    @Nested
    @DisplayName("toString")
    class ToStringTests {

        @Test
        @DisplayName("should return expression string without marks")
        void shouldReturnExpressionStringWithoutMarks() {
            MarkedExpression marked = new MarkedExpression(simpleExpression);

            assertThat(marked.toString()).isEqualTo("P");
        }

        @Test
        @DisplayName("should include marks in string representation")
        void shouldIncludeMarksInStringRepresentation() {
            MarkedExpression marked = new MarkedExpression(simpleExpression);
            marked.mark("applied");

            String result = marked.toString();

            assertThat(result).contains("P");
            assertThat(result).contains("applied");
        }

        @Test
        @DisplayName("should include multiple marks in string representation")
        void shouldIncludeMultipleMarksInStringRepresentation() {
            MarkedExpression marked = new MarkedExpression(simpleExpression);
            marked.mark("mark1");
            marked.mark("mark2");

            String result = marked.toString();

            assertThat(result).contains("P");
            assertThat(result).contains("mark1");
            assertThat(result).contains("mark2");
        }
    }

    @Nested
    @DisplayName("isUsed")
    class IsUsed {

        @Test
        @DisplayName("should delegate isUsed to wrapped expression")
        void shouldDelegateIsUsedToWrappedExpression() {
            Connector notConnector = logic.getConnector("not");
            MarkedExpression marked = new MarkedExpression(compoundExpression);

            // compoundExpression is "and P Q", so "not" connector is not used
            assertThat(marked.isUsed(notConnector)).isFalse();
        }

        @Test
        @DisplayName("should return true when connector is used in expression")
        void shouldReturnTrueWhenConnectorIsUsed() throws ParseException {
            Connector andConnector = logic.getConnector("and");
            Expression andExpr = tokenizer.parseExpression("and P Q");
            MarkedExpression marked = new MarkedExpression(andExpr);

            assertThat(marked.isUsed(andConnector)).isTrue();
        }
    }

    @Nested
    @DisplayName("getCodeString")
    class GetCodeString {

        @Test
        @DisplayName("should return expression code string")
        void shouldReturnExpressionCodeString() {
            MarkedExpression marked = new MarkedExpression(simpleExpression);

            assertThat(marked.getCodeString()).isEqualTo(simpleExpression.getCodeString());
        }

        @Test
        @DisplayName("should not include marks in code string")
        void shouldNotIncludeMarksInCodeString() {
            MarkedExpression marked = new MarkedExpression(simpleExpression);
            marked.mark("someMark");

            // Code string should be just the expression, without marks
            assertThat(marked.getCodeString()).isEqualTo("P");
        }
    }
}
