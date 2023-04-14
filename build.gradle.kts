plugins {
    id("java")
}

val minecraftVersion: String by project
val buildNum = System.getenv("GITHUB_RUN_NUMBER") ?: "SNAPSHOT"
project.group = "net.pl3x.map"
project.version = "$minecraftVersion-$buildNum"

dependencies {
    compileOnly(project(":Core"))
    compileOnly(project(":Bukkit"))
    compileOnly(project(":Fabric"))
    compileOnly(project(":Forge"))
}

defaultTasks("build")

tasks {
    build {
        // this is to ensure the subprojects finish building completely before this task is finished
        subprojects.filter { it.name != "WebMap" }.forEach { project ->
            run {
                dependsOn(project.tasks.build)
            }
        }
        // copy the webmap over
        dependsOn(named("copyWebmap"))
        // after subprojects are finished we can combine their jars into a fatjar
        finalizedBy(named("combineJars"))
    }

    register<Jar>("combineJars") {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        from(files(subprojects.filter { it.name != "WebMap" }.map {
            it.layout.buildDirectory.file("libs/${rootProject.name}-${it.name}-${it.version}.jar").get()
        }).filter { it.name != "MANIFEST.MF" }.map { if (it.isDirectory) it else zipTree(it) })
        manifest {
            // this must be here because it overrides the default jar task
            attributes["Main-Class"] = "net.pl3x.map.core.Pl3xMap"
        }
    }

    register<Copy>("copyWebmap") {
        dependsOn(named("npmBuild"))
        println("Copying webmap...")
        from("$rootDir/webmap/public")
        include("**/*")
        exclude("tiles*/")
        into("$rootDir/core/src/main/resources/web")
        from("$rootDir/webmap/dist")
        include("**/*")
        into("$rootDir/core/src/main/resources/web")
    }

    register<Exec>("npmInstall") {
        println("Installing npm dependencies...")
        workingDir(projectDir.resolve("webmap"))
        commandLine("npm.cmd", "install")
    }

    register<Exec>("npmBuild") {
        dependsOn(named("npmInstall"))
        println("Building webmap...")
        workingDir(projectDir.resolve("webmap"))
        commandLine("npm.cmd", "run", "build")
    }
}

allprojects {
    if (name == "WebMap") {
        return@allprojects
    }

    apply(plugin = "java")

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }

    repositories {
        mavenCentral()
        maven("https://jitpack.io")
    }

    dependencies {
        implementation("com.github.ben-manes.caffeine", "caffeine", "3.1.5")
        implementation("com.github.Querz", "NBT", "6.1")
        implementation("com.github.Carleslc.Simple-YAML", "Simple-Yaml", "1.8.3")
        implementation("io.undertow", "undertow-core", "2.3.5.Final")
        implementation("org.jboss.xnio", "xnio-nio", "3.8.8.Final")

        // provided by mojang
        compileOnly("com.google.code.gson", "gson", "2.10.1")
    }

    tasks {
        jar {
            archiveBaseName.set(if (rootProject.name == project.name) rootProject.name else "${rootProject.name}-${project.name}")
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
        }
    }
}
