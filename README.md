FXyz3D
======

 - FXyz3D Core: 
 [ ![Download](https://api.bintray.com/packages/jpereda/FXyz3D/fxyz3d/images/download.svg) ](https://bintray.com/jpereda/FXyz3D/fxyz3d/_latestVersion)

 - FXyz3D Client: 
[ ![Download](https://api.bintray.com/packages/jpereda/FXyz3D/fxyz3d-client/images/download.svg) ](https://bintray.com/jpereda/FXyz3D/fxyz3d-client/_latestVersion)

A JavaFX 3D Visualization and Component Library

## How to build

The project is managed by gradle. To build, type

	./gradlew assemble

To deploy it to your local Maven repository, type:

	./gradlew publishToMavenLocal

## Use of FXyz3D Core

With FXyz3D there are many different 3D custom shapes. The following sample makes use of `SpringMesh` to create 
a 3D mesh of a spring.

![](/resources/SpringMesh.png)

### Sample

Create a gradle project, edit the build.gradle file and add:

```
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath 'com.google.gradle:osdetector-gradle-plugin:1.6.0'
    }
}

apply plugin: 'java'
apply plugin: 'application'
apply plugin: 'com.google.osdetector'

def platform = osdetector.os == 'osx' ? 'mac' : osdetector.os == 'windows' ? 'win' : osdetector.os

mainClassName = 'org.fxyz3d.Sample'

repositories {
    jcenter()
}

dependencies {
    compile "org.openjfx:javafx-base:11:$platform"
    compile "org.openjfx:javafx-graphics:11:$platform"
    compile "org.openjfx:javafx-controls:11:$platform"
    
    compile 'org.fxyz3d:fxyz3d:0.4.0'
}

compileJava {
    doFirst {
        options.compilerArgs = [
                '--module-path', classpath.asPath,
                '--add-modules', 'javafx.controls'
        ]
    }
}

run {
    doFirst {
        jvmArgs = [
                '--module-path', classpath.asPath,
                '--add-modules', 'javafx.controls'
        ]
    }
}
```

and create a JavaFX Application class `Sample` under the `org.fxyz3d` package: 

```java
    @Override
    public void start(Stage primaryStage) throws Exception {
        PerspectiveCamera camera = new PerspectiveCamera(true);        
        camera.setNearClip(0.1);
        camera.setFarClip(10000.0);
        camera.setTranslateX(10);
        camera.setTranslateZ(-100);
        camera.setFieldOfView(20);
        
        CameraTransformer cameraTransform = new CameraTransformer();
        cameraTransform.getChildren().add(camera);
        cameraTransform.ry.setAngle(-30.0);
        cameraTransform.rx.setAngle(-15.0);
        
        SpringMesh spring = new SpringMesh(10, 2, 2, 8 * 2 * Math.PI, 200, 100, 0, 0);
        spring.setCullFace(CullFace.NONE);
        spring.setTextureModeVertices3D(1530, p -> p.f);
        
        Group group = new Group(cameraTransform, spring);
        
        Scene scene = new Scene(group, 600, 400, true, SceneAntialiasing.BALANCED);
        scene.setFill(Color.BISQUE);
        scene.setCamera(camera);
        
        primaryStage.setScene(scene);
        primaryStage.setTitle("FXyz3D Sample");
        primaryStage.show();
    }
```

Note: For more information on JavaFX 11, check this [link](https://openjfx.io).

### FXSampler

To use the FXSampler and visualize all the samples and the different options available, run:

    ./gradlew run
    
There is a hidden side popup menu at the left, from where different samples can be selected. From the right panels different options can be applied dynamically to the 3D shape.

![](/resources/fxsampler.png)

 #### Shadow Jar

You can create a fat jar running:

    ./gradlew clean :FXyz-Samples:shadow  

And you can run the jar with Java 9+ on Windows, Linux and Mac:

    java -jar FXyz-Samples/build/libs/FXyz-Samples-1.0-SNAPSHOT-all.jar

Special Thanks go to ControlsFX for providing the FXSampler framework.
http://fxexperience.com/controlsfx/
Our Sampler uses a heavily modified version, due to being 3D.
