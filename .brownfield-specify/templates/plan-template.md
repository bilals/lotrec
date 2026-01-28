# Implementation Plan: [FEAT-XXX] Feature Title

> Step-by-step TDD implementation plan for the approved specification.

## Metadata

| Field | Value |
|-------|-------|
| **Plan ID** | PLAN-XXX |
| **Spec Reference** | [FEAT-XXX](../../../specs/FEAT-XXX-feature-name/spec.md) |
| **Author** | [Name] |
| **Created** | YYYY-MM-DD |
| **Status** | Draft / In Progress / Completed |

---

## Prerequisites Checklist

> Verify before starting implementation.

- [ ] Specification approved (Status: Approved)
- [ ] Tech stack constraints verified
- [ ] Architecture constraints verified
- [ ] Existing code to reuse identified
- [ ] Test environment ready (`ant test` passes)
- [ ] Development branch created (if applicable)

---

## Phase 1: Test Setup (RED)

> Write failing tests first. Tests must fail for the right reason.

### 1.1 Create Test File Structure

```
test/
└── lotrec/
    └── [package]/
        └── [FeatureName]Test.java
```

### 1.2 Test Cases to Implement

| Order | Test Method | Tests For | Expected Failure Reason |
|-------|-------------|-----------|------------------------|
| 1 | `should[Behavior1]_when[Condition1]()` | [Requirement] | Class/method doesn't exist |
| 2 | `should[Behavior2]_when[Condition2]()` | [Requirement] | Method returns wrong value |
| 3 | `should[Behavior3]_when[Condition3]()` | [Requirement] | Exception not thrown |

### 1.3 Test Implementation

```java
package lotrec.[package];

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests for [FeatureName].
 * Spec: FEAT-XXX
 */
public class [FeatureName]Test {

    // Test fixtures
    private [Type] systemUnderTest;

    @Before
    public void setUp() {
        // Initialize test fixtures
    }

    @After
    public void tearDown() {
        // Cleanup if needed
    }

    // --- Test Case 1 ---
    @Test
    public void should[Behavior1]_when[Condition1]() {
        // Given
        [preconditions]

        // When
        [action]

        // Then
        assertThat(actual, is(expected));
    }

    // --- Test Case 2 ---
    @Test
    public void should[Behavior2]_when[Condition2]() {
        // Given
        // When
        // Then
    }

    // --- Test Case 3: Exception Test ---
    @Test(expected = [ExceptionType].class)
    public void should[ThrowException]_when[InvalidCondition]() {
        // Given
        // When - this should throw
        [action that throws]
    }
}
```

### 1.4 RED Checkpoint

```bash
# Run tests - they MUST fail
ant test

# Expected: Tests fail because implementation doesn't exist
# NOT acceptable: Compile errors (fix imports, stubs first)
```

- [ ] All new tests compile
- [ ] All new tests fail (RED)
- [ ] Tests fail for the right reasons (not compile errors)

---

## Phase 2: Implementation (GREEN)

> Write minimum code to make tests pass. No extra features.

### 2.1 Files to Create/Modify

| Order | File | Action | Purpose |
|-------|------|--------|---------|
| 1 | `src/lotrec/[package]/[ClassName].java` | Create | [Main implementation] |
| 2 | `src/lotrec/[package]/[HelperClass].java` | Create | [Supporting class] |
| 3 | `src/lotrec/[existing]/[File].java` | Modify | [Integration point] |

### 2.2 Implementation Steps

#### Step 2.2.1: [First Implementation Step]

**File**: `src/lotrec/[package]/[ClassName].java`

```java
package lotrec.[package];

/**
 * [Class description]
 *
 * @see FEAT-XXX specification
 */
public class [ClassName] {

    // Implementation here

}
```

**Verification**:
```bash
ant compile  # Must compile
ant test     # Check if test 1 passes
```

#### Step 2.2.2: [Second Implementation Step]

[Continue with incremental implementation...]

### 2.3 Registration (if applicable)

