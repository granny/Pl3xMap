plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.3.8"
    id("com.github.johnrengelman.shadow") version "7.1.2" apply false
}

val minecraftVersion: String by project
val paperVersion: String by project
val buildNum = System.getenv("GITHUB_RUN_NUMBER") ?: "SNAPSHOT"
version = "$minecraftVersion-$buildNum"

dependencies {
    paperDevBundle(paperVersion)
}

tasks {
    // disable building jar for root project
    jar { enabled = false }
    build {
        dependsOn(named("copyWebmap"))
    }
}

tasks.register<Copy>("copyWebmap") {
    dependsOn(tasks.named("npmBuild"))
    println("Copying webmap...")
    from("$rootDir/webmap/public")
    include("**/*")
    exclude("tiles*/")
    into("$rootDir/plugin/src/main/resources/web")
    from("$rootDir/webmap/dist")
    include("**/*")
    into("$rootDir/plugin/src/main/resources/web")
}

tasks.register<Exec>("npmInstall") {
    println("Installing npm dependencies...")
    workingDir(projectDir.resolve("webmap"))
    commandLine("npm", "install")
}

tasks.register<Exec>("npmBuild") {
    dependsOn(tasks.named("npmInstall"))
    println("Building webmap...")
    workingDir(projectDir.resolve("webmap"))
    commandLine("npm", "run", "build")
}

subprojects {
    if (name != "WebMap") {
        apply(plugin = "java-library")
        apply(plugin = "io.papermc.paperweight.userdev")
        apply(plugin = "com.github.johnrengelman.shadow")

        java {
            toolchain.languageVersion.set(JavaLanguageVersion.of(17))
        }

        repositories {
            mavenCentral()
        }

        dependencies {
            paperDevBundle(paperVersion)
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
                filesMatching(listOf(
                    "addon.yml",
                    "plugin.yml"
                )) {
                    expand(
                        "name" to project.name,
                        "group" to project.group,
                        "version" to project.version,
                        "description" to project.description
                    )
                }
            }
        }
    }
}
