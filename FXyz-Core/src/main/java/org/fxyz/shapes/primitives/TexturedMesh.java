/**
* TexturedMesh.java
*
* Copyright (c) 2013-2015, F(X)yz
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
* * Redistributions of source code must retain the above copyright
* notice, this list of conditions and the following disclaimer.
* * Redistributions in binary form must reproduce the above copyright
* notice, this list of conditions and the following disclaimer in the
* documentation and/or other materials provided with the distribution.
* * Neither the name of the organization nor the
* names of its contributors may be used to endorse or promote products
* derived from this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
* ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
* WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
* DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
* (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
* LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
* (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
* SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.fxyz.shapes.primitives;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.TriangleMesh;
import org.fxyz.geometry.Point3D;
import org.fxyz.geometry.Face3;
import org.fxyz.shapes.primitives.helper.MeshHelper;
import org.fxyz.shapes.primitives.helper.TriangleMeshHelper;
import static org.fxyz.shapes.primitives.helper.TriangleMeshHelper.DEFAULT_COLORS;
import static org.fxyz.shapes.primitives.helper.TriangleMeshHelper.DEFAULT_DENSITY_FUNCTION;
import static org.fxyz.shapes.primitives.helper.TriangleMeshHelper.DEFAULT_DIFFUSE_COLOR;
import static org.fxyz.shapes.primitives.helper.TriangleMeshHelper.DEFAULT_PATTERN_SCALE;
import static org.fxyz.shapes.primitives.helper.TriangleMeshHelper.DEFAULT_UNIDIM_FUNCTION;
import org.fxyz.shapes.primitives.helper.TriangleMeshHelper.SectionType;
import org.fxyz.shapes.primitives.helper.TriangleMeshHelper.TextureType;

/**
 * TexturedMesh is a base class that provides support for different mesh implementations
 * taking into account four different kind of textures
 * - None
 * - Image
 * - Colored vertices
 * - Colored faces
 * 
 * For the last two ones, number of colors and density map have to be provided
 * 
 * Any subclass must use mesh, listVertices and listFaces
 * 
 * @author jpereda
 */
public abstract class TexturedMesh extends MeshView {
    
    private TriangleMeshHelper helper = new TriangleMeshHelper();
    protected MeshHelper meshHelper;
    protected TriangleMesh mesh;
    
    protected final List<Point3D> listVertices = new ArrayList<>();
    protected final List<Face3> listTextures = new ArrayList<>();
    protected final List<Face3> listFaces = new ArrayList<>();
    protected float[] textureCoords;
    protected int[] smoothingGroups;
    
    protected final Rectangle rectMesh=new Rectangle(0,0);
    protected final Rectangle areaMesh=new Rectangle(0,0);
    
    protected TexturedMesh(){
        sectionType.set(SectionType.CIRCLE);
        textureType.set(TextureType.NONE);
        textureType.addListener((ob,o,o1)->{
            if(mesh!=null){
                updateTexture();
                updateTextureOnFaces();
    }
        });
    }
    private final ObjectProperty<SectionType> sectionType = new SimpleObjectProperty<SectionType>(){

        @Override
        protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
        
    };

    public SectionType getSectionType() {
        return sectionType.get();
    }

    public void setSectionType(SectionType value) {
        sectionType.set(value);
    }

    public ObjectProperty sectionTypeProperty() {
        return sectionType;
    }

    private final ObjectProperty<TextureType> textureType = new SimpleObjectProperty<TextureType>();

    public void setTextureModeNone() {
        setTextureModeNone(Color.WHITE);
    }
    
    public void setTextureModeNone(Color color) {
        if(color!=null){
            helper.setTextureType(TextureType.NONE);
            setMaterial(helper.getMaterialWithColor(color));
        }
        setTextureType(helper.getTextureType());
    }
    
    public void setTextureModeImage(String image) {
        if(image!=null && !image.isEmpty()){
            helper.setTextureType(TextureType.IMAGE);
            setMaterial(helper.getMaterialWithImage(image));
            setTextureType(helper.getTextureType());
        }
    }
    
    public void setTextureModePattern(double scale) {
        helper.setTextureType(TextureType.PATTERN);
        patternScale.set(scale);
        setMaterial(helper.getMaterialWithPattern());
        setTextureType(helper.getTextureType());
    }
    
    public void setTextureModeVertices3D(int colors, Function<Point3D,Number> dens) {
        helper.setTextureType(TextureType.COLORED_VERTICES_3D);
        setColors(colors);
        setDensity(dens);
        setTextureType(helper.getTextureType());
    }
    public void setTextureModeVertices1D(int colors, Function<Number,Number> function) {
        helper.setTextureType(TextureType.COLORED_VERTICES_1D);
        setColors(colors);
        setFunction(function);
        setTextureType(helper.getTextureType());
    }
    
