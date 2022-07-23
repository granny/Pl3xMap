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
setupSubproject("BlockInfo") {
    projectDir = file("examples/blockinfo")
}
setupSubproject("InhabitedRenderer") {
    projectDir = file("examples/inhabited")
}
setupSubproject("WebMap") {
    projectDir = file("webmap")
}

inline fun setupSubproject(name: String, block: ProjectDescriptor.() -> Unit) {
    include(name)
    project(":$name").apply(block)
}
