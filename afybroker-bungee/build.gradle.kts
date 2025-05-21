plugins {
    alias(libs.plugins.shadow)
}

dependencies {
    compileOnly(libs.bungeecord.api)
    compileOnly(libs.bungeecord.proxy)
    implementation(project(":afybroker-client"))
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