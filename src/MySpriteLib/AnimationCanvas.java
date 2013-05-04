
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                                                                                                                    //
//  AnimationCanvas.java                                                                                              //
//  Michael Hardeman : Mhardeman2@student.gsu.edu                                                                     //
//  Matt Gold        : mattbgold@gmail.com                                                                            //
//                                                                                                                    //
//  MySprite Specific Gui Components and data structures                                                              //
//                                                                                                                    //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package MySpriteLib;
import  DataStructures.Sprite;
import  java.awt.Color;
import  java.awt.Graphics;
import  java.awt.Graphics2D;
import  java.awt.event.ActionEvent;
import  java.awt.event.ActionListener;
import  java.awt.image.BufferedImage;
import  javax.swing.JPanel;
import  javax.swing.Timer;

/////////////////////
// AnimationCanvas //
/////////////////////
  public class AnimationCanvas
    extends JPanel
  {
    ///////////////
    // Constants //
    ///////////////
      private static final long serialVersionUID = 1L;

    ////////////////////////
    // Instance Variables //
    ////////////////////////
      private BufferedImage[] images;
      private int             current;
      private boolean         frozen;
      private Timer           animationTimer;
      private int             width;

    /////////////////
    // Constructor //
    /////////////////
      public AnimationCanvas(Sprite sprite, int width){
        this.setBackground    (Color.GRAY);
        this.setDoubleBuffered(true);
        this.width          = width;
        this.current        = 0;
        this.frozen         = true;
        this.animationTimer = new Timer(
          (int)(1000/30),
          new ActionListener(){
            public void actionPerformed(ActionEvent e){
              nextImage();
            }
          }
        );
        updateSprite(sprite);
      }
    
    ////////////////////////
    // setBackgroundColor //
    ////////////////////////
      public void setBackgroundColor(Color c) {
        this.setBackground(c);
      }
    
    //////////////////
    // updateSprite //
    //////////////////
      public void updateSprite(Sprite images){
        int length = images.getImages().size();
        BufferedImage[] spriteImages = new BufferedImage[length];
        for(int i=0;i<length;i++){
          spriteImages[i] = images.getImages().get(i).image();
        }
        this.images = spriteImages;
      }
    
    /////////////////
    // rateRefresh //
    /////////////////
      public void rateRefresh(int fps) {
        if(fps>0) {
          animationTimer.setDelay((int)Math.ceil(1000.0f/(float)fps));
          if (!animationTimer.isRunning() && frozen == false) {
            animationTimer.start(); //this starts again after setting fps to 0 without clicking stop button
          }
        }
        else{
          animationTimer.stop(); //it is imperative we keep frozen = false in this case
        }
      }
    
    //////////
    // stop //
    //////////
      public void stop() {
        if (frozen == true){
          current = 0;
        } else {
          frozen = true;
          animationTimer.stop();
        }
      }
    
    ///////////
    // start //
    ///////////
      public void start() {
        if (frozen == false){
          current = 0;
        } else {
          frozen = false;
          animationTimer.start();
        }
      }

    ///////////////
    // nextImage //
    ///////////////
      public void nextImage(){
        if(current >= images.length - 1){
          current = 0;
        }  else {
          current++;
        }
        repaint();
      }

    ///////////////
    // prevImage //
    ///////////////
      public void prevImage(){
        if(current == 0){
          current = images.length - 1;
        }  else {
          current--;
        }
        repaint();
      }

    ///////////
    // paint //
    ///////////
      public void paint(Graphics g){
        super.paint(g);
        Graphics2D g2d = (Graphics2D)g;
        if (images != null) {
          double sc = width/(double)images[current].getWidth();
          g2d.drawImage(images[current], 0, 0, width, (int)(images[current].getHeight()*sc), this);
        }
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
