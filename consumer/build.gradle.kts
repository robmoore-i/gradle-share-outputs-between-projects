val sharedConfiguration: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    add(sharedConfiguration.name, project(mapOf("path" to ":producer", "configuration" to "sharedConfiguration")))
}

tasks.register("showFile") {
    dependsOn(":producer:makeFile")
    doFirst {
        logger.lifecycle(sharedConfiguration.singleFile.absolutePath)
    }
}
