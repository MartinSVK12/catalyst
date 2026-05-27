import com.smushytaco.lwjgl_gradle.Preset
plugins {
	alias(libs.plugins.loom)
	alias(libs.plugins.lwjgl)
    java
}
val modVersion: String = project.properties["mod_version"] as String
val modGroup: String = project.properties["mod_group"] as String
val modName: String = project.properties["mod_name"] as String

val javaVersion: Provider<Int> = libs.versions.java.map { it.toInt() }

base.archivesName = modName
group = modGroup
version = modVersion

loom {
	customMinecraftMetadata.set("https://downloads.betterthanadventure.net/bta-client/${libs.versions.btaChannel.get()}/${libs.versions.bta.get()}/manifest.json")
	accessWidenerPath.set(file("src/main/resources/catalyst-screens.classtweaker"))
}

lwjgl {
	version = libs.versions.lwjgl
	implementation(Preset.MINIMAL_OPENGL)
}

dependencies {
    minecraft("::${libs.versions.bta.get()}")
	include(libs.commonsLang3)
	implementation(project(":catalyst-core"))
	implementation(project(":catalyst-fluids"))
}

tasks {
	//println(project(":catalyst-core").properties["mod_version"] as String)
	processResources {
		val resourceMap = mapOf(
			"version" to modVersion,
			"loader" to libs.versions.loader.get(),
			"halplibe" to libs.versions.halplibe.get(),
			"java" to libs.versions.java.get(),
			"modmenu" to libs.versions.modMenu.get(),
			"core" to project(":catalyst-core").properties["mod_version"] as String
		)
		inputs.properties(resourceMap)
		filesMatching("fabric.mod.json") { expand(resourceMap) }
		filesMatching("**/*.mixins.json") { expand(resourceMap.filterKeys { it == "java" }) }
	}
}
// Removes LWJGL2 dependencies
configurations.configureEach { exclude(group = "org.lwjgl.lwjgl") }

java {
	toolchain {
		languageVersion = javaVersion.map { JavaLanguageVersion.of(it) }
		vendor = JvmVendorSpec.ADOPTIUM
	}
	sourceCompatibility = JavaVersion.toVersion(javaVersion.get())
	targetCompatibility = JavaVersion.toVersion(javaVersion.get())
	withSourcesJar()
}

tasks {
	withType<JavaCompile>().configureEach {
		options.encoding = "UTF-8"
		sourceCompatibility = javaVersion.get().toString()
		targetCompatibility = javaVersion.get().toString()
		if (javaVersion.get() > 8) options.release = javaVersion
	}
}
