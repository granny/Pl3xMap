plugins {
    `java-library`
    id("maven-publish")
    id("io.papermc.paperweight.userdev") version "1.3.7"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "net.pl3x.map"
version = "2.0.0-SNAPSHOT"
description = "Minimalistic and lightweight world map viewer for Paper servers"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

dependencies {
    paperDevBundle("1.19-R0.1-SNAPSHOT")
    implementation("io.undertow:undertow-core:2.2.17.Final")
    implementation("com.sksamuel.scrimage:scrimage-core:4.0.31")
    implementation("com.sksamuel.scrimage:scrimage-webp:4.0.31")
    implementation("cloud.commandframework:cloud-paper:1.7.0")
    implementation("cloud.commandframework:cloud-minecraft-extras:1.7.0")
    implementation("org.bstats:bstats-bukkit:3.0.0")
}

tasks {
    shadowJar {
        // we'll provide our own up-to-date webp binaries, thanks
        exclude(
            "dist_webp_binaries/*",
            "dist_webp_binaries/linux/*",
            "dist_webp_binaries/mac/*",
            "dist_webp_binaries/window/*"
        )
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
    publishToMavenLocal {
        dependsOn(reobfJar)
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

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}
