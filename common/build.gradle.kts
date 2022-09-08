group = "net.pl3x.map.api"
version = rootProject.version
description = "Pl3xMap API"

val cloudVersion: String by rootProject
val undertowVersion: String by rootProject

dependencies {
    implementation("cloud.commandframework", "cloud-core", cloudVersion)
    implementation("cloud.commandframework", "cloud-brigadier", cloudVersion)
    implementation("cloud.commandframework", "cloud-minecraft-extras", cloudVersion) {
        exclude("net.kyori")
    }
    implementation("net.kyori", "adventure-api", "4.11.0")
    implementation("net.kyori", "adventure-text-minimessage", "4.11.0")
    implementation("com.github.Carleslc.Simple-YAML", "Simple-Yaml", "1.8.1")
    implementation("io.undertow", "undertow-core", undertowVersion)
}

tasks {
    reobfJar {
        // do not output this to the root build dir
        outputJar.set(layout.buildDirectory.file("libs/${project.name}-${project.version}.jar"))
    }
}
