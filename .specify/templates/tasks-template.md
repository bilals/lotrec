# Task Tracking: [FEAT-XXX] Feature Title

> Task checklist for implementing the approved specification.

## Metadata

| Field | Value |
|-------|-------|
| **Tasks ID** | TASKS-XXX |
| **Spec Reference** | [FEAT-XXX](./spec.md) |
| **Plan Reference** | [PLAN-XXX](./plan.md) |
| **Author** | [Name] |
| **Created** | YYYY-MM-DD |
| **Last Updated** | YYYY-MM-DD |

---

## Progress Summary

| Phase | Total | Done | Remaining | Status |
|-------|-------|------|-----------|--------|
| Setup | 0 | 0 | 0 | Not Started |
| Red (Tests) | 0 | 0 | 0 | Not Started |
| Green (Impl) | 0 | 0 | 0 | Not Started |
| Refactor | 0 | 0 | 0 | Not Started |
| Integration | 0 | 0 | 0 | Not Started |
| Finalization | 0 | 0 | 0 | Not Started |
| **TOTAL** | **0** | **0** | **0** | **0%** |

---

## Phase 0: Setup Tasks

> Prerequisites and environment setup.

- [ ] **SETUP-1**: Verify specification is approved
- [ ] **SETUP-2**: Create feature branch (if using git flow)
  ```bash
  git checkout -b feature/FEAT-XXX-feature-name
  ```
- [ ] **SETUP-3**: Verify build environment
  ```bash
  ant clean compile test
  ```
- [ ] **SETUP-4**: Create spec directory structure
  ```bash
  mkdir -p specs/FEAT-XXX-feature-name
  ```
- [ ] **SETUP-5**: Copy specification to working directory

---

## Phase 1: Red Phase (Write Failing Tests)

> Write tests first. All tests must fail before implementation.

### Test File Setup

- [ ] **RED-1**: Create test file
  - File: `test/lotrec/[package]/[FeatureName]Test.java`
  - Package declaration and imports

### Test Cases

- [ ] **RED-2**: Write test: `should[Behavior1]_when[Condition1]()`
  - Tests: [FR-1 from spec]
  - Expected failure: [reason]

- [ ] **RED-3**: Write test: `should[Behavior2]_when[Condition2]()`
  - Tests: [FR-2 from spec]
  - Expected failure: [reason]

- [ ] **RED-4**: Write test: `should[Behavior3]_when[Condition3]()`
  - Tests: [FR-3 from spec]
  - Expected failure: [reason]

### Red Checkpoint

- [ ] **RED-CHK-1**: Run `ant compile` - tests compile
- [ ] **RED-CHK-2**: Run `ant test` - new tests FAIL
- [ ] **RED-CHK-3**: Verify tests fail for correct reasons (not compile errors)

---

## Phase 2: Green Phase (Implementation)

> Write minimum code to pass tests. No extra features.

### Core Implementation

- [ ] **GREEN-1**: Create main class
  - File: `src/lotrec/[package]/[ClassName].java`
  - Minimum implementation for first test

- [ ] **GREEN-2**: Implement [Feature Component 1]
  - Make test RED-2 pass
  - Run `ant test` to verify

- [ ] **GREEN-3**: Implement [Feature Component 2]
  - Make test RED-3 pass
  - Run `ant test` to verify

- [ ] **GREEN-4**: Implement [Feature Component 3]
  - Make test RED-4 pass
  - Run `ant test` to verify

### Registration (if applicable)

- [ ] **GREEN-5**: Register in CLASSES_KEYWORDS
  - Location: [registration file]
  - Keyword: `[keyword]`

### Green Checkpoint

- [ ] **GREEN-CHK-1**: Run `ant test` - ALL new tests PASS
- [ ] **GREEN-CHK-2**: Run `ant test` - ALL existing tests still PASS
- [ ] **GREEN-CHK-3**: No regressions introduced

---

## Phase 3: Refactor Phase

