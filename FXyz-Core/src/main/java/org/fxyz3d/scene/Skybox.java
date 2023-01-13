/**
 * Skybox.java
 *
 * Copyright (c) 2013-2023, F(X)yz
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

package org.fxyz3d.scene;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;

/** 
 * 
 * @author Dub
 */
public class Skybox extends Group{
        
    public enum SkyboxImageType{
        MULTIPLE, SINGLE
    }
        
    private final Affine affine = new Affine();
    private final ImageView 
            top   = new ImageView(),
            bottom= new ImageView(),
            left  = new ImageView(),
            right = new ImageView(),            
            back  = new ImageView(),
            front = new ImageView();
    {
        top.setId("top ");
        bottom.setId("bottom ");
        left.setId("left ");
        right.setId("right ");
        back.setId("back ");
        front.setId("front ");
    }
    private final ImageView[] views = new ImageView[]{
        top,   
        left,
        back,
        right,  
        front, 
        bottom  
    };
    private Image 
            topImg, bottomImg, leftImg, rightImg, frontImg, backImg, singleImg;
    private final PerspectiveCamera camera;
    private final SkyboxImageType imageType;
    /**
     * Projects ImageViews in a way that creates a seamless (mostly) view.
     * Images are projected using an Affine transform which is updated whenever 
     * the camera is changed. AnimationTimer is used to synchronize projection
     * updates with camera changes.
     * 
     * @param singleImg One image which is chunked up in this pattern
     *              ____
     *             |top |
     *         ____|____|____ ____
     *        |left|fwd |rght|back|
     *        |____|____|____|____|
     *             |bot |
     *             |____|
     * @param size effective distance to have the image panels projected from
     * the location of the camera
     * @param camera The camera to track
     * 
     */
    public Skybox(Image singleImg, double size, PerspectiveCamera camera) {
        super();
        this.imageType = SkyboxImageType.SINGLE;
        
        this.singleImg = singleImg;
        this.size.set(size);
        this.camera = camera;     
        
        getTransforms().add(affine);
        
        loadImageViews();
        
        getChildren().addAll(views);
    }
    /**
     * Projects ImageViews in a way that creates a seamless (mostly) view.
     * Images are projected using an Affine transform which is updated whenever 
     * the camera is changed.AnimationTimer is used to synchronize projection 
     * updates with camera changes.The images are arranged like this: 
              ____
             |top |
         ____|____|____ ____
        |left|fwd |rght|back|
        |____|____|____|____|
             |bot |
             |____|
     * It is recommended that each image be exactly square in pixels and the same size.
     * 
     * @param topImg The image on top, camera look -y
     * @param bottomImg The image on the bottom, camera look +y
     * @param leftImg The image to the left, camera look -x
     * @param rightImg The image to the right, camera look +x
     * @param frontImg The image initially toward the screen, camera look +z
     * @param backImg The image behind the camera, camera look -z
     * @param size effective distance to have the image panels projected from
     * the location of the camera
     * @param camera The camera to track
     * 
     */
    public Skybox(Image topImg, Image bottomImg, Image leftImg, Image rightImg, Image frontImg, Image backImg, double size, PerspectiveCamera camera) {
        super();            
        this.imageType = SkyboxImageType.MULTIPLE;
                
        this.topImg = topImg;
        this.bottomImg = bottomImg;
        this.leftImg = leftImg;
        this.rightImg = rightImg;
        this.frontImg = frontImg;
        this.backImg = backImg;
        this.size.set(size);
        this.camera = camera;
        
        loadImageViews();
        
        getTransforms().add(affine);
                        
        getChildren().addAll(views);
        // @since 0.5.5
        if (camera != null) {
            camera.localToSceneTransformProperty().addListener((obs, ov, ct) -> {
                if (ct != null) {
                    affine.setTx(ct.getTx());
                    affine.setTy(ct.getTy());
                    affine.setTz(ct.getTz());
                }
            });
        }
    }
    private void loadImageViews(){
        for(ImageView iv : views){      
            iv.setSmooth(true);
            iv.setPreserveRatio(true);            
        }
        validateImageType();
    }

