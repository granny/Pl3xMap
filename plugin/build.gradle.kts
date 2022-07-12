group = "net.pl3x.map"
version = rootProject.version
description = "Minimalistic and lightweight world map viewer for Paper servers"

dependencies {
    implementation("io.undertow:undertow-core:2.2.17.Final")
    implementation("com.sksamuel.scrimage:scrimage-core:4.0.31")
    implementation("com.sksamuel.scrimage:scrimage-webp:4.0.31")
    implementation("cloud.commandframework:cloud-paper:1.7.0")
    implementation("cloud.commandframework:cloud-minecraft-extras:1.7.0")
    implementation("org.bstats:bstats-bukkit:3.0.0")
}

tasks {
    shadowJar {
        // we'll provide our own up-to-date webp binaries, thanks
        exclude(
            "dist_webp_binaries/*",
            "dist_webp_binaries/linux/*",
            "dist_webp_binaries/mac/*",
            "dist_webp_binaries/window/*"
        )
        from(rootProject.projectDir.resolve("LICENSE"))
        minimize {
            // does not like being minimized _or_ relocated (xnio errors)
            exclude(dependency("io.undertow:.*:.*"))
        }
        listOf(
            "io.leangen.geantyref",
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
