pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

rootProject.name = "Pl3xMap"

// Main project

setupSubproject("Common") {
    projectDir = file("common")
}
setupSubproject("Paper") {
    projectDir = file("paper")
}
setupSubproject("WebMap") {
    projectDir = file("webmap")
}

// Addons

setupSubproject("FlowerMapAddon") {
    projectDir = file("addons/flowermap")
}
setupSubproject("HeightmapsAddon") {
    projectDir = file("addons/heightmaps")
}
setupSubproject("InhabitedAddon") {
    projectDir = file("addons/inhabited")
}
setupSubproject("MarkerTestAddon") {
    projectDir = file("addons/markertest")
}
setupSubproject("WebpAddon") {
    projectDir = file("addons/webp")
}
setupSubproject("WorldBorderAddon") {
    projectDir = file("addons/worldborder")
}

inline fun setupSubproject(name: String, block: ProjectDescriptor.() -> Unit) {
    include(name)
    project(":$name").apply(block)
}
