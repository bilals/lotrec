# LoTREC Development Patterns

## Design Patterns

### Common Patterns List

| Pattern | Purpose | Example Location |
|---------|---------|------------------|
| Factory Pattern | Create objects based on type/keyword | `DialogsFactory`, `AbstractAction.CLASSES_KEYWORDS` |
| Observer/Listener | Event-driven state changes | `ProcessEvent`, `ProcessListener` |
| Strategy Pattern | Pluggable execution algorithms | `Strategy`, `Routine`, `AllRules` |
| Command Pattern | Encapsulate tableau modifications | `Action`, `AbstractAction` |
| Prototype Pattern | Clone complex object graphs | `Duplicateable`, `Duplicator` |
| Composite Pattern | Hierarchical worker composition | `Routine` containing `AbstractWorker`s |
| Template Method | Define algorithm skeleton | `Routine.work()` |
| Interpreter | Parse logic DSL | `LogicXMLParser`, `OldiesTokenizer` |

### Pattern Usage Guide

#### Factory Pattern

**DialogsFactory** - Creates UI dialogs:
```java
// Location: src/lotrec/gui/DialogsFactory.java
public class DialogsFactory {
    public static int cancelDialog(Component parent) {
        return JOptionPane.showConfirmDialog(parent,
            "Are you sure you want to cancel?",
            "Cancel", JOptionPane.YES_NO_OPTION);
    }

    public static int deleteDialog(Component parent, String deleted) {
        return JOptionPane.showConfirmDialog(parent,
            "Are you sure you want to delete " + deleted + "?",
            "Delete", JOptionPane.YES_NO_OPTION);
    }
}
```

**AbstractAction Factory** - Creates actions from keywords:
```java
// Location: src/lotrec/dataStructure/tableau/action/AbstractAction.java
public static HashMap<String, String> CLASSES_KEYWORDS;
static {
    CLASSES_KEYWORDS = new HashMap<>();
    CLASSES_KEYWORDS.put("add", "AddExpressionAction");
    CLASSES_KEYWORDS.put("mark", "MarkAction");
    CLASSES_KEYWORDS.put("duplicate", "DuplicateAction");
    CLASSES_KEYWORDS.put("link", "LinkAction");
    // ... more mappings
}

// Usage in parser:
String className = AbstractAction.CLASSES_KEYWORDS.get(keyword);
Class<?> clazz = Class.forName("lotrec.dataStructure.tableau.action." + className);
Action action = (Action) clazz.newInstance();
```

#### Observer/Listener Pattern

**Event System**:
```java
// Location: src/lotrec/process/ProcessEvent.java
public abstract class ProcessEvent extends EventObject implements CompleteDuplicateable {
    public int type;

    public ProcessEvent(Object source, int type) {
        super(source);
        this.type = type;
    }
}

// Location: src/lotrec/process/ProcessListener.java
public interface ProcessListener {
    void processEvent(ProcessEvent event);
}

// Concrete events:
// - NodeEvent (ADDED, REMOVED)
// - GraphEvent (ADDED, REMOVED)
// - ExpressionEvent
// - MarkEvent
// - LinkEvent
```

**Usage Example**:
```java
// Location: src/lotrec/dataStructure/graph/Node.java
public class Node extends ExtendedGraph implements Duplicateable {
    private Dispatcher dispatcher = new Dispatcher();

    public void addProcessListener(ProcessListener listener) {
        dispatcher.addProcessListener(listener);
    }

    protected void send(ProcessEvent event) {
        dispatcher.send(event);
    }
}
```

#### Strategy Pattern

**Strategy Hierarchy**:
```java
// Location: src/lotrec/process/AbstractWorker.java
public abstract class AbstractWorker implements Duplicateable, Serializable {
    public abstract void work();
}

// Location: src/lotrec/process/Routine.java
public abstract class Routine extends AbstractWorker {
    protected Vector<AbstractWorker> workers;

    public void add(AbstractWorker worker, Object constraint) {
        workers.add(worker);
    }
}

// Location: src/lotrec/process/AllRules.java
public class AllRules extends Routine {
    @Override
    public void work() {
        for (AbstractWorker w : workers) {
            w.work();
        }
    }
}

// Location: src/lotrec/process/Strategy.java
public class Strategy extends AllRules {
    private String code;
    private String comment;
}
```

