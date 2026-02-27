import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

plugins {
    alias(libs.plugins.fabric.loom)
    alias(libs.plugins.shadow)
}

val buildNum = System.getenv("NEXT_BUILD_NUMBER") ?: "SNAPSHOT"
project.version = "${libs.versions.minecraft.get()}-$buildNum"
project.group = "net.pl3x.map.fabric"

base {
    archivesName = "${rootProject.name}-${project.name}"
}

loom {
    mixin {
        defaultRefmapName = "pl3xmap.refmap.json"
    }
    accessWidenerPath = file("src/main/resources/pl3xmap.accesswidener")
    runConfigs.configureEach {
        ideConfigGenerated(true)
    }
}

repositories {
    maven("https://repo.granny.dev/snapshots/")
    maven("https://central.sonatype.com/repository/maven-snapshots/") {
        name = "sonatypeSnapshots"
        mavenContent { snapshotsOnly() }
    }
    maven("https://maven.fabricmc.net/")
    maven("https://jitpack.io")
}

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.officialMojangMappings())

    implementation(project(path = ":core", configuration = "shadow"))

    implementation(libs.jspecifyAnnotations)

    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.api)

    modImplementation(libs.cloudFabric)
    include(libs.cloudFabric)

    modImplementation(libs.adventurePlatformFabric) {
        exclude("net.kyori", "ansi") // TODO: temporary
    }
    include(libs.adventurePlatformFabric) {
        exclude("net.kyori", "ansi") // TODO: temporary
    }
}

tasks {
    remapJar {
        dependsOn(shadowJar)
        inputFile.set(shadowJar.get().archiveFile)

        archiveClassifier = ""
    }

    // needed for below jank
    compileJava {
        dependsOn(":core:jar")
    }

    shadowJar {
        mergeServiceFiles()

        dependencies {
            include(project(":core"))
        }

        // this is janky, but it works
        manifest {
            from(project(":core").tasks.named<Jar>("shadowJar").get().manifest)
        }
    }

    build {
        dependsOn(remapJar)
    }

    processResources {
        inputs.properties(mapOf(
            "name" to rootProject.name,
            "group" to project.group,
            "version" to project.version,
            "authors" to project.properties["authors"],
            "description" to rootProject.properties["description"],
            "fabricApiVersion" to libs.versions.fabricApi.get(),
            "fabricLoaderVersion" to libs.versions.fabricLoader.get(),
            "minecraftVersion" to libs.versions.minecraft.get(),
            "website" to rootProject.properties["website"],
            "sources" to rootProject.properties["sources"],
            "issues" to rootProject.properties["issues"]
        ))

        filesMatching("fabric.mod.json") {
            expand(inputs.properties)
        }

        // replace whole array with authors
        doLast {
            val fabricJsonFile = outputs.files.singleFile.resolve("fabric.mod.json")
            @Suppress("UNCHECKED_CAST")
            val jsonContent = JsonSlurper().parse(fabricJsonFile) as MutableMap<String, Any>
            jsonContent["authors"] = JsonSlurper().parseText(project.properties["authors"].toString()) as Any

            fabricJsonFile.writeText(JsonBuilder(jsonContent).toPrettyString())
        }
    }
}
