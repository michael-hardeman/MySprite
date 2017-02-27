
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                                                                                                                    //
// ActionManager.java                                                                                                 //
// Michael Hardeman                                                                                                   //
//                                                                                                                    //
// Interperates actions read from xml files into operations on the Sprite DataStructure                               //
//                                                                                                                    //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package ActionManager;
import java.awt.Color;
import java.util.logging.Logger;
import java.util.logging.Level;
import DataStructures.Stack;
import MySpriteLib.ImageCanvas;

///////////////////
// ActionManager //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// This class acts as a bridge between the xml parsing system, the canvas, and DataStructures.Sprite
//
// When the user interacts with the canvas, the methods mouseDown, mouseDrag, and mouseUp are called.
// The current tool is retrieved, and the actions stored in it are done to the canvas.
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  @SuppressWarnings("unused")
  public class ActionManager {

    ////////////////////////
    // Instance Variables //
    ////////////////////////
      public  boolean       ready;
      public  boolean       line;
      private int           currentImage;
      private int           currentLayer;
      private Color         currentColor;
      private float         currentOpacity;
      private int           currentSize;
      private int           x;
      private int           y;
      private int           previousX;
      private int           previousY;
      private boolean       mouseDown;
      private boolean       mouseDrag;
      private boolean       mouseUp;
      private ToolManager   toolManager;
      private ImageCanvas   imageCanvas;
      private Stack<Action> actionsDone;
      private Stack<Action> actionsUndone;
      private static Logger logger = Logger.getLogger(ActionManager.class.getName());
    
    /////////////////
    // Constructor //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // When the actionManager is first instantiated, it's not ready to be used yet. It still requires a valid
    // imageCanvas to work.
    //
    // To make the actionManager ready, you have to initialize() it
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      public ActionManager() {
        this.ready = false;
      }
    
    ////////////////
    // initialize //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // A delayed constructor that is called when the imageCanvas is ready.
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      public void initalize(ToolManager toolManager, ImageCanvas imageCanvas){
        this.ready          = true;
        this.line           = false;
        this.currentImage   = 0;
        this.currentLayer   = 0;
        this.currentColor   = Color.white;
        this.currentOpacity = 1.0f;
        this.currentSize    = 1;
        this.x              = 0;
        this.y              = 0;
        this.previousX      = 0;
        this.previousY      = 0;
        this.mouseDown      = false;
        this.mouseDrag      = false;
        this.mouseUp        = false;
        this.toolManager    = toolManager;
        this.imageCanvas    = imageCanvas;
        this.actionsDone    = new Stack<Action>();
        this.actionsUndone  = new Stack<Action>();
      }
    
    /////////////////////
    // getCurrentImage //
    /////////////////////
      public int getCurrentImage(){
        return currentImage;
      }
    
    /////////////////////
    // getCurrentLayer //
    /////////////////////
      public int getCurrentLayer(){
        return currentLayer;
      }
    
    /////////////////////
    // getCurrentColor //
    /////////////////////
      public Color getCurrentColor(){
        return currentColor;
      }
    
    /////////////////////
    // setCurrentImage //
    /////////////////////
      public void setCurrentImage(int currentImage){
        if(currentImage < 0 || currentImage > imageCanvas.sprite.getImages().size()){
          logger.log(Level.WARNING, "Tried to access image not in sprite");
          currentImage = 0;
        }
        this.currentImage = currentImage;
        this.imageCanvas.imageIsUpdated();
      }
    
    /////////////////////
    // setCurrentLayer //
    /////////////////////
      public void setCurrentLayer(int currentLayer){
        if(currentLayer > 0 || currentLayer < imageCanvas.sprite.getImage(currentImage).getLayers().size()){
          logger.log(Level.WARNING, "Tried to access layer not in image");
          currentLayer = 0;
        }
        this.currentLayer = currentLayer;
      }
    
    /////////////////////
    // setCurrentColor //
    /////////////////////
      public void setCurrentColor(Color color){
        this.currentColor = color;
      }
    
    ///////////////////////
    // setCurrentOpacity //
    ///////////////////////
      public void setCurrentOpacity(float opacity) {
        this.currentOpacity = opacity;
      }
    
    ///////////////
    // mouseDown //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // called when a mouse button is depressed.
    //
    // This procedure checks the current tool to see if there are actions to be done, 
    // if so it preforms them on the canvas, if not it does nothing
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      public void mouseDown(int x, int y){
        if(this.ready){

          /////////////////////////////////////
          // check if actions are to be done //
          /////////////////////////////////////
            if(this.toolManager.getCurrentTool().downActions.size() != 0){

              /////////////////////////
              // save mouse position //
              /////////////////////////
                this.previousX = this.x;
                this.previousY = this.y;
                this.x         = x;
                this.y         = y;

              ////////////////////////////////////
              // Interperate actions to be done //
              ////////////////////////////////////
                for(Action current : toolManager.getCurrentTool().downActions){
                  
                  ///////////
                  // paint //
                  ///////////
                    if(current.command.equals("paint")){
                      int X = 0;
                      int Y = 0;
                      int S = 1;
                      int A = 255;
                      Color CurrentColor = null;
                      if(current.parameters.get("x").equals("currentx")){
                        X = x;
                      } else {
                        X = Integer.parseInt(current.parameters.get("x"));
                      }
                      if(current.parameters.get("y").equals("currenty")){
                        Y = y;
                      } else {
                        Y = Integer.parseInt(current.parameters.get("y"));
                      }
                      if(current.parameters.get("size").equals("currentsize")){
                        S = currentSize;
                      } else {
                        S = Integer.parseInt(current.parameters.get("size"));
                      }
                      if(current.parameters.get("opacity").equals("currentopacity")){
                        A = (int)(currentOpacity*255);
                      } else {
                        A = Integer.parseInt(current.parameters.get("opacity"));
                      }
                      if(current.parameters.get("color").equals("currentcolor")){
                        CurrentColor = new Color(
                          currentColor.getRed(),
                          currentColor.getGreen(),
                          currentColor.getBlue(),
                          A
                        );
                      } else {
                        int R = (byte)(Long.parseLong(current.parameters.get("color"),16) >>> 16);
                        int G = (byte)(Long.parseLong(current.parameters.get("color"),16) >>> 8 );
                        int B = (byte)(Long.parseLong(current.parameters.get("color"),16) >>> 0 );
                        CurrentColor = new Color(R,G,B,A);
                      }
                      for(int row = 0; row < S; row++){
                        for(int col = 0; col < S; col++){
                          imageCanvas.sprite.getImage(currentImage).paint(
                            currentLayer,
                            X+row,
                            Y+col,
                            CurrentColor
                          );
                        }
                      }

                  //////////
                  // fill //
                  //////////
                    } else if(current.command.equals("fill")){
                      //don't know what to do yet.
                      //selections aren't implemented in the canvas

                  //////////
                  // line //
                  //////////
                    } else if(current.command.equals("line")){
                      int S = 1;
                      int A = 255;
                      Color CurrentColor = null;
                      if(!line){
                        if(!current.parameters.get("x").equals("currentx")){
                          x = Integer.parseInt(current.parameters.get("x"));
                        }
                        if(!current.parameters.get("y").equals("currenty")){
                          y = Integer.parseInt(current.parameters.get("y"));
                        }
                        this.line = true;
                      } else {
                        if(current.parameters.get("size").equals("currentsize")){
                          S = currentSize;
                        } else {
                          S = Integer.parseInt(current.parameters.get("size"));
                        }
                        if(current.parameters.get("opacity").equals("currentopacity")){
                          A = (int)(currentOpacity*255);
                        } else {
                          A = Integer.parseInt(current.parameters.get("opacity"));
                        } 
                        if(current.parameters.get("color").equals("currentcolor")){
                          CurrentColor = new Color(
                            currentColor.getRed(), 
                            currentColor.getGreen(), 
                            currentColor.getBlue(), 
                            A
                          );
                        } else {
                          int R = (byte)(Long.parseLong(current.parameters.get("color"),16) >>> 16);
                          int G = (byte)(Long.parseLong(current.parameters.get("color"),16) >>> 8 );
                          int B = (byte)(Long.parseLong(current.parameters.get("color"),16) >>> 0 );
                          CurrentColor = new Color(R,G,B,A);
                        }
                        imageCanvas.sprite.getImage(currentImage).line(
                          currentLayer, 
                          x, 
                          y, 
                          previousX, 
                          previousY, 
                          S, 
                          CurrentColor
                        );
                        this.line = false;
                      }
                    } else {
                      logger.log(Level.SEVERE, "Invalid Command. This should never happen...");
                    }
                }
            }
          imageCanvas.imageIsUpdated();
        }
      }
    
    ///////////////
    // mouseDrag //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // called when a mouse button is depressed, and the mouse is moving.
    //
    // This procedure checks the current tool to see if there are actions to be done, 
    // if so it preforms them on the canvas, if not it does nothing
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      public void mouseDrag(int x, int y){
        if(this.ready){

          /////////////////////////////////////
          // check if actions are to be done //
          /////////////////////////////////////
            if(this.toolManager.getCurrentTool().dragActions.size() != 0){

              /////////////////////////
              // save mouse position //
              /////////////////////////
                this.previousX = this.x;
                this.previousY = this.y;
                this.x         = x;
                this.y         = y;

              ////////////////////////////////////
              // interperate actions to be done //
              ////////////////////////////////////
                for(Action current : toolManager.getCurrentTool().dragActions){

                  ///////////
                  // paint //
                  ///////////
                    if(current.command.equals("paint")){
                      int X = 0;
                      int Y = 0;
                      int S = 1;
                      int A = 255;
                      Color CurrentColor = null;
                      if(current.parameters.get("x").equals("currentx")){
                        X = x;
                      } else {              
                        X = Integer.parseInt(current.parameters.get("x"));
                      }
                      if(current.parameters.get("y").equals("currenty")){
                        Y = y;
                      } else {
                        Y = Integer.parseInt(current.parameters.get("y"));
                      }
                      if(current.parameters.get("size").equals("currentsize")){
                        S = currentSize;
                      } else {
                        S = Integer.parseInt(current.parameters.get("size"));
                      }
                      if(current.parameters.get("opacity").equals("currentopacity")){
                        A = (int)(currentOpacity*255);
                      } else {
                        A = Integer.parseInt(current.parameters.get("opacity"));
                      }
                      if(current.parameters.get("color").equals("currentcolor")){
                        CurrentColor = new Color(
                          currentColor.getRed(), 
                          currentColor.getGreen(), 
                          currentColor.getBlue(), 
                          A
                        );
                      } else {
                        int R = (byte)(Long.parseLong(current.parameters.get("color"),16) >>> 16);
                        int G = (byte)(Long.parseLong(current.parameters.get("color"),16) >>> 8 );
                        int B = (byte)(Long.parseLong(current.parameters.get("color"),16) >>> 0 );
                        CurrentColor = new Color(R,G,B,A);
                      }
                      for(int row = 0; row < S; row++){
                        for(int col = 0; col < S; col++){
                          imageCanvas.sprite.getImage(currentImage).paint(
                            currentLayer, 
                            X+row, 
                            Y+col, 
                            CurrentColor
                          );
                        }
                      }

                  //////////
                  // fill //
                  //////////
                    } else if(current.command.equals("fill")){
                      //don't know what to do yet.
                      //selections aren't implimented in the canvas
                    
                  //////////
                  // line //
                  //////////
                    } else if(current.command.equals("line")){
                      int S = 1;
                      int A = 255;
                      Color CurrentColor = null;
                      if(!line){              
                        if(!current.parameters.get("x").equals("currentx")){
                          x = Integer.parseInt(current.parameters.get("x"));
                        }
                        if(!current.parameters.get("y").equals("currenty")){
                          y = Integer.parseInt(current.parameters.get("y"));
                        }
                        this.line = true;
                      } else {
                        if(current.parameters.get("size").equals("currentsize")){
                          S = currentSize;
                        } else {
                          S = Integer.parseInt(current.parameters.get("size"));
                        }
                        if(current.parameters.get("opacity").equals("currentopacity")){
                          A = (int)(currentOpacity*255);
                        } else {
                          A = Integer.parseInt(current.parameters.get("opacity"));
                        }if(current.parameters.get("color").equals("currentcolor")){
                          CurrentColor = new Color(
                            currentColor.getRed(), 
                            currentColor.getGreen(), 
                            currentColor.getBlue(), 
                            A
                          );
                        } else {
                          int R = (byte)(Long.parseLong(current.parameters.get("color"),16) >>> 16);
                          int G = (byte)(Long.parseLong(current.parameters.get("color"),16) >>> 8 );
                          int B = (byte)(Long.parseLong(current.parameters.get("color"),16) >>> 0 );
                          CurrentColor = new Color(R,G,B,A);
                        }
                        imageCanvas.sprite.getImage(currentImage).line(
                          currentLayer, 
                          x, 
                          y, 
                          previousX, 
                          previousY, 
                          S, 
                          CurrentColor
                        );
                        this.line = false;
                      }
                    } else {
                      logger.log(Level.SEVERE, "Invalid Command. This should never happen...");
                    }
                }
            }
          imageCanvas.imageIsUpdated();
        }
      }
    
    /////////////
    // mouseUp //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // called when a mouse button is released.
    //
    // This procedure checks the current tool to see if there are actions to be done, 
    // if so it preforms them on the canvas, if not it does nothing
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      public void mouseUp(int x, int y){
        if(this.ready){

          /////////////////////////////////////
          // check if actions are to be done //
          /////////////////////////////////////
            if(this.toolManager.getCurrentTool().upActions.size() != 0){

              /////////////////////////
              // save mouse position //
              /////////////////////////
                this.previousX = this.x;
                this.previousY = this.y;
                this.x         = x;
                this.y         = y;

              /////////////////////////////////////
              //  interperate actions to be done //
              /////////////////////////////////////
                for(Action current : toolManager.getCurrentTool().upActions){

                  ///////////
                  // paint //
                  ///////////
                    if(current.command.equals("paint")){
                      int X = 0;
                      int Y = 0;
                      int S = 1;
                      int A = 255;
                      Color CurrentColor = null;
                      if(current.parameters.get("x").equals("currentx")){
                        X = x;
                      } else {              
                        X = Integer.parseInt(current.parameters.get("x"));
                      }
                      if(current.parameters.get("y").equals("currenty")){
                        Y = y;
                      } else {
                        Y = Integer.parseInt(current.parameters.get("y"));
                      }
                      if(current.parameters.get("size").equals("currentsize")){
                        S = currentSize;
                      } else {
                        S = Integer.parseInt(current.parameters.get("size"));
                      }
                      if(current.parameters.get("opacity").equals("currentopacity")){
                        A = (int)(currentOpacity*255);
                      } else {
                        A = Integer.parseInt(current.parameters.get("opacity"));
                      }
                      if(current.parameters.get("color").equals("currentcolor")){
                        CurrentColor = new Color(
                          currentColor.getRed(), 
                          currentColor.getGreen(), 
                          currentColor.getBlue(), 
                          A
                        );
                      } else {
                        int R = (byte)(Long.parseLong(current.parameters.get("color"),16) >>> 16);
                        int G = (byte)(Long.parseLong(current.parameters.get("color"),16) >>> 8 );
                        int B = (byte)(Long.parseLong(current.parameters.get("color"),16) >>> 0 );
                        CurrentColor = new Color(R,G,B,A);
                      }
                      for(int row = 0; row < S; row++){
                        for(int col = 0; col < S; col++){
                          imageCanvas.sprite.getImage(currentImage).paint(
                            currentLayer, 
                            X+row, 
                            Y+col, 
                            CurrentColor
                          );
                        }
                      }

                  //////////
                  // fill //
                  //////////
                    } else if(current.command.equals("fill")){
                      //don't know what to do yet.
                      //selections aren't implimented in the canvas

                  //////////
                  // line //
                  //////////
                    } else if(current.command.equals("line")){
                      int S = 1;
                      int A = 255;
                      Color CurrentColor = null;
                      if(!line){              
                        if(!current.parameters.get("x").equals("currentx")){
                          x = Integer.parseInt(current.parameters.get("x"));
                        }
                        if(!current.parameters.get("y").equals("currenty")){
                          y = Integer.parseInt(current.parameters.get("y"));
                        }
                        this.line = true;
                      } else {
                        if(current.parameters.get("size").equals("currentsize")){
                          S = currentSize;
                        } else {
                          S = Integer.parseInt(current.parameters.get("size"));
                        }
                        if(current.parameters.get("opacity").equals("currentopacity")){
                          A = (int)(currentOpacity*255);
                        } else {
                          A = Integer.parseInt(current.parameters.get("opacity"));
                        } 
                        if(current.parameters.get("color").equals("currentcolor")){
                          CurrentColor = new Color(
                            currentColor.getRed(), 
                            currentColor.getGreen(), 
                            currentColor.getBlue(), 
                            A
                          );
                        } else {
                          int R = (byte)(Long.parseLong(current.parameters.get("color"),16) >>> 16);
                          int G = (byte)(Long.parseLong(current.parameters.get("color"),16) >>> 8 );
                          int B = (byte)(Long.parseLong(current.parameters.get("color"),16) >>> 0 );
                          CurrentColor = new Color(R,G,B,A);
                        }
                        imageCanvas.sprite.getImage(currentImage).line(
                          currentLayer, 
                          x, 
                          y, 
                          previousX, 
                          previousY, 
                          S, 
                          CurrentColor
                        );
                        this.line = false;
                      }
                    } else {
                      logger.log(Level.SEVERE, "Invalid Command. This should never happen...");
                    }
                }
            }
          imageCanvas.imageIsUpdated();
        }
      }
  }
  
////////////////////////////////////////////////////////////////////////////////
// MySprite - Java Sprite Editor                                              //
// Copyright (C) 2013 Michael Allen Hardeman                                  //
//                                                                            //
// This program is free software: you can redistribute it and/or modify       //
// it under the terms of the GNU General Public License as published by       //
// the Free Software Foundation, either version 3 of the License, or          //
// (at your option) any later version.                                        //
//                                                                            //
// This program is distributed in the hope that it will be useful,            //
// but WITHOUT ANY WARRANTY; without even the implied warranty of             //
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              //
// GNU General Public License for more details.                               //
//                                                                            //
// You should have received a copy of the GNU General Public License          //
// along with this program.  If not, see <http://www.gnu.org/licenses/>.      //
////////////////////////////////////////////////////////////////////////////////