#### Command Pattern

**Action Interface**:
```java
// Location: src/lotrec/process/Action.java
public interface Action {
    void doAction();
}

// Location: src/lotrec/dataStructure/tableau/action/AbstractAction.java
public abstract class AbstractAction implements Action {
    private Vector<Parameter> parameters;

    public abstract void apply(InstanceSet instanceSet) throws ProcessException;
    public abstract String getCode();
}

// Concrete action example:
// Location: src/lotrec/dataStructure/tableau/action/AddExpressionAction.java
public class AddExpressionAction extends AbstractAction {
    @Override
    public void apply(InstanceSet instanceSet) throws ProcessException {
        Node node = (Node) instanceSet.get(nodeScheme);
        Expression expr = expression.getInstance(instanceSet);
        node.add(new MarkedExpression(expr));
    }
}
```

#### Prototype Pattern (Duplicateable)

**Duplication Framework**:
```java
// Location: src/lotrec/util/Duplicateable.java
public interface Duplicateable extends CompleteDuplicateable {
    public abstract Duplicateable duplicate(Duplicator duplicator);
}

// Location: src/lotrec/util/Duplicator.java
public interface Duplicator {
    public void setImage(Object o, Object image);
    public Object getImage(Object o) throws DuplicateException;
    public boolean hasImage(Object o);
}

// Usage in Strategy:
public class Strategy extends AllRules {
    public Strategy(Strategy toDuplicate) {
        super(toDuplicate);
        this.code = toDuplicate.code;
        this.comment = toDuplicate.comment;
    }

    @Override
    public Duplicateable duplicate(Duplicator duplicator) {
        Duplicateable d = new Strategy(this);
        duplicator.setImage(this, d);
        return d;
    }
}
```

## Code Organization Patterns

### DTO/VO/Entity Separation

In LoTREC, domain objects serve multiple purposes:

| Type | Description | Location |
|------|-------------|----------|
| **Logic** | Logic definition entity | `dataStructure/Logic.java` |
| **Expression** | Formula representation | `dataStructure/expression/` |
| **Tableau** | Proof state entity | `dataStructure/tableau/Tableau.java` |
| **InstanceSet** | Variable binding VO | `dataStructure/expression/InstanceSet.java` |

### Interface and Implementation Separation

| Interface | Implementation | Location |
|-----------|----------------|----------|
| `Expression` | `Connector`, `VariableExpression`, etc. | `dataStructure/expression/` |
| `Condition` | `ExpressionCondition`, `MarkCondition`, etc. | `dataStructure/tableau/condition/` |
| `Action` | `AbstractAction` and concrete actions | `dataStructure/tableau/action/` |
| `Duplicateable` | Various data classes | Throughout `dataStructure/` |
| `ProcessListener` | `EventMachine`, GUI components | `process/`, `gui/` |

### Configuration Externalization

**Logic Definitions** (XML):
```xml
<!-- Location: src/lotrec/logics/*.xml -->
<logic>
    <name>Classical-Propositional-Logic</name>
    <connector>
        <name>AND</name>
        <arity>2</arity>
        <associative>true</associative>
    </connector>
    <rule>
        <name>And-Rule</name>
        <condition>hasElement node A and B</condition>
        <action>add A; add B</action>
    </rule>
    <strategy>
        <name>Main</name>
        <code>repeat allRules</code>
    </strategy>
</logic>
```

**Static Configuration**:
```java
// Location: src/lotrec/Lotrec.java
public class Lotrec {
    private static String predefLogicsPath;
    private static String userdefLogicsPath;
    private static String workingPath;
    private static String runMode;

    public static void initialize(String runMode) {
        if (runMode.equals(Lotrec.GUI_RUN_MODE)) {
            workingPath = System.getProperty("user.dir") +
                          System.getProperty("file.separator");
        }
        setPredefLogicsPath(getWorkingPath() + "PredefinedLogics" +
                            System.getProperty("file.separator"));
    }
}
```

