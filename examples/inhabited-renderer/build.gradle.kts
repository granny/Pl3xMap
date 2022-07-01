plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.3.7"
}

group = "net.pl3x.map.addon"
version = "1.0"
description = "Pl3xMap addon that renders chunk inhabited times as a heatmap"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    mavenLocal()
}

dependencies {
    paperDevBundle("1.19-R0.1-SNAPSHOT")
    compileOnly("net.pl3x.map:Pl3xMap:2.0.0-SNAPSHOT")
}

tasks {
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
