
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                                                                                                                    //
//  Layer.java                                                                                                        //
//  Michael Hardeman                                                                                                  //
//  Mhardeman2@student.gsu.edu                                                                                        //
//                                                                                                                    //
//  Represents a matrix of pixels in our Sprite.LayerdImage data structure using a BufferedImage                      //
//                                                                                                                    //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package DataStructures;
import  java.awt.BasicStroke;
import  java.awt.Color;
import  java.awt.Dimension;
import  java.awt.Graphics2D;
import  java.awt.geom.AffineTransform;
import  java.awt.image.AffineTransformOp;
import  java.awt.image.BufferedImage;
import  java.awt.image.ColorModel;
import  java.awt.image.WritableRaster;
import  java.nio.ByteBuffer;
import  java.util.logging.Logger;
import  java.util.logging.Level;

///////////
// Layer //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Stores pixel information on the current image in the form of a BufferedImage
//
// RGBA is a 4byte integer with 8 bits for each field.
// Operations are preformed on this data by procedures at the bottom.
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  public class Layer {

    ///////////////
    // Constants //
    ///////////////
      public final int    MIN_COLOR_RANGE     = 0;
      public final int    MAX_COLOR_RANGE     = 255;
      public final int    MIN_DATA_RANGE      = 0;
      public final float  MIN_OPACITY_RANGE   = 0.0f;
      public final float  MAX_OPACITY_RANGE   = 1.0f;
      public final String ERROR_MESSAGE       = "Error: Illegal ";
      public final String ERROR_DATA_RANGE    = "data range ";
      public final String ERROR_ALPHA_RANGE   = "alpha range ";
      public final String ERROR_OPACITY_RANGE = "opacity range ";
      public final String ERROR_COLOR_RANGE   = "color range ";
    
    ////////////////////////
    // Instance Variables //
    ////////////////////////
      public         String        name;
      private        float         opacity;
      private        BufferedImage data;
      private static Logger        logger = Logger.getLogger(Layer.class.getName());
    
    /////////////////
    // Constructor //
    /////////////////
      public Layer(String name, Dimension size){
        this.name      = name;
        this.opacity   = MAX_OPACITY_RANGE;
        this.data      = new BufferedImage(
          size.width, 
          size.height, 
          BufferedImage.TYPE_4BYTE_ABGR
        );
        Graphics2D g2d = data.createGraphics();
        g2d.setColor(new Color(255, 255, 255, 0));
        g2d.fillRect(0, 0, size.width, size.height);
        g2d.dispose();
      }
      
    /////////////////
    // Constructor //
    /////////////////
      public Layer(String name, BufferedImage data){
        this.name    = name;
        this.opacity = MAX_OPACITY_RANGE;
        this.data    = data;
      }
      
    /////////////////
    // Constructor //
    /////////////////
      public Layer( Layer layer ){
        this.name    = layer.name;
        this.opacity = layer.opacity;
        this.data    = deepCopy(layer.data);
      }
      
    //////////////
    // deepCopy //
    //////////////
      private static BufferedImage deepCopy(BufferedImage bufferedImage) {
        ColorModel colorModel = bufferedImage.getColorModel();
        WritableRaster raster = bufferedImage.copyData(null);
        return new BufferedImage(colorModel, raster, colorModel.isAlphaPremultiplied(), null);
      }
    
    ////////////////
    // updateData //
    ////////////////
      public void updateData(BufferedImage data){
        if(
          (this.data.getWidth()  != data.getWidth() ) ||
          (this.data.getHeight() != data.getHeight()) ){
          logger.log(Level.WARNING, ERROR_MESSAGE + ERROR_DATA_RANGE + "(" + data.getWidth() + "," + data.getHeight() + ")");
        } else {
          this.data = data;
        }
      }
      
    ///////////////////
    // updateOpacity //
    ///////////////////
      public void updateOpacity(float opacity){
        if( opacity < MIN_OPACITY_RANGE || opacity > MAX_OPACITY_RANGE ){
          logger.log(Level.WARNING, ERROR_MESSAGE + ERROR_OPACITY_RANGE + opacity);
        }
        float previousOpacity = this.opacity;
        this.opacity = opacity;
        for( int row = 0; row < data.getWidth(); row++ ){
          for( int col = 0; col < data.getHeight(); col++ ){
            byte red      = (byte)(data.getRGB(row, col) >>> 24);
            byte green    = (byte)(data.getRGB(row, col) >>> 16);
            byte blue     = (byte)(data.getRGB(row, col) >>> 8 );
            byte alpha    = (byte)(data.getRGB(row, col) >>> 0 );
            byte newAlpha = (byte)((alpha/previousOpacity)*opacity);
            data.setRGB( row, col, ByteBuffer.wrap(new byte[]{red,green,blue,newAlpha}).getInt());
          }
        }
      }
      
    ////////////////
    // getOpacity //
    ////////////////
      public float getOpacity(){
        return this.opacity;
      }
      
    ///////////
    // image //
    ///////////
      public BufferedImage getData(){
        return data;
      }
      
    //////////////////
    // flipVertical //
    //////////////////
      public void flipVertical(){
        AffineTransform transformation = AffineTransform.getScaleInstance(1, -1);
        transformation.translate(0, -data.getHeight());
        AffineTransformOp transformOperation = new AffineTransformOp(transformation, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        data = transformOperation.filter(data, null);
      }
    
    ////////////////////
    // flipHorizontal //
    ////////////////////
      public void flipHorizontal(){
        AffineTransform transformation = AffineTransform.getScaleInstance(-1, 1);
        transformation.translate(-data.getWidth(),0);
        AffineTransformOp transformOperation = new AffineTransformOp(transformation, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        data = transformOperation.filter(data, null);
      }

    ////////////
    // rotate //
    ////////////
      public void rotate(double radians){
        AffineTransform transformation = new AffineTransform();
        transformation.rotate(radians, data.getWidth()/2, data.getHeight()/2);
        AffineTransformOp transformationOperation = new AffineTransformOp(transformation, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        data = transformationOperation.filter(data, null);
      }
      
    ///////////
    // paint //
    ///////////
      public void paint( int x, int y, Color color ){
        Graphics2D g2d = data.createGraphics();
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawLine(x, y, x, y);
        g2d.dispose();
      }
      
    //////////
    // line //
    //////////
      public void line( int x, int y, int x2, int y2, int s, Color color){
        Graphics2D g2d = data.createGraphics();
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(s));
        g2d.drawLine(x, y, x2, y2);
        g2d.dispose();
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
