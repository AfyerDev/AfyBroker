plugins {
    alias(libs.plugins.shadow)
}

dependencies {
    compileOnly(libs.spigot.api)
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
    filesMatching("plugin.yml") {
        expand(props)
    }
}
