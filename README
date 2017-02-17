# SimpleDiContainer

SimpleDiContainer aims to be a reasonable dependency injection container intended for small projects.

Its main development goals are a minimum amount of magic and surprises - and of course provide a sane environment for dependency injection.

## Features

`SDIC` sports only constructor injection. The main reason is: a constructor is a method that initializes an instance of a class in a manner, that the instance is usable. That means, that the constructor is the only place where dependencies are meant to be injected. Furthermore, it is required that only one constructor exists, even if it has no parameters.

`SDIC` supports singletons. Not the crappy anti-pattern ones with a `getInstance()` method or `INSTANCE` member as you would think of in the first place. A singleton in a dependency injection container is a service that is created only once. As long as it's loaded by the same container, you will receive the same instance.

`SDIC` is configured via a properties file, because this format is supported by Java directly. Supporting YAML, JSON or other formats would mean to include parser libraries which would bloat the artifact.

## Configuration

Either, you put a file named `sdic.properties` into the root of your classpath (`src/main/resources` in maven projects) and call `SdiContainer.load()` or you put your config file elsewhere and call `SdiContainer.load(filename)`.

### Format

sdic.properties:

    service.serviceOne: com.group.artifact.services.ServiceOne

    service.singletonService: com.group.artifact.services.SingletonService
    service.singletonService.singleton: true

The key for a service starts with `service.`. After that, a self defined service name follows. The value is the full qualified class name of that said service.
If that service shall be handled as a singleton, provide a key with the pattern `service.[name].singleton` and either `true` or `false` as value. Only services that are marked as `true` will be handled as singletons. So, setting `false` as value, and simply not defining the key results in same behaviour.
All other keys will be ignored.

In future versions, maybe other config keys will be added.

## Integrity check

`SDIC` executes a basic integrity check while loading the config file. Following errors can be found:

- Dependency Cycles
- Not satisfied dependencies
    - All dependencies defined in constructors must be defined in the same container
- Duplicate classes
    - A FQCN may only be defined once in a container
- More than one constructor defined
    - Only one and exactly one constructor may be defined in a class
- No visible constructor found
- Class not found
    - The class for a defined FQCN can not be found at runtime

## License

SimpleDiContainer is released under GNU GPL v3. Act accordingly. If you do not know what that means, check the provided license file.