## Error Handling Patterns

### Exception Hierarchy

```
java.lang.Exception
└── ParseException (lotrec.parser.exceptions)
    ├── GraphXMLParserException
    ├── LexicalException
    ├── ExtrasTokensInStringException
    └── InternalParseException

java.lang.RuntimeException
├── LinkNodeException (lotrec.dataStructure.graph)
├── DuplicateException (lotrec.util)
└── ProcessException (lotrec.process)
```

### Exception Message Constants

```java
// Location: src/lotrec/parser/exceptions/ParseException.java
public class ParseException extends java.lang.Exception {
    public static String EXCEPTION_HEADER = "Logic parsing error: ";
    public static String EXCEPTION_CAUSE = "\nThis exception was caused by this value: ";

    // Predefined error messages
    public static String NO_LOGIC = EXCEPTION_HEADER +
        "XML file doesn't contain a logic definition...";
    public static String NO_LOGIC_NAME = EXCEPTION_HEADER +
        "Logic name should be specified...";
    public static String NO_CONNECTOR_NAME = EXCEPTION_HEADER +
        "Connector name field is empty...";
    public static String BAD_STRATEGY_DEF = EXCEPTION_HEADER +
        "Unknown sub-strategy identifier...";
    public static String UNKOWN_CODITION = EXCEPTION_HEADER +
        "Unknown condition name...";
    public static String UNKOWN_ACTION = EXCEPTION_HEADER +
        "Unknown action name...";
}
```

### Error Handling Pattern

```java
// Location: src/lotrec/Lotrec.java
public static Logic openLogicFile(String completeFileName) {
    Logic resultLogic = null;
    LogicXMLParser lxmlparser = new LogicXMLParser();
    try {
        resultLogic = lxmlparser.parseLogic(completeFileName);
    } catch (ParseException ex) {
        Lotrec.println("Exception while reading the logic file " + completeFileName);
        Lotrec.println(ex.getMessage());
    }
    return resultLogic;
}
```

## Logging Standards

### Logging Approach

**No formal logging framework** - uses direct console output:

```java
// Location: src/lotrec/Lotrec.java
public static void print(Object o) {
    System.out.print(o);
}

public static void println(Object o) {
    System.out.println(o);
}

// Usage throughout codebase:
Lotrec.println("Loading logic: " + logicName);
```

### Console Output Pattern

```java
// Informational output
System.out.println("Processing rule: " + ruleName);

// Error output
System.err.println("Exception while reading: " + fileName);
System.err.println(ex.getMessage());

// Stack traces for debugging
catch (IOException ex) {
    ex.printStackTrace();
}
```

### Log Level Equivalent

| Level | Method | Use Case |
|-------|--------|----------|
| INFO | `System.out.println()` | Normal operations |
| ERROR | `System.err.println()` | Error conditions |
| DEBUG | `ex.printStackTrace()` | Exception details |

## Testing Patterns

### Test Structure

```
test/
└── lotrec/
    └── SampleJUnitTest.java
```

### Main Method Testing Pattern

```java
// Location: src/lotrec/parser/TokenizerTest.java
public class TokenizerTest {
    public static void main(String[] args) throws Exception {
        // Setup
        String myData = "kawa kawa <mawa> kaw\n kah kah";
        TokenizerSource source = new ReaderSource(new StringReader(myData));

        // Configure tokenizer
        Tokenizer tokenizer = new StandardTokenizer();
        tokenizer.setSource(source);

        // Execute and verify (manual inspection)
        while (tokenizer.hasMoreToken()) {
            Token token = tokenizer.nextToken();
            switch (token.getType()) {
                case Token.NORMAL:
                    System.out.print(tokenizer.currentImage());
                    break;
                case Token.SPECIAL_SEQUENCE:
                    System.out.print("[" + tokenizer.currentImage() + "]");
                    break;
            }
        }
    }
}
```

### JUnit Test Pattern

