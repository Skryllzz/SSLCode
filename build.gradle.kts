plugins {
    kotlin("jvm")
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    maven(url = "https://repo1.maven.org/maven2/") // Maven Central
    maven(url = "https://jitpack.io")
    flatDir {
        dirs("lib")
    }
}

dependencies {
    implementation(":toml4j-0.7.3-SNAPSHOT")
    implementation(":api-model-1.0")

    implementation("pl.allegro.finance:tradukisto:2.5.0")
    implementation("org.apache.commons:commons-lang3:3.13.0")
    implementation("com.google.guava", "guava", "32.1.3-jre")

    val log4j = "2.20.0"
    implementation("org.apache.logging.log4j", "log4j-core", log4j)
    implementation("org.apache.logging.log4j", "log4j-1.2-api", log4j)
    implementation("org.apache.logging.log4j", "log4j-slf4j-impl", log4j)

    implementation("com.jolbox", "bonecp", "0.8.0.RELEASE")
    implementation("org.jsoup:jsoup:1.16.1")
    implementation("mysql", "mysql-connector-java", "8.0.33")
    implementation("com.google.code.gson", "gson", "2.10.1")

    val javacord = "3.4.0"
    implementation("org.javacord:javacord-api:$javacord")
    implementation("org.javacord:javacord-core:$javacord")

    val sdcf4j = "v1.0.10"
    implementation("de.btobastian.sdcf4j:sdcf4j-core:$sdcf4j")
    implementation("de.btobastian.sdcf4j:sdcf4j-javacord:$sdcf4j")

    implementation("it.unimi.dsi", "fastutil", "8.5.12")
    implementation("io.github.classgraph", "classgraph", "4.8.162")
    implementation("commons-io", "commons-io", "2.14.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("org.apache.commons", "commons-text", "1.10.0")
    implementation("org.apache.commons", "commons-compress", "1.24.0")
    implementation("com.fasterxml.jackson.dataformat", "jackson-dataformat-yaml", "2.15.3")
    implementation("net.lingala.zip4j", "zip4j", "2.11.5")
    implementation("com.esotericsoftware", "kryo", "5.5.0")
    implementation("org.apache.commons", "commons-pool2", "2.12.0")
    implementation("org.jctools:jctools-core:4.0.1")
    implementation("net.openhft:affinity:3.23.3")

    /* Netty */
    val nettyVer = "4.1.100.Final"
    val nettyIoUringVer = "0.0.23.Final"

    implementation("io.netty", "netty-all", nettyVer)
    runtimeOnly("io.netty:netty-transport-native-epoll:$nettyVer:linux-aarch_64")
    runtimeOnly("io.netty:netty-transport-native-epoll:$nettyVer:linux-x86_64")
    runtimeOnly("io.netty:netty-transport-native-kqueue:$nettyVer:osx-x86_64")

    implementation("io.netty.incubator:netty-incubator-transport-native-io_uring:$nettyIoUringVer")
    runtimeOnly("io.netty.incubator:netty-incubator-transport-native-io_uring:$nettyIoUringVer:linux-aarch_64")
    runtimeOnly("io.netty.incubator:netty-incubator-transport-native-io_uring:$nettyIoUringVer:linux-x86_64")

    implementation("org.jetbrains:annotations:24.0.1")

    val deviousVersion = "1.0.16"
    implementation(":runelite-api-$deviousVersion")
    implementation(":cache-$deviousVersion")

    val javaparser = "3.25.5"
    implementation("com.github.javaparser:javaparser-core:$javaparser")
    implementation("com.github.javaparser:javaparser-symbol-solver-core:$javaparser")

    implementation("org.apache.ant:ant:1.10.14")
}

kotlin {
    jvmToolchain(19)
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

val defaultJvmArgs = arrayOf(
    //"-XX:+UseZGC",
    "-Xmx4g",
    "-Xms2g",
    "-XX:-OmitStackTraceInFastThrow",
    "--add-opens=java.base/java.time=ALL-UNNAMED",
    "--add-exports=java.base/sun.nio.ch=ALL-UNNAMED",
    "--add-opens=java.base/java.lang=ALL-UNNAMED",
    "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED",
    "--add-opens=java.base/java.io=ALL-UNNAMED",
    "--add-exports=jdk.unsupported/sun.misc=ALL-UNNAMED"
)

val defaultMainClassName = "com.zenyte.GameEngine"

fun execTask(
    name: String, mainClassName: String = defaultMainClassName, configure: (JavaExecSpec.() -> Unit)? = null
) = tasks.register(name, JavaExec::class.java) {
    group = ApplicationPlugin.APPLICATION_GROUP

    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set(mainClassName)
    jvmArgs(*defaultJvmArgs)

    enableAssertions = true
    if (hasProperty("args")) {
        val argsProperty = property("args")
        val argsList = argsProperty as List<*>
        if (argsList.isNotEmpty()) {
            args(argsList)
        }
    }

    configure?.invoke(this)
}

execTask("runMain") {
    args = listOf("main")
}

execTask("runOfflineDev") {
    args = listOf("offline_dev")
}

execTask("typeParser", "mgi.tools.parser.TypeParser") {
    args = listOf("--unzip")
}

execTask("mapPacker", "com.zenyte.game.util.MapPacker")

execTask("cachePacker", "com.zenyte.openrs.cache.CachePacking")

execTask("idTransform", "org.jire.zenytersps.idtransform.IDTransform")

application {
    mainClass.set(defaultMainClassName)
    applicationDefaultJvmArgs += defaultJvmArgs
}

tasks.shadowJar {
    isZip64 = true
}