plugins {
    id("java")
    id("fabric-loom") version "1.1-SNAPSHOT"
}

group = "net.pl3x.map.fabric"
version = rootProject.version

repositories {
    maven("https://maven.fabricmc.net/")
}

dependencies {
    compileOnly(project(":Core"))

    minecraft("com.mojang", "minecraft", "1.19.4")
    mappings(loom.officialMojangMappings())

    modImplementation("net.fabricmc", "fabric-loader", "0.14.17")
    modImplementation("net.fabricmc.fabric-api", "fabric-api", "0.75.3+1.19.4")
}

base.archivesName.set("${rootProject.name}-${project.name}")

tasks {
    remapJar {
        dependsOn(":Core:shadowJar")
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name()
        filesMatching("fabric.mod.json") {
            expand(
                    "version" to project.version
            )
        }
    }
}

loom {
    @Suppress("UnstableApiUsage")
    mixin.defaultRefmapName.set("pl3xmap.refmap.json")
    accessWidenerPath.set(file("src/main/resources/pl3xmap.accesswidener"))
    runConfigs.configureEach {
        ideConfigGenerated(true)
    }
}
