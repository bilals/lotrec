# LoTREC Tech Stack

## Core Tech Stack (Locked)

> These technologies are determined by the project. New code must use the same stack.

| Category | Technology | Version | Notes |
|----------|------------|---------|-------|
| **Language** | Java | 1.8 | Target compatibility level, do not use newer features |
| **Runtime** | JVM | 1.8+ | Must run on Java 8 or higher |
| **Build Tool** | Apache Ant | NetBeans integration | Build via `ant jar` or NetBeans |
| **IDE** | NetBeans | - | Primary development environment |
| **GUI Framework** | Swing | Built-in | Standard Java desktop UI |
| **Graph Visualization** | Cytoscape | 2.x | Heavy dependency, ~60 JARs |
| **Tokenizer** | JTopas | - | Formula parsing |
| **Test Framework** | JUnit | 4.12 | Unit testing (minimal usage) |
| **Matcher Library** | Hamcrest | 1.3 | Test assertions |

## Dependency Details

### Production Dependencies

| Dependency | Version | Purpose |
|------------|---------|---------|
| **Cytoscape Core** | 2.x | Graph rendering and visualization engine |
| **GINY** | - | Graph interface library (part of Cytoscape) |
| **Piccolo** | - | 2D graphics framework (Cytoscape rendering) |
| **JTopas** | - | Tokenizer/parser library for formula expressions |
| **Servlet API** | - | Web service support (WEB run mode) |
| **JDOM** | - | XML document manipulation |
| **JAXB** | - | XML binding for serialization |
| **Xerces** | - | XML parsing |

### Cytoscape Libraries (lib/Cytoscape_lib/)

| Library | Purpose |
|---------|---------|
| cytoscape.jar | Core Cytoscape library |
| giny.jar | Graph interface library |
| ding.jar | Cytoscape rendering |
| piccolo.jar, piccolox.jar | 2D graphics |
| yfiles-*.jar | Layout algorithms |
| freehep-*.jar | Graphics export |
| jdom.jar | XML handling |
| Various others | Supporting functionality |

### Cytoscape Plugins (lib/Cytoscape_plugins/)

| Plugin | Purpose |
|--------|---------|
| AutomaticLayout.jar | Auto graph layout |
| ManualLayout.jar | Manual node positioning |
| GraphMerge.jar | Graph merging operations |
| QuickFind.jar | Search functionality |
| Browser.jar | Node/edge browsing |
| Filter.jar | Graph filtering |
| TableImport.jar | Data import |

### Development Dependencies

| Dependency | Version | Purpose |
|------------|---------|---------|
| **JUnit** | 4.12 | Unit testing framework |
| **Hamcrest** | 1.3 | Matcher library for assertions |

## Data Storage

| Type | Technology | Purpose |
|------|------------|---------|
| Logic Definitions | XML Files | Store logic definitions in `PredefinedLogics/` |
| User Logics | XML Files | Store in `UserDefinedLogics/` |
| Serialization | Java Serialization | In-memory object persistence |

## Infrastructure

| Category | Technology | Config File |
|----------|------------|-------------|
| Build | Apache Ant | `build.xml` |
| Build Config | NetBeans | `nbproject/project.properties` |
| Project Metadata | NetBeans | `nbproject/project.xml` |
| JAR Manifest | - | `manifest.mf` |

## Build Configuration

### Key Build Properties (nbproject/project.properties)

```properties
# Source and output directories
src.dir=src
test.src.dir=test
build.dir=build
dist.dir=dist
dist.jar=${dist.dir}/LoTREC.jar

# Java version
javac.source=1.8
javac.target=1.8

# Main class
main.class=lotrec.Launcher

# Classpath includes all JARs in lib/
javac.classpath=\
    ${libs.Cytoscape.classpath}:\
    ${libs.junit_4.classpath}
```

### Build Targets

| Target | Command | Purpose |
|--------|---------|---------|
| Clean | `ant clean` | Remove build artifacts |
| Compile | `ant compile` | Compile Java sources |
| JAR | `ant jar` | Create LoTREC.jar |
| Test | `ant test` | Run JUnit tests |
| Run | `ant run` | Execute application |

### Distribution

After `ant jar`, the post-jar target:
1. Copies `README.md` to `dist/`
2. Copies `run.bat` to `dist/`
3. Creates `LoTREC-distribution.zip` containing:
   - `LoTREC.jar`
   - `lib/` directory with all dependencies
   - `README.txt`
   - `run.bat`

## Version Constraints

### Must Follow
- Java 1.8 compatibility - do not use newer Java features (streams lambda expressions are OK, modules are not)
- Cytoscape 2.x API - do not upgrade to Cytoscape 3.x (breaking API changes)
- Swing UI - do not introduce JavaFX or other GUI frameworks
- Ant build - do not convert to Maven/Gradle without explicit decision

### Upgrade Guidelines

**Before Upgrade:**
1. Document current behavior thoroughly
2. Create backup of working version
3. Test with all predefined logics
4. Check Cytoscape API compatibility

**After Upgrade:**
1. Run all predefined logics
2. Verify graph visualization works
3. Test XML parsing/saving
4. Verify formula tokenization
5. Run any existing tests

## Run Modes

| Mode | Constant | Usage |
|------|----------|-------|
| GUI | `Lotrec.GUI_RUN_MODE` | Desktop application with full UI |
| WEB | `Lotrec.WEB_RUN_MODE` | Headless/server mode (limited) |

## File Locations

| Resource | Location |
|----------|----------|
| Predefined Logics | `PredefinedLogics/` (extracted at runtime) |
| User Logics | `UserDefinedLogics/` (working directory) |
| Logic resources in JAR | `lotrec/logics/*.xml` |
| Images | `lotrec/images/` |
