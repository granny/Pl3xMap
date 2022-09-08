group = "net.pl3x.map.addon.webp"
version = rootProject.version
description = "Pl3xMap addon that adds webp tile image support"

val scrimageVersion: String by rootProject

dependencies {
    compileOnly(project(":Common"))
    implementation("com.sksamuel.scrimage", "scrimage-core", scrimageVersion)
    implementation("com.sksamuel.scrimage", "scrimage-webp", scrimageVersion)
}
