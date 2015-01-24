package org.fxyz.shapes.primitives.helper;

/**
 *
 * @author jpereda
 */
public class MeshHelper {

    private float[] points;
    private float[] texCoords;
    private int[] faces;
    private int[] faceSmoothingGroups;

    public MeshHelper() {
    }
    
    public MeshHelper(float[] points, float[] texCoords, int[] faces, int[] faceSmoothingGroups) {
        this.points = points;
        this.texCoords = texCoords;
        this.faces = faces;
        this.faceSmoothingGroups = faceSmoothingGroups;
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
    
}
