#  Atomic-DI
[![CodeFactor](https://www.codefactor.io/repository/github/nort3x/atomicdi/badge/master)](https://www.codefactor.io/repository/github/nort3x/atomicdi/overview/master)
[![](https://jitpack.io/v/nort3x/AtomicDI.svg)](https://jitpack.io/#nort3x/AtomicDI)

Atomic is light-weight relatively fast ClassGrapher and Dependency Injector
it provides some convenient features and tools especially designed to simplify IoC 

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
    implementation 'com.github.nort3x:AtomicDI:bet-null'
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
    <version>bet-null</version>
</dependency>
```

## Features
<ul>
    <li>Concurrent Modular Bean system</li>
    <li>Annotated Dependency Injection</li>
    <li>Configuration and Policies</li> 
</ul>

## Examples
+ [AtomicJDA](https://github.com/nort3x/AtomicJDA) - AtomicDI approach for developing Discord Bot(s)
