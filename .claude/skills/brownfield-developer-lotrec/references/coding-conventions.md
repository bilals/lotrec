# LoTREC Coding Conventions

## Naming Conventions

### File Naming

| Type | Convention | Example |
|------|------------|---------|
| Java class files | PascalCase matching class name | `Logic.java`, `MainFrame.java` |
| Action classes | Descriptive + Action suffix | `AddExpressionAction.java` |
| Condition classes | Descriptive + Condition suffix | `ExpressionCondition.java` |
| Exception classes | Descriptive + Exception suffix | `ParseException.java` |
| Test files | Class + Test suffix | `TokenizerTest.java` |
| XML logic files | Descriptive-kebab-case | `Classical-Propositional-Logic.xml` |

### Code Naming

| Type | Convention | Example |
|------|------------|---------|
| Class/Interface | PascalCase | `UserService`, `Expression`, `Duplicateable` |
| Method/Function | camelCase | `getName()`, `setDescription()`, `parseLogic()` |
| Variable | camelCase | `userId`, `currentNode`, `edgeSetNext` |
| Constant | UPPER_SNAKE_CASE | `GUI_RUN_MODE`, `EXCEPTION_HEADER` |
| Private member | camelCase (no prefix) | `name`, `description`, `connectors` |
| Boolean methods | is/has prefix | `isClosed()`, `hasSuccessor()`, `isUsedConnector()` |
| Getter methods | get prefix | `getName()`, `getConnectors()` |
| Setter methods | set prefix | `setName()`, `setDescription()` |

### Package Naming

| Package | Purpose |
|---------|---------|
| `lotrec` | Root package |
| `lotrec.gui` | GUI components |
| `lotrec.gui.dialogs` | Dialog windows |
| `lotrec.engine` | Execution engine |
| `lotrec.process` | Strategy processing |
| `lotrec.dataStructure` | Core data models |
| `lotrec.dataStructure.expression` | Expression system |
| `lotrec.dataStructure.graph` | Graph structures |
| `lotrec.dataStructure.tableau` | Tableau system |
| `lotrec.dataStructure.tableau.action` | Tableau actions |
| `lotrec.dataStructure.tableau.condition` | Tableau conditions |
| `lotrec.parser` | Parsing utilities |
| `lotrec.parser.exceptions` | Parser exceptions |
| `lotrec.util` | Utilities |

## Code Formatting

### Tool Configuration
- **Formatter**: NetBeans default formatter
- **Config file**: NetBeans project settings
- **Run command**: Format via IDE (Alt+Shift+F in NetBeans)

### Format Rules
- **Indentation**: 4 spaces (not tabs)
- **Line length**: No strict limit (but keep reasonable ~120 chars)
- **Quotes**: Double quotes for strings `"string"`
- **Braces**: Opening brace on same line (Egyptian/K&R style)

### Brace Style
```java
// CORRECT - Egyptian style
public class Logic {
    public void method() {
        if (condition) {
            // code
        } else {
            // code
        }
    }
}

// INCORRECT - Allman style (do not use)
public class Logic
{
    public void method()
    {
        // ...
    }
}
```

### Spacing
```java
// Spaces after keywords
if (condition) { }
for (int i = 0; i < n; i++) { }
while (running) { }

// Spaces around operators
int result = a + b;
boolean check = x == y;

// No space after method name
methodName();  // correct
methodName (); // incorrect

// Blank line between methods
public void method1() {
}

public void method2() {
}
```

### Import Organization

Order:
1. Standard Java libraries (`java.*`, `javax.*`)
2. Third-party libraries
3. Project-specific imports (`lotrec.*`)

```java
// Example from Lotrec.java
import java.io.File;

import lotrec.dataStructure.Logic;
import lotrec.dataStructure.expression.MarkedExpression;
import lotrec.dataStructure.graph.Wallet;
import lotrec.gui.GraphvizDisplayer;
import lotrec.parser.exceptions.ParseException;
import lotrec.parser.LogicXMLParser;
import lotrec.parser.OldiesTokenizer;
```

**Wildcard Imports**: Allowed for `java.io.*`, `java.util.*` but prefer specific imports for project packages.

## Comment Standards

### File Headers (NetBeans Template)
```java
/*
 * ClassName.java
 *
 * Created on [date]
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
```

