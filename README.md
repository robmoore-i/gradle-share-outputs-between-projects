# How to share files across Gradle subprojects: A minimal example

This is the Gradle project:

```
.
├── app
│   └── build.gradle.kts
├── lib
│   └── build.gradle.kts
└── settings.gradle.kts
```

This is the producer:

```
# lib/build.gradle.kts
# - Register a single task, which creates a file.
# - Publish an artifact which contains just that file.

val outputFile = project.layout.buildDirectory.dir("some-subdir").map { it.file("shared-file.txt") }
val makeFile = tasks.register("makeFile") {
    outputs.file(outputFile)
    doFirst {
        outputFile.get().asFile
            .writeText("This file is shared across Gradle subprojects.")
    }
}

val sharedConfiguration: Configuration by configurations.creating {
    isCanBeConsumed = true
    isCanBeResolved = false
}

artifacts {
    add(sharedConfiguration.name, makeFile.map { it.outputs.files.singleFile })
}
```

This is the consumer:

```
# app/build.gradle.kts
# - Declare a dependency on app to consume its published configuration
# - Register a single task, which resolves that configuration and uses the file.

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
```
