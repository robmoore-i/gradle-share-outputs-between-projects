val sharedConfiguration: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    sharedConfiguration(project(mapOf("path" to ":producer", "configuration" to "sharedConfiguration")))
}

tasks.register("showFile") {
    inputs.files(sharedConfiguration)
    doFirst {
        logger.lifecycle(sharedConfiguration.singleFile.absolutePath)
    }
}