### Documentation Comments (JavaDoc)
```java
/**
 * Defines an expression used in all the Lotrec project.
 * An expression must be able to compute all the matching and instancing actions.
 * @author David Fauthoux
 */
public interface Expression extends java.io.Serializable {

    /**
     * Computes the matching process, watching for the contained objects.
     * This method uses the specified instance set, and completes it.
     * @param e the expression on which the instanciation will run
     * @param current the instance set to use and complete
     * @return the completed instance set if matching succeeds, null if it fails
     */
    public abstract InstanceSet matchWith(Expression e, InstanceSet current);
}
```

### Inline Comments
```java
// Single line comment for brief explanations
int count = 0; // trailing comment

// Multi-line explanation
// for more complex logic
// that needs elaboration
```

### TODO/FIXME
```java
// TODO: implement this feature
// FIXME: this needs to be fixed
// NOTE: important consideration
```

### Commented-Out Code
- Legacy code uses commented-out blocks for disabled features
- Prefer removing unused code over commenting
- If keeping for reference, add explanation comment

## Git Conventions

### Commit Message Format
```
<type>: <description>

[optional body]

[optional footer]
```

### Commit Types
| Type | Description |
|------|-------------|
| feat | New feature |
| fix | Bug fix |
| docs | Documentation only |
| style | Formatting, no code change |
| refactor | Refactoring without new features |
| test | Adding tests |
| chore | Build, tooling changes |

### Branch Naming
- Feature branch: `feature/description`
- Fix branch: `fix/description`
- Release branch: `release/version`

## Code Quality Guidelines

### Access Modifiers
- **public**: API methods, constants, main classes
- **private**: Internal implementation details
- **protected**: Rarely used, for inheritance
- **package-default**: Internal helper methods within package

### Static Members
```java
// Constants
public static String GUI_RUN_MODE = "GUI";
public static String WEB_RUN_MODE = "WEB";

// Counters for auto-naming
private static int forName = 1;

// Utility methods
public static Logic getNewEmptyLogic() { }
```

### Collections Usage
- **Vector**: Legacy but consistently used throughout
- **ArrayList**: Acceptable for new code
- **HashMap**: Standard map usage
- **Collections.synchronizedMap**: For thread-safe maps

```java
// Existing pattern - Vector with generics
private Vector<Connector> connectors;
private Vector<Rule> rules;
private Vector<Strategy> strategies;

// Acceptable for new code
private ArrayList<Expression> expressions;
```

### Null Handling
```java
// Defensive null checks
if (o instanceof Logic) {
    Logic l = (Logic) o;
    return this.getName().equals(l.getName());
}
return false;

// Null checks before operations
if (result != null) {
    process(result);
}
```

### Serialization
```java
// Classes that need persistence implement Serializable
public class Logic implements Serializable {
    private String name;
    private String description;
    // ...
}
```

## Exception Handling

### Exception Declaration
```java
public class ParseException extends java.lang.Exception {
    public static String EXCEPTION_HEADER = "Logic parsing error: ";
    public static String NO_LOGIC = EXCEPTION_HEADER + "XML file doesn't contain...";

    public ParseException(String message) {
        super(message);
    }
}
```

### Try-Catch Pattern
```java
try {
    resultLogic = lxmlparser.parseLogic(completeFileName);
} catch (ParseException ex) {
    Lotrec.println("Exception while reading: " + completeFileName);
    Lotrec.println(ex.getMessage());
}
```

### Resource Management
```java
BufferedWriter bw = null;
BufferedReader br = null;
try {
    bw = new BufferedWriter(new FileWriter(file));
    br = new BufferedReader(new InputStreamReader(is));
    // operations
} catch (IOException ex) {
    ex.printStackTrace();
} finally {
    try {
        if (bw != null) bw.close();
        if (br != null) br.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```

## Testing Conventions

### Test Structure
- Tests in `test/` directory mirroring `src/` structure
- Main method testing for quick verification
- JUnit for formal unit tests

### Main Method Testing Pattern
```java
public class TokenizerTest {
    public static void main(String[] args) throws Exception {
        // Setup
        String myData = "test input";

        // Execute
        TokenizerSource source = new ReaderSource(new StringReader(myData));

        // Verify (manual inspection)
        while (tokenizer.hasMoreToken()) {
            System.out.println(tokenizer.currentImage());
        }
    }
}
```

### JUnit Pattern (When Used)
```java
import org.junit.Test;
import static org.junit.Assert.*;

public class SampleJUnitTest {
    @Test
    public void testSomething() {
        // arrange
        // act
        // assert
        assertTrue(condition);
    }
}
```
