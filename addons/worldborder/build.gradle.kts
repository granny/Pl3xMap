group = "net.pl3x.map.addon.worldborder"
version = rootProject.version
description = "Pl3xMap addon that adds world border marker to maps"

repositories {
    maven("https://repo.codemc.io/repository/maven-releases")
    maven("https://maven.addstar.com.au/artifactory/ext-release-local")
}

dependencies {
    compileOnly(project(":Pl3xMap"))
    implementation("org.popcraft", "chunky-common", "1.3.29")
    implementation("org.popcraft", "chunkyborder-common", "1.0.71")
    implementation("com.wimbli.WorldBorder", "WorldBorder", "1.9.10 (beta)")
}
