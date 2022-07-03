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
setupSubproject("BiomeRenderer") {
    projectDir = file("examples/biomes")
}
setupSubproject("InhabitedRenderer") {
    projectDir = file("examples/inhabited")
}

inline fun setupSubproject(name: String, block: ProjectDescriptor.() -> Unit) {
    include(name)
    project(":$name").apply(block)
}
