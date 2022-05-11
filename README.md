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

val makeFile = tasks.register("makeFile") {
    val sharedFile = project.layout.buildDirectory.file("some-subdir/shared-file.txt")
    outputs.file(sharedFile)
    doFirst {
        sharedFile.get().asFile.writeText("This file is shared across Gradle subprojects.")
    }
}

val sharedConfiguration by configurations.creating {
    isCanBeConsumed = true
    isCanBeResolved = false
}

artifacts {
    add(sharedConfiguration.name, makeFile)
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
    sharedConfiguration(project(path = ":producer", configuration = "sharedConfiguration"))
}

tasks.register("showFile") {
    val sharedFiles: FileCollection = sharedConfiguration
    inputs.files(sharedFiles)
    doFirst {
        logger.lifecycle("Shared file contains the text: '{}'", sharedFiles.singleFile.readText())
    }
}
```
