plugins {
    id("com.github.johnrengelman.shadow")
}

dependencies {
    compileOnly("net.md-5:bungeecord-api:1.19-R0.1-SNAPSHOT")
    compileOnly("net.md-5:bungeecord-proxy:1.19-R0.1-SNAPSHOT")
    implementation(project(":afybroker-client")) {
        exclude(group = "io.netty", module = "netty-all")
        exclude(group = "org.slf4j", module = "slf4j-api")
        exclude(group = "com.google.guava", module = "guava")
        exclude(group = "org.jetbrains", module = "annotations")
    }
}

tasks.assemble {
    dependsOn(tasks.shadowJar)
}


tasks.processResources {
    val props = mapOf(
        "version" to project.version
    )
    inputs.properties(props)
    filesMatching("bungee.yml") {
        expand(props)
    }
}