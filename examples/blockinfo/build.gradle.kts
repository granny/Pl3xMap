group = "net.pl3x.map.addon"
version = rootProject.version
description = "Pl3xMap addon that adds block info on hover"

dependencies {
    compileOnly(project(":Pl3xMap"))
    implementation("org.bstats:bstats-bukkit:3.0.0")

    val brotli = "1.7.1"
    implementation("com.aayushatharva.brotli4j:brotli4j:$brotli")
    implementation("com.aayushatharva.brotli4j:native-windows-x86_64:$brotli")
    implementation("com.aayushatharva.brotli4j:native-osx-x86_64:$brotli")
    implementation("com.aayushatharva.brotli4j:native-linux-aarch64:$brotli")
    implementation("com.aayushatharva.brotli4j:native-linux-x86_64:$brotli")
}

tasks {
    shadowJar {
        from(rootProject.projectDir.resolve("LICENSE"))
        //minimize()
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
