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
    "afybroker-core",
    "afybroker-server",
    "afybroker-server-bootstrap",
    "afybroker-client",
    "afybroker-bukkit:folia-support",
    "afybroker-bukkit",
    "afybroker-bungee",
    "afybroker-velocity"
).forEach { include(it) }