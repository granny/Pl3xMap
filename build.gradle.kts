plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.3.6"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("xyz.jpenilla.run-paper") version "1.0.6"
}

group = "net.pl3x.map"
version = "2.0.0-SNAPSHOT"
description = "Minimalistic and lightweight world map viewer for Paper servers"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

dependencies {
    paperDevBundle("1.18.2-R0.1-SNAPSHOT")
    implementation("io.undertow", "undertow-core", "2.2.17.Final")
    implementation("org.bstats", "bstats-bukkit", "3.0.0")
}

tasks {
    shadowJar {
        from(rootProject.projectDir.resolve("LICENSE"))
        minimize {
            // does not like being minimized _or_ relocated (xnio errors)
            exclude(dependency("io.undertow:.*:.*"))
        }
        listOf(
            "io.leangen.geantyref",
            "org.bstats"
        ).forEach { relocate(it, "${rootProject.group}.plugin.lib.$it") }
    }
    reobfJar {
        outputJar.set(project.layout.buildDirectory.file("libs/${rootProject.name}-${rootProject.version}.jar"))
    }
    assemble {
        dependsOn(reobfJar)
    }
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name()
        filesMatching("plugin.yml") {
            expand(
                "name" to rootProject.name,
                "group" to project.group,
                "version" to project.version,
                "description" to project.description
            )
        }
    }
}