    public void setTextureModeFaces(int colors) {
        helper.setTextureType(TextureType.COLORED_FACES);
        setColors(colors);
        setTextureType(helper.getTextureType());
    }
    
    public TextureType getTextureType() {
        return textureType.get();
    }

    public void setTextureType(TextureType value) {
        textureType.set(value);
    }

    public ObjectProperty textureTypeProperty() {
        return textureType;
    }
    
    private final DoubleProperty patternScale = new SimpleDoubleProperty(DEFAULT_PATTERN_SCALE){
    
        @Override
        protected void invalidated() {
            updateTexture();
        }
        
    };
    
    public final double getPatternScale(){
        return patternScale.get();
    }
    
    public final void setPatternScale(double scale){
        patternScale.set(scale);
    }
    
    public DoubleProperty patternScaleProperty(){
        return patternScale;
    }
    
    private final ObjectProperty<Color> diffuseColor = new SimpleObjectProperty<Color>(DEFAULT_DIFFUSE_COLOR){
        
        @Override protected void invalidated() {
            updateMaterial();
        }
    };

    public Color getDiffuseColor() {
        return diffuseColor.get();
    }

    public void setDiffuseColor(Color value) {
        diffuseColor.set(value);
    }

    public ObjectProperty diffuseColorProperty() {
        return diffuseColor;
    }

    private final IntegerProperty colors = new SimpleIntegerProperty(DEFAULT_COLORS){

        @Override protected void invalidated() {
            createPalette(getColors());
            updateTexture();
            updateTextureOnFaces();
        }
    };

    public final int getColors() {
        return colors.get();
    }

    public final void setColors(int value) {
        colors.set(value);
    }

    public IntegerProperty colorsProperty() {
        return colors;
    }
    
    private final ObjectProperty<Function<Point3D,Number>> density = new SimpleObjectProperty<Function<Point3D,Number>>(DEFAULT_DENSITY_FUNCTION){
        
        @Override protected void invalidated() {
            helper.setDensity(density.get());
            updateTextureOnFaces();
        }
    };
    
    public final Function<Point3D,Number> getDensity(){
        return density.get();
    }
    
    public final void setDensity(Function<Point3D,Number> value){
        this.density.set(value);
    }
    
    public final ObjectProperty<Function<Point3D,Number>> densityProperty() {
        return density;
    }

    private final ObjectProperty<Function<Number,Number>> function = new SimpleObjectProperty<Function<Number,Number>>(DEFAULT_UNIDIM_FUNCTION){
        
        @Override protected void invalidated() {
            helper.setFunction(function.get());
            updateTextureOnFaces();
        }
    };

    public Function<Number,Number> getFunction() {
        return function.get();
    }

    public void setFunction(Function<Number,Number> value) {
        function.set(value);
    }

    public ObjectProperty functionProperty() {
        return function;
    }
    
    private void createPalette(int colors) {
        helper.createPalette(colors,false);        
        setMaterial(helper.getMaterialWithPalette());
    }
    
    public void updateMaterial(){
        setMaterial(helper.getMaterialWithColor(diffuseColor.get()));
    }
    
    public void updateVertices(float factor){
        if(mesh!=null){
            mesh.getPoints().setAll(helper.updateVertices(listVertices, factor));
        }
    }
        
    private void updateTexture(){
        if(mesh!=null){
            switch(textureType.get()){
                case NONE: 
                    mesh.getTexCoords().setAll(0f,0f);
                    break;
                case IMAGE: 
                    mesh.getTexCoords().setAll(textureCoords);
                    break;
                case PATTERN: 
                    if(areaMesh.getHeight()>0 && areaMesh.getWidth()>0){
                        mesh.getTexCoords().setAll(
                            helper.updateTexCoordsWithPattern((int)rectMesh.getWidth(),
                                    (int)rectMesh.getHeight(),patternScale.get(),
                                    areaMesh.getHeight()/areaMesh.getWidth()));
                    } else {
                        mesh.getTexCoords().setAll(
                            helper.updateTexCoordsWithPattern((int)rectMesh.getWidth(),
                                    (int)rectMesh.getHeight(),patternScale.get()));
                    }
                    break;
                case COLORED_VERTICES_1D:
                    mesh.getTexCoords().setAll(helper.getTexturePaletteArray());
                    break;
                case COLORED_VERTICES_3D:
                    mesh.getTexCoords().setAll(helper.getTexturePaletteArray());
                    break;
                case COLORED_FACES:
                    mesh.getTexCoords().setAll(helper.getTexturePaletteArray());
                    break;
            }
        }
    }
    
