group = "net.pl3x.map.addon.webp"
version = rootProject.version
description = "Pl3xMap addon that adds webp tile image support"

dependencies {
    compileOnly(project(":Pl3xMap"))
    implementation("com.sksamuel.scrimage:scrimage-core:4.0.31")
    implementation("com.sksamuel.scrimage:scrimage-webp:4.0.31")
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
