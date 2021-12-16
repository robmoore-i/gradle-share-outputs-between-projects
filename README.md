# How to share files across Gradle subprojects: A minimal example

This is the Gradle project:

```
.
├── producer
│   └── build.gradle.kts
├── consumer
│   └── build.gradle.kts
└── settings.gradle.kts
```

```

### producer/build.gradle.kts ###

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

```

### consumer/build.gradle.kts ###

# - Declare a dependency on the producer to consume its published configuration.
# - Register a single task, which resolves that configuration and uses the file. Explicitly depend on the execution of the producing task.

val sharedConfiguration: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    add(sharedConfiguration.name, project(mapOf("path" to ":producer", "configuration" to "sharedConfiguration")))
}

tasks.register("showFile") {
    inputs.files(sharedConfiguration)
    doFirst {
        logger.lifecycle(sharedConfiguration.singleFile.absolutePath)
    }
}
```
