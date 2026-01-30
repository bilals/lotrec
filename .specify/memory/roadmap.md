# LoTREC Modernization Roadmap

## Project Context
- **Current State**: NetBeans Ant project, Java 1.8, ~771 Java files, ~49K LOC
- **GUI**: Pure Swing/AWT
- **Cytoscape**: v2.x embedded as source code (not dependency)
- **Testing**: Minimal (1 sample JUnit test)
- **User Goals**: AI-assisted development, Modern tech stack, End-user experience
- **Timeline**: Incremental over months
- **Constraint**: Core theorem proving must be preserved

---

## Recommended Enhancement Order

### Phase 1: Foundation (Enables all other phases)

#### 1.1 Migrate Build System: Ant → Gradle ✅ COMPLETE
**Status**: COMPLETE (2025-01-30)

**Completed**:
- Gradle Kotlin DSL build configuration (`build.gradle.kts`)
- Gradle wrapper for reproducible builds
- JUnit 5 + AssertJ testing framework
- JaCoCo coverage reporting
- `publishDist` task for distribution (creates `LoTREC-{version}.zip` at project root)
- Removed NetBeans/Ant files (`nbproject/`, `build.xml`)
- Removed obsolete test JARs (`junit-4.12.jar`, `hamcrest-core-1.3.jar`)
- Removed old distribution files (`src/lotrec/dist/`)

**See**: `.specify/memory/migration-history.md` for full details

---

#### 1.2 Establish Test Infrastructure ✅ COMPLETE
**Status**: COMPLETE (2025-01-30)

**Priority**: CRITICAL - Enables safe refactoring
**Rationale**: Cannot safely modernize without regression tests

**Plan Options**:
| Option | Coverage | Effort |
|--------|----------|--------|
| **A. Unit tests only** | Core logic classes | Medium |
| **B. Unit + Integration** | Logic + GUI flows | High |
| **C. Characterization tests** | Capture current behavior | Medium |

**Recommended**: Option C first, then A incrementally

**Steps**:
1. Add JUnit 5 + AssertJ dependencies
2. Create characterization tests for formula parsing
3. Create characterization tests for rule engine
4. Create characterization tests for tableau construction
5. Add test coverage reporting (JaCoCo)

**Key Files to Test First**:
- `lotrec/parser/OldiesTokenizer.java`
- `lotrec/dataStructure/tableau/Rule.java`
- `lotrec/engine/Engine.java`
- `gi/transformers/PriorityInfixToPrefix.java`

---

### Phase 2: Code Quality (Safe with tests in place) 

#### 2.1 Resolve Compilation Warnings
**Priority**: HIGH
**Rationale**: Clean foundation for further changes

**Steps**:
1. Enable `-Xlint:all` in Gradle
2. Fix raw type warnings (add generics)
3. Fix deprecation warnings
4. Fix unused variable warnings
5. Fix unchecked cast warnings

**Estimated Issues**: ~500-1000 warnings (based on project size)

---

#### 2.2 Clean Code & Static Analysis
**Priority**: MEDIUM
**Rationale**: Maintainability

**Plan**:
1. Add SpotBugs and Checkstyle to Gradle
2. Fix critical/blocker issues first
3. Incrementally address major issues
4. Establish coding standards for new code

**Focus Areas**:
- Remove dead code
- Extract long methods
- Eliminate code duplication
- Improve naming

---

### Phase 3: UI Modernization

#### 3.1 JavaFX Migration
**Priority**: MEDIUM-HIGH
**Rationale**: Modern UI, better maintainability

**Plan Options**:
| Option | Approach | Risk |
|--------|----------|------|
| **A. Big Bang** | Rewrite all UI at once | High |
| **B. Incremental** | Migrate panel by panel | Low |
| **C. Hybrid** | Embed JavaFX in Swing first | Medium |

**Recommended**: Option C then B

**Steps**:
1. Add JavaFX dependencies to Gradle
2. Create JFXPanel wrapper in existing Swing app
3. Migrate dialogs first (simpler)
4. Migrate LogicsPanel
5. Migrate TableauxPanel (complex - last)
6. Remove Swing entirely

**Key Challenges**:
- `RulesTabPanel.java` is ~36K tokens - very complex
- Cytoscape integration is Swing-based

---

#### 3.2 Simpler Rule Editor with DSL
**Priority**: MEDIUM
**Rationale**: Better UX, requested feature

**Plan**:
1. Design text-based DSL for rule definition
2. Implement syntax highlighting (CodeArea/RichTextFX)
3. Add auto-completion for connectors/actions/conditions
4. Provide real-time validation
5. Keep existing tree-based editor as fallback

