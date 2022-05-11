# How to share files across Gradle subprojects: A minimal example

This is the Gradle project:

```
.
├── producer
│   └── build.gradle
├── consumer
│   └── build.gradle
└── settings.gradle
```

```

### producer/build.gradle ###

# - Register a single task, which creates a file.
# - Publish an artifact which contains just that file.

def makeFile = tasks.register("makeFile") {
    def sharedFile = project.layout.buildDirectory.file("some-subdir/shared-file.txt")
    outputs.file(sharedFile)
    doFirst {
        sharedFile.get().asFile << "This file is shared across Gradle subprojects."
    }
}

configurations {
    sharedConfiguration {
        canBeConsumed = true
        canBeResolved = false
    }
}

artifacts {
    sharedConfiguration(makeFile)
}
```

```

### consumer/build.gradle ###

# - Declare a dependency on the producer to consume its published configuration.
# - Register a single task, which resolves that configuration and uses the file. Explicitly depend on the execution of the producing task.

configurations {
    sharedConfiguration {
        canBeConsumed = false
        canBeResolved = true
    }
}

dependencies {
    sharedConfiguration(project("path": ":producer", "configuration": "sharedConfiguration"))
}

tasks.register("showFile") {
    FileCollection sharedFiles = configurations.getByName("sharedConfiguration")
    inputs.files(sharedFiles)
    doFirst {
        logger.lifecycle("Shared file contains the text: '{}'", sharedFiles.singleFile.text)
    }
}
```
