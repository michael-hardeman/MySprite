
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                                                                                                                    //
//  LayeredImage.java                                                                                                 //
//  Michael Hardeman                                                                                                  //
//                                                                                                                    //
//  Represents an ArrayList() of Sprite.Layers in our Sprite data structure                                           //
//                                                                                                                    //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package DataStructures;
import  MySpriteLib.ImageAnalysis;
import  java.util.ArrayList;
import  java.util.logging.Logger;
import  java.util.logging.Level;
import  java.awt.Color;
import  java.awt.Dimension;
import  java.awt.image.BufferedImage;

//////////////////
// LayeredImage //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Stores information on the layers that comprise an Image.
// in a linear arrayList.
//
// This class also contains procedures that manage data manipulation on all layer levels.
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  public class LayeredImage {

    ///////////////
    // Constants //
    /////////////// 
      public final int    MIN_DATA_RANGE      = 0;
      public final float  MIN_OPACITY_RANGE   = 0.0f;
      public final float  MAX_OPACITY_RANGE   = 1.0f;
      public final String DEFAULT_LAYER_NAME  = "Background";
      public final String NEW_LAYER_NAME      = "New Layer";
      public final String ERROR_MESSAGE       = "Error: Illegal ";
      public final String ERROR_LAYER_RANGE   = "layer range ";
      public final String ERROR_OPACITY_RANGE = "opacity range ";
    
    ////////////////////////
    // Instance Variables //
    ////////////////////////
      private        String           name;
      private        Dimension        size;
      private        float            opacity;
      private        ArrayList<Layer> layers;
      private static Logger           logger = Logger.getLogger(LayeredImage.class.getName());
    
    /////////////////
    // Constructor //
    /////////////////
      public LayeredImage( String name, Dimension size ){
        this.name     = name;
        this.opacity  = MAX_OPACITY_RANGE;
        this.size     = size;
        this.layers   = new ArrayList<Layer>();
        this.layers.add(new Layer(DEFAULT_LAYER_NAME, size));
      }
      
    /////////////////
    // Constructor //
    /////////////////
      public LayeredImage( String name, BufferedImage data ){ 
        this.name     = name;
        this.opacity  = MAX_OPACITY_RANGE;
        this.size     = new Dimension(data.getWidth(), data.getHeight());
        this.layers   = new ArrayList<Layer>();
        this.layers.add(new Layer(DEFAULT_LAYER_NAME, data));
      }
      
    /////////////////
    // Constructor //
    /////////////////
      public LayeredImage(LayeredImage image) {
        this.name = image.name;
        this.opacity = image.opacity;
        this.size = image.size;
        this.layers = new ArrayList<Layer>();
        for(Layer layer : image.layers) {
          layers.add(new Layer(layer));
        }
      }
    
    //////////////
    // addLayer //
    //////////////
      public void addLayer( int position ){
        this.layers.add(position, new Layer(NEW_LAYER_NAME, this.size));
      }
    
    //////////////
    // addLayer //
    //////////////
      public void addLayer( String name, int position ){
        this.layers.add(position, new Layer(name, this.size));
      }
    //////////////
    // addLayer //
    //////////////
      public void addLayer( Layer layer, int position ){
        this.layers.add(position, layer);
      }

    /////////////////
    // removeLayer //
    /////////////////
      public void removeLayer( int position ){
        this.layers.remove(position);
      }
    
    /////////////////
    // moveLayerTo //
    /////////////////
      public void moveLayerTo( int position, int destination ){
        this.layers.add(destination, layers.remove(position));
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
          logger.log(Level.WARNING, ERROR_MESSAGE + ERROR_OPACITY_RANGE + opacity);
        } else {
          float previousOpacity = this.opacity;
          this.opacity = opacity;
          for(Layer current : layers){
            if(previousOpacity != MIN_OPACITY_RANGE){
              current.updateOpacity(Math.max(current.getOpacity() - previousOpacity,MIN_OPACITY_RANGE));
            }
            current.updateOpacity(Math.min(current.getOpacity() + opacity, MAX_OPACITY_RANGE));
          }
        }
      }
      
    /////////////
    // getName //
    /////////////
      public String getName(){
        return this.name;
      }
    
    ////////////////
    // getOpacity //
    ////////////////
      public float getOpacity(){
        return this.opacity;
      }

    ///////////////////
    // getLayerNames //
    ///////////////////
      public String[] getLayerNames(){
        String[] output = new String[layers.size()];
        for(int i=0; i<layers.size(); i++){
          output[i] = layers.get(i).name;
        }
        return output;
      }

    ////////////////////
    // numberOfLayers //
    ////////////////////
      public ArrayList<Layer> getLayers(){
        return layers;
      }
      
    ///////////
    // image //
    ///////////
      public BufferedImage image(){
        BufferedImage image = layers.get(0).getData();
        for( int i = 1; i < layers.size(); i++ ){
          image = ImageAnalysis.makeComposite(layers.get(i).getData(), image);
        }
        return image;
      }

    //////////////////
    // flipVertical //
    //////////////////
      public void flipVertical(){
        for(Layer current : layers){
          current.flipVertical();
        }
      }
      
    ////////////////////
    // flipHorizontal //
    ////////////////////
      public void flipHorizontal(){
        for(Layer current : layers){
          current.flipHorizontal();
        }
      }

    ////////////
    // rotate //
    ////////////
      public void rotate(double radians){
        for(Layer current : layers){
          current.rotate(radians);
        }
      }

    ///////////
    // paint //
    ///////////
      public void paint( int selectedLayer, int x, int y, Color color ){
        this.layers.get(selectedLayer).paint(x,y,color);
      }
      
    //////////
    // line //
    //////////
      public void line( int selectedLayer, int x, int y, int x2, int y2, int size, Color color){
        this.layers.get(selectedLayer).line(x,y,x2,y2,size,color);
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
