
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                                                                                                                    //
//  IniInterface.java                                                                                                 //
//  Michael Hardeman : Mhardeman2@student.gsu.edu                                                                     //
//                                                                                                                    //
//  Just a front end for properties. I didn't actually have to do much. Thought it would be harder.                   //
//                                                                                                                    //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package MySpriteLib;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

//////////////////
// IniInterface //
//////////////////
  public class IniInterface{
    
    ////////////////////////
    // Instance Variables //
    ////////////////////////
      private Properties properties;
    
    /////////////////
    // Constructor //
    /////////////////
      public IniInterface(){
        this.properties = new Properties();
      }
    
    //////////
    // load //
    //////////
      public void load(FileInputStream inStream){
        try {
          this.properties.load(inStream);
          inStream.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    
    //////////
    // save //
    //////////
      public void save(FileOutputStream outStream){
        try {
          this.properties.store(outStream, "Auto-Generated Configuration File");
          outStream.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    
    /////////////////
    // containsKey //
    /////////////////
      public boolean containsKey(String key){
        return this.properties.containsKey(key);
      }
    
    //////////////
    // getValue //
    //////////////
      public String getValue(String key){
        if(this.properties.containsKey(key)){
          return (String)this.properties.get(key);
        } else {
          return null;
        }
      }
    
    ////////////
    // addKey //
    ////////////
      public void addKey(String key, String value){
        if(!this.properties.containsKey(key)){
          this.properties.put(key, value);
        } else {
          setKey(key, value);
        }
      }
    
    ////////////
    // setKey //
    ////////////
      public void setKey(String key, String value){
        if(this.properties.containsKey(key)){
          this.properties.setProperty(key, value);
        }
      }
    
    ///////////////
    // removeKey //
    ///////////////
      public void removeKey(String key){
        if(this.properties.containsKey(key)){
          this.properties.remove(key);
        }
      }
    
    ///////////
    // clear //
    ///////////
      public void clear(){
        this.properties.clear();
      }
    
    ///////////
    // print //
    ///////////
      public void print(){
        System.out.println();
        this.properties.list(System.out);
        System.out.println();
      }
  }
