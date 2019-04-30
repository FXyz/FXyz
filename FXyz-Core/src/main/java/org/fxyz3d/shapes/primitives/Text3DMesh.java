/**
 * Text3DMesh.java
 *
 * Copyright (c) 2013-2016, F(X)yz
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *     * Neither the name of F(X)yz, any associated website, nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL F(X)yz BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

package org.fxyz3d.shapes.primitives;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Translate;
import javafx.util.Callback;
import org.fxyz3d.geometry.Face3;
import org.fxyz3d.geometry.Point3D;
import org.fxyz3d.scene.paint.Palette.ColorPalette;
import org.fxyz3d.scene.paint.Patterns;
import org.fxyz3d.shapes.primitives.helper.LineSegment;
import org.fxyz3d.shapes.primitives.helper.MeshHelper;
import org.fxyz3d.shapes.primitives.helper.Text3DHelper;
import org.fxyz3d.shapes.primitives.helper.TextureMode;

/**
 *
 * @author José Pereda 
 */
public class Text3DMesh extends Group implements TextureMode {
    
    private final static String DEFAULT_TEXT3D = "F(X)yz 3D";
    private final static String DEFAULT_FONT = "Arial";
    private final static int DEFAULT_FONT_SIZE = 100;
    private final static double DEFAULT_HEIGHT = 10d;
    private final static double DEFAULT_GAP = 0d;
    private final static int DEFAULT_LEVEL = 1;
    private final static boolean DEFAULT_JOIN_SEGMENTS = true;
    private final static char SPACE = 32;
    
    private ObservableList<TexturedMesh> meshes=null;
    private List<Point3D> offset;
    
    public Text3DMesh(){
        this(DEFAULT_TEXT3D,DEFAULT_FONT,DEFAULT_FONT_SIZE,DEFAULT_JOIN_SEGMENTS,DEFAULT_HEIGHT,DEFAULT_GAP,DEFAULT_LEVEL);
    }
    
    public Text3DMesh(String text3D){
        this(text3D,DEFAULT_FONT,DEFAULT_FONT_SIZE,DEFAULT_JOIN_SEGMENTS,DEFAULT_HEIGHT,DEFAULT_GAP,DEFAULT_LEVEL);
    }
    
    public Text3DMesh(String text3D, String font,int fontSize){
        this(text3D,font,fontSize,DEFAULT_JOIN_SEGMENTS,DEFAULT_HEIGHT,DEFAULT_GAP,DEFAULT_LEVEL);
    }
    
    public Text3DMesh(String text3D, String font,int fontSize, boolean joinSegments){
        this(text3D,font,fontSize,joinSegments,DEFAULT_HEIGHT,DEFAULT_GAP,DEFAULT_LEVEL);
    }
    
    public Text3DMesh(String text3D, double height){
        this(text3D,DEFAULT_FONT,DEFAULT_FONT_SIZE,DEFAULT_JOIN_SEGMENTS,height,DEFAULT_GAP, DEFAULT_LEVEL);
    }
    public Text3DMesh(String text3D, double height, double gap){
        this(text3D,DEFAULT_FONT,DEFAULT_FONT_SIZE,DEFAULT_JOIN_SEGMENTS,height,gap, DEFAULT_LEVEL);
    }
    
    public Text3DMesh(String text3D, double height, int level){
        this(text3D,DEFAULT_FONT,DEFAULT_FONT_SIZE,DEFAULT_JOIN_SEGMENTS,height,DEFAULT_GAP,level);
    }
    public Text3DMesh(String text3D, double height, double gap, int level){
        this(text3D,DEFAULT_FONT,DEFAULT_FONT_SIZE,DEFAULT_JOIN_SEGMENTS,height,gap,level);
    }
    
    public Text3DMesh(String text3D, String font, int fontSize, boolean joinSegments, double height, double gap, int level){
        setText3D(text3D);
        setFont(font);
        setFontSize(fontSize);
        setJoinSegments(joinSegments);
        setHeight(height);
        setGap(gap);
        setLevel(level);
        
        updateMesh();
    }
    private final StringProperty text3D = new SimpleStringProperty(DEFAULT_TEXT3D){

        @Override
        protected void invalidated() {
            if(meshes!=null){
                updateMesh();
            }
        }
    };

    public String getText3D() {
        return text3D.get();
    }

    public final void setText3D(String value) {
        text3D.set(value);
    }

    public StringProperty text3DProperty() {
        return text3D;
    }
    private final StringProperty font = new SimpleStringProperty(DEFAULT_FONT){
        @Override
        protected void invalidated() {
            if(meshes!=null){
                updateMesh();
            }
        }
    };

    public String getFont() {
        return font.get();
    }

    public final void setFont(String value) {
        font.set(value);
    }

