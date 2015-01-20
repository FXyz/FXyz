/*
 * Copyright (C) 2013-2015 jpereda, 
 * Sean Phillips, Jason Pollastrini
 * All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fxyz.io;

import org.fxyz.scene.paint.Patterns;
import org.fxyz.scene.paint.Palette;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.TriangleMesh;
import javax.imageio.ImageIO;
import org.fxyz.geometry.Point3D;
import org.fxyz.shapes.primitives.helper.TriangleMeshHelper.TextureType;

/**
 *
 * @author jpereda
 */
public class OBJWriter {
    
    private final String newline = System.getProperty("line.separator");
    private float[] points0, texCoord0;
    private int[] faces0, sm0;
    private BufferedWriter writer = null;
    private final TriangleMesh mesh;
    private final String fileName;
    private String diffuseMap;
    private String diffuseColor="0.0 0.0 0.0"; // black
    private TextureType defaultTexture=TextureType.NONE;
        
    public OBJWriter(TriangleMesh mesh, String fileName){
        this.mesh=mesh;
        this.fileName=fileName;
    }
    
    public void setMaterialColor(Color color){
        diffuseColor=""+((float)(color.getRed()))+" "+((float)(color.getGreen()))+" "+((float)(color.getBlue()));
    }
    
    public void setTextureColors(int numColors){
        if(numColors>0){
            defaultTexture=TextureType.COLORED_VERTICES_3D;
            Palette palette=new Palette(numColors);
            palette.createPalette(true);
            diffuseMap="palette_"+numColors+".png";
        }
    }
    public void setTexturePattern(){
        defaultTexture=TextureType.PATTERN;
        Patterns pattern = new Patterns(12, 12);
        pattern.createPattern(true);
        diffuseMap="patterne_12x12.png";
    }
    
    public void setTextureImage(String image){
        try {
            // save
            ImageIO.write(SwingFXUtils.fromFXImage(new Image(image), null), "png", new File("image.png"));
        } catch (IOException ex) { 
            System.out.println("Error saving image");
        }
        diffuseMap="image.png";
    }
    
    public void exportMesh(){
        File objFile = new File(fileName+".obj");
        try{
            writer = new BufferedWriter(new FileWriter(objFile));
            
            writer.write("# Material"+newline);
            writer.write("mtllib "+fileName+".mtl"+newline);
            
            points0=new float[mesh.getPoints().size()];
            mesh.getPoints().toArray(points0);
            List<Point3D> points1 = IntStream.range(0, points0.length/3)
                .mapToObj(i -> new Point3D(points0[3*i], points0[3*i+1], points0[3*i+2]))
                .collect(Collectors.toList());
            
            writer.write("# Vertices ("+points1.size()+")"+newline);
            points1.forEach(p->{
                try {
                    writer.write("v "+p.x+" "+p.y+" "+p.z+""+newline);
                } catch (IOException ex) {
                    System.out.println("Error writting vertex "+ex);
                }
            });
            writer.write("# End Vertices"+newline);
            writer.write(newline);
            
            texCoord0=new float[mesh.getTexCoords().size()];
            mesh.getTexCoords().toArray(texCoord0);
            
            List<Point2D> texCoord1 = IntStream.range(0, texCoord0.length/2)
                    .mapToObj(i -> new Point2D(texCoord0[2*i], texCoord0[2*i+1]))
                    .collect(Collectors.toList());
            
            writer.write("# Textures Coordinates ("+texCoord1.size()+")"+newline);
            texCoord1.forEach(t->{
                try {
                    // objimporter u->u, v->(1-v)
                    writer.write("vt "+((float)t.getX())+" "+((float)(1d-t.getY()))+""+newline);
                } catch (IOException ex) {
                    System.out.println("Error writting texture coordinate "+ex);
                }
            });
            writer.write("# End Texture Coordinates "+newline);
            writer.write(newline);
            
            faces0=new int[mesh.getFaces().size()];
            mesh.getFaces().toArray(faces0);
            List<Integer[]> faces1 = IntStream.range(0, faces0.length/6)
                    .mapToObj(i -> new Integer[]{faces0[6*i], faces0[6*i+1], 
                        faces0[6*i+2], faces0[6*i+3], 
                        faces0[6*i+4], faces0[6*i+5]})
                    .collect(Collectors.toList());
            
            writer.write("# Faces ("+faces1.size()+")"+newline);
            writer.write("# Material"+newline);
            writer.write("usemtl "+fileName+""+newline);
            sm0=new int[mesh.getFaces().size()];
            mesh.getFaceSmoothingGroups().toArray(sm0);
            if(sm0[0]>0){
                writer.write("s "+sm0[0]+""+newline);
            }
            AtomicInteger count = new AtomicInteger();
            faces1.forEach(f->{
                try {
                    writer.write("f "+(f[0]+1)+"/"+(f[1]+1)+
                                 " "+(f[2]+1)+"/"+(f[3]+1)+
                                 " "+(f[4]+1)+"/"+(f[5]+1)+""+newline);
                    if(sm0[count.getAndIncrement()]!=sm0[count.get()]){
                        writer.write("s "+(sm0[count.get()]>0?sm0[count.get()]:"off")+""+newline);
                    }
                } catch (IOException ex) {
                    System.out.println("Error writting face "+ex);
                }
            });
            writer.write("# End Faces "+newline);
            writer.write(newline);
            
        } catch(IOException io){
             System.out.println("Error creating writer obj "+io);
        } finally {
            try {
                if(writer!=null){
                    writer.close();
                }
            } catch (Exception e) {}
        }
        
        File mtlFile = new File(fileName+".mtl");
        try{
            writer = new BufferedWriter(new FileWriter(mtlFile));
            writer.write("# Material "+fileName+""+newline);
            writer.write("newmtl "+fileName+""+newline);
            writer.write("illum 4"+newline); // Illumination [0-10]
            writer.write("Kd "+diffuseColor+""+newline); // diffuse color black
            writer.write("Ka 0.10 0.10 0.10"+newline); // ambient color
            writer.write("Tf 1.00 1.00 1.00"+newline); // Transmission filter
            if(diffuseMap!=null){
                writer.write("map_Kd "+diffuseMap+""+newline);
            }
            writer.write("Ni 1.00"+newline); // optical density
            writer.write("Ks 1.00 1.00 1.00"+newline); // specular reflectivity
            writer.write("Ns 32.00"+newline); // specular exponent
            
        } catch(IOException io){
             System.out.println("Error creating writer mtl "+io);
        } finally {
            try {
                if(writer!=null){
                    writer.close();
                }
            } catch (Exception e) {}
        }
    }
}
