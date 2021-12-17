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

import java.nio.file.Files

def outputFile = project.layout.buildDirectory.dir("some-subdir").map { it.file("shared-file.txt") }
def makeFile = tasks.register("makeFile", DefaultTask) {
    it.outputs.file(outputFile)
    it.doFirst {
        Files.writeString(outputFile.get().asFile.toPath(), "This file is shared across Gradle subprojects.")
    }
}

configurations {
    sharedConfiguration {
        canBeConsumed = true
        canBeResolved = false
    }
}

artifacts {
    sharedConfiguration(makeFile.map { task -> task.outputs.files.singleFile })
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

def sharedConfiguration = configurations.getByName("sharedConfiguration")

tasks.register("showFile") {
    it.inputs.files(sharedConfiguration)
    it.doFirst {
        logger.lifecycle("File is at {}", sharedConfiguration.singleFile.absolutePath)
    }
}
```
