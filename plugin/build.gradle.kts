group = "net.pl3x.map"
version = rootProject.version
description = "Minimalistic and lightweight world map viewer for Paper servers"

dependencies {
    implementation("io.undertow:undertow-core:2.2.19.Final")
    implementation("cloud.commandframework:cloud-paper:1.7.1")
    implementation("cloud.commandframework:cloud-minecraft-extras:1.7.1") {
        exclude("net.kyori")
    }
    implementation("org.bstats:bstats-bukkit:3.0.0")
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
