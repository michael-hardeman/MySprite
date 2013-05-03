
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                                                                                                                    //
//  ImageCanvas.java                                                                                                  //
//  Michael Hardeman : Mhardeman2@student.gsu.edu                                                                     //
//                                                                                                                    //
//  MySprite Specific Gui Components and data structures                                                              //
//                                                                                                                    //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package MySpriteLib;
import  ActionManager.ActionManager;
import  ActionManager.ToolManager;
import  DataStructures.Sprite;
import  MySpriteLib.ImageAnalysis;
import  java.awt.BasicStroke;
import  java.awt.Color;
import  java.awt.Cursor;
import  java.awt.Dimension;
import  java.awt.Graphics;
import  java.awt.Graphics2D;
import  java.awt.Point;
import  java.awt.Toolkit;
import  java.awt.event.MouseEvent;
import  java.awt.event.MouseListener;
import  java.awt.event.MouseMotionListener;
import  java.awt.event.MouseWheelEvent;
import  java.awt.event.MouseWheelListener;
import  java.awt.geom.Line2D;
import  java.awt.geom.Rectangle2D;
import  java.awt.image.BufferedImage;
import  java.util.LinkedList;
import  java.util.logging.Level;
import  java.util.logging.Logger;
import  javax.swing.JPanel;
import  javax.swing.SwingUtilities;

