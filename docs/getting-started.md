# Getting Started with LoTREC

This guide walks you through installing LoTREC and running your first proof.

## Requirements

- **Java 8 or later** (Java 21 recommended)
- Download Java from [Eclipse Adoptium](https://adoptium.net/) or [Oracle](https://www.oracle.com/java/technologies/downloads/)

## Installation

### Option 1: Download Release (Recommended)

1. [**Download the latest release**](https://github.com/bilals/lotrec/releases/latest)
2. Extract the ZIP file to your preferred location
3. Run the application:
   - **Windows:** Double-click `bin/LoTREC.bat`
   - **Linux/macOS:** Run `bin/LoTREC` in terminal

### Option 2: Build from Source

```bash
git clone https://github.com/bilals/lotrec.git
cd lotrec
./gradlew build
./gradlew run
```

## First Launch

When LoTREC starts, you'll see the **Task Pane** offering several options:

![Task Pane](images/task-pane.png)

| Option | Description |
|--------|-------------|
| **Open Predefined Logic** | Choose from 38 built-in logics (K, KT, S4, S5, etc.) |
| **Open Existing File** | Load a custom logic XML file |
| **Create Your Own** | Start defining a new logic from scratch |

## Your First Proof

Let's prove a formula in modal logic K:

### Step 1: Load a Logic

1. Click **"Modal logic K..."** in the Task Pane
2. Or go to **Logic → Open Predefined Logic** and select **Monomodal-K**

![Predefined Logics](images/predefined-logics.png)

### Step 2: Examine the Logic

The main window shows the loaded logic with four tabs:

![Main Frame](images/connectors-tab.png)

| Tab | Contents |
|-----|----------|
| **Connectors** | Logical operators (not, and, or, nec, pos, etc.) |
| **Rules** | Inference rules that decompose formulas |
| **Strategies** | Order in which rules are applied |
| **Predefined Formulas** | Sample formulas to test |

### Step 3: Enter a Formula

In the **"Premodels Construction Settings"** panel at the bottom:

1. Select a predefined formula from the dropdown, or
2. Type your own formula in the text area

**Formula syntax:**
- Use **prefix notation**: `and P Q` instead of `P and Q`
- Example: `and nec P pos or P Q` means `□P ∧ ◇(P ∨ Q)`

### Step 4: Build the Proof

Click **"Build Premodels"** to start the tableau construction.

![Tableau Result](images/tableau-result.png)

The result shows:
- **Premodels List:** All generated tableau branches
- **Graph View:** Visual representation of the current premodel
- **Tableaux Tree:** Overview of all tableaux (bottom panel)

### Step 5: Interpret the Result

- **Open (non-closed) premodels** indicate the formula is **satisfiable**
- **All premodels closed** indicates the formula is **unsatisfiable**
- For validity checking, test the negation of your formula

## Step-by-Step Debugging

For detailed analysis, use **"Step By Step..."** instead of "Build Premodels":

![Step by Step](images/step-by-step.png)

This allows you to:
- Set breakpoints on specific rules
- Watch each rule application
- Pause and inspect intermediate states

## Memory Settings

For large proofs or extended sessions, increase Java memory:

**Windows** - Edit `bin/LoTREC.bat`:
```batch
set DEFAULT_JVM_OPTS=-Xmx512M
```

**Linux/macOS** - Edit `bin/LoTREC`:
```bash
DEFAULT_JVM_OPTS="-Xmx512M"
```

Common values: `-Xmx512M` (512 MB), `-Xmx1024M` (1 GB), `-Xmx2048M` (2 GB)

## Next Steps

- [User Guide](user-guide.md) - Learn all interface features
- [Defining Logics](defining-logics.md) - Create your own logics
- [Predefined Logics](predefined-logics.md) - Explore built-in logics