    public StringProperty fontProperty() {
        return font;
    }
    private final IntegerProperty fontSize = new SimpleIntegerProperty(DEFAULT_FONT_SIZE){
        @Override
        protected void invalidated() {
            if(meshes!=null){
                updateMesh();
            }
        }
    };

    public int getFontSize() {
        return fontSize.get();
    }

    public final void setFontSize(int value) {
        fontSize.set(value);
    }

    public IntegerProperty fontSizeProperty() {
        return fontSize;
    }
    private final DoubleProperty height = new SimpleDoubleProperty(DEFAULT_HEIGHT){
        @Override
        protected void invalidated() {
            if(meshes!=null){
                updateMesh();
            }
        }
    };

    public double getHeight() {
        return height.get();
    }

    public final void setHeight(double value) {
        height.set(value);
    }

    public DoubleProperty heightProperty() {
        return height;
    }
    private final DoubleProperty gap = new SimpleDoubleProperty(DEFAULT_GAP){

        @Override
        protected void invalidated() {
            if(meshes!=null){
                updateMesh();
            }
        }

    };

    public double getGap() {
        return gap.get();
    }

    public final void setGap(double value) {
        gap.set(value);
    }

    public DoubleProperty gapProperty() {
        return gap;
    }

    
    private final IntegerProperty level = new SimpleIntegerProperty(DEFAULT_LEVEL){

        @Override
        protected void invalidated() {
            if(meshes!=null){
                updateMesh();
            }
        }

    };
    
    public final int getLevel() {
        return level.get();
    }

    public final void setLevel(int value) {
        level.set(value);
    }

    public final IntegerProperty levelProperty() {
        return level;
    }
    private final BooleanProperty joinSegments = new SimpleBooleanProperty(DEFAULT_JOIN_SEGMENTS){
        @Override
        protected void invalidated() {
            if(meshes!=null){
                updateMesh();
            }
        }
    };

    public boolean isJoinSegments() {
        return joinSegments.get();
    }

    public final void setJoinSegments(boolean value) {
        joinSegments.set(value);
    }

    public BooleanProperty joinSegmentsProperty() {
        return joinSegments;
    }
    
    protected final void updateMesh() {
        // 1. Full Text to get position of each letter
        Text3DHelper helper = new Text3DHelper(text3D.get(), font.get(), fontSize.get());
        offset=helper.getOffset();

        // 2. Create mesh for each LineSegment        
        meshes=FXCollections.<TexturedMesh>observableArrayList();
        indLetters=new AtomicInteger();
        indSegments=new AtomicInteger();
        letterPath=new Path();
        
        text3D.get().chars().mapToObj(i->(char)i).filter(c->c!=SPACE)
                .forEach(letter->createLetter(letter.toString()));
        
        // 3. Update texture
//        switch(textureType.get()){
//            case NONE:
//                triangleMesh.getTexCoords().setAll(textureCoords);
//                triangleMesh.getFaces().setAll(helper.updateFacesWithTextures(listFaces,listTextures));
//                break;
//        }
        getChildren().setAll(meshes);
        updateTransforms();
    }
    
    private AtomicInteger indSegments, indLetters;
    private Shape letterPath=new Path();
    private void createLetter(String letter) {
        
        Text3DHelper helper = new Text3DHelper(letter, font.get(), fontSize.get());
        List<Point3D> origin = helper.getOffset();
        
        final int ind=indSegments.get();
        helper.getLineSegment().stream().map(poly->poly.getPath()).forEach(path->letterPath=Shape.union(letterPath, path));
        helper.getLineSegment().stream().forEach(poly->{
            final List<Point3D> points=poly.getPoints();
            List<List<Point3D>> holes=null;
            if(poly.getHoles().size()>0){
                holes=poly.getHoles().stream().map(LineSegment::getPoints).collect(Collectors.toList());
            }
            List<Point3D> invert = IntStream.range(0,points.size())
                    .mapToObj(i->points.get(points.size()-1-i))
                    .distinct().collect(Collectors.toList());
            Bounds bounds = null;
            if(joinSegments.get()){
                bounds=letterPath.getBoundsInParent();
            }
            TriangulatedMesh polyMesh = new TriangulatedMesh(invert,holes,level.get(),height.get(),0d,bounds);
            if(indSegments.get()>ind && joinSegments.get()){
                /*
                Combine new polyMesh with previous polyMesh into one single polyMesh
                */
                MeshHelper mh = new MeshHelper((TriangleMesh)meshes.get(meshes.size()-1).getMesh());
                MeshHelper mh1 = new MeshHelper((TriangleMesh)polyMesh.getMesh());
                mh1.addMesh(mh);
                polyMesh.updateMesh(mh1);
                meshes.set(meshes.size()-1,polyMesh);
            } else {
                meshes.add(polyMesh);
            }
            polyMesh.getTransforms().addAll(new Translate(offset.get(ind).x-origin.get(0).x+indLetters.get()*gap.doubleValue(),0,0));
            polyMesh.setCullFace(CullFace.BACK);
            polyMesh.setDrawMode(DrawMode.FILL);
            polyMesh.setDepthTest(DepthTest.ENABLE);
            polyMesh.setId(poly.getLetter());
            indSegments.getAndIncrement();
        });
        indLetters.getAndIncrement();
        
        vertCountBinding.invalidate();
        faceCountBinding.invalidate();

    }

