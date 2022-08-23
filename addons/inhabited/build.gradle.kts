group = "net.pl3x.map.addon.inhabited"
version = rootProject.version
description = "Pl3xMap addon that renders chunk inhabited times as a heatmap"

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
