
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                                                                                                                    //
//  SkinHelper.java                                                                                                   //
//  Michael Hardeman : Mhardeman2@student.gsu.edu                                                                     //
//                                                                                                                    //
//  This is really nasty code. Please ignore it. I put it here so I could forget about it and never see it again.     //
//                                                                                                                    //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package MySpriteLib;
import  MySpriteLib.Strings;
import  java.util.HashMap;
import  java.util.Map;
import  java.util.Iterator;
import  java.util.logging.Level;
import  java.util.logging.Logger;

import  javax.swing.JDialog;
import  javax.swing.JFrame;
import  javax.swing.UIManager;
import  javax.swing.UIManager.LookAndFeelInfo;
import  org.pushingpixels.substance.api.SubstanceLookAndFeel;
import  org.pushingpixels.substance.api.skin.SkinInfo;

////////////////
// SkinHelper //
////////////////
  public class SkinManager {
    
    ////////////////////////
    // Instance Variables //
    ////////////////////////
      private        HashMap<String, Object> skins;
      private        String                  currentSkin;
      private static Logger                  logger = Logger.getLogger(SkinManager.class.getName());
        
    /////////////////
    // Constructor //
    /////////////////
      public SkinManager(){
        this.currentSkin = UIManager.getLookAndFeel().getName();
        this.skins       = new HashMap<String, Object>();
        Map<String, SkinInfo> substanceSkins = SubstanceLookAndFeel.getAllSkins();
        Iterator<String> keys  = substanceSkins.keySet().iterator();
        while(keys.hasNext()){
          String currentKey = keys.next();
          this.skins.put(currentKey, substanceSkins.get(currentKey));
        }
        LookAndFeelInfo[] installedSkins = UIManager.getInstalledLookAndFeels();
        for(int i=0;i<installedSkins.length;i++){
          this.skins.put(installedSkins[i].getName(), installedSkins[i]);
        }
      }
      
    ///////////////////////////
    // stringToSubstanceSkin //
    ///////////////////////////
      private String stringToSkin(String item){
        String minKey      = null;
        int    minDistance = Integer.MAX_VALUE;
        Iterator<String> keys  = skins.keySet().iterator();
        while(keys.hasNext()){
          String currentKey = keys.next();
          int Distance = Strings.levenshteinDistance(item, currentKey);
          if(Distance < minDistance){
            minDistance = Distance;
            minKey      = currentKey;
          }
        }
        // Don't say anything or i'll hurt you. It had to be done.
        if(skins.get(minKey).getClass().getName().equals(SkinInfo.class.getName())){
          return ((SkinInfo)skins.get(minKey)).getClassName();
        } else if(skins.get(minKey).getClass().getName().equals(LookAndFeelInfo.class.getName())) {
          return ((LookAndFeelInfo)skins.get(minKey)).getClassName();
        } else {
          logger.log(Level.WARNING, "Unknown skin class: "+minKey);
          return null;
        }
      }
      
    ////////////////
    // changeSkin //
    ////////////////
      public void changeSkin(String skin){
        String realSkin = stringToSkin(skin);
        if(realSkin != null){
          if(realSkin.contains("substance")){
            try{
              JFrame.setDefaultLookAndFeelDecorated(true);
              JDialog.setDefaultLookAndFeelDecorated(true);
              SubstanceLookAndFeel.setSkin(realSkin);
              this.currentSkin = skin;
            } catch(Exception exception){
              logger.log(Level.WARNING, "Unable to change skins to: "+skin);
            }
          } else {
            try{
              JFrame.setDefaultLookAndFeelDecorated(false);
              JDialog.setDefaultLookAndFeelDecorated(false);
              UIManager.setLookAndFeel(realSkin);
              this.currentSkin = skin;
            } catch(Exception exception){
              logger.log(Level.WARNING, "Unable to change skins to: "+skin);
            }
          }
        } else {
          // Shouldn't happen. unless stringToSkin is enumerating weird skins that aren't installed or Substance.
          logger.log(Level.WARNING, "Unable to find skin name close to: "+skin);
        }
      }
    
    ////////////////////
    // getCurrentSkin //
    ////////////////////
      public String getCurrentSkin(){
        return this.currentSkin;
      }
    
    //////////////
    // getSkins //
    //////////////
      public String[] getSkins(){
        String[] output = new String[skins.keySet().size()];
        Iterator<String> keys = skins.keySet().iterator();
        int i=0;
        while(keys.hasNext()){
          String currentKey = keys.next();
          output[i++] = currentKey;
        }
        return output;
      }
    
    ////////////////
    // printSkins //
    ////////////////
      public void printSkins(){
        Iterator<String> keys = skins.keySet().iterator();
        while(keys.hasNext()){
          String currentKey = keys.next();
          System.out.println(currentKey);
        }
      }
    }
