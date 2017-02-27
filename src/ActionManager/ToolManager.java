
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                                                                                                                    //
//  ToolManager.java                                                                                                  //
//  Michael Hardeman                                                                                                  //
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
