package multimedeapro;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import java.io.File;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import java.awt.image.RenderedImage;
import javafx.stage.Stage;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import static java.lang.System.out;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import static javafx.scene.paint.Color.color;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import java.awt.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.*;
import java.util.ArrayList;
import javafx.event.EventType;
import javafx.stage.StageStyle;
import static javax.swing.Spring.height;
import static javax.swing.Spring.width;
class Position{
    public int row;
    public int column;
    public Color color;
    public Position (int row , int column , Color color){
        this.row=row;
        this.column=column;
        this.color=color;
    }
}
class Check{
    public Color color ;
    public boolean check=false;
}
class FILLFLOOD {
    public static int height;
    public static int width;
     public FILLFLOOD (int height , int width){
         this.height=height;
         this.width=width;
     }
   public static void fill(Color image[][], int row, int column,Color value, Color color){
    if (column >= width-1 || row <= 0 || row >= height-1 || column <= 0)
        return;
    if (!image[row][column].equals(value))
        return;
    image[row][column] = color;
    fill(image, row+1, column, value, color);
    fill(image, row-1, column, value, color);
    fill(image, row, column+1, value, color);
    fill(image, row, column-1, value, color);
}
    public static void fillFlood(Color image[][], int row, int column, Color color)
{
    Color value = image[row][column];
      if(value==color) return;
    fill(image, row, column, value, color);
}
}
public class Multimedeapro extends Application {
    private static double SCENE_WIDTH = 500;
    private static double SCENE_HEIGHT = 600;
    Canvas canvas;
    GraphicsContext graphicsContext;
    AnimationTimer loop;
    Point2D mouseLocation = new Point2D(0, 0);
    boolean mousePressed = false;
    Point2D prevMouseLocation = new Point2D(0, 0);
    Scene scene;
    double brushMaxSize = 10;
    double pressure = 0;
    double pressureDelay = 0.04;
    double pressureDirection = 1;
    double strokeTimeMax = 1;
    double strokeTime = 0;
    double strokeTimeDelay = 0.07;
    private Image[] brushVariations = new Image[256];
    private BufferedImage  bufferedImage;
    ColorPicker colorPicker = new ColorPicker();
    Color originalColor = Color.BLUE;
    ImageView myImageView;
    private WritableImage  viewImage;
    private WritableImage  canvasImage;
    public static Color [][] img;
    Check[][] can;
    Color canvasColor = Color.BLUE;
    ArrayList<Position>position= new ArrayList<Position>();
    @Override
    public void start(Stage primaryStage) {
        StackPane layerPane = new StackPane();
        Button btnLoad = new Button("Load Image");
        btnLoad.setOnAction(btnLoadEventListener);
        Button btnColor = new Button("color image");
        btnColor.setOnAction(buttonHandler);
        Button btnSave = new Button("save image");
        btnSave.setOnAction(btnSaveEventListener);
        VBox root = new VBox();
        myImageView = new ImageView();
        myImageView.setFitHeight(500);
        myImageView.setFitWidth(600);
        canvas = new Canvas(myImageView.getFitWidth(), myImageView.getFitHeight());
        canvas.setLayoutX(myImageView.getX());
        canvas.setLayoutY(myImageView.getY());
        canvas.setHeight(500);
        canvas.setWidth(600);
        graphicsContext = canvas.getGraphicsContext2D();
        layerPane.getChildren().add(myImageView);
        layerPane.getChildren().add(canvas);
        colorPicker.setValue(Color.BLUE);
        colorPicker.setOnAction(e -> {
        createBrushVariations();
        });
        root.getChildren().add(layerPane);
        root.getChildren().add(colorPicker);
        root.getChildren().add(btnLoad);
        root.getChildren().add(btnColor);
        root.getChildren().add(btnSave);
        root.setAlignment(Pos.TOP_CENTER);
        scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT, Color.WHITE);
        primaryStage.setScene(scene);
        primaryStage.show();
        createBrushVariations();
        addListeners();
        startAnimation();
    }
    private void createBrushVariations() {
        for (int i = 0; i < brushVariations.length; i++) {

            double size = (brushMaxSize - 1) / (double) brushVariations.length * (double) i + 1;

            brushVariations[i] = createBrush(size, colorPicker.getValue());
        }

    }

    private void startAnimation() {

        loop = new AnimationTimer() {

            @Override
            public void handle(long now) {
             
                if (mousePressed) {
                     canvasImage = new WritableImage((int)canvas.getWidth(),(int)canvas.getHeight());
                SnapshotParameters sp = new SnapshotParameters();
                sp.getFill();
                canvas.snapshot(sp, canvasImage);
                for (int i=0;i<canvasImage.getHeight();i++){
                   for (int j=0;j<canvasImage.getWidth();j++){
                       if (can[i][j]!=null)
                       can[i][j].color= canvasImage.getPixelReader().getColor(j, i);
                   }
               }
                for (int i=0 ; i<canvasImage.getHeight();i++){
                    for(int j=0;j<canvasImage.getWidth();j++){
                       if (can[i][j]!=null){ 
                        if (!can[i][j].color.equals(canvasColor)&&can[i][j].check==false){
                            Position p = new Position(i,j,colorPicker.getValue());
                            position.add(p);
                            break;
                        }
                       }
                    }
                }
                 for (int i=0 ; i<canvasImage.getHeight();i++){
                     for(int j=0;j<canvasImage.getWidth();j++){
                        if (can[i][j]!=null){
                         if (!can[i][j].color.equals(canvasColor)){
                          can[i][j].check=true;
                        }
                        }
                    }
                }
    
                    bresenhamLine(prevMouseLocation.getX(), prevMouseLocation.getY(), mouseLocation.getX(), mouseLocation.getY());
                 
                    
                    strokeTime += strokeTimeDelay * pressureDirection;

                    if (strokeTime > strokeTimeMax) {
                        pressureDirection = -1;
                    }
                    if (strokeTime > 0) {
                        pressure += pressureDelay * pressureDirection;
                        if (pressure > 1) {
                            pressure = 1;
                        } else if (pressure < 0) {
                            pressure = 0;
                        }

                    } else {

                        pressure = 0;

                    }

                } else {

                    pressure = 0;
                    pressureDirection = 1;
                    strokeTime = 0;

                }

                prevMouseLocation = new Point2D(mouseLocation.getX(), mouseLocation.getY());

            }
        };

        loop.start();

    }

    private void bresenhamLine(double x0, double y0, double x1, double y1) {
        double dx = Math.abs(x1 - x0), sx = x0 < x1 ? 1. : -1.;
        double dy = -Math.abs(y1 - y0), sy = y0 < y1 ? 1. : -1.;
        double err = dx + dy, e2; /* error value e_xy */

        while (true) {

            int variation = (int) (pressure * (brushVariations.length - 1));
            Image brushVariation = brushVariations[variation];

            graphicsContext.setGlobalAlpha(pressure);
            graphicsContext.drawImage(brushVariation, x0 - brushVariation.getWidth() / 2.0, y0 - brushVariation.getHeight() / 2.0);
              
            if (x0 == x1 && y0 == y1)
                break;
            e2 = 2. * err;
            if (e2 > dy) {
                err += dy;
                x0 += sx;
            } /* e_xy+e_x > 0 */
            if (e2 < dx) {
                err += dx;
                y0 += sy;
            } /* e_xy+e_y < 0 */
        }
    }

    private void addListeners() {

        canvas.addEventFilter(MouseEvent.ANY, e -> {
            mouseLocation = new Point2D(e.getX(), e.getY());
            mousePressed = e.isPrimaryButtonDown();

        });
        
    }

    public static Image createImage(Node node) {

        WritableImage wi;

        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);

        int imageWidth = (int) node.getBoundsInLocal().getWidth();
        int imageHeight = (int) node.getBoundsInLocal().getHeight();

        wi = new WritableImage(imageWidth, imageHeight);
        node.snapshot(parameters, wi);

        return wi;

    }

    public static Image createBrush(double radius, Color color) {
        Circle brush = new Circle(radius);

        RadialGradient gradient1 = new RadialGradient(0, 0, 0, 0, radius, false, CycleMethod.NO_CYCLE, new Stop(0, color.deriveColor(1, 1, 1, 0.3)), new Stop(1, color.deriveColor(1, 1, 1, 0)));

        brush.setFill(gradient1);
        return createImage(brush);
    }

    public static void main(String[] args) {
        launch(args);
      
    }
    EventHandler<ActionEvent> btnLoadEventListener
            = new EventHandler<ActionEvent>(){

        @Override
        public void handle(ActionEvent t) {
             canvasImage = new WritableImage((int)canvas.getWidth(),(int)canvas.getHeight());
               SnapshotParameters sp = new SnapshotParameters();
               sp.getFill();
               canvas.snapshot(sp, canvasImage);
               can = new Check [(int)canvasImage.getHeight()][(int)canvasImage.getWidth()];
               canvasColor=canvasImage.getPixelReader().getColor(0,0);
               for (int i=0;i<canvasImage.getHeight();i++){
                   for (int j=0;j<canvasImage.getWidth();j++){
                       can[i][j]= new Check();
                       can[i][j].check=false;
                   }
               }
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(null);

            try {
                 BufferedImage img = new BufferedImage(600,500,BufferedImage.TYPE_INT_RGB);
                 bufferedImage = ImageIO.read(file);
                 Graphics2D g = img.createGraphics();
                 g.drawImage(bufferedImage,0,0,600,500,0,0,bufferedImage.getWidth(),bufferedImage.getHeight(),null);
                 g.dispose();
                 viewImage = SwingFXUtils.toFXImage(img, null);
                 myImageView.setImage(viewImage);

            } catch (IOException ex) {
                Logger.getLogger(Multimedeapro.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    };
    EventHandler<ActionEvent>btnSaveEventListener = new EventHandler<ActionEvent>(){
        @Override
        public void handle(ActionEvent t) {
            String s = "ABCDEFGHIJKLMNOPQRS"+"abcdefghijklmnopqrs"+"0123456789";
            StringBuilder filename = new StringBuilder(10);
            for(int i=0 ; i< 10 ;i++){
            int index=(int)(s.length()*Math.random());
            filename.append(s.charAt(index));
        }
            try {
            File file = new File("C:\\Users\\Lenovo\\Desktop\\"+filename.toString()+".PNG");
            RenderedImage renderedImage = SwingFXUtils.fromFXImage(viewImage, null);
            
                ImageIO.write(renderedImage, "png", file);
            } catch (IOException ex) {
                Logger.getLogger(Multimedeapro.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        };
    EventHandler<ActionEvent> buttonHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) { 

                img = new Color [(int)viewImage.getHeight()][(int)viewImage.getWidth()];
                for (int i=0 ; i<viewImage.getHeight();i++){
                    for (int j=0 ; j<viewImage.getWidth();j++){
                        img[i][j] = viewImage.getPixelReader().getColor(j, i);
                    }
                }
                    for (int i=0;i<position.size();i++){
                    FILLFLOOD fillflood = new FILLFLOOD((int)viewImage.getHeight(),(int)viewImage.getWidth());   
                    fillflood.fillFlood(img,position.get(i).row,position.get(i).column,position.get(i).color);
                    }
                    PixelWriter writer = viewImage.getPixelWriter();
                    for (int i=0 ; i<viewImage.getHeight();i++){
                    for (int j=0 ; j<viewImage.getWidth();j++){
                       writer.setColor(j, i, img[i][j]);  
                    }
                }
                    bufferedImage = SwingFXUtils.fromFXImage((Image)viewImage, null);
                    viewImage = SwingFXUtils.toFXImage(bufferedImage, null);
                    myImageView.setImage(viewImage);    
            }
        };

}
