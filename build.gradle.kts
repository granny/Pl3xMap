plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.3.6"
}

group = "net.pl3x.map"
version = "2.0.0-SNAPSHOT"
description = "Minimalistic and lightweight world map viewer for Paper servers"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

dependencies {
    paperDevBundle("1.18.2-R0.1-SNAPSHOT")
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
        expand(
            "name" to rootProject.name,
            "group" to project.group,
            "version" to project.version,
            "description" to project.description,
        )
    }
}