    private void updateTextureOnFaces(){
        // textures for level
        if(mesh!=null){
            switch(textureType.get()){
                case NONE: 
                    mesh.getFaces().setAll(helper.updateFacesWithoutTexture(listFaces));
                    break;
                case IMAGE: 
                    if(listTextures.size()>0){
                        mesh.getFaces().setAll(helper.updateFacesWithTextures(listFaces,listTextures));
                    } else { 
                        mesh.getFaces().setAll(helper.updateFacesWithVertices(listFaces));
                    }
                    break;
                case PATTERN: 
                    mesh.getFaces().setAll(helper.updateFacesWithTextures(listFaces,listTextures));
                    break;
                case COLORED_VERTICES_1D:
                    mesh.getFaces().setAll(helper.updateFacesWithFunctionMap(listVertices, listFaces));
                    break;
                case COLORED_VERTICES_3D:
                    mesh.getFaces().setAll(helper.updateFacesWithDensityMap(listVertices, listFaces));
                    break;
                case COLORED_FACES:
                    mesh.getFaces().setAll(helper.updateFacesWithFaces(listFaces));
                    break;
            }
        }
    }
    
    protected abstract void updateMesh();
    
    protected void createTexCoords(int width, int height){
        rectMesh.setWidth(width);
        rectMesh.setHeight(height);
        textureCoords=helper.createTexCoords(width, height);
    }
    
    protected void createReverseTexCoords(int width, int height){
        rectMesh.setWidth(width);
        rectMesh.setHeight(height);
        textureCoords=helper.createReverseTexCoords(width, height);
    }
    
    protected MeshHelper precreateMesh(){
        MeshHelper mh = new MeshHelper();
        mh.setPoints(helper.updateVertices(listVertices));
        switch(textureType.get()){
            case NONE:
                mh.setTexCoords(textureCoords);
                mh.setFaces(helper.updateFacesWithTextures(listFaces,listTextures));
                break;
            case PATTERN: 
                if(areaMesh.getHeight()>0 && areaMesh.getWidth()>0){
                    mh.setTexCoords(
                        helper.updateTexCoordsWithPattern((int)rectMesh.getWidth(),
                                (int)rectMesh.getHeight(),patternScale.get(),
                                areaMesh.getHeight()/areaMesh.getWidth()));
                } else {
                    mh.setTexCoords(
                        helper.updateTexCoordsWithPattern((int)rectMesh.getWidth(),
                                (int)rectMesh.getHeight(),patternScale.get()));
                }
                mh.setFaces(helper.updateFacesWithTextures(listFaces,listTextures));
                break;
            case IMAGE: 
                mh.setTexCoords(textureCoords);
                if(listTextures.size()>0){
                    mh.setFaces(helper.updateFacesWithTextures(listFaces,listTextures));
                } else { 
                    mh.setFaces(helper.updateFacesWithVertices(listFaces));
                }
                break;
            case COLORED_VERTICES_1D:
                mh.setTexCoords(helper.getTexturePaletteArray());
                mh.setFaces(helper.updateFacesWithFunctionMap(listVertices, listFaces));
                break;
            case COLORED_VERTICES_3D:
                mh.setTexCoords(helper.getTexturePaletteArray());
                mh.setFaces(helper.updateFacesWithDensityMap(listVertices, listFaces));
                break;
            case COLORED_FACES:
                mh.setTexCoords(helper.getTexturePaletteArray());
                mh.setFaces(helper.updateFacesWithFaces(listFaces));
                break;
        }
        
        int[] faceSmoothingGroups = new int[listFaces.size()]; // 0 == hard edges
        Arrays.fill(faceSmoothingGroups, 1); // 1: soft edges, all the faces in same surface
        if(smoothingGroups!=null){
//            for(int i=0; i<smoothingGroups.length; i++){
//                System.out.println("i: "+smoothingGroups[i]);
//            }
            mh.setFaceSmoothingGroups(smoothingGroups);
        } else {
            mh.setFaceSmoothingGroups(faceSmoothingGroups);
        }
        
        return mh;
    }
    
    protected TriangleMesh createMesh(MeshHelper mh){
        TriangleMesh triangleMesh = new TriangleMesh();
        triangleMesh.getPoints().setAll(mh.getPoints());
        triangleMesh.getTexCoords().setAll(mh.getTexCoords());
        triangleMesh.getFaces().setAll(mh.getFaces());
        triangleMesh.getFaceSmoothingGroups().setAll(mh.getFaceSmoothingGroups());
        return triangleMesh;
    }
    