    private void layoutViews() {
        
        for(ImageView v : views){
            v.setFitWidth(getSize());
            v.setFitHeight(getSize());
        }
        
        back.setTranslateX(-0.5 * getSize());
        back.setTranslateY(-0.5 * getSize());
        back.setTranslateZ(-0.5 * getSize());          
                
        front.setTranslateX(-0.5 * getSize());
        front.setTranslateY(-0.5 * getSize());
        front.setTranslateZ(0.5 * getSize());
        front.setRotationAxis(Rotate.Z_AXIS);
        front.setRotate(-180);
        front.getTransforms().add(new Rotate(180,front.getFitHeight() / 2, 0,0, Rotate.X_AXIS));
        front.setTranslateY(front.getTranslateY() - getSize());
                
        top.setTranslateX(-0.5 * getSize());
        top.setTranslateY(-1 * getSize());
        top.setRotationAxis(Rotate.X_AXIS);
        top.setRotate(-90);
                
        bottom.setTranslateX(-0.5 * getSize());
        bottom.setTranslateY(0);
        bottom.setRotationAxis(Rotate.X_AXIS);
        bottom.setRotate(90);
                
        left.setTranslateX(-1 * getSize());
        left.setTranslateY(-0.5 * getSize());
        left.setRotationAxis(Rotate.Y_AXIS);
        left.setRotate(90);        
        
        right.setTranslateX(0);
        right.setTranslateY(-0.5 * getSize());
        right.setRotationAxis(Rotate.Y_AXIS);
        right.setRotate(-90);      
       
    }
    
    /**
     *  for single image creates viewports and sets all views(image) to singleImg
     *  for multiple... sets images per view.
     */
    private void validateImageType(){
        switch(imageType){
            case SINGLE:
                loadSingleImageViewports();
                break;
            case MULTIPLE:
                setMultipleImages();
                break;
        }
    }
    /**
     * this will layout the viewports to this style pattern
     *              ____
     *             |top |
     *         ____|____|____ ____
     *        |left|fwd |rght|back|
     *        |____|____|____|____|
     *             |bot |
     *             |____|
     * 
     */
    private void loadSingleImageViewports(){        
        layoutViews();
        double width = singleImg.getWidth(),
               height = singleImg.getHeight();
               
        //simple chack to see if cells will be square
        if(width / 4 != height / 3){
            throw new UnsupportedOperationException("Image does not comply with size constraints");            
        }
        double cellSize = singleImg.getWidth() - singleImg.getHeight();               
        
        recalculateSize(cellSize);        
        
        double  
                topx = cellSize, topy = 0, 
                
                botx = cellSize, boty = cellSize * 2,
                
                leftx = 0, lefty = cellSize,
                
                rightx = cellSize * 2, righty = cellSize,
                
                fwdx = cellSize, fwdy = cellSize,
                
                backx = cellSize * 3, backy = cellSize;
        
        //add top padding x+, y+, width-, height
        top.setViewport(new Rectangle2D(topx , topy , cellSize, cellSize ));
        
        //add left padding x, y+, width, height-
        left.setViewport(new Rectangle2D(leftx , lefty , cellSize - 1, cellSize - 1));
        
        //add front padding x+, y+, width-, height
        back.setViewport(new Rectangle2D(fwdx , fwdy, cellSize , cellSize));
        
        //add right padding x, y+, width, height-
        right.setViewport(new Rectangle2D(rightx, righty , cellSize , cellSize ));
        
        //add back padding x, y+, width, height-
        front.setViewport(new Rectangle2D(backx + 1, backy - 1, cellSize - 1, cellSize - 1));
        
        //add bottom padding x+, y, width-, height-
        bottom.setViewport(new Rectangle2D(botx, boty, cellSize , cellSize));
        
        for(ImageView v : views){
            v.setImage(singleImg);
            //System.out.println(v.getId() + v.getViewport() + cellSize);
        }       
    }
    
    private void recalculateSize(double cell){        
        double factor = Math.floor(getSize() / cell);
        setSize(cell * factor);         
    }
    
    private void setMultipleImages() {        
        layoutViews();
        
        back.setImage(backImg);
        front.setImage(frontImg);
        top.setImage(topImg);
        bottom.setImage(bottomImg);
        left.setImage(leftImg);
        right.setImage(rightImg);
    }
    
    /*
        Properties
    */
    private final DoubleProperty size = new SimpleDoubleProperty(){
        @Override
        protected void invalidated() {  
            switch(imageType){
            case SINGLE:
                layoutViews();
                break;
            case MULTIPLE:
                break;
            }
            
        }        
    };

    public final double getSize() {
        return size.get();
    }

    public final void setSize(double value) {
        size.set(value);
    }

    public DoubleProperty sizeProperty() {
        return size;
    }
    
}
