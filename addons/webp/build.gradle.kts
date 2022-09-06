group = "net.pl3x.map.addon.webp"
version = rootProject.version
description = "Pl3xMap addon that adds webp tile image support"

val scrimageVersion: String by rootProject

dependencies {
    compileOnly(project(":Common"))
    implementation("com.sksamuel.scrimage", "scrimage-core", scrimageVersion)
    implementation("com.sksamuel.scrimage", "scrimage-webp", scrimageVersion)
}

tasks {
    shadowJar {
        exclude(
            "dist_webp_binaries/**",
            "META-INF/maven/**",
            "META-INF/LICENSE.txt",
            "META-INF/NOTICE.txt",
            "LICENSE.txt",
            "NOTICE.txt"
        )
        minimize()
        listOf(
            "ar.com.hjg.pngj",
            "com.adobe.internal.xmp",
            "com.drew",
            "com.sksamuel.scrimage",
            "com.twelvemonkeys.imageio.color",
            "org.apache.commons.io",
            "org.apache.commons.lang3",
            "profiles",
        ).forEach { relocate(it, "thirdparty.$it") }
    }
}
