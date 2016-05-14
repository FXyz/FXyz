/**
* MeshHelper.java
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

package org.fxyz.shapes.primitives.helper;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.scene.shape.TriangleMesh;
import org.fxyz.geometry.Point3D;

/**
 *
 * @author jpereda
 */
public class MeshHelper {

    private float[] points;
    private float[] texCoords;
    private int[] faces;
    private int[] faceSmoothingGroups;
    private float[] f;

    public MeshHelper() {
    }
    public MeshHelper(TriangleMesh tm) {
        this.points = tm.getPoints().toArray(points);
        this.texCoords = tm.getTexCoords().toArray(texCoords);
        this.faces = tm.getFaces().toArray(faces);
        this.faceSmoothingGroups = tm.getFaceSmoothingGroups().toArray(faceSmoothingGroups);
        this.f=new float[points.length/3];
    }
    
    public MeshHelper(float[] points, float[] texCoords, int[] faces, int[] faceSmoothingGroups) {
        this.points = points;
        this.texCoords = texCoords;
        this.faces = faces;
        this.faceSmoothingGroups = faceSmoothingGroups;
        this.f=new float[points.length/3];
    }
    
    public MeshHelper(float[] points, float[] texCoords, int[] faces, int[] faceSmoothingGroups, float[] f) {
        this.points = points;
        this.texCoords = texCoords;
        this.faces = faces;
        this.faceSmoothingGroups = faceSmoothingGroups;
        this.f=f;
    }

    public float[] getPoints() {
        return points;
    }

    public void setPoints(float[] points) {
        this.points = points;
    }

    public float[] getTexCoords() {
        return texCoords;
    }

    public void setTexCoords(float[] texCoords) {
        this.texCoords = texCoords;
    }

    public int[] getFaces() {
        return faces;
    }

    public void setFaces(int[] faces) {
        this.faces = faces;
    }

    public int[] getFaceSmoothingGroups() {
        return faceSmoothingGroups;
    }

    public void setFaceSmoothingGroups(int[] faceSmoothingGroups) {
        this.faceSmoothingGroups = faceSmoothingGroups;
    }

    public float[] getF() {
        return f;
    }

    public void setF(float[] f) {
        this.f = f;
    }
    
    /*
    Add to the meshHelper a new meshHelper, to store the data of two
    meshes that can be joined into one
    */
    public void addMesh(MeshHelper mh){
        int numPoints = points.length/3;
        int numTexCoords = texCoords.length/2;
        this.points = addFloatArray(points,mh.getPoints());
        this.f = addFloatArray(f,mh.getF());
        this.texCoords = addFloatArray(texCoords,mh.getTexCoords());
        this.faces = addIntArray(faces,traslateFaces(mh.getFaces(),numPoints,numTexCoords));
        this.faceSmoothingGroups = addIntArray(faceSmoothingGroups,mh.getFaceSmoothingGroups());
    }
    
    /*
    Add to the meshHelper a list of meshHelpers, to store the data of several
    meshes that can be joined into one
    */
    public void addMesh(List<MeshHelper> mhs){
        mhs.forEach(mh->{
            int numPoints = points.length/3;
            int numTexCoords = texCoords.length/2;
            this.points = addFloatArray(points,mh.getPoints());
            this.f = addFloatArray(f,mh.getF());
            this.texCoords = addFloatArray(texCoords,mh.getTexCoords());
            this.faces = addIntArray(faces,traslateFaces(mh.getFaces(),numPoints,numTexCoords));
            this.faceSmoothingGroups = addIntArray(faceSmoothingGroups,mh.getFaceSmoothingGroups());
        });
    }
    
