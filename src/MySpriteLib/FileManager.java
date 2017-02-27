
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                                                                                                                    //
//  FileManager.java                                                                                                  //
//  Michael Hardeman                                                                                                  //
//                                                                                                                    //
//  Reads all ini, xml, png, and spr files in .\assets into default data structures                                   //
//                                                                                                                    //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package MySpriteLib;
import  java.io.File;
import  java.io.IOException;
import  java.awt.image.BufferedImage;
import  java.util.HashMap;
import  java.util.Iterator;
import  java.util.LinkedList;
import  java.util.ListIterator;
import  java.util.Set;
import  java.util.logging.Level;
import  java.util.logging.Logger;
import  javax.imageio.ImageIO;

/////////////////
// FileManager //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// This file is pretty self explanitory. It traverses all files in TOP_DIR and if they match criterion specified
// in searchFolders, it adds them to the data structures ini, xml, png, or spr.
//
// the program can then retrieve the data by using .getIni(), .getXml(), .getPng(), and .getSpr()
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  public class FileManager {
    
    ///////////////
    // Constants //
    ///////////////
      public final String TOP_DIR = ".\\assets";
    
    /////////////////////
    // Data Structures //
    /////////////////////
      public        LinkedList<File>             ini;
      public        LinkedList<File>             xml;
      public        HashMap<File, BufferedImage> png;
      public        LinkedList<File>             spr;
      public static Logger                       logger = Logger.getLogger(FileManager.class.getName());
    
    /////////////////
    // Constructor //
    /////////////////
      public FileManager() {
        ini = new LinkedList<File>();
        xml = new LinkedList<File>();
        png = new HashMap<File, BufferedImage>();
        spr = new LinkedList<File>();
        File start = new File(TOP_DIR);
        if(start.exists() && start.isDirectory()){
          searchFolders(start);
        } else {
          logger.log(Level.WARNING, "/asset folder not found searching current directory");
          File backup = new File(".");
          if(backup.exists() && backup.isDirectory()){
            searchFolders(backup);
          } else {
            logger.log(Level.SEVERE, "current folder does not exist. Fatal Error.");
          }
        }
      }
    
    ///////////////////
    // searchFolders //
    ///////////////////
      public void searchFolders(File current) {
        if(current.isDirectory()){
          String subDirectories[] = current.list();
          for(int i=0; i<subDirectories.length; i++){
            searchFolders(new File(current.getAbsolutePath() + File.separator + subDirectories[i]));
          }
        } else {
          String file = current.getName().trim().toLowerCase();
          if(file.endsWith(".ini")){
            ini.add(current);
          } else if (file.endsWith(".xml")) {
            xml.add(current);
          } else if (file.endsWith(".png")) {
            try {
              png.put(current, ImageIO.read(current));
            } catch (IOException exception) {
              logger.log(Level.WARNING, "Could not read image: "+current.getName(), exception);
            }
          } else if (file.endsWith(".spr")) {
            spr.add(current);
          }
        }
      }
    
    ////////////
    // getIni //
    ////////////
      public File getIni(String name) {
        ListIterator<File> iterator = ini.listIterator();
        while(iterator.hasNext()){
          File current = iterator.next();
          if(current.getName().equals(name)){
            return current;
          }
        }
        return null;
      }
      
    ////////////
    // getXml //
    ////////////
      public File getXml(String name) {
        ListIterator<File> iterator = xml.listIterator();
        while(iterator.hasNext()){
          File current = iterator.next();
          if(current.getName().equals(name)){
            return current;
          }
        }
        return null;
      }
      
    ////////////
    // getPng //
    ////////////
      public BufferedImage getPng(String name) {
        Set<File> keys = png.keySet();
        Iterator<File> iterator = keys.iterator();
        while(iterator.hasNext()){
          File current = iterator.next();
          if(current.getName().equals(name)){
            return png.get(current);
          }
        }
        return null;
      }
    
    ////////////
    // getPng //
    ////////////
      public File getPngFile(String name){
        Set<File> keys = png.keySet();
        ListIterator<File> iterator = (ListIterator<File>)keys.iterator();
        while(iterator.hasNext()){
          File current = iterator.next();
          if(current.getName().equals(name)){
            return current;
          }
        }
        return null;
      }
    
    ////////////
    // getSpr //
    ////////////
      public File getSpr(String name) {
        ListIterator<File> iterator = spr.listIterator();
        while(iterator.hasNext()){
          File current = iterator.next();
          if(current.getName().equals(name)){
            return current;
          }
        }
        return null;
      }
      
  /////////////
  // getInis //
  /////////////
    public LinkedList<File> getInis() {
      return ini;
    }
    
  /////////////
  // getXmls //
  /////////////
    public LinkedList<File> getXmls() {
      return xml;
    }
    
  
  
  /////////////
  // getPngs //
  /////////////
    public HashMap<File, BufferedImage> getPngs() {
      return png;
    }
  
  /////////////
  // getSprs //
  /////////////
    public LinkedList<File> getSprs() {
      return spr;
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
