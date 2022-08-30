group = "net.pl3x.map"
version = rootProject.version
description = "Minimalistic and lightweight world map viewer for Paper servers"

val bstatsVersion: String by rootProject
val cloudVersion: String by rootProject
val undertowVersion: String by rootProject

dependencies {
    implementation("io.undertow", "undertow-core", undertowVersion)
    implementation("cloud.commandframework", "cloud-paper", cloudVersion)
    implementation("cloud.commandframework", "cloud-minecraft-extras", cloudVersion) {
        exclude("net.kyori")
    }
    implementation("org.bstats", "bstats-bukkit", bstatsVersion)
}

tasks {
    shadowJar {
        exclude(
            "META-INF/maven/**",
            "META-INF/versions/**",
            "META-INF/LICENSE.txt"
        )
        minimize {
            // undertow does not like being minimized (UndertowLogger errors)
            exclude(dependency("io.undertow:.*:.*"))
        }
        listOf(
            "cloud.commandframework",
            "io.leangen.geantyref",
            //"io.undertow", // do not relocate!
            "org.bstats",
            "org.jboss.logging",
            "org.jboss.threads",
            "org.wildfly.client",
            "org.wildfly.common",
            //"org.xnio", // do not relocate!
        ).forEach { relocate(it, "thirdparty.$it") }
    }
}
