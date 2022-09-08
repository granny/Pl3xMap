group = "net.pl3x.map"
version = rootProject.version
description = "Minimalistic and lightweight world map viewer for Paper servers"

val bstatsVersion: String by rootProject
val cloudVersion: String by rootProject

dependencies {
    implementation(project(":Common"))
    implementation("cloud.commandframework", "cloud-paper", cloudVersion)
    implementation("cloud.commandframework", "cloud-minecraft-extras", cloudVersion) {
        exclude("net.kyori")
    }
    implementation("org.bstats", "bstats-bukkit", bstatsVersion)
}

tasks {
    shadowJar {
        from(
            fileTree(project(":Common").projectDir).matching {
                include("LICENSE*")
            }
        )
        minimize {
            // undertow does not like being minimized (UndertowLogger errors)
            exclude(dependency("io.undertow:.*:.*"))
        }
        listOf(
            "cloud.commandframework",
            "org.bstats"
        ).forEach { relocate(it, "${project.group}.$it") }
    }
}
