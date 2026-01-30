# Predefined Logics

LoTREC includes 38 predefined logic definitions ready for use. This page provides an overview of the available logics.

## Accessing Predefined Logics

1. Launch LoTREC
2. In the Task Pane, click **"Others..."** under "Open Predefined Logic"
3. Or go to **Logic → Open Predefined Logic**

![Predefined Logics Dialog](images/predefined-logics.png)

## Logic Categories

### Classical Logic

| Logic | Description |
|-------|-------------|
| **Classical-Propositional-Logic** | Standard propositional logic with and, or, not, implication |

### Basic Modal Logics

| Logic | Description |
|-------|-------------|
| **Monomodal-K** | Basic modal logic K with one accessibility relation |
| **Multimodal-Kn** | Modal logic K with multiple accessibility relations |

### Modal Logics with Frame Conditions

| Logic | Axioms | Frame Property |
|-------|--------|----------------|
| **KT-explicit-edges** | K + T | Reflexive frames |
| **KT-implicit-edges** | K + T | Reflexive (implicit) |
| **KB-explicit-edges** | K + B | Symmetric frames |
| **KB-implicit-edges** | K + B | Symmetric (implicit) |
| **K4** | K + 4 | Transitive frames |
| **K5** | K + 5 | Euclidean frames |
| **KD** | K + D | Serial frames |
| **KD45** | K + D + 4 + 5 | Serial, transitive, Euclidean |
| **Kalt1** | K + alt1 | Functional frames |
| **S4** | K + T + 4 | Reflexive, transitive |
| **S5** | K + T + 4 + 5 | Equivalence relations |

### Description Logics

| Logic | Description |
|-------|-------------|
| **K2-with-Inclusion** | Modal K with inclusion axioms |
| **ALC** | Attributive concept Language with Complements |

### Specialized Logics

| Logic | Description |
|-------|-------------|
| **Model-Checking** | For model checking rather than satisfiability |
| **Intuitionistic** | Intuitionistic propositional logic |
| **Provability** | Provability logic (GL) |
| **Temporal** | Basic temporal logic |

## Logic File Locations

All predefined logic files are located in:
```
src/lotrec/logics/
```

You can study these XML files to understand how logics are defined and use them as templates for your own logics.

## Modal Logic Reference

### Common Axioms

| Name | Axiom | Meaning |
|------|-------|---------|
| **K** | □(P → Q) → (□P → □Q) | Distribution axiom |
| **T** | □P → P | Reflexivity |
| **4** | □P → □□P | Transitivity |
| **5** | ◇P → □◇P | Euclidean property |
| **B** | P → □◇P | Symmetry |
| **D** | □P → ◇P | Seriality |

### Frame Correspondences

| Axiom | Frame Property |
|-------|----------------|
| T | ∀w: wRw (reflexive) |
| 4 | ∀w,v,u: (wRv ∧ vRu) → wRu (transitive) |
| 5 | ∀w,v,u: (wRv ∧ wRu) → vRu (Euclidean) |
| B | ∀w,v: wRv → vRw (symmetric) |
| D | ∀w∃v: wRv (serial) |

### Named Systems

| System | Axioms | Also Known As |
|--------|--------|---------------|
| K | K | Basic modal logic |
| T | K + T | Reflexive modal logic |
| K4 | K + 4 | Transitive modal logic |
| S4 | K + T + 4 | Reflexive-transitive |
| S5 | K + T + 5 | Equivalence-based |
| KD45 | K + D + 4 + 5 | Doxastic logic (belief) |

## Using Predefined Logics

### Quick Start

1. Open **Monomodal-K** for basic modal logic experiments
2. Use the **Predefined Formulas** tab to test sample formulas
3. Click **Build Premodels** to see the tableau proof

### Testing Validity

To check if a formula φ is valid:
1. Enter `not φ` (the negation)
2. Build premodels
3. If all premodels are closed → φ is valid
4. If any premodel is open → φ is not valid

### Comparing Logics

Test the same formula in different logics to see how frame conditions affect satisfiability:

| Formula | K | KT | S4 | S5 |
|---------|---|----|----|-----|
| `□P → P` | SAT | VALID | VALID | VALID |
| `□P → □□P` | SAT | SAT | VALID | VALID |
| `◇P → □◇P` | SAT | SAT | SAT | VALID |

## Creating Variations

To create a variation of a predefined logic:

1. Open the predefined logic
2. **Logic → Save Logic As** with a new name
3. Modify connectors, rules, or strategies
4. Save your changes

## Further Reading

- [Defining Logics](defining-logics.md) - Create your own logics
- [User Guide](user-guide.md) - Complete interface reference
- [Getting Started](getting-started.md) - Installation and first proof
