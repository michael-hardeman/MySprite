
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                                                                                                                    //
//  ToolManager.java                                                                                                  //
//  Michael Hardeman : Mhardeman2@student.gsu.edu                                                                     //
//                                                                                                                    //
//  Initializes all tools in assets\tools                                                                             //
//                                                                                                                    //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package ActionManager;
import java.io.File;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.modelmbean.XMLParseException;
import javax.xml.parsers.ParserConfigurationException;

/////////////////
// ToolManager //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// on program start, this is initialized with all xml files in assets
// it creates a tool for all valid tool definitions in assets
// and throws a null pointer if there aren't any tool definitions.
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  public class ToolManager {
    
    ////////////////////////
    // Instance Variables //
    ////////////////////////
      private        Tool             currentTool;
      private        LinkedList<Tool> tools;
      private static Logger           logger = Logger.getLogger(ToolManager.class.getName());
    
    /////////////////
    // Constructor //
    /////////////////
      public ToolManager(LinkedList<File> xmlFiles) throws NullPointerException{
        tools = new LinkedList<Tool>();
        for(File current : xmlFiles){
          try {
            Tool temp;
            temp = new Tool(current);
            tools.add(temp);
          } catch (XMLParseException e) {
            //invalid tool definition. ignore it
          } catch (ParserConfigurationException e) {
            //something wrong occurred. ignore it
          }
        }
        if(tools.size() != 0){
          currentTool = tools.element();
        } else {
          logger.log(Level.SEVERE, "No valid tool definitions in assets folder");
          throw new NullPointerException();
        }
      }
    
    //////////////
    // getTools //
    //////////////
      public LinkedList<Tool> getTools(){
        return tools;
      }
    
    //////////
    // size //
    //////////
      public int size(){
        return tools.size();
      }
      
    ////////////////////
    // getCurrentTool //
    ////////////////////
      public Tool getCurrentTool(){
        return this.currentTool;
      }
      
    ////////////////////
    // setCurrentTool //
    ////////////////////
      public void setCurrentTool(Tool tool){
        this.currentTool = tool;
      }
  }