> Clean up code while keeping tests green.

### Code Quality

- [ ] **REFACTOR-1**: Review for code smells
  - Long methods
  - Duplicate code
  - Poor naming

- [ ] **REFACTOR-2**: Extract methods (if needed)
  - Run `ant test` after each extraction

- [ ] **REFACTOR-3**: Improve naming (if needed)
  - Run `ant test` after each rename

- [ ] **REFACTOR-4**: Remove duplication (if needed)
  - Run `ant test` after each change

### Documentation

- [ ] **REFACTOR-5**: Add Javadoc to public APIs
- [ ] **REFACTOR-6**: Add inline comments for complex logic

### Refactor Checkpoint

- [ ] **REFACTOR-CHK-1**: Run `ant test` - ALL tests still PASS
- [ ] **REFACTOR-CHK-2**: Code is cleaner than before
- [ ] **REFACTOR-CHK-3**: No functionality changes during refactoring

---

## Phase 4: Integration Phase

> Verify in full application context.

### Build Verification

- [ ] **INT-1**: Clean build
  ```bash
  ant clean
  ```
- [ ] **INT-2**: Full compile
  ```bash
  ant compile
  ```
- [ ] **INT-3**: Run all tests
  ```bash
  ant test
  ```
- [ ] **INT-4**: Build JAR
  ```bash
  ant jar
  ```

### Manual Verification

- [ ] **INT-5**: Launch application
  ```bash
  ant run
  ```
- [ ] **INT-6**: Test feature manually
  - [Step 1]
  - [Step 2]
  - [Step 3]

### Logic Integration

- [ ] **INT-7**: Test with K.xml logic
- [ ] **INT-8**: Test with [other relevant logic].xml
- [ ] **INT-9**: Verify no regressions in existing functionality

### Integration Checkpoint

- [ ] **INT-CHK-1**: `ant clean compile test jar` succeeds
- [ ] **INT-CHK-2**: Manual testing complete
- [ ] **INT-CHK-3**: Logic integration verified

---

## Phase 5: Finalization

> Complete documentation and prepare for merge.

### Documentation

- [ ] **FINAL-1**: Update specification status to "Implemented"
- [ ] **FINAL-2**: Update plan status to "Completed"
- [ ] **FINAL-3**: Document any deviations from plan

### Code Review Prep

- [ ] **FINAL-4**: Self-review all changes
- [ ] **FINAL-5**: Verify coding conventions followed
- [ ] **FINAL-6**: Check for debug code/comments to remove

### Commit

- [ ] **FINAL-7**: Stage changes
  ```bash
  git add -A
  ```
- [ ] **FINAL-8**: Commit with meaningful message
  ```bash
  git commit -m "FEAT-XXX: [Feature description]"
  ```
- [ ] **FINAL-9**: Push (if applicable)
  ```bash
  git push origin feature/FEAT-XXX-feature-name
  ```

### Finalization Checkpoint

- [ ] **FINAL-CHK-1**: All tasks complete
- [ ] **FINAL-CHK-2**: All checkpoints passed
- [ ] **FINAL-CHK-3**: Ready for merge/review

---

## Blocked Tasks

> Tasks that cannot proceed and their blockers.

| Task ID | Blocked By | Description | Resolution |
|---------|------------|-------------|------------|
| [ID] | [Blocker] | [Why blocked] | [How to resolve] |

---

## Daily Notes

### YYYY-MM-DD
- [What was accomplished]
- [Issues encountered]
- [Next steps]

### YYYY-MM-DD
- [What was accomplished]
- [Issues encountered]
- [Next steps]

---

## Time Log (Optional)

| Date | Phase | Tasks | Duration | Notes |
|------|-------|-------|----------|-------|
| YYYY-MM-DD | Setup | SETUP-1, SETUP-2 | -- | [Notes] |

---

*Template version: 1.0 | Based on LoTREC Constitution TDD Discipline*
