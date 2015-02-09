
package org.fxyz.scene;

import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SnapshotParameters;
import javafx.scene.SubScene;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import org.fxyz.utils.CameraTransformer;
import org.fxyz.event.CloseCutawayEvent;

/**
 * Builds upon base CameraView class but provides event handling for 
 * dragging and closing the cutaway
 * 
 * @author Sean
 */
public final class Cutaway extends VBox {
    
    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;
    private double mouseDeltaX;
    private double mouseDeltaY;

    private double cutawayPosX;
    private double cutawayPosY;
    private double cutawayOldX;
    private double cutawayOldY;
    private double cutawayDeltaX;
    private double cutawayDeltaY;
    
    private final SnapshotParameters params = new SnapshotParameters();
    private WritableImage image = null;    

    private double startX = 0;
    private double startY = 0;

    public CameraTransformer cameraTransform = new CameraTransformer();
    public PerspectiveCamera camera;
    public Rotate rx = new Rotate(0,0,0,0,Rotate.X_AXIS),
            ry = new Rotate(0,0,0,0,Rotate.X_AXIS),
            rz = new Rotate(0,0,0,0,Rotate.X_AXIS);
    public Translate t = new Translate(0,0,0);
    
    private Group worldToView;

    private AnimationTimer viewTimer = null;
    public ImageView imageView = new ImageView();
    
    private double controlSize = 15;
    
