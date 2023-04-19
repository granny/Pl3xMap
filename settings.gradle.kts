pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.minecraftforge.net/")
    }
}

rootProject.name = "Pl3xMap"

setup("Core")
setup("Bukkit")
setup("Fabric")
setup("Forge")
setup("WebMap")

fun setup(name: String) {
    subproject(name) {
        projectDir = file(name.toLowerCase())
    }
}

inline fun subproject(name: String, block: ProjectDescriptor.() -> Unit) {
    include(name)
    project(":$name").apply(block)
}
