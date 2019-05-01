FXyz3D
======

 - FXyz3D Core: 
 [ ![Download](http://img.shields.io/maven-central/v/org.fxyz3d/fxyz3d.svg?color=%234DC71F) ](https://search.maven.org/search?q=g:org.fxyz3d%20AND%20a:fxyz3d)

 - FXyz3D Client: 
[ ![Download](https://img.shields.io/maven-central/v/org.fxyz3d/fxyz3d-client.svg?color=%234DC71F) ](https://search.maven.org/search?q=g:org.fxyz3d%20AND%20a:fxyz3d-client)

 - FXyz3D Importers: 
[ ![Download](https://img.shields.io/maven-central/v/org.fxyz3d/fxyz3d-importers.svg?color=%234DC71F) ](https://search.maven.org/search?q=g:org.fxyz3d%20AND%20a:fxyz3d-importers)

A JavaFX 3D Visualization and Component Library

[![BSD-3 license](https://img.shields.io/badge/license-BSD--3-%230778B9.svg)](https://opensource.org/licenses/BSD-3-Clause)


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
plugins {
    id 'application'
    id 'org.openjfx.javafxplugin' version '0.0.7'
}

mainClassName = 'org.fxyz3d.Sample'

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.fxyz3d:fxyz3d:0.5.0'
}

javafx {
    modules = [ 'javafx.controls' ]
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

 #### Custom image

You can create a custom image for your platform running:

    ./gradlew clean :FXyz-Samples:jlink  

And you can run it with Java 9+ on your platform:

    FXyz-Samples/build/FXyz/bin/FXyzSamples

Special Thanks go to ControlsFX for providing the FXSampler framework.
http://fxexperience.com/controlsfx/
Our Sampler uses a heavily modified version, due to being 3D.
