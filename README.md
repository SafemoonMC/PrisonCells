# PrisonCells

PrisonCells is a plugin adding new functionalities to Prison for both, OG and OP, variants.

## Building

PrisonCells uses Gradle to handle dependencies and building.

**Requirements:**

- Java 16 JDK
- Git

**Compiling from source:**

```sh
git clone https://github.com/SafemoonMC/PrisonCells.git
cd PrisonCells/
./gradlew buildAll
```

You can find the output artifacts in the `/COMPILED_JARS` directory.

**Other Gradle custom tasks:**

- **buildBase**, it gets just the jar without any dependency in;
- **buildSources**, it gets just the jar with project files in;
- **buildJavadoc**, it gets just the final JavaDoc;
- **buildShadowjar**, it gets just the final jar with all necessary dependencies;

## Contributing

PrisonCells follows the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).
Generally, you can import the style from the `java-google-style.xml` file you can find at the root of
the project.