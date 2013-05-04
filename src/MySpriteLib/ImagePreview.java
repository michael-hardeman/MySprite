
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                                                                                                                    //
//  ImagePreview.java                                                                                                 //
//  Michael Hardeman : Mhardeman2@student.gsu.edu                                                                     //
//  Matt Gold        : mattbgold@gmail.com                                                                            //
//                                                                                                                    //
//  MySprite Specific Gui Components and data structures                                                              //
//                                                                                                                    //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package MySpriteLib;
import  java.awt.Graphics2D;
import  java.awt.GridLayout;
import  java.awt.Image;
import  java.awt.event.ActionListener;
import  java.awt.image.BufferedImage;
import  javax.swing.ImageIcon;
import  javax.swing.JButton;
import  javax.swing.JLabel;
import  javax.swing.JPanel;

//////////////////
// ImagePreview //
//////////////////
  public class ImagePreview
    extends JPanel
  {

    ///////////////
    // Constants //
    ///////////////
      private static final long serialVersionUID = 1L;
    
    ////////////////////////
    // Instance Variables //
    ////////////////////////
      private JPanel  icon;
      private JButton wrapper;
      public  double  width;

    /////////////////
    // Constructor //
    /////////////////
      public ImagePreview(double width, BufferedImage image) {
        this.width    = width;
        this.icon     = new JPanel();
        this.icon     . setLayout(new GridLayout(0,1));
        double iscale = ((double)width/(double)image.getWidth());
        this          . icon.add(new JLabel(scale(image,iscale)));
        this.wrapper  = new JButton();
        this.wrapper.add                  (icon);
        this.wrapper.setOpaque            (false);
        this.wrapper.setBorderPainted     (false);
        this.wrapper.setContentAreaFilled (false);
        this.add(wrapper);
      }
    
    /////////////////////////////
    // setButtonActionListener //
    /////////////////////////////
      public void setButtonActionListener(ActionListener actionListener){
        wrapper.addActionListener(actionListener);
      }
    
    /////////////////
    // updateImage //
    /////////////////
      public void updateImage(double width, BufferedImage image) {
        this.removeAll();
        this.width    = width;
        this.icon     = new JPanel();
        this.icon     . setLayout(new GridLayout(0,1));
        double iscale = ((double)width/(double)image.getWidth());
        this.icon     . add(new JLabel(scale(image,iscale)));
        this.wrapper  = new JButton();
        this.wrapper.add                  (icon);
        this.wrapper.setOpaque            (false);
        this.wrapper.setBorderPainted     (false);
        this.wrapper.setContentAreaFilled (false);
        this.add(wrapper);
      }

    ///////////
    // scale //
    ///////////
      private ImageIcon scale(Image src, double scale) {
        int           w    = (int)(scale*src.getWidth(this));
        int           h    = (int)(scale*src.getHeight(this));
        int           type = BufferedImage.TYPE_INT_ARGB;
        BufferedImage dst  = new BufferedImage(w, h, type);
        Graphics2D    g2d  = dst.createGraphics();
        g2d.drawImage(src, 0, 0, w, h, this);
        g2d.dispose();
        return new ImageIcon(dst);
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
