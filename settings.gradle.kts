@file:Suppress("UnstableApiUsage")

rootProject.name = "afybroker"

pluginManagement {
    includeBuild("build-logic")
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

sequenceOf(
    "core",
    "server",
    "server-bootstrap",
    "client",
    "bukkit",
    "bungee",
    "velocity"
).forEach {
    val project = ":afybroker-$it"
    include(project)
}
