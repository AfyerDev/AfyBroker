plugins {
    alias(libs.plugins.shadow)
}

dependencies {
    compileOnly(libs.bungeecord.api)
    compileOnly(libs.bungeecord.proxy)
    implementation(project(":afybroker-client"))
    implementation("org.bstats:bstats-bungeecord:3.0.2")
}

tasks.assemble {
    dependsOn(tasks.shadowJar)
}

tasks.shadowJar {
    relocate("org.bstats", "net.afyer.afybroker.bungee.bstats")
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