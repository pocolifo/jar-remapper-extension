# JAR Remapper Extension

Extensions to [JAR Remapper](https://github.com/pocolifo/jar-remapper) that include extra features that you may need

## Features

#### More Remapping Engines
- Tiny Remapper by FabricMC 


## Coming Soon
- Parameter name remapping

## Getting Started

#### Tiny Remapper Remapping Engine

Append the `withRemappingEngine` option to JAR Remapper. 

```java
JarRemapper.newRemap()
    .withRemappingEngine(new TinyRemapperEngine()
        // (required)
        // Use any options you'd like from Tiny Remapper
        // except for: withMappings and ignoreFieldDesc
        // These are automatically set.
        .setOptions(TinyRemapper.newRemapper())
        
        // (optional)
        // Excludes the META-INF directory from output JAR
        .excludeMetaInf()
    )
// ...whatever other options you use for JAR Remapper...
```

## Develop

1. Clone this repository
2. Import the project into IntelliJ IDEA
3. Run the `setup` Gradle task
