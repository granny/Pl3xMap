group = "net.pl3x.map.addon.worldborder"
version = rootProject.version
description = "Pl3xMap addon that adds world border marker to maps"

dependencies {
    compileOnly(project(":Pl3xMap"))
}

tasks {
    reobfJar {
        outputJar.set(rootProject.layout.buildDirectory.file("libs/${project.name}-${project.version}.jar"))
    }
    assemble {
        dependsOn(reobfJar)
    }
}
