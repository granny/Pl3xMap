group = "net.pl3x.map.addon"
version = rootProject.version
description = "Pl3xMap addon that renders chunk inhabited times as a heatmap"

dependencies {
    compileOnly(project(":Pl3xMap"))
    implementation("org.bstats:bstats-bukkit:3.0.0")
}

tasks {
    shadowJar {
        from(rootProject.projectDir.resolve("LICENSE"))
        minimize()
        listOf(
            "org.bstats"
        ).forEach { relocate(it, "${project.group}.plugin.lib.$it") }
    }
    reobfJar {
        outputJar.set(rootProject.layout.buildDirectory.file("libs/${project.name}-${project.version}.jar"))
    }
    assemble {
        dependsOn(reobfJar)
    }
}
