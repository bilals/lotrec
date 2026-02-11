plugins {
    java
    application
    jacoco
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "lotrec"
version = "3.0"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

// Define dependency versions
val junitVersion = "5.10.2"
val assertjVersion = "3.25.3"

javafx {
    version = "21.0.5"
    modules("javafx.controls", "javafx.swing")
}

dependencies {
    // Local JARs - Cytoscape libs (these don't have Maven equivalents)
    implementation(fileTree("lib/Cytoscape_lib") { include("*.jar") })

    // Local JARs - Cytoscape plugins
    implementation(fileTree("lib/Cytoscape_plugins") { include("*.jar") })

    // Local JARs - other dependencies
    implementation(files("lib/jtopas.jar"))
    implementation(files("lib/servlet-api.jar"))

    // Jakarta JAXB (replaces javax.xml.bind removed in Java 11)
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:4.0.2")
    runtimeOnly("com.sun.xml.bind:jaxb-impl:4.0.5")

    // JUnit 5 for testing
    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // AssertJ for fluent assertions
    testImplementation("org.assertj:assertj-core:$assertjVersion")

    // TestFX (JavaFX UI testing)
    testImplementation("org.testfx:testfx-core:4.0.18")
    testImplementation("org.testfx:testfx-junit5:4.0.18")
    testRuntimeOnly("org.testfx:openjfx-monocle:21.0.2")
}

application {
    mainClass.set("lotrec.guifx.LauncherFX")
    applicationDefaultJvmArgs = listOf(
        "--add-opens", "javafx.graphics/com.sun.javafx.application=ALL-UNNAMED",
        "--add-opens", "javafx.graphics/com.sun.glass.ui=ALL-UNNAMED"
    )
}

// Swing GUI task (on demand, for regression testing)
tasks.register<JavaExec>("runSwing") {
    group = "application"
    description = "Launch the legacy Swing GUI"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("lotrec.Launcher")
}

// Capture Swing baseline screenshots for visual migration validation
tasks.register<JavaExec>("captureSwingBaseline") {
    group = "verification"
    description = "Capture Swing GUI baseline screenshots for visual comparison"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("lotrec.guifx.validation.SwingBaselineCapture")
}

distributions {
    main {
        contents {
            from("README.md")
        }
    }
}

sourceSets {
    main {
        java {
            srcDirs("src")
        }
        resources {
            srcDirs("src")
            exclude("**/*.java")
        }
    }
    test {
        java {
            srcDirs("test")
        }
        resources {
            srcDirs("test")
            exclude("**/*.java")
        }
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    // Enable all warnings for Phase 2 cleanup
    // options.compilerArgs.addAll(listOf("-Xlint:all", "-Xlint:-serial"))
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
    // JavaFX classpath access for TestFX headless testing
    jvmArgs(
        "--add-opens", "javafx.graphics/com.sun.javafx.application=ALL-UNNAMED",
        "--add-opens", "javafx.graphics/com.sun.glass.ui=ALL-UNNAMED"
    )
    // Enable headless TestFX via Monocle
    systemProperty("testfx.robot", "glass")
    systemProperty("testfx.headless", "true")
    systemProperty("glass.platform", "Monocle")
    systemProperty("monocle.platform", "Headless")
    systemProperty("prism.order", "sw")
    systemProperty("prism.text", "t2k")
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "lotrec.Launcher",
            "Implementation-Title" to "LoTREC",
            "Implementation-Version" to version
        )
    }

    // Include dependencies in a libs folder alongside the main JAR
    // For a fat JAR, uncomment the from() block below

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

// Task to create a fat/uber JAR with all dependencies
tasks.register<Jar>("fatJar") {
    archiveClassifier.set("all")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes(
            "Main-Class" to "lotrec.Launcher",
            "Implementation-Title" to "LoTREC",
            "Implementation-Version" to version
        )
    }

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get()
            .filter { it.name.endsWith("jar") }
            .map { zipTree(it) }
    })
}

// JaCoCo configuration for test coverage
jacoco {
    toolVersion = "0.8.11"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

// Clean task extension
tasks.clean {
    // Other cleaning actions can be added here if needed
}

// Default tasks
defaultTasks("build")
