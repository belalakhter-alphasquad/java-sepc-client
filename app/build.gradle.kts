

plugins {
    // Apply the application plugin to add support for building a CLI application in Java.
    application
}

repositories {
    mavenCentral()
}

dependencies {

    implementation("com.google.guava:guava:30.1.1-jre")
    implementation(files("libs/SEPC-connector-3.8.0.jar"))
    implementation("log4j:log4j:1.2.17")
    implementation("org.agrona:agrona:1.20.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.0")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.13.0")
    implementation("mysql:mysql-connector-java:8.0.23")
    implementation("com.zaxxer:HikariCP:5.0.0")



}

application {
    mainClass.set("sepc.sample.App")
}

tasks.named<Jar>("jar") {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    manifest {
        attributes(
            "Main-Class" to "sepc.sample.App",
            "Add-Opens" to "java.base/sun.nio.ch",
            "Implementation-Title" to "OM Sample",
            "Implementation-Version" to project.version
        )
    }
    archiveBaseName.set("app")
    from(sourceSets.main.get().output)

    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}


tasks.named<JavaExec>("run") {
    mainClass.set("sepc.sample.App")
    classpath = files(tasks.named<Jar>("jar").get().archiveFile)
    jvmArgs("-Xmx6g", "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED")
}



 


tasks.named("build") {
    dependsOn("jar")
}