    public Cutaway(SubScene scene, double width, double height) {
        // Make sure "world" is a group
        assert scene.getRoot().getClass().equals(Group.class);
        
        setFillWidth(true);
        setPrefSize(width, height+controlSize);
        setMaxSize(width, height+controlSize);
        this.setBorder(new Border(
            new BorderStroke(Color.DARKKHAKI,
                             BorderStrokeStyle.SOLID,
                             new CornerRadii(25),
                             new BorderWidths(5)
                             )));
        
        Rectangle closeSquare = new Rectangle(controlSize, controlSize, Color.LIGHTCORAL);
        closeSquare.setOnMouseClicked((MouseEvent me) -> {
            this.fireEvent(new CloseCutawayEvent(this));
        });
        Circle dragCircle = new Circle(controlSize/2, Color.STEELBLUE);        

        HBox top = new HBox(20,closeSquare,dragCircle);
        dragCircle.setVisible(false);
        StackPane.setAlignment(closeSquare, Pos.TOP_RIGHT);
        StackPane.setMargin(closeSquare, new Insets(50));        
        
        top.setBackground(new Background(
            new BackgroundFill(Color.KHAKI, 
                new CornerRadii(20,20,0,0,false),
                Insets.EMPTY)));
        
        top.setAlignment(Pos.TOP_RIGHT);

        top.setOnMousePressed((MouseEvent me) -> {
            cutawayPosX = me.getSceneX();
            cutawayPosY = me.getSceneY();
            cutawayOldX = me.getSceneX();
            cutawayOldY = me.getSceneY();
            
        });
        top.setOnMouseDragged((MouseEvent me) -> {
            cutawayOldX = cutawayPosX;
            cutawayOldY = cutawayPosY;
            cutawayPosX = me.getSceneX();
            cutawayPosY = me.getSceneY();
            cutawayDeltaX = (cutawayPosX - cutawayOldX);
            cutawayDeltaY = (cutawayPosY - cutawayOldY);
            setTranslateX(getTranslateX() + cutawayDeltaX);
            setTranslateY(getTranslateY() + cutawayDeltaY);
        });
        
        getChildren().addAll(top,imageView);
        
        
        
        worldToView = (Group)scene.getRoot();
               
        camera = new PerspectiveCamera(true);
        cameraTransform.getChildren().add(camera);
        camera.setNearClip(0.1);
        camera.setFarClip(15000.0);
        camera.setTranslateZ(-1500);
        params.setCamera(camera);
        
        params.setDepthBuffer(true);
        params.setFill(Color.rgb(0, 0, 0, 0.5));

        viewTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                redraw();
            }
        };
        setOnMouseEntered(e->{
            requestFocus();
        });        
    }
    
    public void startViewing() {
        viewTimer.start();
    }

    public void pause() {
        viewTimer.stop();
    }

    public void setFirstPersonNavigationEabled(boolean b) {
        if (b) {
            // Navigation
            setMouseTransparent(false);
            
            //First person shooter keyboard movement
        imageView.setOnKeyPressed(event -> {
            double change = 10.0;
            //Add shift modifier to simulate "Running Speed"
            if(event.isShiftDown()) { change = 50.0; }
            //What key did the user press?
            KeyCode keycode = event.getCode();
            //Step 2c: Add Zoom controls
            if(keycode == KeyCode.W) { camera.setTranslateZ(camera.getTranslateZ() + change); }
            if(keycode == KeyCode.S) { camera.setTranslateZ(camera.getTranslateZ() - change); }
            //Step 2d: Add Strafe controls
            if(keycode == KeyCode.A) { camera.setTranslateX(camera.getTranslateX() - change); }
            if(keycode == KeyCode.D) { camera.setTranslateX(camera.getTranslateX() + change); }            
        });
        imageView.setOnScroll((ScrollEvent event) -> {
            event.consume();
            if (event.getDeltaY() == 0) {
                return;
            }
            double change = event.getDeltaY();
            //Add shift modifier to simulate "Running Speed"
            if (event.isShiftDown()) {
                change *= 2;
            }
            camera.setTranslateZ(camera.getTranslateZ() + change);
        });       
        imageView.setOnMousePressed((MouseEvent me) -> {
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            mouseOldX = me.getSceneX();
            mouseOldY = me.getSceneY();
            
        });
        imageView.setOnMouseDragged((MouseEvent me) -> {
            mouseOldX = mousePosX;
            mouseOldY = mousePosY;
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            mouseDeltaX = (mousePosX - mouseOldX);
            mouseDeltaY = (mousePosY - mouseOldY);
            
            double modifier = 10.0;
            double modifierFactor = 0.1;
            
            if (me.isControlDown()) {
                modifier = 0.1;
            }
            if (me.isShiftDown()) {
                modifier = 50.0;
            }
            if (me.isPrimaryButtonDown()) {
                cameraTransform.ry.setAngle(((cameraTransform.ry.getAngle() + mouseDeltaX * modifierFactor * modifier * 2.0) % 360 + 540) % 360 - 180); // +
                cameraTransform.rx.setAngle(((cameraTransform.rx.getAngle() - mouseDeltaY * modifierFactor * modifier * 2.0) % 360 + 540) % 360 - 180); // -                
            } else if (me.isSecondaryButtonDown()) {
                double z = camera.getTranslateZ();
                double newZ = z + mouseDeltaX * modifierFactor * modifier;
                camera.setTranslateZ(newZ);
            } else if (me.isMiddleButtonDown() ) {
                cameraTransform.t.setX(cameraTransform.t.getX() + mouseDeltaX * modifierFactor * modifier * 0.3); // -
                cameraTransform.t.setY(cameraTransform.t.getY() + mouseDeltaY * modifierFactor * modifier * 0.3); // -
                
            }
        });
            
        }else{
            imageView.setOnMouseDragged(null);
            imageView.setOnScroll(null);
            imageView.setOnMousePressed(null);
            imageView.setOnKeyPressed(null);
            imageView.setMouseTransparent(true);
        }
    }

    private void redraw() {

        params.setViewport(new Rectangle2D(0, 0, imageView.getFitWidth(), imageView.getFitHeight()));
        if (image == null
                || image.getWidth() != imageView.getFitWidth() || image.getHeight() != imageView.getFitHeight()) {
            image = worldToView.snapshot(params, null);
        } else {
            worldToView.snapshot(params, image);
        }
        // set a clip to apply rounded border to the original image.
        Rectangle clip = new Rectangle(
            imageView.getFitWidth(), imageView.getFitHeight()
        );
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        imageView.setClip(clip);        
        imageView.setImage(image);
    }

    
    public PerspectiveCamera getCamera() {
        return camera;
    }

    public Rotate getRx() {
        return rx;
    }

    public Rotate getRy() {
        return ry;
    }

    public Rotate getRz() {
        return rz;
    }

    public Translate getT() {
        return t;
    }
}