**DSL Example**:
```
rule DiamondRule
  when
    hasElement ?n (pos ?A)
    notMarked ?n (pos ?A)
  then
    addSuccessor ?n ?m R
    addElement ?m ?A
    mark ?n (pos ?A)
end
```

---

#### 3.3 Infix/Prefix Notation Toggle
**Priority**: LOW-MEDIUM
**Rationale**: Existing transformer code can be leveraged

**Plan**:
1. Extend `TransformerGUI` to support logic-specific connectors
2. Add toggle button in formula input areas
3. Store user preference
4. Auto-detect notation on paste

**Key File**: `gi/transformers/TransformerGUI.java` (already has core logic)

---

### Phase 4: Graph Visualization (Highest Risk)

#### 4.1 Cytoscape Migration/Replacement
**Priority**: HIGH but DEFER until Phase 1-2 complete
**Rationale**: Core feature, but risky

**Current Features to Preserve**:
- yFiles-powered hierarchical layout
- Auto-adapting node width to formula text
- Pan and zoom
- Node selection and manipulation
- Export to PDF/PNG/PS

**Plan Options**:
| Option | Library | Pros | Cons |
|--------|---------|------|------|
| **A. Cytoscape.js** | Web-based | Modern, well-maintained | Requires embedding browser |
| **B. JGraphX** | Pure Java | Similar API to old Cytoscape | Less active |
| **C. JUNG** | Pure Java | Academic focus | Limited layouts |
| **D. GraphStream** | Java | Real-time, good layouts | Different paradigm |
| **E. Upgrade Cytoscape** | Cytoscape 3.x | Familiar | Major API changes |

**Recommended**: Prototype B and A, compare results

**Steps**:
1. Create isolated prototype module
2. Implement basic tableau visualization with Option B (JGraphX)
3. Implement same with Option A (Cytoscape.js via JxBrowser)
4. Compare: features, performance, code complexity
5. Decide and migrate incrementally

---

#### 4.2 Rule Visualization (Graph Rewriting Pattern)
**Priority**: LOW (new feature)
**Rationale**: Nice-to-have visualization

**Plan**:
1. Design visual representation (LHS → RHS pattern)
2. Reuse graph library from 4.1
3. Generate visualization from Rule object
4. Integrate into rule editor

---

## Dependency Graph

```
Phase 1.1 (Gradle) ──┬──→ Phase 1.2 (Tests) ──→ Phase 2.1 (Warnings)
                     │                              │
                     │                              ↓
                     │                         Phase 2.2 (Clean Code)
                     │                              │
                     ↓                              ↓
              Phase 3.1 (JavaFX) ←─────────────────┘
                     │
                     ├──→ Phase 3.2 (Rule DSL)
                     ├──→ Phase 3.3 (Infix/Prefix)
                     │
                     ↓
              Phase 4.1 (Graph Library) ──→ Phase 4.2 (Rule Viz)
```

---

## Verification Strategy

After each phase:
1. Run all characterization tests
2. Manual smoke test: Load logic → Enter formula → Run tableau → Verify visualization
3. Compare output with baseline (screenshots, exported files)

---

## Files to Modify (Summary)

**Phase 1** (1.1 COMPLETE):
- ✅ NEW: `build.gradle.kts`, `settings.gradle.kts`, `gradle/`
- ✅ DELETE: `nbproject/`, `build.xml`
- ✅ DELETE: `lib/junit-4.12.jar`, `lib/hamcrest-core-1.3.jar`
- ✅ DELETE: `src/lotrec/dist/`
- IN PROGRESS: `test/**/*Test.java` (characterization tests)

**Phase 2**:
- MODIFY: Most Java files for warnings/cleanup

**Phase 3**:
- NEW: `src/main/java/lotrec/gui/fx/**/*.java`
- MODIFY: `lotrec/gui/*.java` → eventual deletion
- NEW: `src/main/java/lotrec/dsl/**/*.java`

**Phase 4**:
- NEW: `src/main/java/lotrec/viz/**/*.java`
- MODIFY/DELETE: `src/cytoscape/**/*.java`

---

## Next Steps

1. ✅ ~~Approve this roadmap~~
2. ✅ ~~Phase 1.1: Create Gradle build configuration~~
3. ✅ ~~Validate build produces working JAR~~
4. **CURRENT**: Phase 1.2: Create characterization tests for core components
5. Phase 2.1: Enable compiler warnings and fix issues
