# LoTREC Migration History

## 2025-01-30: Build System Migration Complete

### Summary
Migrated from NetBeans Ant build to Gradle Kotlin DSL.

### Changes Made

#### Build System
- **Added**: `build.gradle.kts` with Kotlin DSL
- **Added**: `settings.gradle.kts`
- **Added**: Gradle wrapper (`gradlew`, `gradlew.bat`, `gradle/`)
- **Removed**: `build.xml` (Ant build file)
- **Removed**: `nbproject/` (NetBeans project files)

#### Dependencies
- **Removed**: `lib/junit-4.12.jar` (replaced by JUnit 5 from Maven)
- **Removed**: `lib/hamcrest-core-1.3.jar` (replaced by AssertJ from Maven)
- **Kept**: `lib/jtopas.jar`, `lib/servlet-api.jar` (no Maven equivalents)
- **Kept**: `lib/Cytoscape_lib/`, `lib/Cytoscape_plugins/` (local dependencies)

#### Distribution
- **Removed**: `src/lotrec/dist/` (README.TXT, run.bat)
- **New task**: `./gradlew publishDist` creates `LoTREC-3.0.zip` at project root
- **Improvement**: Auto-generated startup scripts (`bin/lotrec`, `bin/lotrec.bat`)
- **Improvement**: `README.md` included in distribution

#### Testing
- **Upgraded**: JUnit 4 → JUnit 5
- **Added**: AssertJ for fluent assertions
- **Added**: JaCoCo for coverage reporting

### Verification
- `./gradlew build` - Compiles and tests successfully
- `./gradlew publishDist` - Creates distribution ZIP
- `./gradlew run` - Launches application
