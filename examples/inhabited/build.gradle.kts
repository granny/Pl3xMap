plugins {
    `java-library`
}

group = "net.pl3x.map.addon"
version = "1.0"
description = "Pl3xMap addon that renders chunk inhabited times as a heatmap"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

dependencies {
    paperDevBundle("1.19-R0.1-SNAPSHOT")
    compileOnly(project(":Pl3xMap"))
}

tasks {
    reobfJar {
        outputJar.set(rootProject.layout.buildDirectory.file("libs/${project.name}-${project.version}.jar"))
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
                "name" to project.name,
                "group" to project.group,
                "version" to project.version,
                "description" to project.description
            )
        }
    }
}
