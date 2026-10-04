plugins {
    `maven-publish`

    kotlin("jvm") version "2.4.0-Beta1"
}

group = "com.github.17gon"
version = "0.2.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.2.build.+")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.spongepowered:configurate-yaml:4.1.2")
    implementation("org.spongepowered:configurate-extra-kotlin:4.1.2")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
}

kotlin { jvmToolchain(25) }

tasks {
    processResources {
        val props = mapOf("version" to version)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}
publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "adapterslib"
            from(components["java"])
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/17gon/AdaptersLib")

            credentials {
                username = findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
                password = findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}