    /*
    Add to the meshHelper a new meshHelper, and given a list of new positions,
    the data of a list of meshes on these locations will be created, to store the data of 
    these meshes that can be joined into one
    */
    public void addMesh(MeshHelper mh, List<Point3D> traslate){
        float[] newPoints = new float[points.length+mh.getPoints().length*traslate.size()];
        float[] newF = new float[f.length+mh.getF().length*traslate.size()];
        float[] newTexCoords = new float[texCoords.length+mh.getTexCoords().length*traslate.size()];
        int[] newFaces = new int[faces.length+mh.getFaces().length*traslate.size()];
        int[] newFaceSmoothingGroups = new int[faceSmoothingGroups.length+mh.getFaceSmoothingGroups().length*traslate.size()];
        System.arraycopy(points, 0, newPoints, 0, points.length);
        System.arraycopy(f, 0, newF, 0, f.length);
        System.arraycopy(texCoords, 0, newTexCoords, 0, texCoords.length);
        System.arraycopy(faces, 0, newFaces, 0, faces.length);
        System.arraycopy(faceSmoothingGroups, 0, newFaceSmoothingGroups, 0, faceSmoothingGroups.length);
        int numPoints = mh.getPoints().length;
        int numF = mh.getF().length;
        int numTexCoords = mh.getTexCoords().length;
        int numFaces = mh.getFaces().length;
        int numFaceSmoothingGroups = mh.getFaceSmoothingGroups().length;
        AtomicInteger count=new AtomicInteger();
        
//        List<float[]> collect = traslate.parallelStream().map(p3d->transform(mh.getPoints(),p3d)).collect(Collectors.toList());
        
        traslate.forEach(p3d->{
            System.arraycopy(transform(mh.getPoints(),p3d), 0, newPoints, points.length+numPoints*count.get(), mh.getPoints().length);
            float[] ff=mh.getF();
            Arrays.fill(ff,p3d.f);
            System.arraycopy(ff, 0, newF, f.length+numF*count.get(), ff.length);
            System.arraycopy(mh.getTexCoords(), 0, newTexCoords, texCoords.length+numTexCoords*count.get(), mh.getTexCoords().length);
            System.arraycopy(traslateFaces(mh.getFaces(),numPoints/3*(count.get()+1),numTexCoords/2*(count.get()+1)), 0, newFaces, faces.length+numFaces*count.get(), mh.getFaces().length);
            System.arraycopy(mh.getFaceSmoothingGroups(), 0, newFaceSmoothingGroups, faceSmoothingGroups.length+numFaceSmoothingGroups*count.getAndIncrement(), mh.getFaceSmoothingGroups().length);
        });
        points=newPoints;
        f=newF;
        texCoords=newTexCoords;
        faces=newFaces;
        faceSmoothingGroups=newFaceSmoothingGroups;
    }
    
    private int[] traslateFaces(int[] faces, int points, int texCoords){
        int[] newFaces=new int[faces.length];
        for(int i=0; i<faces.length; i++){
            newFaces[i]=faces[i]+(i%2==0?points:texCoords);
        }
        return newFaces;
    }
    
    private int[] addIntArray(int[] array1, int[] array2){
        int[] array1and2 = new int[array1.length + array2.length];
        System.arraycopy(array1, 0, array1and2, 0, array1.length);
        System.arraycopy(array2, 0, array1and2, array1.length, array2.length);
        return array1and2;
    }
    
    private float[] addFloatArray(float[] array1, float[] array2){
        float[] array1and2 = new float[array1.length + array2.length];
        System.arraycopy(array1, 0, array1and2, 0, array1.length);
        System.arraycopy(array2, 0, array1and2, array1.length, array2.length);
        return array1and2;
    }

    private float[] transform(float[] points, Point3D p3d) {
        float[] newPoints=new float[points.length];
        for(int i=0; i<points.length/3; i++){
            newPoints[3*i]=points[3*i]+p3d.x;
            newPoints[3*i+1]=points[3*i+1]+p3d.y;
            newPoints[3*i+2]=points[3*i+2]+p3d.z;
        }
        return newPoints;
    }
}
