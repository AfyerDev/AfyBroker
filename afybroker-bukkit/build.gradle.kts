plugins {
    alias(libs.plugins.shadow)
}

dependencies {
    compileOnly(libs.spigot.api)
    implementation(libs.bstats.bukkit)
    implementation(project(":afybroker-client"))
    implementation("org.bstats:bstats-bukkit:3.0.2")
}

tasks.assemble {
    dependsOn(tasks.shadowJar)
}

tasks.shadowJar {
    relocate("org.bstats", "net.afyer.afybroker.bukkit.bstats")
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
