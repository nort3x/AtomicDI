#  Atomic-DI
[![CodeFactor](https://www.codefactor.io/repository/github/nort3x/atomicdi/badge/master)](https://www.codefactor.io/repository/github/nort3x/atomicdi/overview/master)
[![](https://jitpack.io/v/nort3x/AtomicDI.svg)](https://jitpack.io/#nort3x/AtomicDI)

Atomic is lightweight,  flexible, fast and fast  DI framework <br/>
based around concurrent lookups and in-memory caching<br/>
it will provide a simple but rich API<br/>
take a look:

```
package structure:

TopPackage
|
|__ InnerPackage+
|               |__Beans
|             
|__CoffieMachine
|__Java
```

```java

@Atomic 
class CoffeeMachine{
    
    @Atom Beans;
    @Atom Java;
    
  }


@Atomic 
class Java{
    
    @Atom Beans;
    @Atom CoffeMachine;

    @Exclude
    BadBeans;
    @Atom(scope = Scope.Shared, concreteType = MyImpl.class)
    Database db;

    @Predifiend(key = "Author")
    String author;
    @Predifiend(key="Mode") MyEnumType mode;
    
    @Interaction void beingJava(@Atom Beans newBeans)
  }
  
  
@Atomic 
class Beans{
    
    @Atom CoffeMachine;
    @Atom Java;
    
  }


/* somewhere in your routines */

    AtomicDI.getInstance().resolve(MainClassOfTopPackage.class);
    Java j = (Java) Container.makeContainerAround(AtomicType.of(Java.class)).getCentral();
    
    // do what ever you want with java!
    

```


## Features
<ul>
    <li>Small and Flexible DI framework</li>
    <li>Fully Extendable and injectable</li>
    <li>Concurrent and Modular</li>
    <li>Cyclic Dependency Resolution</li> 
    <li>CompileTime Processor for minimizing Exceptions</li> 
    <li>Flexible Logger</li> 
    <li>Vanilla Java, lightweight</li>
    <li>AutoRunners and Module Handlers [not yet]</li> 
    <li>LiveReloadable Modules [not yet]</li> 
</ul>

## Examples
+ [AtomicJDA](https://github.com/nort3x/AtomicJDA) - AtomicDI approach for developing Discord Bot(s)



## Installation

### gradle
Add it in your root build.gradle at the end of repositories: (if not already!)

```gradle
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}

```
Step 2. Add the dependency
```gradle
dependencies {
    implementation 'com.github.nort3x:AtomicDI:2.0.5'
}
```

### maven
Step 1. Add the JitPack repository to your build file (if not already!)

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
Step 2. Add the dependency
```xml
<dependency>
    <groupId>com.github.nort3x</groupId>
    <artifactId>AtomicDI</artifactId>
    <version>2.0.5</version>
</dependency>
```

