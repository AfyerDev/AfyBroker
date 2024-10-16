plugins {
    java
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "7.1.2" apply false
}

subprojects {

    apply(plugin = "java")
    apply(plugin = "maven-publish")

    group = "net.afyer.afybroker"
    version = "2.2"

    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://repo.tabooproject.org/repository/releases/")
        maven("https://oss.sonatype.org/content/groups/public/")
        maven("https://repo.papermc.io/repository/maven-public/")
    }

    dependencies {
        testImplementation ("org.junit.jupiter:junit-jupiter-api:5.8.2")
        testRuntimeOnly ("org.junit.jupiter:junit-jupiter-engine:5.8.2")

        compileOnly ("org.projectlombok:lombok:1.18.24")
        annotationProcessor ("org.projectlombok:lombok:1.18.24")

        testCompileOnly ("org.projectlombok:lombok:1.18.24")
        testAnnotationProcessor ("org.projectlombok:lombok:1.18.24")
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    tasks.withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
    }

    publishing {
        repositories {
            maven("http://101.34.76.125:8081/repository/maven-releases/") {
                isAllowInsecureProtocol = true
                credentials {
                    username = project.findProperty("afyerUser").toString()
                    password = project.findProperty("afyerPassword").toString()
                }
            }
        }
    }
}



tasks.withType<Jar> {
    enabled = false
}