    protected TriangleMesh createMesh(){
        TriangleMesh triangleMesh = new TriangleMesh();
        triangleMesh.getPoints().setAll(helper.updateVertices(listVertices));
        switch(textureType.get()){
            case NONE:
                triangleMesh.getTexCoords().setAll(textureCoords);
                triangleMesh.getFaces().setAll(helper.updateFacesWithTextures(listFaces,listTextures));
                break;
            case PATTERN: 
                if(areaMesh.getHeight()>0 && areaMesh.getWidth()>0){
                    triangleMesh.getTexCoords().setAll(
                        helper.updateTexCoordsWithPattern((int)rectMesh.getWidth(),
                                (int)rectMesh.getHeight(),patternScale.get(),
                                areaMesh.getHeight()/areaMesh.getWidth()));
                } else {
                    triangleMesh.getTexCoords().setAll(
                        helper.updateTexCoordsWithPattern((int)rectMesh.getWidth(),
                                (int)rectMesh.getHeight(),patternScale.get()));
                }
                triangleMesh.getFaces().setAll(helper.updateFacesWithTextures(listFaces,listTextures));
                break;
            case IMAGE: 
                triangleMesh.getTexCoords().setAll(textureCoords);
                if(listTextures.size()>0){
                    triangleMesh.getFaces().setAll(helper.updateFacesWithTextures(listFaces,listTextures));
                } else { 
                    triangleMesh.getFaces().setAll(helper.updateFacesWithVertices(listFaces));
                }
                break;
            case COLORED_VERTICES_1D:
                triangleMesh.getTexCoords().setAll(helper.getTexturePaletteArray());
                triangleMesh.getFaces().setAll(helper.updateFacesWithFunctionMap(listVertices, listFaces));
                break;
            case COLORED_VERTICES_3D:
                triangleMesh.getTexCoords().setAll(helper.getTexturePaletteArray());
                triangleMesh.getFaces().setAll(helper.updateFacesWithDensityMap(listVertices, listFaces));
                break;
            case COLORED_FACES:
                triangleMesh.getTexCoords().setAll(helper.getTexturePaletteArray());
                triangleMesh.getFaces().setAll(helper.updateFacesWithFaces(listFaces));
                break;
        }
        
        int[] faceSmoothingGroups = new int[listFaces.size()]; // 0 == hard edges
        Arrays.fill(faceSmoothingGroups, 1); // 1: soft edges, all the faces in same surface
        if(smoothingGroups!=null){
//            for(int i=0; i<smoothingGroups.length; i++){
//                System.out.println("i: "+smoothingGroups[i]);
//            }
            triangleMesh.getFaceSmoothingGroups().addAll(smoothingGroups);
        } else {
            triangleMesh.getFaceSmoothingGroups().addAll(faceSmoothingGroups);
        }
        
        System.out.println("nodes: "+listVertices.size()+", faces: "+listFaces.size());
//        System.out.println("area: "+helper.getMeshArea(listVertices, listFaces));
        return triangleMesh;
    }
    
    protected double polygonalSection(double angle){
        if(sectionType.get().equals(SectionType.CIRCLE)){
            return 1d;
        }
        int n=sectionType.get().getSides();
        return Math.cos(Math.PI/n)/Math.cos((2d*Math.atan(1d/Math.tan((n*angle)/2d)))/n);
    }
    
    protected double polygonalSize(double radius){
        if(sectionType.get().equals(SectionType.CIRCLE)){
            return 2d*Math.PI*radius;
        }
        int n=sectionType.get().getSides();
        return n*Math.cos(Math.PI/n)*Math.log(-1d - 2d/(-1d + Math.sin(Math.PI/n)))*radius;
    }
    
    public Point3D getOrigin(){
        if(listVertices.size()>0){
            return listVertices.get(0);
        } 
        return new Point3D(0f,0f,0f);
    }
    
    public int getIntersections(Point3D origin, Point3D direction){
        setTextureModeFaces(10);
        
        int[] faces= helper.updateFacesWithIntersections(origin, direction, listVertices, listFaces);
        mesh.getFaces().setAll(faces);
        long time=System.currentTimeMillis();
        List<Face3> listIntersections = helper.getListIntersections(origin, direction, listVertices, listFaces);
        System.out.println("t: "+(System.currentTimeMillis()-time));
        listIntersections.forEach(System.out::println);
        return listIntersections.size();        
    }
}
