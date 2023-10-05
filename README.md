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

The project is managed by gradle. To build with JDK 17, type:

	./gradlew assemble

To deploy it to your local Maven repository, type:

	./gradlew publishToMavenLocal

## Use of FXyz3D Core

With FXyz3D there are many different 3D custom shapes. The following sample makes use of `SpringMesh` to create 
a 3D mesh of a spring.

### Sample

#### Gradle project

If you have a gradle project, edit the `build.gradle` file and add:

```
plugins {
    id 'application'
    id 'org.openjfx.javafxplugin' version '0.1.0'
}

application {
    mainClass = 'org.fxyz3d.Sample'
}

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.fxyz3d:fxyz3d:0.6.0'
}

javafx {
    modules = [ 'javafx.controls' ]
}
```

#### Maven project

Or if you have maven project, edit the `pom.xml` file and add:

```
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>org.fxyz3d</groupId>
    <artifactId>sample</artifactId>
    <version>1.0-SNAPSHOT</version>
    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.release>17</maven.compiler.release>
        <javafx.maven.plugin.version>0.0.8</javafx.maven.plugin.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-controls</artifactId>
            <version>21</version>
        </dependency>
        <dependency>
            <groupId>org.fxyz3d</groupId>
            <artifactId>fxyz3d</artifactId>
            <version>0.6.0</version>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.1</version>
            </plugin>
            <plugin>
                <groupId>org.openjfx</groupId>
                <artifactId>javafx-maven-plugin</artifactId>
                <version>${javafx.maven.plugin.version}</version>
                <configuration>
                    <mainClass>org.fxyz3d.Sample</mainClass>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

#### Application class

Then create a JavaFX Application class `Sample` under the `org.fxyz3d` package: 

```java
package org.fxyz3d;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.paint.Color;
import javafx.scene.shape.CullFace;
import javafx.stage.Stage;
import org.fxyz3d.shapes.primitives.SpringMesh;
import org.fxyz3d.utils.CameraTransformer;

public class Sample extends Application {

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
}
```

#### Run the sample

If you have a gradle project:

```
.gradlew run
```
or if you have a maven one:
```
mvn javafx:run
```

and you should see the result:

![](/resources/SpringMesh.png)

Note: For more information on JavaFX, check this [link](https://openjfx.io).

### FXSampler

To use the FXSampler and visualize all the samples and the different options available, run:

    ./gradlew run
    
There is a hidden side popup menu at the left, from where different samples can be selected. From the right panels different options can be applied dynamically to the 3D shape.

![](/resources/fxsampler.png)

 #### Custom image

You can create a custom image for your platform running:

    ./gradlew clean :FXyz-Samples:jlink  

And you can run it with Java 17 on your platform:

    FXyz-Samples/build/FXyz/bin/FXyzSamples

Special Thanks go to ControlsFX for providing the FXSampler framework.
http://fxexperience.com/controlsfx/
Our Sampler uses a heavily modified version, due to being 3D.
