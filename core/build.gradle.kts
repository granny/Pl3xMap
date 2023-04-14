plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "net.pl3x.map.core"
version = rootProject.version

tasks {
    assemble {
        dependsOn(shadowJar)
        doLast {
            delete(fileTree("$buildDir").matching {
                include("**/*-dev*.jar")
            })
        }
    }

    shadowJar {
        archiveBaseName.set("${rootProject.name}-${project.name}")
        archiveClassifier.set("")
        mergeServiceFiles()
        exclude(
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt"
        )
        listOf(
                "com.github.benmanes.caffeine.cache",
                "com.github.Carleslc.Simple-YAML",
                "com.google.errorprone.annotations",
                "com.luciad",
                "io.undertow",
                "net.querz",
                "org.checkerframework",
                "org.jboss",
                "org.simpleyaml",
                "org.wildfly",
                "org.xnio",
                "org.yaml.snakeyaml",
        ).forEach { relocate(it, "libs.$it") }
    }
}

