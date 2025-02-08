plugins {
    `java-library`
    `maven-publish`
    id("com.gradleup.shadow") version "8.3.5"
}

group = "dev.oribuin"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17

    withJavadocJar()
    withSourcesJar()
    disableAutoTargetJvm()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}


//compileJava {
//    options.compilerArgs += ["-parameters"]
//    options.fork = true
//    options.encoding = "UTF-8"
//}
//
repositories {
    mavenCentral()
    mavenLocal()

    maven("https://libraries.minecraft.net")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.rosewooddev.io/repository/public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://repo.mattstudios.me/artifactory/public/")
}

dependencies {
    api("dev.rosewood:rosegarden:1.4.4")
    api("dev.triumphteam:triumph-gui:3.1.10") {  // https://mf.mattstudios.me/triumph-gui/introduction
        exclude(group = "com.google.code.gson", module = "gson") // Remove GSON, Already included in spigot api
        exclude(group = "net.kyori", module = "*") // Remove kyori
    }

    compileOnly("io.papermc.paper:paper-api:1.21.3-R0.1-SNAPSHOT")
    compileOnly("com.mojang:authlib:1.5.21")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")

    implementation("net.objecthunter:exp4j:0.4.8")
    implementation("com.jeff-media:MorePersistentDataTypes:2.4.0")
}

tasks {
    compileJava {
        this.options.compilerArgs.add("-parameters")
        this.options.isFork = true
        this.options.encoding = "UTF-8"
    }

    shadowJar {
        this.archiveClassifier.set("")

        this.relocate("dev.rosewood.rosegarden", "${project.group}.fishing.libs.rosegarden")
        this.relocate("com.jeff_media.morepersistentdatatypes", "${project.group}.fishing.libs.pdt")
        this.relocate("net.objecthunter.exp4j", "${project.group}.fishing.libs.exp4j")
        this.relocate("dev.triumphteam.gui", "${project.group}.fishing.libs.triumphgui")

        // rosegarden relocation
        this.relocate("com.zaxxer", "${project.group}.fishing.libs.hikari")
        this.relocate("org.slf4j", "${project.group}.fishing.libs.slf4j")
    }

    processResources {
        // TODO: only expand version in plugin.yml
        this.expand("version" to project.version)
    }

    javadoc {
        this.options {
            this as StandardJavadocDocletOptions

            this.links("https://jd.papermc.io/paper/1.21.4/")
            this.links("https://www.javadoc.io/doc/dev.triumphteam/triumph-gui/3.1.10/")

            // Exclude unnecessary classes from javadocs
            this.excludeDocFilesSubDir("command")
            this.excludeDocFilesSubDir("database")
            this.excludeDocFilesSubDir("listener")
            this.excludeDocFilesSubDir("gui")

            // encoding options
            this.addStringOption("encoding", "UTF-8")
            this.addStringOption("docencoding", "UTF-8")
            this.addStringOption("charset", "UTF-8")
            this.addStringOption("locale", "en_US")
        }
    }

    publishing {
        publications {
            create("shadow", MavenPublication::class) {
                project.shadow.component(this)
                this.artifactId = "fishing"
                this.pom.name.set("fishing")
            }
        }

        repositories {
            val version = project.version as String
            val mavenUser = project.properties["mavenUser"] as String?
            val mavenPassword = project.properties["mavenPassword"] as String?

            if (mavenUser != null && mavenPassword != null) {
                maven {
                    credentials {
                        username = mavenUser
                        password = mavenPassword
                    }

                    val releasesRepoUrl = "https://repo.rosewooddev.io/repository/public-releases/"
                    val snapshotsRepoUrl = "https://repo.rosewooddev.io/repository/public-snapshots/"
                    url = uri(if (version.endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
                }
            }
        }
    }
    build {
        this.dependsOn(javadoc)
        this.dependsOn(shadowJar)
    }
}