plugins {
    id("java")
    id("application")
}

group = "il.ac.kinneret.mjmay.grades"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "il.ac.kinneret.mjmay.grades.GradeClient"
    }
}

application{
    mainClass.set("il.ac.kinneret.grades.GradeClient")
}