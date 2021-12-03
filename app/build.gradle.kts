val sharedConfiguration: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    add(sharedConfiguration.name, project(mapOf("path" to ":lib", "configuration" to "sharedConfiguration")))
}

tasks.register("showFile") {
    doFirst {
        logger.lifecycle(sharedConfiguration.singleFile.absolutePath)
    }
}
