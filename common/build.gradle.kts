group = "net.pl3x.map.api"
version = rootProject.version
description = "Pl3xMap API"

val undertowVersion: String by rootProject

dependencies {
    implementation("io.undertow", "undertow-core", undertowVersion)
    implementation("com.github.Carleslc.Simple-YAML", "Simple-Yaml", "1.8.1")
}

tasks {
    shadowJar {
        listOf(
            "io.leangen.geantyref",
            //"io.undertow", // do not relocate!
            "org.jboss.logging",
            "org.jboss.threads",
            "org.wildfly.client",
            "org.wildfly.common",
            //"org.xnio", // do not relocate!
        ).forEach { relocate(it, "thirdparty.$it") }
    }
    reobfJar {
        // do not output this to the root build dir
        outputJar.set(layout.buildDirectory.file("libs/${project.name}-${project.version}.jar"))
    }
}