```java
// Add to appropriate registration class
CLASSES_KEYWORDS.put("[keyword]", [ClassName].class);
```

### 2.4 GREEN Checkpoint

```bash
# Run all tests
ant test

# Expected: ALL tests pass (GREEN)
```

- [ ] All new tests pass
- [ ] All existing tests still pass
- [ ] No regressions introduced

---

## Phase 3: Refactor

> Clean up while keeping tests green. No new functionality.

### 3.1 Refactoring Candidates

| Item | Current State | Target State | Risk |
|------|--------------|--------------|------|
| [Code smell] | [Description] | [Improvement] | Low/Med/High |

### 3.2 Refactoring Steps

1. [ ] **Extract Method**: [Description]
2. [ ] **Rename**: [Old name] → [New name]
3. [ ] **Simplify**: [Description]
4. [ ] **Remove Duplication**: [Description]

### 3.3 Refactoring Rules

- Run `ant test` after EACH refactoring
- If tests fail, revert immediately
- Commit after each successful refactoring
- No new features during refactoring

### 3.4 REFACTOR Checkpoint

```bash
ant test  # Must still be GREEN
```

- [ ] All tests still pass
- [ ] Code is cleaner/simpler
- [ ] No functionality changes

---

## Phase 4: Integration

> Verify feature works in full application context.

### 4.1 Build Verification

```bash
# Clean build
ant clean

# Full compile
ant compile

# Run all tests
ant test

# Build JAR
ant jar
```

### 4.2 Manual Verification

| Check | Steps | Expected Result | Actual |
|-------|-------|-----------------|--------|
| [Check 1] | [Steps to verify] | [Expected] | [ ] Pass / [ ] Fail |
| [Check 2] | [Steps to verify] | [Expected] | [ ] Pass / [ ] Fail |

### 4.3 Integration with Predefined Logics

Test with existing logic files:
```bash
# Launch application
ant run

# Load and test with:
# - lotrec/resources/K.xml
# - lotrec/resources/[other relevant logic].xml
```

- [ ] Feature works with K logic
- [ ] Feature works with [other logic]
- [ ] No regressions in existing functionality

### 4.4 INTEGRATION Checkpoint

- [ ] `ant clean compile test jar` succeeds
- [ ] Manual verification complete
- [ ] Integration with predefined logics verified

---

## Build Commands Reference

| Command | Purpose | When to Use |
|---------|---------|-------------|
| `ant compile` | Compile sources | After any code change |
| `ant test` | Run JUnit tests | After each TDD phase |
| `ant jar` | Build LoTREC.jar | Before integration testing |
| `ant clean` | Clean build artifacts | Before final verification |
| `ant run` | Launch application | For manual testing |
| `ant` | Default (compile) | Quick compile check |

---

## Rollback Plan

If implementation fails or causes regressions:

### Immediate Rollback
```bash
# Discard all changes (DESTRUCTIVE - use with caution)
git checkout -- .

# Or revert to last good commit
git reset --hard [commit-hash]
```

### Partial Rollback
1. Identify failing component
2. Revert only that file: `git checkout -- path/to/file.java`
3. Re-run tests to verify

### Recovery Steps
1. [ ] Identify root cause of failure
2. [ ] Document what went wrong
3. [ ] Update specification if requirements were wrong
4. [ ] Restart from Phase 1 with corrected approach

---

## Completion Checklist

- [ ] **Phase 1 (RED)**: All tests written and failing
- [ ] **Phase 2 (GREEN)**: All tests passing with minimum code
- [ ] **Phase 3 (REFACTOR)**: Code cleaned up, tests still green
- [ ] **Phase 4 (INTEGRATION)**: Full application verification complete
- [ ] **Documentation**: Code comments and Javadoc added
- [ ] **Tasks Updated**: All task items marked complete

---

## Notes

[Space for implementation notes, decisions made, issues encountered]

---

*Template version: 1.0 | Based on LoTREC Constitution TDD Discipline*
