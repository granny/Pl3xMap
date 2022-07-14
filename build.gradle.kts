plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.3.7"
    id("com.github.johnrengelman.shadow") version "7.1.2" apply false
}

version = "2.0-SNAPSHOT"

dependencies {
    paperDevBundle("1.19-R0.1-SNAPSHOT")
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

tasks.register<Exec>("npmBuild") {
    println("Building webmap...")
    workingDir(projectDir.resolve("webmap"))
    commandLine("npm", "run", "build")
}

subprojects {
    if (name == "Pl3xMap") {
        apply(plugin = "com.github.johnrengelman.shadow")
    }
    if (name == "Pl3xMap" || name == "InhabitedRenderer") {
        apply(plugin = "java-library")
        apply(plugin = "io.papermc.paperweight.userdev")

        java {
            toolchain.languageVersion.set(JavaLanguageVersion.of(17))
        }

        repositories {
            mavenCentral()
        }

        dependencies {
            paperDevBundle("1.19-R0.1-SNAPSHOT")
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
    }
}
