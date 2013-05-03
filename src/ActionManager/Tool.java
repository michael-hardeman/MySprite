
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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import MySpriteLib.XmlInterface;
import javax.management.modelmbean.XMLParseException;
import javax.xml.parsers.ParserConfigurationException;

//////////
// Tool //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Stores name, description, icon, and actions parsed from xml tool definitions in the assets folder.
// 
// These objects will be instantiated at the programs start, and the appropriate
// data structures initialized. This each valid xml tool definition will spawn an instance of Tool to track it's
// information, and generate buttons in the toolbar for user access to that tool.
//
// on mouse canvas interactions, these actions will be be retrieved and processed by the action manager.
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  public class Tool {
    
    ////////////////////////
    // Instance Variables //
    ////////////////////////
      public File   file;
      public String name        = "default name.";
      public String description = "default description.";
      public String icon        = "default.png";
      public LinkedList<Action> downActions;
      public LinkedList<Action> dragActions;
      public LinkedList<Action> upActions;
      private static Logger logger = Logger.getLogger(Tool.class.getName());
    
    /////////////////
    // Constructor //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // throws exceptions to notify the ToolManager that this tool isn't usable.
    // ParserConfigurationException is thrown when the parser breaks
    // XMLParseException is thrown when there is an invalid tool definition
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      public Tool(File file) throws XMLParseException, ParserConfigurationException{
        
        this.downActions = new LinkedList<Action>();
        this.dragActions = new LinkedList<Action>();
        this.upActions   = new LinkedList<Action>();
        
        ///////////////////////////
        // parse tool definition //
        ///////////////////////////
        this.file = file;
        XmlInterface xmlInterface = new XmlInterface();
        try{
          xmlInterface.parse(file);
        } catch(Exception exception){
          logger.log(Level.WARNING, "Could not parse ini file: "+this.file.getPath()+" Invalid tool definition");
          throw new XMLParseException();
        }
        if(xmlInterface.getRootTag().equalsIgnoreCase("tool")){
          
          //////////
          // name //
          //////////
            try{
              name = xmlInterface.getTagValue("name");
            } catch (NullPointerException e){
              logger.log(Level.WARNING, "No 'name' definied in "+file.getName());
            }
          
          /////////////////
          // description //
          /////////////////
            try{
              description = xmlInterface.getTagValue("description");
            } catch (NullPointerException e){
              logger.log(Level.WARNING, "No 'description' definied in "+file.getName());
            }
          
          //////////
          // icon //
          //////////
            try{
              icon = xmlInterface.getTagValue("icon");
            } catch (NullPointerException e){
              logger.log(Level.WARNING, "No 'icon' definied in "+file.getName());
            }
          
          ///////////////
          // mousedown //
          ///////////////
            try{
              Node mousedown = xmlInterface.getNodeWithTag("mousedown");
              LinkedList<Node> downActionNodes = xmlInterface.getAllNodesWithTag("action", mousedown);
              for(Node current : downActionNodes){
                NodeList children = current.getChildNodes();
                String command = null;
                HashMap<String,String> parameters = new HashMap<String,String>();
                boolean isCommand = true;
                for(int i=0; i < children.getLength(); i++ ){
                  if(children.item(i).getNodeType() == Node.ELEMENT_NODE){
                    if(isCommand){
                      command = xmlInterface.getNodeValue(children.item(i));
                      isCommand = false;
                    } else {
                      parameters.put(children.item(i).getNodeName(), xmlInterface.getNodeValue(children.item(i)));
                    }
                  }
                }
                if(command != null && parameters.size() != 0){
                  downActions.add(new Action(command, parameters));
                }
              }
            } catch (NullPointerException e){
              logger.log(Level.WARNING, "No 'mousedown' definied in "+file.getName());
            }
          
          ///////////////
          // mousedrag //
          ///////////////
            try{
              Node mousedrag = xmlInterface.getNodeWithTag("mousedrag");
              LinkedList<Node> dragActionNodes = xmlInterface.getAllNodesWithTag("action", mousedrag);
              for(Node current : dragActionNodes){
                NodeList children = current.getChildNodes();
                String command = new String();
                HashMap<String,String> parameters = new HashMap<String,String>();
                boolean isCommand = true;
                for(int i=0; i < children.getLength(); i++ ){
                  if(children.item(i).getNodeType() == Node.ELEMENT_NODE){
                    if(isCommand){
                      command = xmlInterface.getNodeValue(children.item(i));
                      isCommand = false;
                    } else {
                      parameters.put(children.item(i).getNodeName(), xmlInterface.getNodeValue(children.item(i)));
                    }
                  }
                }
                if(command != null && parameters.size() != 0){
                  dragActions.add(new Action(command, parameters));
                }
              }
            } catch (NullPointerException e){
            	logger.log(Level.WARNING, "No 'mousedrag' definied in "+file.getName());
            }
          
          /////////////
          // mouseup //
          /////////////
            try{
              Node mouseup = xmlInterface.getNodeWithTag("mouseup");
              LinkedList<Node> upActionNodes = xmlInterface.getAllNodesWithTag("action", mouseup);
              for(Node current : upActionNodes){
                NodeList children = current.getChildNodes();
                String command = new String();
                HashMap<String,String> parameters = new HashMap<String,String>();
                boolean isCommand = true;
                for(int i=0; i < children.getLength(); i++ ){
                  if(children.item(i).getNodeType() == Node.ELEMENT_NODE){
                    if(isCommand){
                      command = xmlInterface.getNodeValue(children.item(i));
                      isCommand = false;
                    } else {
                      parameters.put(children.item(i).getNodeName(), xmlInterface.getNodeValue(children.item(i)));
                    }
                  }
                }
                if(command != null && parameters.size() != 0){
                  upActions.add(new Action(command, parameters));
                }
              }
            } catch (NullPointerException e){
            	logger.log(Level.WARNING, "No 'mouseup' definied in "+file.getName());
            }
        
        } else {
          logger.log(Level.WARNING, "Invalid tool definition: "+file.getName());
          throw new XMLParseException();
        }
      }
  }
