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

setupSubproject("FlowerMap") {
    projectDir = file("addons/flowermap")
}
setupSubproject("Heightmaps") {
    projectDir = file("addons/heightmaps")
}
setupSubproject("Inhabited") {
    projectDir = file("addons/inhabited")
}
setupSubproject("MarkerTest") {
    projectDir = file("addons/markertest")
}
setupSubproject("Webp") {
    projectDir = file("addons/webp")
}
setupSubproject("WorldBorder") {
    projectDir = file("addons/worldborder")
}

inline fun setupSubproject(name: String, block: ProjectDescriptor.() -> Unit) {
    include(name)
    project(":$name").apply(block)
}
