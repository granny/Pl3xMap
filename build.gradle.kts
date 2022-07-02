plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.3.7"
}

subprojects {
    apply(plugin = "io.papermc.paperweight.userdev")
}

dependencies {
    paperDevBundle("1.19-R0.1-SNAPSHOT")
}
