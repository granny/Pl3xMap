plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.3.8"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("com.modrinth.minotaur") version "2.+"
}

val minecraftVersion: String by project
val paperVersion: String by project
val buildNum = System.getenv("GITHUB_RUN_NUMBER") ?: "SNAPSHOT"
project.version = "$minecraftVersion-$buildNum"

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
    into("$rootDir/common/src/main/resources/web")
    from("$rootDir/webmap/dist")
    include("**/*")
    into("$rootDir/common/src/main/resources/web")
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

        val projectName = if (name == "Paper") rootProject.name else project.name

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
            shadowJar {
                from(rootProject.projectDir.resolve("LICENSE"))
                exclude(
                    "META-INF/maven/**",
                    "META-INF/versions/**",
                    "META-INF/LICENSE.txt"
                )
                minimize {
                    // undertow does not like being minimized (UndertowLogger errors)
                    exclude(dependency("io.undertow:.*:.*"))
                }
            }

            reobfJar {
                outputJar.set(jar(projectName))
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
                filesMatching(
                    listOf(
                        "addon.yml",
                        "plugin.yml"
                    )
                ) {
                    expand(
                        "name" to projectName,
                        "group" to project.group,
                        "version" to project.version,
                        "description" to project.description
                    )
                }
            }
        }
    }
}

tasks {
    modrinth {
        token.set(System.getenv("MODRINTH_TOKEN"))
        projectId.set("pl3xmap")
        versionName.set("${project.version}")
        versionNumber.set("${project.version}")
        versionType.set("alpha")
        uploadFile.set(jar(rootProject.name))
        additionalFiles.set(
            listOf(
                jar(project(":FlowerMapAddon").name),
                jar(project(":HeightmapsAddon").name),
                jar(project(":InhabitedAddon").name),
                jar(project(":WebpAddon").name),
                jar(project(":WorldBorderAddon").name)
            )
        )
        gameVersions.addAll(listOf(minecraftVersion))
        loaders.addAll(listOf("paper", "purpur"))
        changelog.set(System.getenv("COMMIT_MESSAGE"))
    }
}

fun jar(name: String): RegularFile {
    return rootProject.layout.buildDirectory.file("libs/${name}-${project.version}.jar").get()
}
