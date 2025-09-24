plugins {
    id("java")
    id("application")
}

group = "com.todoapp"
version = "1.0.0"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    // Javalin framework - VERSÃO MAIS ATUAL
    implementation("io.javalin:javalin:6.6.0")

    // Jackson para JSON - VERSÕES ATUALIZADAS
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.18.2")

    // Logging - VERSÃO ATUALIZADA
    implementation("org.slf4j:slf4j-simple:2.0.16")

    // Testes - VERSÕES ATUALIZADAS
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.3")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.11.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.11.3")
}

application {
    mainClass.set("com.todoapp.TodoApp")
}

tasks.test {
    useJUnitPlatform()
}