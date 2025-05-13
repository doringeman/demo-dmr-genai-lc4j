plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    // https://docs.langchain4j.dev/get-started/
    implementation("dev.langchain4j:langchain4j:1.0.0-rc1")
    implementation("dev.langchain4j:langchain4j-open-ai:1.0.0-rc1")
    implementation("dev.langchain4j:langchain4j-easy-rag:1.0.0-beta4")
    implementation("dev.langchain4j:langchain4j-embeddings-all-minilm-l6-v2:1.0.0-beta4")

    // https://java.testcontainers.org/modules/docker_model_runner/
    implementation("com.github.testcontainers:testcontainers-java:main-SNAPSHOT")

    implementation("ch.qos.logback:logback-classic:1.5.13")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}