```java
// Location: test/lotrec/SampleJUnitTest.java
import org.junit.Test;
import static org.junit.Assert.*;

public class SampleJUnitTest {
    @Test
    public void testExample() {
        // Arrange
        Logic logic = new Logic();
        logic.setName("Test Logic");

        // Act
        String name = logic.getName();

        // Assert
        assertEquals("Test Logic", name);
    }
}
```

### TestingFormula Structure

```java
// Location: src/lotrec/dataStructure/TestingFormula.java
// Not a unit test - data structure for formula verification
public class TestingFormula {
    private String formula;
    private String expectedResult; // "valid", "satisfiable", "unsatisfiable"

    // Used in Logic XML files:
    // <testing-formula expected="valid">A -> A</testing-formula>
}
```

## Configuration Management

### Config File Structure

| File | Purpose | Format |
|------|---------|--------|
| `nbproject/project.properties` | Build configuration | Properties |
| `nbproject/project.xml` | Project metadata | XML |
| `build.xml` | Ant build targets | XML |
| `manifest.mf` | JAR manifest | Text |
| `src/lotrec/logics/*.xml` | Logic definitions | XML |

### Environment Configuration

| Environment | Configuration | Notes |
|-------------|---------------|-------|
| Development | NetBeans project | Full IDE integration |
| Build | Ant + project.properties | `ant jar` command |
| Runtime GUI | `Lotrec.GUI_RUN_MODE` | Desktop application |
| Runtime WEB | `Lotrec.WEB_RUN_MODE` | Server/headless mode |

### Runtime Path Configuration

```java
// Location: src/lotrec/Lotrec.java
public static void initialize(String runMode) {
    Lotrec.setRunMode(runMode);
    if (runMode.equals(Lotrec.GUI_RUN_MODE)) {
        workingPath = System.getProperty("user.dir") +
                      System.getProperty("file.separator");
    } else if (runMode.equals(Lotrec.WEB_RUN_MODE)) {
        // Different path handling for web mode
    }
    setPredefLogicsPath(getWorkingPath() + "PredefinedLogics" +
                        System.getProperty("file.separator"));
    setUserdefLogicsPath(getWorkingPath() + "UserDefinedLogics" +
                         System.getProperty("file.separator"));
}
```

### Sensitive Config Handling

- No credentials stored in source
- File paths determined at runtime
- Logic files are user-editable but not sensitive

## Adding New Components

### Adding a New Tableau Action

1. Create class in `src/lotrec/dataStructure/tableau/action/`:
```java
public class MyNewAction extends AbstractAction {
    private SchemeVariable nodeScheme;
    // other parameters

    @Override
    public void apply(InstanceSet instanceSet) throws ProcessException {
        Node node = (Node) instanceSet.get(nodeScheme);
        // implementation
    }

    @Override
    public String getCode() {
        return "myaction " + nodeScheme.getName();
    }
}
```

2. Register in `AbstractAction.CLASSES_KEYWORDS`:
```java
CLASSES_KEYWORDS.put("myaction", "MyNewAction");
```

### Adding a New Tableau Condition

1. Create class in `src/lotrec/dataStructure/tableau/condition/`:
```java
public class MyCondition extends AbstractCondition {
    @Override
    public BasicActivator createActivator() {
        // return activator that listens for relevant events
    }

    @Override
    public Restriction createRestriction() {
        // return restriction that checks condition
    }
}
```

2. Register in `AbstractCondition.CLASSES_KEYWORDS`:
```java
CLASSES_KEYWORDS.put("mycheck", "MyCondition");
```

### Adding a New Event Type

1. Create event class in `src/lotrec/process/` or appropriate location:
```java
public class MyEvent extends ProcessEvent {
    public static int MY_EVENT_TYPE = 100;
    private Object payload;

    public MyEvent(Object source, Object payload) {
        super(source, MY_EVENT_TYPE);
        this.payload = payload;
    }
}
```

2. Dispatch from source:
```java
send(new MyEvent(this, data));
```

3. Handle in listener:
```java
public void processEvent(ProcessEvent event) {
    if (event instanceof MyEvent) {
        MyEvent me = (MyEvent) event;
        // handle event
    }
}
```
