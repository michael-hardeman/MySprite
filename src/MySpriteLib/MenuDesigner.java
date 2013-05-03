
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                                                                                                                    //
//  MenuDesigner.java                                                                                                 //
//  Michael Hardeman : Mhardeman2@student.gsu.edu                                                                     //
//  Matt Gold        : mattbgold@gmail.com                                                                            //
//                                                                                                                    //
//  MySprite Specific Gui Components and data structures                                                              //
//                                                                                                                    //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package MySpriteLib;
import  java.awt.event.KeyEvent;
import  javax.swing.ButtonGroup;

//////////////////
// MenuDesigner //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// This class contains a weird data structure
// that is used to generate the menubar.
// That's about it.
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  public class MenuDesigner {
    
    //////////////
    // MenuType //
    //////////////
      public enum MenuType {
        Menu,
        MenuItem,
        Checkbox,
        Radiobutton,
        Separator,
        EndMenu;
      }
    
    /////////////////
    // MenuElement //
    /////////////////
      public static class MenuElement{
      
        ////////////////////////
        // Instance Variables //
        ////////////////////////
          public String      value;
          public MenuType    type;
          public ButtonGroup group;
          public int         key;
        
        /////////////////
        // Constructor //
        /////////////////
          public MenuElement(MenuType type){
            this.value = null;
            this.type  = type;
            this.group = null;
            this.key   = 0;
          }
        
        /////////////////
        // Constructor //
        /////////////////
          public MenuElement(String value, MenuType type){
            this.value = value;
            this.type  = type;
            this.group = null;
            this.key   = 0;
          }
        
        /////////////////
        // Constructor //
        /////////////////
          public MenuElement(String value, MenuType type, int key){
            this.value = value;
            this.type  = type;
            this.group = null;
            this.key   = key;
          }
        
        /////////////////
        // Constructor //
        /////////////////
          public MenuElement(String value, MenuType type, ButtonGroup group){
            this.value = value;
            this.type  = type;
            this.group = group;
          }
      }
    
    ///////////////////
    // Button Groups //
    ///////////////////
      static ButtonGroup skinButtonGroup = new ButtonGroup();
    
    ////////////////
    // MenuDesign //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // This data structure utilizes the constructors above to store information.
    // The information is then read during program initialization by Menu Building code in the MySprite.java
    // constructor to create a JMenuBar for the GUI.
    //
    // Every time a MenuType.Menu is encountered, it is pushed onto the stack.
    // every other MenuType is added to the top element until an MenuType.EndMenu is encountered. Then the
    // Stack is popped.
    //
    // There is an implicit EndMenu at the end of each row in the array.
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      public static MenuElement[][]  menuDesign =
      {
        {
          new MenuElement( "File",                                     MenuType.Menu,        KeyEvent.VK_F   ),
          new MenuElement( "New",                                      MenuType.MenuItem,    KeyEvent.VK_N   ),
          new MenuElement( "Open",                                     MenuType.MenuItem,    KeyEvent.VK_O   ),
          new MenuElement( "Import",                                   MenuType.Menu,        KeyEvent.VK_I   ),
          new MenuElement( "Overwrite Current",                        MenuType.MenuItem,    KeyEvent.VK_O   ),
          new MenuElement( "Add to Current",                           MenuType.MenuItem,    KeyEvent.VK_A   ),
          new MenuElement(                                             MenuType.EndMenu                      ),
          new MenuElement(                                             MenuType.Separator                    ),
          new MenuElement( "Save",                                     MenuType.MenuItem,    KeyEvent.VK_S   ),
          new MenuElement( "Save As",                                  MenuType.MenuItem,    KeyEvent.VK_A   ),
          new MenuElement( "Export As",                                MenuType.Menu,        KeyEvent.VK_E   ),
          new MenuElement( "Images",                                   MenuType.MenuItem,    KeyEvent.VK_I   ),
          new MenuElement( "Sheet",                                    MenuType.MenuItem,    KeyEvent.VK_H   ),
          new MenuElement(                                             MenuType.EndMenu                      ),
          new MenuElement(                                             MenuType.Separator                    ),
          new MenuElement( "Close",                                    MenuType.MenuItem,    KeyEvent.VK_C   ),
          new MenuElement( "Exit",                                     MenuType.MenuItem,    KeyEvent.VK_X   ),
        },
        
        {
          new MenuElement( "Edit",                                     MenuType.Menu,        KeyEvent.VK_E   ),
          new MenuElement( "Undo",                                     MenuType.MenuItem,    KeyEvent.VK_U   ),
          new MenuElement( "Redo",                                     MenuType.MenuItem,    KeyEvent.VK_R   ),
          new MenuElement(                                             MenuType.Separator                    ),
          new MenuElement( "Cut",                                      MenuType.MenuItem,    KeyEvent.VK_T   ),
          new MenuElement( "Copy",                                     MenuType.MenuItem,    KeyEvent.VK_C   ),
          new MenuElement( "Paste",                                    MenuType.MenuItem,    KeyEvent.VK_P   ),
          new MenuElement( "Delete",                                   MenuType.MenuItem,    KeyEvent.VK_D   ),
          new MenuElement(                                             MenuType.Separator                    ),
          new MenuElement( "Preferences",                              MenuType.MenuItem,    KeyEvent.VK_F   ),
        },
        
        {
          new MenuElement( "Select",                                   MenuType.Menu,        KeyEvent.VK_S   ),
          new MenuElement( "Deselect",                                 MenuType.MenuItem,    KeyEvent.VK_D   ),
          new MenuElement( "Select All",                               MenuType.MenuItem,    KeyEvent.VK_A   ),
          new MenuElement( "Invert Selection",                         MenuType.MenuItem,    KeyEvent.VK_I   ),
          new MenuElement(                                             MenuType.Separator                    ),
          new MenuElement( "Expand",                                   MenuType.MenuItem,    KeyEvent.VK_E   ),
          new MenuElement( "Shrink",                                   MenuType.MenuItem,    KeyEvent.VK_S   ),
          new MenuElement( "To Layer",                                 MenuType.MenuItem,    KeyEvent.VK_L   ),
        },
        
        {
          new MenuElement( "View",                                     MenuType.Menu,        KeyEvent.VK_V   ),
          new MenuElement( "Zoom In",                                  MenuType.MenuItem,    KeyEvent.VK_I   ),
          new MenuElement( "Zoom Out",                                 MenuType.MenuItem,    KeyEvent.VK_O   ),
          new MenuElement( "Zoom To",                                  MenuType.Menu,        KeyEvent.VK_Z   ),
          new MenuElement( "100%",                                     MenuType.MenuItem,    KeyEvent.VK_1   ),
          new MenuElement( "200%",                                     MenuType.MenuItem,    KeyEvent.VK_2   ),
          new MenuElement( "300%",                                     MenuType.MenuItem,    KeyEvent.VK_3   ),
          new MenuElement( "400%",                                     MenuType.MenuItem,    KeyEvent.VK_4   ),
          new MenuElement( "500%",                                     MenuType.MenuItem,    KeyEvent.VK_5   ),
          new MenuElement( "1000%",                                    MenuType.MenuItem,    KeyEvent.VK_0   ),
          new MenuElement(                                             MenuType.EndMenu                      ),
          new MenuElement(                                             MenuType.Separator                    ),
          new MenuElement( "Show Grid",                                MenuType.Checkbox,    KeyEvent.VK_G   ),
          new MenuElement( "Transparency Image",                       MenuType.MenuItem,    KeyEvent.VK_T   ),
          new MenuElement(                                             MenuType.Separator                    ),
          new MenuElement( "Animation Ghosting",                       MenuType.Menu,        KeyEvent.VK_A   ),
          new MenuElement( "Next Image",                               MenuType.Checkbox,    KeyEvent.VK_N   ),
          new MenuElement( "Previous Image",                           MenuType.Checkbox,    KeyEvent.VK_P   ),
          new MenuElement(                                             MenuType.EndMenu                      ),
        },
        
        {
          new MenuElement( "Image",                                    MenuType.Menu,        KeyEvent.VK_I   ),
          new MenuElement( "Goto",                                     MenuType.Menu,        KeyEvent.VK_G   ),
          new MenuElement( "Next",                                     MenuType.MenuItem,    KeyEvent.VK_N   ),
          new MenuElement( "Previous",                                 MenuType.MenuItem,    KeyEvent.VK_P   ),
          new MenuElement(                                             MenuType.EndMenu                      ),
          new MenuElement(                                             MenuType.Separator                    ),
          new MenuElement( "Delete",                                   MenuType.MenuItem,    KeyEvent.VK_D   ),
          new MenuElement( "Canvas Size",                              MenuType.MenuItem,    KeyEvent.VK_C   ),
          new MenuElement(                                             MenuType.Separator                    ),
          new MenuElement( "New Layer",                                MenuType.MenuItem,    KeyEvent.VK_L   ),
          new MenuElement( "Delete Layer",                             MenuType.MenuItem,    KeyEvent.VK_T   ),
          new MenuElement( "Merge Layer",                              MenuType.Menu,        KeyEvent.VK_M   ),
          new MenuElement( "Up",                                       MenuType.MenuItem,    KeyEvent.VK_U   ),
          new MenuElement( "Down",                                     MenuType.MenuItem,    KeyEvent.VK_D   ),
          new MenuElement(                                             MenuType.EndMenu                      ),
        },
        
        {
          new MenuElement( "Modifiers",                                MenuType.Menu,        KeyEvent.VK_M   ),
          new MenuElement( "Transform",                                MenuType.Menu,        KeyEvent.VK_T   ),
          new MenuElement( "Sprite",                                   MenuType.Menu                         ),
          new MenuElement( "Shift",                                    MenuType.MenuItem,    KeyEvent.VK_S   ),
          new MenuElement( "Flip Sprite Vertically",                   MenuType.MenuItem                     ),
          new MenuElement( "Flip Sprite Horizontally",                 MenuType.MenuItem                     ),
          new MenuElement( "Rotate Sprite by Degrees",                 MenuType.MenuItem                     ),
          new MenuElement( "Rotate Sprite 90\u00b0 Clockwise",         MenuType.MenuItem                     ),
          new MenuElement( "Rotate Sprite 90\u00b0 Counter-clockwise", MenuType.MenuItem                     ),
          new MenuElement( "Scale Sprite",                             MenuType.MenuItem,    KeyEvent.VK_C   ),
          new MenuElement( "Stretch Sprite",                           MenuType.MenuItem,    KeyEvent.VK_T   ),
          new MenuElement(                                             MenuType.EndMenu                      ),
          new MenuElement( "Image",                                    MenuType.Menu                         ),
          new MenuElement( "Shift Image",                              MenuType.MenuItem                     ),
          new MenuElement( "Flip Image Vertically",                    MenuType.MenuItem                     ),
          new MenuElement( "Flip Image Horizontally",                  MenuType.MenuItem                     ),
          new MenuElement( "Rotate Image",                             MenuType.MenuItem                     ),
          new MenuElement(                                             MenuType.EndMenu                      ),
          new MenuElement( "Layer",                                    MenuType.Menu                         ),
          new MenuElement( "Shift Layer",                              MenuType.MenuItem                     ),
          new MenuElement( "Flip Layer Vertically",                    MenuType.MenuItem                     ),
          new MenuElement( "Flip Layer Horizontally",                  MenuType.MenuItem                     ),
          new MenuElement( "Rotate Layer",                             MenuType.MenuItem                     ),
          new MenuElement(                                             MenuType.EndMenu                      ),
          new MenuElement(                                             MenuType.Separator                    ),
          new MenuElement( "Hue/Saturation",                           MenuType.MenuItem,    KeyEvent.VK_H   ),
          new MenuElement( "Opacity",                                  MenuType.MenuItem,    KeyEvent.VK_O   ),
          new MenuElement( "Invert",                                   MenuType.MenuItem,    KeyEvent.VK_I   ),
          new MenuElement(                                             MenuType.Separator                    ),
          new MenuElement( "Erase Color",                              MenuType.MenuItem,    KeyEvent.VK_E   ),
          new MenuElement( "Anti-Alias",                               MenuType.MenuItem,    KeyEvent.VK_A   ),
          new MenuElement(                                             MenuType.Separator                    ),
          new MenuElement( "Crop To Selection",                        MenuType.MenuItem,    KeyEvent.VK_C   ),
          new MenuElement( "Auto-Crop",                                MenuType.MenuItem,    KeyEvent.VK_U   ),
        },
        
        {
          new MenuElement( "Animation",                                MenuType.Menu,        KeyEvent.VK_A   ),
          new MenuElement( "Cycle Images",                             MenuType.Menu,        KeyEvent.VK_C   ),
          new MenuElement( "Left",                                     MenuType.MenuItem,    KeyEvent.VK_L   ),
          new MenuElement( "Right",                                    MenuType.MenuItem,    KeyEvent.VK_R   ),
          new MenuElement(                                             MenuType.EndMenu                      ),
          new MenuElement( "Reverse",                                  MenuType.MenuItem,    KeyEvent.VK_R   ),
          new MenuElement( "Add Reverse",                              MenuType.Menu,        KeyEvent.VK_A   ),
          new MenuElement( "Even",                                     MenuType.MenuItem,    KeyEvent.VK_E   ),
          new MenuElement( "Odd",                                      MenuType.MenuItem,    KeyEvent.VK_O   ),
          new MenuElement(                                             MenuType.EndMenu                      ),
          new MenuElement( "Stretch",                                  MenuType.MenuItem,    KeyEvent.VK_S   ),
          new MenuElement( "Set Length",                               MenuType.MenuItem,    KeyEvent.VK_L   ),
        },
        
        {
          new MenuElement( "Window",                                   MenuType.Menu,        KeyEvent.VK_W   ),
          new MenuElement( "Toolbars",                                 MenuType.Menu,        KeyEvent.VK_T   ),
          new MenuElement( "Instant Tools",                            MenuType.Checkbox,    KeyEvent.VK_T   ),
          new MenuElement( "Color Picker",                             MenuType.Checkbox,    KeyEvent.VK_C   ),
          new MenuElement( "Pallet",                                   MenuType.Checkbox,    KeyEvent.VK_P   ),
          new MenuElement(                                             MenuType.EndMenu                      ),
          new MenuElement(                                             MenuType.Separator                    ),
          new MenuElement( "GUI Skin",                                 MenuType.Menu,        KeyEvent.VK_G   ),
          new MenuElement( "Autumn",                                   MenuType.Radiobutton, skinButtonGroup ),
          new MenuElement( "Business Black Steel",                     MenuType.Radiobutton, skinButtonGroup ),
          new MenuElement( "Business Blue Steel",                      MenuType.Radiobutton, skinButtonGroup ),
          new MenuElement( "Business",                                 MenuType.Radiobutton, skinButtonGroup ),
          new MenuElement( "Challenger Deep",                          MenuType.Radiobutton, skinButtonGroup ),
          new MenuElement( "Creme Coffee",                             MenuType.Radiobutton, skinButtonGroup ),
          new MenuElement( "Creme",                                    MenuType.Radiobutton, skinButtonGroup ),
          new MenuElement( "Dust Coffee",                              MenuType.Radiobutton, skinButtonGroup ),
          new MenuElement( "Dust",                                     MenuType.Radiobutton, skinButtonGroup ),
          new MenuElement( "Emerald Dusk",                             MenuType.Radiobutton, skinButtonGroup ),
          new MenuElement( "Gemini",                                   MenuType.Radiobutton, skinButtonGroup ),
          new MenuElement( "Graphite Aqua",                            MenuType.Radiobutton, skinButtonGroup ),
          new MenuElement( "Graphite Glass",                           MenuType.Radiobutton, skinButtonGroup ),
          new MenuElement( "Graphite",                                 MenuType.Radiobutton, skinButtonGroup ),
          new MenuElement( "Magellan",                                 MenuType.Radiobutton, skinButtonGroup ),
          new MenuElement( "Mariner",                                  MenuType.Radiobutton, skinButtonGroup ),
          new MenuElement( "Mist Aqua",                                MenuType.Radiobutton, skinButtonGroup ),
          new MenuElement( "Mist Silver",                              MenuType.Radiobutton, skinButtonGroup ),
          new MenuElement( "Moderate",                                 MenuType.Radiobutton, skinButtonGroup ),
          new MenuElement( "Nebula Brick Wall",                        MenuType.Radiobutton, skinButtonGroup ),
          new MenuElement( "Nebula",                                   MenuType.Radiobutton, skinButtonGroup ),
          new MenuElement( "Office Black 2007",                        MenuType.Radiobutton, skinButtonGroup ),
          new MenuElement( "Office Blue 2007",                         MenuType.Radiobutton, skinButtonGroup ),
          new MenuElement( "Office Silver 2007",                       MenuType.Radiobutton, skinButtonGroup ),
          new MenuElement( "Raven",                                    MenuType.Radiobutton, skinButtonGroup ),
          new MenuElement( "Sahara",                                   MenuType.Radiobutton, skinButtonGroup ),
          new MenuElement( "Twilight",                                 MenuType.Radiobutton, skinButtonGroup ),
          new MenuElement(                                             MenuType.EndMenu                      ),
          new MenuElement(                                             MenuType.Separator                    ),
          new MenuElement( "About",                                    MenuType.MenuItem,    KeyEvent.VK_A   ),
          new MenuElement( "Help",                                     MenuType.MenuItem,    KeyEvent.VK_H   ),
        },
      };
  }
