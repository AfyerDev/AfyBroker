plugins {
    id("com.github.johnrengelman.shadow")
}

dependencies {
    compileOnly("net.md-5:bungeecord-api:1.18-R0.1-SNAPSHOT")
    implementation(project(":afybroker-client")) {
        exclude(group = "io.netty", module = "netty-all")
        exclude(group = "org.slf4j", module = "slf4j-api")
        exclude(group = "com.google.guava", module = "guava")
    }
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveClassifier.set("")
}

tasks.build {
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