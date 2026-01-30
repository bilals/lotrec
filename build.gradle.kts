plugins {
    java
    application
    jacoco
}

group = "lotrec"
version = "3.0"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
}

// Define dependency versions
val junitVersion = "5.10.2"
val assertjVersion = "3.25.3"

dependencies {
    // Local JARs - Cytoscape libs (these don't have Maven equivalents)
    implementation(fileTree("lib/Cytoscape_lib") { include("*.jar") })

    // Local JARs - Cytoscape plugins
    implementation(fileTree("lib/Cytoscape_plugins") { include("*.jar") })

    // Local JARs - other dependencies
    implementation(files("lib/jtopas.jar"))
    implementation(files("lib/servlet-api.jar"))

    // JUnit 5 for testing
    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // AssertJ for fluent assertions
    testImplementation("org.assertj:assertj-core:$assertjVersion")    
}

application {
    mainClass.set("lotrec.Launcher")
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
