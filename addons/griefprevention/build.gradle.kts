group = "net.pl3x.map.addon.griefprevention"
version = rootProject.version
description = "Adds GriefPrevention claims to the map"

dependencies {
    compileOnly(project(":Common"))
    compileOnly("com.github.TechFortress", "GriefPrevention", "16.18")
}