/////////////////
// ImageCanvas //
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Interface between DataStrucutre.Sprite, ActionManager.ActionManager, and the user
//
// in the paint() procedure, the canvas is drawn, and appropriate grid lines are added
// this procedure is invoked by using component.repaint().
//
// this class also contains mouse listening procedures that monitor mouse movement and interaction.
// these procedures fire procedures mouseDown(), mouseDrag(), and mouseUp() in actionManager at the appropriate times
// allowing DataStructure.Sprite to be changed, and handle mouse scrolling for zooming.
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  public class ImageCanvas
    extends
      JPanel
    implements
      MouseListener,
      MouseWheelListener,
      MouseMotionListener
  {

    ///////////////////
    // CanvasElement //
    ///////////////////
      public static class CanvasElement{
      
        ///////////////
        // Variables //
        ///////////////
          private Point   position;
          private Object  element;
          private Color   color;
          public  boolean isRealPixelAligned;
        
        //////////////////
        // Constructors //
        /////////////////
          public CanvasElement(){
            this.position           = null;
            this.element            = null;
            this.color              = null;
            this.isRealPixelAligned = false;
          }
          public CanvasElement(
            Point         position,
            BufferedImage element,
            boolean       isRealPixelAligned)
          {
            this.position           = position;
            this.element            = element;
            this.color              = new Color(0,0,0,0);
            this.isRealPixelAligned = isRealPixelAligned;
          }
          public CanvasElement(
            Point   position,
            Line2D  element,
            Color   color,
            boolean isRealPixelAligned)
          {
            this.position           = position;
            this.element            = element;
            this.color              = color;
            this.isRealPixelAligned = isRealPixelAligned;
          }
          public CanvasElement(
            Point         position,
            Rectangle2D element,
            Color         color,
            boolean       isRealPixelAligned)
          {
            this.position           = position;
            this.element            = element;
            this.color              = color;
            this.isRealPixelAligned = isRealPixelAligned;
          }
        
        ///////////////
        // Accessors //
        ///////////////
          public Point getPosition(){
            return this.position;
          }
          public Color getColor(){
            return this.color;
          }
          public Object getElement(){
            return this.element;
          }
      
        ///////////////
        // Modifiers //
        ///////////////
          public void setPosition(Point position){
            this.position = position;
          }
          public void setColor(Color color){
            this.color = color;
          }
          public void setElement(BufferedImage element){
            this.element = element;
          }
          public void setElement(Line2D element){
            this.element = element;
          }
          public void setElement(Rectangle2D element){
            this.element = element;
          }
        
        ///////////////////////////////
        // getElementAsBufferedImage //
        ///////////////////////////////
          public BufferedImage getElementAsBufferedImage(){
            if(this.element.getClass().getName().equals(BufferedImage.class.getName())){
              return (BufferedImage)this.element;
            } else {
              return null;
            }
          }
        
        //////////////////////
        // getElementAsLine //
        //////////////////////
          public Line2D getElementAsLine(){
            if(this.element.getClass().getName().contains(Line2D.class.getName())){
              return (Line2D)this.element;
            } else {
              return null;
            }
          }
          
        ///////////////////////////
        // getElementAsRectangle //
        ///////////////////////////
          public Rectangle2D getElementAsRectangle(){
            if(this.element.getClass().getName().equals(Rectangle2D.class.getName())){
              return (Rectangle2D)this.element;
            } else {
              return null;
            }
          }
      }
    
    ///////////////
    // Constants //
    ///////////////
      private static final long  serialVersionUID = 1L;
      private static final float MAXIMUM_ZOOM     = 64.0f;
      private static final float MINIMUM_ZOOM     = 1.0f;
      private static final float INITIAL_ZOOM     = 10.0f;
      private static final float ZOOM_INCRIMENTS  = 1.0f;
      private static final float GRID_THREASHOLD  = 4.0f;
      private static final float BACKGROUND_SCALE = 0.5f;
      private static final Color CANVAS_COLOR     = new Color(220,220,220,50);
    
    ////////////////////////
    // Instance Variables //
    ////////////////////////
      private       Point                     displacement;
      private       Point                     previousMousePosition;
      private       Point                     currentMousePosition;
      private       Dimension                 canvasSize;
      private       Dimension                 pixelSize;
      private       Color                     canvasColor;
      private       LinkedList<CanvasElement> canvasElements;
      private       float                     currentZoom;
      private       boolean                   rightMouseDown;
      private       boolean                   leftMouseDown;
      public        boolean                   updated;
      public        Dimension                 size;
      public        Sprite                    sprite;
      public        ActionManager             actionManager;
      public        BufferedImage             backgroundTile;
      public        BufferedImage             background;
      public static Logger                    logger = Logger.getLogger(ImageCanvas.class.getName());
    
    /////////////////
    // Constructor //
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Initializes all instance variables and sets them to default values.
    // Initializes actionManager, and passes relevant parameters.
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      public ImageCanvas(ActionManager actionManager, ToolManager toolManager, Sprite sprite, BufferedImage backgroundTile, BufferedImage cursor){
        this.setDoubleBuffered(true);
        this.setFocusable     (true);
        this.displacement          = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);
        this.currentZoom           = INITIAL_ZOOM;
        this.rightMouseDown        = false;
        this.leftMouseDown         = false;
        this.previousMousePosition = new Point(0,0);
        this.currentMousePosition  = new Point(0,0);
        this.canvasSize            = new Dimension(0,0);
        this.pixelSize             = new Dimension(0,0);
        this.updated               = true;
        this.canvasColor           = CANVAS_COLOR;
        this.canvasElements        = new LinkedList<CanvasElement>();
        this.sprite                = sprite;
        this.size                  = sprite.size;
        this.actionManager         = actionManager;
        this.actionManager         . initalize(toolManager, this);
        this.backgroundTile        = backgroundTile;
        this.addMouseListener      (this);
        this.addMouseWheelListener (this);
        this.addMouseMotionListener(this);
        this.updateCursor(cursor);
      }
    
    //////////////////
    // updateSprite //
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // This is called by MySprite.java every time the sprite is changed.
    // This resets zoom, and canvas position values. When displacementX and displacementY are
    // set to Integer.MIN_VALUE, the canvas is centered by paint().
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      public void updateSprite(Sprite sprite){
        this.displacement  = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);
        this.currentZoom   = INITIAL_ZOOM;
        this.sprite        = sprite;
        this.size          = sprite.size;
        this.actionManager . setCurrentImage(actionManager.getCurrentImage());
        this.actionManager . setCurrentLayer(actionManager.getCurrentLayer());
        this.updated       = true;
        this.background    = ImageAnalysis.tileImage((int)(this.sprite.size.width*BACKGROUND_SCALE), (int)(this.sprite.size.height*BACKGROUND_SCALE), this.backgroundTile);
        this.repaint();
      }
      
    ///////////////////////
    // updateCanvasColor //
    ///////////////////////
      public void updateCanvasColor(Color color){
        this.canvasColor = color;
      }
    
    ////////////////////
    // imageIsUpdated //
    ////////////////////
      public void imageIsUpdated(){
        this.updated = true;
        this.repaint();
      }
    
    //////////////////
    // updateCursor //
    //////////////////
      public void updateCursor(BufferedImage cursorImage){
          Toolkit toolkit = Toolkit.getDefaultToolkit();
          BufferedImage argbImg = new BufferedImage(cursorImage.getWidth(), cursorImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
          Graphics2D g2d = argbImg.createGraphics();
          g2d.drawImage(
            cursorImage,
            0,
            0,
            argbImg.getWidth(),
            argbImg.getHeight(),
            null
          );
          g2d.dispose();
          Cursor cursor = toolkit.createCustomCursor(
            cursorImage,
            ImageAnalysis.findClosestEdgeFromCorner(argbImg, ImageAnalysis.Corner.BOTTOM_LEFT),
            "img");
          this.setCursor(cursor);
        }
    
    //////////////////
    // updateCursor //
    //////////////////
      public void updateCursor(BufferedImage cursorImage, int x, int y){
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Cursor cursor = toolkit.createCustomCursor(
          cursorImage,
          new Point(x,y),
          "img");
        this.setCursor(cursor);
      }
      
    ///////////////////////
    // addCanvasElements //
    ///////////////////////
      public void addCanvasElements(CanvasElement element){
        this.canvasElements.addLast(element);
      }
      
    //////////////////////////
    // removeCanvasElements //
    //////////////////////////
      public void removeCanvasElements(CanvasElement element){
        this.canvasElements.remove(element);
      }
    
    ///////////
    // paint //
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // invoked with component.repaint();
    //
    // calculates all displacements, widths and heights,
    // sets all variables, then paints the sprite, then the grid overlay.
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      public void paint(Graphics g){
        ////////////////
        // initialize //
        ////////////////
          super.paint(g);
          Graphics2D g2d  = (Graphics2D)g;
          this.canvasSize = new Dimension(
            (int)(this.size.getWidth()*currentZoom),
            (int)(this.size.getHeight()*currentZoom));
          this.pixelSize = new Dimension(
            canvasSize.width/this.size.width,
            canvasSize.height/this.size.height);
          // center canvas
          if(this.displacement.x == Integer.MIN_VALUE && this.displacement.y == Integer.MIN_VALUE){
            this.displacement.x   = (this.getWidth()/2 )-(canvasSize.width/2 );
            this.displacement.y   = (this.getHeight()/2)-(canvasSize.height/2);
          }
        
        /////////////////////
        // draw background //
        /////////////////////
          g2d.setColor(this.canvasColor);
          g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
          if(this.background == null){
            this.background = ImageAnalysis.tileImage((int)(this.sprite.size.width*BACKGROUND_SCALE), (int)(this.sprite.size.height*BACKGROUND_SCALE), this.backgroundTile);
          }
          g2d.drawImage(
            this.background,
            displacement.x,
            displacement.y,
            canvasSize.width,
            canvasSize.height,
            null
          );
          
        /////////////////
        // draw sprite //
        /////////////////
          BufferedImage image = sprite.getImage(actionManager.getCurrentImage()).image();
          if(image != null){
            g2d.drawImage(
              image,
              displacement.x,
              displacement.y,
              canvasSize.width,
              canvasSize.height,
              null
            );
          } else {
            logger.log(Level.SEVERE, "Canvas image unable to be displayed");
          }
        
        ///////////////////////
        // draw gird overlay //
        ///////////////////////
          g2d.setColor(Color.BLACK);
          g2d.setStroke(new BasicStroke(1));
          if(currentZoom >= GRID_THREASHOLD){
            for(int i=0; i < this.size.width+1; i++){
              g2d.drawLine
              (
                displacement.x + i*pixelSize.width,
                displacement.y,
                displacement.x + i*pixelSize.width,
                displacement.y + canvasSize.height
              );
            }
            for(int j=0; j < this.size.height+1; j++){
              g2d.drawLine
              (
                displacement.x,
                displacement.y + j*pixelSize.height,
                displacement.x + canvasSize.width,
                displacement.y + j*pixelSize.height
              );
            }
          }
        
        /////////////////////////////
        // Draw square under mouse //
        /////////////////////////////
          g2d.setColor(Color.red);
          Point pixelOver = mouseToPixel(this.currentMousePosition);
          if(pixelOver != null){
            g2d.drawRect(
              pixelOver.x * this.pixelSize.width  + this.displacement.x,
              pixelOver.y * this.pixelSize.height + this.displacement.y,
              pixelSize.width,
              pixelSize.height);
          }
          
        //////////////////////////
        // Draw Canvas Elements //
        //////////////////////////
          for(CanvasElement current : canvasElements){
            g2d.setColor(current.getColor());
            BufferedImage elementImage = current.getElementAsBufferedImage();
            if(elementImage != null){
              if(current.isRealPixelAligned){
                g2d.drawImage(
                  elementImage,
                  this.displacement.x + current.getPosition().x,
                  this.displacement.y + current.getPosition().y,
                  elementImage.getWidth(),
                  elementImage.getHeight(),
                  null);
              } else {
                g2d.drawImage(
                  elementImage,
                  this.displacement.x + (current.getPosition().x * this.pixelSize.width),
                  this.displacement.y + (current.getPosition().y * this.pixelSize.height),
                  elementImage.getWidth()  * this.pixelSize.width,
                  elementImage.getHeight() * this.pixelSize.height,
                  null);
              }
            }
            Line2D elementLine = current.getElementAsLine();
            if(elementLine != null){
              if(current.isRealPixelAligned){
                g2d.drawLine(
                  this.displacement.x + current.position.x + (int)(elementLine.getX1()),
                  this.displacement.y + current.position.y + (int)(elementLine.getY1()),
                  this.displacement.x + current.position.x + (int)(elementLine.getX2()),
                  this.displacement.y + current.position.y + (int)(elementLine.getY2()));
              } else {
                g2d.setStroke(new BasicStroke(this.pixelSize.width));
                g2d.drawLine(
                  this.displacement.x + (current.position.x + (int)(elementLine.getX1()) * this.pixelSize.width),
                  this.displacement.y + (current.position.y + (int)(elementLine.getY1()) * this.pixelSize.height),
                  this.displacement.x + (current.position.x + (int)(elementLine.getX2()) * this.pixelSize.width),
                  this.displacement.y + (current.position.y + (int)(elementLine.getY2()) * this.pixelSize.height));
              }
            }
            Rectangle2D elementRectangle = current.getElementAsRectangle();
            if(elementRectangle != null){
              if(current.isRealPixelAligned){
                g2d.drawRect(
                  this.displacement.x + current.position.x,
                  this.displacement.y + current.position.y,
                  (int)elementRectangle.getWidth(),
                  (int)elementRectangle.getHeight());
              } else {
               g2d.drawRect(
                  this.displacement.x + (current.position.x * this.pixelSize.width),
                  this.displacement.y + (current.position.y * this.pixelSize.height),
                  ((int)elementRectangle.getWidth()   * this.pixelSize.width),
                  ((int)elementRectangle.getHeight()) * this.pixelSize.height);
              }
            }
          }
          
        ////////////////
        // clean exit //
        ////////////////
          g.dispose();
          g2d.dispose();
      }
    
    //////////////////
    // mouseToPixel //
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // This function converts the mouse coordinates to sprite pixels.
    // input is mouse coordinates relative to the top left corner of the canvas
    // output is pixel coordinates relative to the top left corner of the sprite.
    // output is null if the mouse is not over a pixel.
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      public Point mouseToPixel(Point mousePosition){
        //////////////////
        // check bounds //
        //////////////////
          if(mousePosition.x < displacement.x || mousePosition.x > displacement.x+canvasSize.width)
            return null;
          if(mousePosition.y < displacement.y || mousePosition.y > displacement.y+canvasSize.height)
            return null;
        ////////////////////////////////
        // calculate cell coordinates //
        ////////////////////////////////
          return new Point(
            ((mousePosition.x - displacement.x)/pixelSize.width),
            ((mousePosition.y - displacement.y)/pixelSize.height));
      }
    
    /////////////////////
    // mouseWheelMoved //
    /////////////////////
      public void mouseWheelMoved(MouseWheelEvent event) {
        if(this.currentZoom >= MINIMUM_ZOOM && this.currentZoom <= MAXIMUM_ZOOM){
          this.currentZoom += event.getWheelRotation()*ZOOM_INCRIMENTS;
        }
        if(this.currentZoom < MINIMUM_ZOOM)
           this.currentZoom = MINIMUM_ZOOM;
        if(this.currentZoom > MAXIMUM_ZOOM)
           this.currentZoom = MAXIMUM_ZOOM;
        this.repaint();
      }

    //////////////////
    // mousePressed //
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Invoked on mouse button down
    //
    // if the user right clicks, don't do anything yet
    //
    // if the user left clicks, then inform the actionManager a tool action needs to happen
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      public void mousePressed(MouseEvent event) {
        if (SwingUtilities.isLeftMouseButton(event)){
          this.leftMouseDown = true;
          Point pixel = mouseToPixel(this.currentMousePosition);
          if(pixel != null){
            this.updated = true;
            actionManager.mouseDown(pixel.x, pixel.y);
          }
        } else if (SwingUtilities.isRightMouseButton(event)){
          this.rightMouseDown = true;
        } else {
          // middle mouse button ignore for now
        }
      }
    
    ///////////////////
    // mouseReleased //
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Invoked on mouse button up
    //
    // if it's the right mouse button, don't do anything yet
    //
    // if it's the left mouse button, inform the actionManager a tool action needs to happen
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      public void mouseReleased(MouseEvent event) {
        if (SwingUtilities.isLeftMouseButton(event)){
          this.leftMouseDown = false;
          Point pixel = mouseToPixel(this.currentMousePosition);
          if(pixel != null){
            this.updated = true;
            actionManager.mouseUp(pixel.x, pixel.y);
          }
        } else if (SwingUtilities.isRightMouseButton(event)){
          this.rightMouseDown = false;
        } else {
          // middle mouse button ignore for now
        }
      }
    
    //////////////////
    // mosueDragged //
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Invoked on mouse movement while a button is depressed :'(
    //
    // if it's the right mouse button, move the canvas relative to the mouse movement
    //
    // if it's the left mouse button, don't do anything yet
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      public void mouseDragged(MouseEvent event) {
        this.previousMousePosition = this.currentMousePosition;
        this.currentMousePosition  = event.getPoint();
        if(leftMouseDown && !rightMouseDown){
          Point previousCell = mouseToPixel(this.previousMousePosition);
          Point currentCell  = mouseToPixel(this.currentMousePosition);
          if(previousCell != null && currentCell != null){
            if(previousCell.x != currentCell.x || previousCell.y != currentCell.y){
              this.updated = true;
              actionManager.mouseDrag(currentCell.x, currentCell.y);
            }
          }
        } else if(!leftMouseDown && rightMouseDown){
          this.displacement.x += (this.currentMousePosition.x - this.previousMousePosition.x);
          this.displacement.y += (this.currentMousePosition.y - this.previousMousePosition.y);
        } else if(leftMouseDown && rightMouseDown){
          // ignored for now
        } else {
          // should never happen
        }
        this.repaint();
      }
    
    ////////////////
    // mouseMoved //
    ////////////////
      public void mouseMoved(MouseEvent event) {
        this.previousMousePosition = this.currentMousePosition;
        this.currentMousePosition  = event.getPoint();
        this.repaint();
      }
    
    ////////////
    // unused //
    ////////////
      public void mouseClicked (MouseEvent event) {}
      public void mouseEntered (MouseEvent event) {}
      public void mouseExited  (MouseEvent event) {}
  }
