import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    application
    kotlin("jvm") version "1.5.0"
    kotlin("plugin.serialization") version "1.5.0"
}
group = "dev.engel"
version = "0.1-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    maven {
        url = uri("https://dl.bintray.com/kotlin/ktor")
    }
    maven {
        url = uri("https://dl.bintray.com/kotlin/kotlinx")
    }
}
dependencies {
    val ktorVersion = "1.5.3"
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-serialization:$ktorVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-serialization:$ktorVersion")

    implementation("ch.qos.logback:logback-classic:1.2.3")

    // Google Cloud
    implementation(platform("com.google.cloud:libraries-bom:20.1.0"))
    implementation("com.google.cloud:google-cloud-datastore")

    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.6.0")

    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.0")

    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
    testImplementation("io.ktor:ktor-client-mock:$ktorVersion")

    testImplementation(platform("io.strikt:strikt-bom:0.31.0"))
    testImplementation("io.strikt:strikt-jvm")
    testImplementation("io.strikt:strikt-mockk")

    testImplementation("io.mockk:mockk:1.11.0")
}
tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "12"
}

tasks.withType<Test> {
    useJUnitPlatform()
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    freeCompilerArgs = listOf("-Xinline-classes")
}

application {
    mainClassName = "io.ktor.server.netty.EngineMain"
}