    @Override
    public void setTextureModeNone() {
        meshes.stream().forEach(m->m.setTextureModeNone());
    }

    @Override
    public void setTextureModeNone(Color color) {
        meshes.stream().forEach(m->m.setTextureModeNone(color));
    }

    @Override
    public void setTextureModeNone(Color color, String image) {
        meshes.stream().forEach(m->m.setTextureModeNone(color,image));
    }

    @Override
    public void setTextureModeImage(String image) {
        meshes.stream().forEach(m->m.setTextureModeImage(image));
    }

    @Override
    public void setTextureModePattern(Patterns.CarbonPatterns pattern, double scale) {
        meshes.stream().forEach(m->m.setTextureModePattern(pattern, scale));
    }

    @Override
    public void setTextureModeVertices3D(int colors, Function<Point3D, Number> dens) {
        meshes.stream().forEach(m->m.setTextureModeVertices3D(colors, dens));
    }

    @Override
    public void setTextureModeVertices3D(ColorPalette palette, Function<Point3D, Number> dens) {
        meshes.stream().forEach(m->m.setTextureModeVertices3D(palette, dens));
    }

    @Override
    public void setTextureModeVertices3D(int colors, Function<Point3D, Number> dens, double min, double max) {
        meshes.stream().forEach(m->m.setTextureModeVertices3D(colors, dens, min, max));
    }

    @Override
    public void setTextureModeVertices1D(int colors, Function<Number, Number> function) {
        meshes.stream().forEach(m->m.setTextureModeVertices1D(colors, function));
    }

    @Override
    public void setTextureModeVertices1D(ColorPalette palette, Function<Number, Number> function) {
        meshes.stream().forEach(m->m.setTextureModeVertices1D(palette, function));
    }

    @Override
    public void setTextureModeVertices1D(int colors, Function<Number, Number> function, double min, double max) {
        meshes.stream().forEach(m->m.setTextureModeVertices1D(colors, function, min, max));
    }

    @Override
    public void setTextureModeFaces(int colors) {
        meshes.stream().forEach(m->m.setTextureModeFaces(colors));
    }
    
    @Override
    public void setTextureModeFaces(ColorPalette palette) {
        meshes.stream().forEach(m->m.setTextureModeFaces(palette));
    }

    @Override
    public void setTextureOpacity(double value) {
        meshes.stream().forEach(m->m.setTextureOpacity(value));
    }

    @Override
    public void updateF(List<Number> values) {
         meshes.stream().forEach(m->m.updateF(values));
    }
    
    public void setDrawMode(DrawMode mode) {
        meshes.stream().forEach(m->m.setDrawMode(mode));
    }
    
    private void updateTransforms() {
        meshes.stream().forEach(m->m.updateTransforms());
    }
    
    public TexturedMesh getMeshFromLetter(String letter){
        return meshes.stream().filter(p->p.getId().equals(letter)).findFirst().orElse(meshes.get(0));
    }
    
    public TexturedMesh getMeshFromLetter(String letter, int order){
        return meshes.stream().filter(p->p.getId().equals(letter)).skip(order-1).findFirst().orElse(meshes.get(0));
    }
 
    private final Callback<List<Point3D>, Integer> vertexCount = (List<Point3D> param) -> {
        return param.size();
    };
    private final Callback<List<Face3>, Integer> faceCount = (List<Face3> param) -> {
        return param.size();
    };
    
    /**
     * @return an unmodifiable list of all meshes
     */
    public List<TexturedMesh> getMeshes() {
        return Collections.unmodifiableList(meshes);
    }

    protected final StringBinding vertCountBinding = new StringBinding() {
        @Override
        protected String computeValue() {
            int sum = meshes.stream()
                    .mapToInt(m -> vertexCount.call(m.listVertices))
                    .sum();
            return String.valueOf(sum);
        }
    };

    protected final StringBinding faceCountBinding = new StringBinding() {
        @Override
        protected String computeValue() {
            int sum = meshes.stream()
                    .mapToInt(m -> faceCount.call(m.listFaces))
                    .sum();
            return String.valueOf(sum);
        }
    };
    
    public final StringBinding faceCountBinding() {
        return faceCountBinding;
    }

    public final StringBinding vertCountBinding() {
        return vertCountBinding;
    }
}
