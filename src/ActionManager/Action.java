
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                                                                                                                    //
//  Action.java                                                                                                       //
//  Michael Hardeman                                                                                                  //
//                                                                                                                    //
//  checks syntax and parses out literals from <action> tag in xml tool definitions                                   //
//                                                                                                                    //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package ActionManager;
import  java.util.HashMap;
import  java.util.Set;
import  java.util.logging.Logger;
import  java.util.logging.Level;

////////////
// Action //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// This class verifies syntax of actions in xml files
// 
// each instance must have command and parameters to match a Commands enumerated type.
// The constructor requires a command and parameter to be present. They are matched against the enumerated class
// to confirm valid syntax. The parameters values are then checked for correctness, and given default values if not.
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  public class Action {
    
    private static Logger logger = Logger.getLogger(Action.class.getName());
    
    //////////////
    // Commands //
    //////////////
      public enum Commands{
        
        //////////////////////
        // Enumerated Types //
        //////////////////////
          PAINT ( "paint", new String[] { "x",         "y", "size", "opacity", "color"} ),
          FILL  ( "fill",  new String[] { "selection",              "opacity", "color"} ),
          LINE  ( "line",  new String[] { "x",         "y", "size", "opacity", "color"} );
        
        ////////////////////////
        // Instance Variables //
        ////////////////////////
          private String   command;
          private String[] parameters;
        
        /////////////////
        // Constructor //
        /////////////////
          private Commands(String command, String[] parameters){
            this.command = command;
            this.parameters = parameters;
          }
        
        //////////////////
        // matchCommand //
        //////////////////
          public static Commands matchCommand(String command){
            for(Commands current : values()){
              if(current.command.equalsIgnoreCase(command)){
                return current;
              }
            }
            return null;
          }
        
        /////////////////////
        // matchParameters //
        /////////////////////
          public static boolean matchParameters(Commands command, Set<String> parameters){
            if(parameters.size() != command.parameters.length){
              logger.log(Level.WARNING, "Incorrect parameters size");
              return false;
            }
            for(String current : command.parameters){
              if(!current.contains(current)){
                logger.log(Level.WARNING, command.command+"::"+current);
                return false;
              }
            }
            return true;
          }
      }
    
    ////////////////////////
    // Instance Variables //
    ////////////////////////
      public String                 command;
      public HashMap<String,String> parameters;
    
    /////////////////
    // Constructor //
    /////////////////
      public Action(String command, HashMap<String,String> parameters){
        this.parameters = new HashMap<String,String>();
        if(!this.validateSyntax(command, parameters)){
          logger.log(Level.WARNING, "Invalid action definition.");
        }
      }
    
    ////////////////////
    // validateSyntax //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // This function takes the input command and parameters and validates the syntax.
    //
    // If the command does not match any Commands.command then false is returned.
    // If the command does match, then the parameters.keys are matched to the Commands.parameters.keys
    // if they keys do not match then false is returned
    // if they do match, check the value of the keys.
    // if the values are correct, only then do we accept and return true.
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      public boolean validateSyntax(String command, HashMap<String,String> parameters) {
        
        ///////////////////////////////////
        // check which command they want //
        ///////////////////////////////////
          switch(Commands.matchCommand(command)){
            
            ///////////
            // paint //
            ///////////
              case PAINT:
                this.command = "paint";
                if(Commands.matchParameters(Commands.PAINT, parameters.keySet())){
                  
                  ///////
                  // X //
                  ///////
                    if(parameters.containsKey("x")){
                      if(parameters.get("x").equalsIgnoreCase("currentx")){
                        this.parameters.put("x", "currentx");
                      } else {
                        try{
                          Integer.parseInt(parameters.get("x"));
                          this.parameters.put("x", parameters.get("x"));
                        } catch(NumberFormatException e){
                          logger.log(Level.WARNING, "Invalid value for action parameter x: "+parameters.get("x"));
                          this.parameters.put("x", "0");
                        }
                      }
                    } else {
                      logger.log(Level.WARNING, "No action parameter x");
                      this.parameters.put("x", "0");
                    }

                  ///////
                  // Y //
                  ///////
                    if(parameters.containsKey("y")){
                      if(parameters.get("y").equalsIgnoreCase("currenty")){
                        this.parameters.put("y", "currenty");
                      } else {
                        try{
                          Integer.parseInt(parameters.get("y"));
                          this.parameters.put("y", parameters.get("y"));
                        } catch(NumberFormatException e){
                          logger.log(Level.WARNING, "Invalid value for action parameter y: "+parameters.get("y"));
                          this.parameters.put("y", "0");
                        }
                      }
                    } else {
                      logger.log(Level.WARNING, "No action parameter y");
                      this.parameters.put("y", "0");
                    }

                  //////////
                  // size //
                  //////////
                    if(parameters.containsKey("size")){
                      if(parameters.get("size").equalsIgnoreCase("currentsize")){
                        this.parameters.put("size", "currentsize");
                      } else {
                        try{
                          Integer.parseInt(parameters.get("size"));
                          this.parameters.put("size", parameters.get("size"));
                        } catch(NumberFormatException e){
                          logger.log(Level.WARNING, "Invalid value for action parameter size: "+parameters.get("size"));
                          this.parameters.put("size", "0");
                        }
                      }
                    } else {
                      logger.log(Level.WARNING, "No action parameter size");
                      this.parameters.put("size", "1");
                    }

                  /////////////
                  // opacity //
                  /////////////
                    if(parameters.containsKey("opacity")){
                      if(parameters.get("opacity").equalsIgnoreCase("currentopacity")){
                        this.parameters.put("opacity", "currentopacity");
                      } else {
                        try{
                          Integer.parseInt(parameters.get("opacity"));
                          this.parameters.put("opacity", parameters.get("opacity"));
                        } catch(NumberFormatException e){
                          logger.log(Level.WARNING, "Invalid value for action parameter opacity: "+parameters.get("opacity"));
                          this.parameters.put("opacity", "0");
                        }
                      }
                    } else {
                      logger.log(Level.WARNING, "No action parameter opacity");
                      this.parameters.put("opacity", "0");
                    }

                  ///////////
                  // color //
                  ///////////
                    if(parameters.containsKey("color")){
                      if(parameters.get("color").equalsIgnoreCase("currentcolor")){
                        this.parameters.put("color", "currentcolor");
                      } else {
                      try{
                          Integer.parseInt(parameters.get("color"),16);
                          this.parameters.put("color", parameters.get("color"));
                        } catch(NumberFormatException e){
                          logger.log(Level.WARNING, "Invalid value for action parameter color: "+parameters.get("color"));
                          this.parameters.put("color", "0");
                        }
                      }
                    } else {
                      logger.log(Level.WARNING, "No action parameter color");
                      this.parameters.put("color", "0");
                    }
                } else {
                  logger.log(Level.WARNING, "parameters don't match.");
                }
                break;

            //////////
            // fill //
            //////////
              case FILL:
                this.command = "fill";
                if(Commands.matchParameters(Commands.FILL, parameters.keySet())){

                  ///////////////
                  // selection //
                  ///////////////
                    if(parameters.containsKey("selection")){
                      if(parameters.get("selection").equalsIgnoreCase("currentselection")){
                        this.parameters.put("selection", "currentselection");
                      } else {
                        logger.log(Level.WARNING, "Invalid value for action parameter selection: "+parameters.get("selection"));
                      }
                    } else {
                      logger.log(Level.WARNING, "No action parameter selection");
                      this.parameters.put("selection", "currentselection");
                    }

                  /////////////
                  // opacity //
                  /////////////
                    if(parameters.containsKey("opacity")){
                      if(parameters.get("opacity").equalsIgnoreCase("currentopacity")){
                        this.parameters.put("opacity", "currentopacity");
                      } else {
                      try{
                          Integer.parseInt(parameters.get("opacity"));
                          this.parameters.put("opacity", parameters.get("opacity"));
                        } catch(NumberFormatException e){
                          logger.log(Level.WARNING, "Invalid value for action parameter opacity: "+parameters.get("opacity"));
                          this.parameters.put("opacity", "0");
                        }
                      }
                    } else {
                      logger.log(Level.WARNING, "No action parameter opacity");
                      this.parameters.put("opacity", "0");
                    }

                  ///////////
                  // color //
                  ///////////
                    if(parameters.containsKey("color")){
                      if(parameters.get("color").equalsIgnoreCase("color")){
                        this.parameters.put("color", "currentcolor");
                      } else {
                      try{
                          Integer.parseInt(parameters.get("color"),16);
                          this.parameters.put("color", parameters.get("color"));
                        } catch(NumberFormatException e){
                          logger.log(Level.WARNING, "Invalid value for action parameter color: "+parameters.get("color"));
                          this.parameters.put("color", "0");
                        }
                      }
                    } else {
                      logger.log(Level.WARNING, "No action parameter color");
                      this.parameters.put("color", "0");
                    }
                } else {
                  logger.log(Level.WARNING, "parameters don't match.");
                }
                break;

            //////////
            // line //
            //////////
              case LINE:
                this.command = "line";
                if(Commands.matchParameters(Commands.LINE, parameters.keySet())){

                  ///////
                  // X //
                  ///////
                    if(parameters.containsKey("x")){
                      if(parameters.get("x").equalsIgnoreCase("currentx")){
                        this.parameters.put("x", "currentx");
                      } else {
                        try{
                          Integer.parseInt(parameters.get("x"));
                          this.parameters.put("x", parameters.get("x"));
                        } catch(NumberFormatException e){
                          logger.log(Level.WARNING, "Invalid value for action parameter x: "+parameters.get("x"));
                          this.parameters.put("x", "0");
                        }
                      }
                    } else {
                      logger.log(Level.WARNING, "No action parameter x");
                      this.parameters.put("x", "0");
                    }

                  ///////
                  // Y //
                  ///////
                    if(parameters.containsKey("y")){
                      if(parameters.get("y").equalsIgnoreCase("currenty")){
                        this.parameters.put("y", "currenty");
                      } else {
                        try{
                          Integer.parseInt(parameters.get("y"));
                          this.parameters.put("y", parameters.get("y"));
                        } catch(NumberFormatException e){
                          logger.log(Level.WARNING, "Invalid value for action parameter y: "+parameters.get("y"));
                          this.parameters.put("y", "0");
                        }
                      }
                    } else {
                      logger.log(Level.WARNING, "No action parameter y");
                      this.parameters.put("y", "0");
                    }

                  //////////
                  // size //
                  //////////
                    if(parameters.containsKey("size")){
                      if(parameters.get("size").equalsIgnoreCase("currentsize")){
                        this.parameters.put("size", "currentsize");
                      } else {
                      try{
                          Integer.parseInt(parameters.get("size"));
                          this.parameters.put("size", parameters.get("size"));
                        } catch(NumberFormatException e){
                          logger.log(Level.WARNING, "Invalid value for action parameter size: "+parameters.get("size"));
                          this.parameters.put("size", "0");
                        }
                      }
                    } else {
                      logger.log(Level.WARNING, "No action parameter size");
                      this.parameters.put("size", "1");
                    }

                  /////////////
                  // opacity //
                  /////////////
                    if(parameters.containsKey("opacity")){
                      if(parameters.get("opacity").equalsIgnoreCase("currentopacity")){
                        this.parameters.put("opacity", "currentopacity");
                      } else {
                      try{
                          Integer.parseInt(parameters.get("opacity"));
                          this.parameters.put("opacity", parameters.get("opacity"));
                        } catch(NumberFormatException e){
                          logger.log(Level.WARNING, "Invalid value for action parameter opacity: "+parameters.get("opacity"));
                          this.parameters.put("opacity", "0");
                        }
                      }
                    } else {
                      logger.log(Level.WARNING, "No action parameter opacity");
                      this.parameters.put("opacity", "0");
                    }

                  ///////////
                  // color //
                  ///////////
                    if(parameters.containsKey("color")){
                      if(parameters.get("color").equalsIgnoreCase("currentcolor")){
                        this.parameters.put("color", "currentcolor");
                      } else {
                      try{
                          Integer.parseInt(parameters.get("color"),16);
                          this.parameters.put("color", parameters.get("color"));
                        } catch(NumberFormatException e){
                          logger.log(Level.WARNING, "Invalid value for action parameter color: "+parameters.get("color"));
                          this.parameters.put("color", "0");
                        }
                      }
                    } else {
                      logger.log(Level.WARNING, "No action parameter color");
                      this.parameters.put("color", "0");
                    }
                } else {
                  logger.log(Level.WARNING, "parameters don't match.");
                }
                break;

            /////////////
            // default //
            /////////////
              default:
                logger.log(Level.SEVERE, "Invalid command: "+command);
                return false;
          }
        return true;
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
