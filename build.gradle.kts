plugins {
	id("net.fabricmc.fabric-loom-remap").version("1.14-SNAPSHOT")
	id("maven-publish")
    id("org.jetbrains.kotlin.jvm").version("2.2.21")
}

loom {
    runs {
        register("datagen") {
            client()
            name = "Data Generation"

            vmArg("-Dfabric-api.datagen")
            vmArg("-Dfabric-api.datagen.modid=${BuildConfig.modId}")
            vmArg("-Dfabric-api.datagen.output-dir=${project.file("src/main/generated")}")
            runDir("build/datagen")

            ideConfigGenerated(true)
        }
        register("clientMacuguita") {
            client()
            name = "Minecraft Client macuguita"
            programArgs.add("--username=macuguita")
            programArgs.add("--uuid=0e56050b-ee27-478a-a345-d2b384919081")
        }
        configureEach {
            if (name == "client") {
                programArgs.add("--username=Ladybrine")
                programArgs.add("--uuid=5d66606c-949c-47ce-ba4c-a1b9339ba3c8")
            }
        }
    }
    if (project.file("src/main/resources/${BuildConfig.modId}.classtweaker").exists()) {
        accessWidenerPath = project.file("src/main/resources/${BuildConfig.modId}.classtweaker")
    }
}

sourceSets {
    main {
        resources.srcDir("src/main/generated")
        resources.exclude(".cache")
    }
}

version = BuildConfig.modVersion
group = BuildConfig.mavenGroup

base {
    archivesName.set(BuildConfig.modId)
}

repositories {
    val exclusiveRepos = listOf(
        Triple("Shedaniel", "https://maven.shedaniel.me/", listOf("me.shedaniel.cloth")),
        Triple("TerraformersMC", "https://maven.terraformersmc.com/", listOf("com.terraformersmc", "dev.emi")),
        Triple("Modrinth", "https://api.modrinth.com/maven", listOf("maven.modrinth")),
        Triple("ParchmentMC", "https://maven.parchmentmc.org", listOf("org.parchmentmc.data")),
        Triple("Sleeping town", "https://repo.sleeping.town/", listOf("folk.sisby")),
    )

    exclusiveRepos.forEach { (name, url, groups) ->
        exclusiveContent {
            forRepository {
                maven {
                    this.name = name
                    setUrl(url)
                }
            }
            if (groups.isNotEmpty())
                filter {
                    groups.forEach { includeGroupByRegex(it) }
                }
        }
    }
    mavenCentral()
}

dependencies {
    minecraft("com.mojang:minecraft:${BuildConfig.minecraftVersion}")
	mappings(loom.layered {
        officialMojangMappings()
        BuildConfig.parchmentMappings?.let {
            parchment("org.parchmentmc.data:parchment-${BuildConfig.minecraftVersion}:${it}@zip")
        }
    })
    modImplementation("net.fabricmc:fabric-loader:${BuildConfig.loaderVersion}")

    modImplementation("net.fabricmc.fabric-api:fabric-api:${BuildConfig.fabricVersion}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${BuildConfig.fabricKotlinVersion}")
    include("net.fabricmc:fabric-language-kotlin:${BuildConfig.fabricKotlinVersion}")

    modImplementation("maven.modrinth:macu-lib:${BuildConfig.maculibVersion}-${BuildConfig.minecraftVersion}-fabric"){
        exclude("net.fabricmc.fabric-api")
        include("maven.modrinth:macu-lib:${BuildConfig.maculibVersion}-${BuildConfig.minecraftVersion}-fabric")
    }

    implementation("folk.sisby:kaleido-config:0.3.3+1.3.2")
    include("folk.sisby:kaleido-config:0.3.3+1.3.2")
}

tasks.register<net.fabricmc.loom.task.FabricModJsonV1Task>("genModJson") {
    outputFile = project.file("src/main/resources/fabric.mod.json")

    json {
        modId = BuildConfig.modId
        version = BuildConfig.modVersion
        name = BuildConfig.modName
        description = BuildConfig.description
        author("macuguita") {
            contactInformation = mapOf(
                "discord" to "macuguita"
            )
        }
        contactInformation.set(mapOf(
            "homepage" to "https://macuguita.com",
        ))
        licenses = listOf(BuildConfig.license)
        if (project.file("src/main/resources/assets/${BuildConfig.modId}/icon.png").exists()) {
            icon("assets/${BuildConfig.modId}/icon.png")
        }
        if (project.file("src/main/resources/${BuildConfig.modId}.mixins.json").exists()) {
            mixin("${BuildConfig.modId}.mixins.json")
        }
        if (project.file("src/main/resources/${BuildConfig.modId}.classtweaker").exists()) {
            accessWidener = "${BuildConfig.modId}.classtweaker"
        }
        environment = "*"

        entrypoint("main", "com.macuguita.island.common.Island", "kotlin")
        entrypoint("client", "com.macuguita.island.client.ClientEntrypoint", "kotlin")
        entrypoint("server", "com.macuguita.island.server.ServerEntrypoint", "kotlin")
        entrypoint("fabric-datagen", "com.macuguita.island.datagen.DatagenEntrypoint", "kotlin")

        depends("fabricloader", ">=${BuildConfig.loaderVersion}")
        depends("minecraft", BuildConfig.minecraftVersionRange)
        depends("java", ">=21")
        depends("fabric-api", "*")
        depends("fabric-language-kotlin", "*")
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}


tasks.withType<JavaCompile>().configureEach {
    options.release.set(21)
}

java {
    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.processResources {
    dependsOn(tasks.named("genModJson"))
}

tasks.named("sourcesJar") {
    dependsOn(tasks.named("genModJson"))
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${BuildConfig.modId}"}
    }
}
