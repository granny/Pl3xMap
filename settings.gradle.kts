pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

rootProject.name = "Pl3xMap"

setupSubproject("Pl3xMap") {
    projectDir = file("plugin")
}
setupSubproject("WebMap") {
    projectDir = file("webmap")
}

setupSubproject("FlowerMapAddon") {
    projectDir = file("addons/flowermap")
}
setupSubproject("HeightmapsAddon") {
    projectDir = file("addons/heightmaps")
}
setupSubproject("InhabitedAddon") {
    projectDir = file("addons/inhabited")
}
setupSubproject("WorldBorderAddon") {
    projectDir = file("addons/worldborder")
}

inline fun setupSubproject(name: String, block: ProjectDescriptor.() -> Unit) {
    include(name)
    project(":$name").apply(block)
}
