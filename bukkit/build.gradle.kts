plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "1.3.11"
}

group = "net.pl3x.map.bukkit"
version = rootProject.version

dependencies {
    compileOnly(project(":Core"))

    paperDevBundle("1.19.4-R0.1-SNAPSHOT")
}

tasks {
    assemble {
        dependsOn(reobfJar)
        doLast {
            delete(fileTree("$buildDir").matching {
                include("**/*-dev*.jar")
            })
        }
    }

    reobfJar {
        outputJar.set(file("$buildDir/libs/${rootProject.name}-${project.name}-${project.version}.jar"))
    }

    processResources {
        filesMatching("plugin.yml") {
            expand("version" to project.version)
        }
    }
}
