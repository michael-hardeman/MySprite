
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                                                                                                                    //
//  Sprite.java                                                                                                       //
//  Michael Hardeman                                                                                                  //
//                                                                                                                    //
//  The whole shebang, represents an ArrayList<LayeredImages>                                                         //
//                                                                                                                    //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package DataStructures;
import  java.util.ArrayList;
import  java.util.logging.Logger;
import  java.util.logging.Level;
import  java.awt.Dimension;
import  java.awt.image.BufferedImage;

////////////
// Sprite //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Stores data on the LayeredImages that comprise a sprite in a linear
// arrayList of LayeredImages
//
// This class also contains procedures that manages data manipulation on all the layeredImages
// in the sprite.
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  public class Sprite {

    ///////////////
    // Constants //
    ///////////////
      public final int    MIN_COLOR_RANGE     = 0;
      public final int    MAX_COLOR_RANGE     = 255;
      public final float  MIN_OPACITY_RANGE   = 0.0f;
      public final float  MAX_OPACITY_RANGE   = 1.0f;
      public final String DEFAULT_IMAGE_NAME  = "New Frame";
      public final String ERROR_MESSAGE       = "Error: Illegal ";
      public final String ERROR_LAYER_RANGE   = "layer range ";
      public final String ERROR_OPACITY_RANGE = "opacity range ";
    
    ////////////////////////
    // Instance Variables //
    ////////////////////////
      private        String                  name;
      public         Dimension               size;
      private        float                   opacity;
      private        ArrayList<LayeredImage> images;
      private static Logger                  logger = Logger.getLogger(Sprite.class.getName());
    
    /////////////////
    // Constructor //
    /////////////////
      public Sprite( String name, Dimension size ){
        this.name     = name;
        this.opacity  = MAX_OPACITY_RANGE;
        this.size     = size;
        this.images   = new ArrayList<LayeredImage>();
        this.images.add(new LayeredImage(DEFAULT_IMAGE_NAME, size));
      }
      
    /////////////////
    // Constructor //
    /////////////////
      public Sprite(Sprite sprite) {
        this.name = sprite.getName();
        this.size = sprite.getSize();
        this.opacity = sprite.getOpacity();
        this.images = new ArrayList<LayeredImage>();
        for(LayeredImage image : sprite.getImages()){
          this.images.add(new LayeredImage(image));
        }
      }

    ////////////////
    // updateName //
    ////////////////
      public void updateName(String name){
        this.name = name;
      }

    ///////////////////
    // updateOpacity //
    ///////////////////
      public void updateOpacity(float opacity){
        if(( opacity < MIN_OPACITY_RANGE ) || ( opacity > MAX_OPACITY_RANGE )){
          System.err.println(ERROR_MESSAGE + ERROR_OPACITY_RANGE + opacity);
        }
        float previousOpacity = this.opacity;
        this.opacity = opacity;
        for(LayeredImage current : images){
          if(previousOpacity != MIN_OPACITY_RANGE){
            current.updateOpacity(Math.max(current.getOpacity() - previousOpacity,MIN_OPACITY_RANGE));
          }
          current.updateOpacity(Math.min(current.getOpacity() + opacity, MAX_OPACITY_RANGE));
        }
      }
      
    ///////////////
    // getImages //
    ///////////////
      public ArrayList<LayeredImage> getImages(){
        return images;
      }
    
    //////////////
    // getImage //
    //////////////
      public LayeredImage getImage(int i){
    	if(i >= 0 || i <= this.images.size()){
    	  return this.images.get(i);
    	} else {
    	  logger.log(Level.WARNING, "Tried to get image not in sprite: "+i);
    	  return null;
    	}
      }
    
    /////////////
    // getName //
    /////////////
      public String getName(){
        return name;
      }

    ////////////////
    // getOpacity //
    ////////////////
      public float getOpacity(){
        return opacity;
      }
      
    /////////////
    // getSize //
    /////////////
      public Dimension getSize(){
        return size;
      }
    
    /////////////
    //setImages//
    /////////////
      public void setImages(BufferedImage[] images){
        if (!this.size.equals( new Dimension(images[0].getWidth(),images[0].getHeight())))
          logger.log(Level.WARNING, "Images are not the same size as the sprite. Please setSize() first.");
        else {
          this.images.clear();
          for(int i=0; i<images.length; i++) {
            this.images.add(new LayeredImage(DEFAULT_IMAGE_NAME + i, images[i]));
          }
        }
      }
    /////////////
    //addImages//
    /////////////
      public void addImages(BufferedImage[] images){
        if (!this.size.equals( new Dimension(images[0].getWidth(),images[0].getHeight())))
          logger.log(Level.WARNING, "Images are not the same size as the sprite. Please setSize() first.");
        else {
          for(int i=0; i<images.length; i++) {
            this.images.add(new LayeredImage(DEFAULT_IMAGE_NAME + i, images[i]));
          }
        }
      }

    //////////////////
    // flipVertical //
    //////////////////
     public void flipVertical(){
        for(LayeredImage current : images){
          current.flipVertical();
        }
     }
     
    ////////////////////
    // flipHorizontal //
    ////////////////////
      public void flipHorizontal(){
        for(LayeredImage current : images){
          current.flipHorizontal();
        }
      }

    ////////////
    // rotate //
    ////////////
      public void rotate(double radians){
        for(LayeredImage current : images){
          current.rotate(radians);
        }
      }
    
    ///////////////
    //insertImage//
    ///////////////
      public void insertImage(int index, LayeredImage image) {
        this.images.add(index, image);
      }
      public void insertImages(int index, LayeredImage[] images)  {
        for (LayeredImage image : images) {
          insertImage(index, image);
        }
      }
    
    ///////////////
    //deleteImage//
    ///////////////
      public void deleteImage(int index) {
        this.images.remove(index);
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
