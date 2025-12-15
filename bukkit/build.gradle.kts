plugins {
    id("java")
    alias(libs.plugins.paperweight.userdev)
    alias(libs.plugins.shadow)
    alias(libs.plugins.run.paper)
}

val buildNum = System.getenv("NEXT_BUILD_NUMBER") ?: "SNAPSHOT"
project.version = "${libs.versions.minecraft.get()}-$buildNum"
project.group = "net.pl3x.map.bukkit"

base {
    archivesName = "${rootProject.name}-${project.name}"
}

repositories {
    maven("https://repo.granny.dev/snapshots/")
    maven("https://central.sonatype.com/repository/maven-snapshots/") {
        name = "sonatypeSnapshots"
        mavenContent { snapshotsOnly() }
    }
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation(project(":core", configuration = "shadow"))

    implementation(libs.cloudBrigadier)
    implementation(libs.cloudPaper)

    implementation(libs.adventurePlatformBukkit)

    paperweight.paperDevBundle(libs.versions.bukkit)
}

tasks {
    /*reobfJar { // TODO: uncomment once paper has reobf support again
        dependsOn(jar)
        outputJar.set(jar.get().archiveFile)
    }*/ // TODO: uncomment once paper has reobf support again

    // needed for below jank
    compileJava {
        dependsOn(":core:jar")
    }

    shadowJar {
        mergeServiceFiles()

        // this is janky, but it works
        manifest {
            from(project(":core").tasks.named<Jar>("shadowJar").get().manifest)
        }

        archiveClassifier.set("") // TODO: delete once paper has reobf support again
    }

    build {
        //dependsOn(reobfJar) // TODO: uncomment once paper has reobf support again
        dependsOn(shadowJar) // TODO: delete once paper has reobf support again
    }

    runServer {
        minecraftVersion(libs.versions.minecraft.get())
    }

    processResources {
        inputs.properties(mapOf(
            "name" to rootProject.name,
            "group" to project.group,
            "version" to project.version,
            "authors" to project.properties["authors"],
            "description" to project.properties["description"],
            "website" to project.properties["website"]
        ))

        filesMatching("plugin.yml") {
            expand(inputs.properties)
        }
    }
}
