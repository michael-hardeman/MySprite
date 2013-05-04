
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                                                                                                                    //
//  MySprite.java                                                                                                     //
//  Michael Hardeman : Mhardeman2@student.gsu.edu                                                                     //
//  Matt Gold        : mattbgold@gmail.com                                                                            //
//  Robert Jenkins   : rjenkins12@student.gsu.edu                                                                     //
//  Michael Burlison : mburlison@gmail.com                                                                            //
//                                                                                                                    //
//  Main GUI, Thread Creation, file managing,  event handling methods found here.                                     //
//                                                                                                                    //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

import ActionManager.ActionManager;
import ActionManager.Tool;
import ActionManager.ToolManager;
import DataStructures.Layer;
import DataStructures.LayeredImage;
import DataStructures.Sprite;
import DataStructures.Stack;
import MySpriteLib.FileManager;
import MySpriteLib.MenuDesigner;
import MySpriteLib.AnimationCanvas;
import MySpriteLib.ImageCanvas;
import MySpriteLib.ImagePreview;
import MySpriteLib.IniInterface;
import MySpriteLib.SkinManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.*;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import static javax.swing.ScrollPaneConstants.*;

//////////////
// MySprite //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// MySprite extends JFrame and acts as both our gui, and our main class
// It instantiates itself, spawning a concurrent thread that waits for user input
// and reacts triggering listener functions.
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  public class MySprite extends JFrame {
  
  ///////////////
  // Constants //
  ///////////////
    private static final long       serialVersionUID   = 1L;
    private static final String     INIT_TITLE         = "MySprite v0.25";
    private static final String     PREFERENCES_FILE   = "MySprite.ini";
    private static final String     DEFAULT_SKIN       = "Raven";
    private static final String     DEFAULT_BACKGROUND = "background.png";
    private        final int        MIN_FPS            = 0;
    private        final int        MAX_FPS            = 60;
    private        final int        INIT_FPS           = 30;
    private        final Dimension  NEW_IMAGE_SIZE     = new Dimension(32,  32);
    private        final Dimension  minimumSize        = new Dimension(640, 480);
    private        final Dimension  rightSize          = new Dimension(128, 480);
    private        final Dimension  toolsGridSize      = new Dimension(256, 96);
    private        final Dimension  toolsGridAxis      = new Dimension(3,   8);
    private        final Dimension  animationGridSize  = new Dimension(800, 600);
    private        final Dimension  animationImageSize = new Dimension(90,  90);
    private        final Dimension  animationGridAxis  = new Dimension(0,   6);
    private        final Dimension  fpsSliderSize      = new Dimension(128, 58);
    private        final Dimension  opacitySliderSize  = new Dimension(128, 58);
    
  ////////////////////////
  // Instance Variables //
  ////////////////////////
    private        boolean                      showLeft       = true;
    private        boolean                      showCenter     = true;
    private        boolean                      showRight      = true;
    private        boolean                      imageTabActive = true;
    private static Logger                       logger         = Logger.getLogger(MySprite.class.getName());
    public  static FileManager                  fileManager;
    public  static IniInterface                 preferences;
    public  static ToolManager                  toolManager;
    public  static ActionManager                actionManager;
    public  static MySprite                     window;
    public         SkinManager                  skinManager;
    public         BorderLayout                 mainLayout;
    private        JMenuBar                     menuBar;
    private        Stack<JMenu>                 menuStack;
    private        MenuDesigner.MenuElement[][] menuDesign;
    public         Sprite                       sprite;
    public         LayeredImage                 imageClipboard;
    public         boolean                      applySkinToAnimation;
    
  ////////////////////
  // GUI Components //
  ////////////////////
    private JSplitPane      left;
    private JPanel          imageTools;
    private JPanel          animationPreview;
    private JPanel          imageToolsGrid;
    private JPanel          animationGrid;
    private JColorChooser   colorPicker;
    private JColorChooser   animColorPicker;
    private JTabbedPane     center;
    private ImageCanvas     image;
    private AnimationCanvas animation;
    private JSplitPane      right;
    private JPanel          images;
    private JScrollPane     imagesScrollPane;
    private JList<String>   imageLayers;
    private JPanel          animationButtons;
    private JPanel          animationControls;
    private JPanel          spriteButtons;
    private JPanel          rgbaPanel;
    private JSlider         opacitySlider;
    private JSpinner        opacityField;
  
  /////////////////
  // Constructor //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // This constructor instantiates all instance variables with their default values, It also instantiates
  // all GUI components and adds them to the window, setting all relevant properties.
  // Code in this procedure is arranged by which screen region the component will be occupying. Each region is
  // proceeded by a header giving details to it's location on the page.
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public MySprite ( String windowName ) {
      super(windowName);
      this.skinManager = new SkinManager();
      this.applySkinToAnimation = true;
      
      /////////////
      // MenuBar //
      //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      // This section instantiates the menubar, commonly found at the top of an application.
      // This section reads GuiElement.menuDesign, a 2D matrix of GuiElement, into a stack based upon the
      // enum GuiElement.MenuType. Every time a MenuType.Menu is encountered, it is pushed onto the stack.
      // every other MenuType is added to the top element until an MenuType.EndMenu is encountered. Then the
      // Stack is popped.
      //
      // There is an implicit EndMenu at the end of each row in the array.
      //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        menuDesign = MenuDesigner.menuDesign;
        menuBar    = new JMenuBar();
        menuStack  = new Stack<JMenu>();
        
        for(MenuDesigner.MenuElement[] currentMenu : menuDesign){
          for(MenuDesigner.MenuElement currentElement : currentMenu){
            
            //////////////////////
            // MenuType.EndMenu //
            //////////////////////
              if (currentElement.type == MenuDesigner.MenuType.EndMenu){
                JMenu subMenu = new JMenu("Temp");
                try{
                  subMenu = menuStack.pop();
                  menuStack.peek().add(subMenu);
                } catch (NullPointerException e){
                  logger.log(Level.WARNING, "Invalid Menu Design: "+subMenu.getText()+" failed to initialize");
                }
              }
            
            ///////////////////
            // MenuType.Menu //
            ///////////////////
              else if(currentElement.type == MenuDesigner.MenuType.Menu){
                JMenu temp = new JMenu(currentElement.value);
                temp.setMnemonic(currentElement.key);
                temp.addActionListener(new MenuActionListener());
                menuStack.push(temp);
              }
            
            ///////////////////////
            // MenuType.MenuItem //
            ///////////////////////
              else if (currentElement.type == MenuDesigner.MenuType.MenuItem){
                try{
                  JMenuItem temp = new JMenuItem(currentElement.value);
                  temp.setMnemonic(currentElement.key);
                  temp.addActionListener(new MenuActionListener());
                  menuStack.peek().add(temp);
                } catch (NullPointerException e){
                  logger.log(Level.WARNING, "Invalid Menu Design: "+currentElement.value+" - No parent menu");
                }
              }
            
            ///////////////////////
            // MenuType.Checkbox //
            ///////////////////////
              else if (currentElement.type == MenuDesigner.MenuType.Checkbox){
                try{
                  JCheckBoxMenuItem temp = new JCheckBoxMenuItem(currentElement.value);
                  temp.setMnemonic(currentElement.key);
                  temp.addActionListener(new MenuActionListener());
                  menuStack.peek().add(temp);
                } catch (NullPointerException e){
                  logger.log(Level.WARNING, "Invalid Menu Design: "+currentElement.value+" - No parent menu");
                }
              }
            
            //////////////////////////
            // MenuType.Radiobutton //
            //////////////////////////
              else if (currentElement.type == MenuDesigner.MenuType.Radiobutton){
                try{
                  JRadioButtonMenuItem temp = new JRadioButtonMenuItem(currentElement.value);
                  temp.addActionListener(new MenuActionListener());
                  temp.setMnemonic(currentElement.key);
                  if(currentElement.value == DEFAULT_SKIN){
                    temp.setSelected(true);
                  }
                  currentElement.group.add(temp);
                  menuStack.peek().add(temp);
                } catch (NullPointerException e){
                  logger.log(Level.WARNING, "Invalid Menu Design: "+currentElement.value+" - No parent menu");
                }
              }
            
            ////////////////////////
            // MenuType.Separator //
            ////////////////////////
              else if (currentElement.type == MenuDesigner.MenuType.Separator){
                try{
                  menuStack.peek().addSeparator();
                } catch (NullPointerException e){
                  logger.log(Level.WARNING, "Invalid Menu Design: "+currentElement.value+" - No parent menu");
                }
              }
            else {
              logger.log(Level.WARNING, "Invalid Menu Design: "+currentElement.type.toString()+" is invalid JMenu Component");
            }
          }
          //////////////////////
          // Implicit EndMenu //
          //////////////////////
            JMenu menu = new JMenu("Temp");
            try{
              menu = menuStack.pop();
              menuBar.add(menu);
            } catch (NullPointerException e){
              logger.log(Level.WARNING, "Invalid Menu Design: "+ menu.getText()+" failed to initialize properly");
            }
        }
        this.setJMenuBar(menuBar);
      
      ////////////////
      // MainWindow //
      //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      // This section instantiates all main Gui Components.
      //
      // Main Regions include:
      //   Left, Right, Center
      //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        mainLayout = new BorderLayout();
        this.setLayout(mainLayout);
        sprite = new Sprite("mySprite", NEW_IMAGE_SIZE);
        
        //////////
        // Left //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // local indentation indicates nesting level. first previous item of lower indentation is parent. 
        //
        // imageTools           : JPanel
        //   imageToolsGrid     : JPanel
        //   opacityPanel       : JPanel
        //     opacityBox       : JPanel
        //       opacityField   : JPanel
        //     opacitySlider    : JSlider
        //   rbgaPanel          : Jpanel
        //     colorPicker      : JColorChooser
        // animationTools       : JPanel
        //   animationPreview   : JPanel
        //     animation        : GuiElement.AnimationCanvas
        //   animationControl   : JPanel
        //     animationButtons : JPanel
        //     framesPerSecond  : JSlider
        //   rbgaPanel          : JPanel
        //     colorPicker      : JColorChooser
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
          
          ////////////////////
          // imageToolsGrid //
          ////////////////////
          imageToolsGrid = new JPanel();
          imageToolsGrid.setLayout(new GridLayout(toolsGridAxis.width,toolsGridAxis.height,1,1));
          imageToolsGrid.setPreferredSize(toolsGridSize);
          for(int gridx=0; gridx < toolsGridAxis.width ;gridx++){
            for(int gridy=0; gridy < toolsGridAxis.height ;gridy++){
              int index = (gridx*toolsGridAxis.width)+gridy;
              if (toolManager == null || index >= toolManager.size()) {
                JLabel blank = new JLabel();
                imageToolsGrid.add(blank);
              } else {
                JButton current = new JButton();
                try{
                  LinkedList<Tool> tools = toolManager.getTools();
                  current.addActionListener(new ToolActionListener(tools.get(index)));
                  current.setToolTipText(tools.get(index).name);
                  current.setIcon(
                    new ImageIcon(
                      fileManager.getPng(
                        tools.get(index).icon
                      )
                    )
                  );
                } catch (NullPointerException exception) {
                  logger.log(Level.WARNING, "Tool icon missing", exception);
                  current.setText("I");
                }
                imageToolsGrid.add(current);
              }
            }
          }
          
          ///////////////////
          // opacitySlider //
          ///////////////////
          opacitySlider = new JSlider(JSlider.HORIZONTAL,0,100,100);
          opacitySlider.addChangeListener(new OpacitySliderListener());
          opacitySlider.setMajorTickSpacing(20);
          opacitySlider.setMinorTickSpacing(5);
          opacitySlider.setPaintTicks(true);
          opacitySlider.setPaintLabels(true);
          opacitySlider.setPreferredSize(opacitySliderSize);
          opacitySlider.setToolTipText("Opacity");
          
          /////////////////
          // ColorPicker //
          /////////////////
          colorPicker = new JColorChooser();
          colorPicker.getSelectionModel().addChangeListener(new ColorPickerListener());
          
          ///////////////
          // rbgaPanel //
          ///////////////
          rgbaPanel = new JPanel();
          rgbaPanel.setLayout(new BoxLayout(rgbaPanel,BoxLayout.Y_AXIS));
          
          //////////////////
          // opacityField //
          //////////////////
          SpinnerModel sm = new SpinnerNumberModel(100, 0, 100, 1);
          opacityField = new JSpinner(sm);
          opacityField.addChangeListener(new OpacityFieldListener());
          
          ////////////////
          // opacityBox //
          ////////////////
          JPanel opacityBox = new JPanel();
          opacityBox.setLayout(new BoxLayout(opacityBox,BoxLayout.Y_AXIS));
          opacityBox.add(new JLabel("Opacity"));
          opacityBox.add(opacityField);
          
          //////////////////
          // opacityPanel //
          //////////////////
          JPanel opacityPanel = new JPanel();
          opacityPanel.setLayout(new BoxLayout(opacityPanel,BoxLayout.X_AXIS));
          opacityPanel.add(opacityBox);
          opacityPanel.add(opacitySlider);
          rgbaPanel.add(opacityPanel);
          rgbaPanel.add(colorPicker);
          
          ///////////////
          // animation //
          ///////////////
          animation = new AnimationCanvas(sprite,toolsGridSize.width);
            
          //////////////////////
          // animationButtons //
          //////////////////////
          animationButtons = new JPanel();
          animationButtons.setLayout(new BoxLayout(animationButtons,BoxLayout.X_AXIS));
          
          JButton   temp = null;
          try{
            ImageIcon prev = new ImageIcon(fileManager.getPng("anim-prev.png"));
            temp = new JButton(prev);
          } catch(NullPointerException exception){
            logger.log(Level.WARNING, "Tool icon missing", exception);
            temp = new JButton("<");
          }
          temp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              animation.prevImage();
            }
          });
          temp.setPreferredSize(new Dimension(35,35));
          temp.setToolTipText("Back");
          animationButtons.add(temp);
          
          temp = null;
          try{
            ImageIcon stop = new ImageIcon(fileManager.getPng("anim-stop.png"));
            temp = new JButton(stop);
          } catch(NullPointerException exception){
            logger.log(Level.WARNING, "Tool icon missing", exception);
            temp = new JButton("S");
          }
          temp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              animation.stop();
            }
          });
          temp.setPreferredSize(new Dimension(35,35));
          temp.setToolTipText("Stop");
          animationButtons.add(temp);
          
          temp = null;
          try{
            ImageIcon play = new ImageIcon(fileManager.getPng("anim-play.png"));
            temp = new JButton(play);
          } catch(NullPointerException exception){
            logger.log(Level.WARNING, "Tool icon missing", exception);
            temp = new JButton("P");
          }
          temp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              animation.start();
            }
          });
          temp.setPreferredSize(new Dimension(35,35));
          temp.setToolTipText("Play");
          animationButtons.add(temp);
          
          temp = null;
          try{
            ImageIcon next = new ImageIcon(fileManager.getPng("anim-next.png"));
            temp = new JButton(next);
          } catch(NullPointerException exception){
            logger.log(Level.WARNING, "Tool icon missing", exception);
            temp = new JButton(">");
          }
          temp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              animation.nextImage();
            }
          });
          temp.setPreferredSize(new Dimension(35,35));
          temp.setToolTipText("Forward");
          animationButtons.add(temp);
          
          /////////////////////
          // framesPerSecond //
          /////////////////////
          JSlider framesPerSecond = new JSlider(JSlider.HORIZONTAL, MIN_FPS, MAX_FPS, INIT_FPS);
          framesPerSecond.addChangeListener(new SliderChangeListener());
          framesPerSecond.setMajorTickSpacing(10);
          framesPerSecond.setMinorTickSpacing(2);
          framesPerSecond.setPaintTicks(true);
          framesPerSecond.setPaintLabels(true);
          framesPerSecond.setPreferredSize(fpsSliderSize);
          framesPerSecond.setToolTipText("FPS");
          
          ///////////////////////
          // animationControls //
          ///////////////////////
          animationControls = new JPanel();
          animationControls.setLayout(new BoxLayout(animationControls,BoxLayout.X_AXIS));
          animationControls.add(animationButtons);
          animationControls.add(framesPerSecond);
          
          imageTools = new JPanel();
          imageTools.add(imageToolsGrid);
          
          //////////////////////
          // animationPreview //
          //////////////////////
          animationPreview = new JPanel();
          animationPreview.setLayout(new BoxLayout(animationPreview,BoxLayout.Y_AXIS));
          animationPreview.add(animation);
          animationPreview.add(animationControls);
          
          animColorPicker = new JColorChooser();
          animColorPicker.getSelectionModel().addChangeListener(new ColorPickerListener());
          
        left = new JSplitPane(JSplitPane.VERTICAL_SPLIT, imageTools, rgbaPanel);
        left.setResizeWeight(1);
        if(showLeft) { this.add(left, BorderLayout.WEST); }
        
        ////////////
        // Center //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Center is a JTabbedPane, The top two elements are tabs, all others are nested within the tabs
        // according to their indentation level.
        //
        // image          : GuiElement.ImageCanvas
        // animationPanel : JPanel
        //   scrollBorder : JPanel
        //     gridScroll : JScrollPane
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
          
          ///////////
          // image //
          ///////////
          image = new ImageCanvas(actionManager, toolManager, sprite, fileManager.getPng(DEFAULT_BACKGROUND), fileManager.getPng(toolManager.getCurrentTool().icon));
          image.setName("Image");
          
          ////////////////////
          // animationPanel //
          ////////////////////
          JPanel animationPanel = new JPanel();
          animationPanel.setName("Animation");
          
          //////////////////
          // scrollBorder //
          //////////////////
          JPanel scrollBorder = new JPanel();
          animationGrid = new JPanel();
          animationGrid.setLayout(new GridLayout(animationGridAxis.width,animationGridAxis.height,8,8));
          scrollBorder.add(animationGrid);
          
          ////////////////
          // gridScroll //
          ////////////////
          JScrollPane gridScroll = new JScrollPane(scrollBorder);
          gridScroll.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
          gridScroll.setPreferredSize(animationGridSize);
          gridScroll.setName("Animation");
          animationPanel.add(gridScroll);
          
        center = new JTabbedPane();
        center.addTab(image.getName(), image);
        center.addTab(animationPanel.getName(), animationPanel);
        center.addChangeListener(new TabChangeListener());
        if(showCenter) { this.add(center, BorderLayout.CENTER); }
        
        ///////////
        // Right //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // This region is actually fairly easy. No nesting involved.
        //
        // images        : JPanel
        // imageLayers   : JList
        // spriteButtons : JPanel
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
          
          ////////////
          // images //
          ////////////
          images = new JPanel();
          images.setLayout(new GridLayout(0,1,0,7));
          JPanel imagesHolder =new JPanel();
          imagesHolder.add(images);
          imagesScrollPane = new JScrollPane(imagesHolder);
          imagesScrollPane.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
          imagesScrollPane.setPreferredSize(rightSize);
          
          /////////////////
          // imageLayers //
          /////////////////
          String[] initialLayer = {"Background"};
          imageLayers = new JList<String>(initialLayer);
          imageLayers.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
          imageLayers.setLayoutOrientation(JList.VERTICAL);
          imageLayers.addListSelectionListener(new LayerSelectionListener());
          
          ///////////////////
          // spriteButtons //
          ///////////////////
          spriteButtons = new JPanel();
          spriteButtons.setLayout(new BoxLayout(spriteButtons,BoxLayout.Y_AXIS));
          spriteButtons.add(new JLabel(" "));
          temp = new JButton("Insert");
          temp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              insertImage();
            }
          });
          spriteButtons.add(temp);
          spriteButtons.add(new JLabel(" "));
          temp = new JButton("Edit");
          temp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              editImage();
            }
          });
          spriteButtons.add(temp);
          spriteButtons.add(new JLabel(" "));
          temp = new JButton("Duplicate");
          temp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              duplicateImage();
            }
          });
          spriteButtons.add(temp);
          temp = new JButton("Copy");
          temp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              copyImage();
            }
          });
          spriteButtons.add(temp,BorderLayout.CENTER);
          temp = new JButton("Paste");
          temp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              pasteImage();
            }
          });
          spriteButtons.add(temp);
          temp = new JButton("Delete");
          temp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              deleteImage();
            }
          });
          spriteButtons.add(temp);
          spriteButtons.add(new JLabel(" "));
          temp = new JButton("Move Right");
          temp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              moveImageRight();
            }
          });
          spriteButtons.add(temp);
          temp = new JButton("Move Left");
          temp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              moveImageLeft();
            }
          });
          spriteButtons.add(temp);
          
        right = new JSplitPane(JSplitPane.VERTICAL_SPLIT, imagesScrollPane, imageLayers);
        right.setResizeWeight(.75);
        right.setPreferredSize(rightSize);
        if(showRight) { this.add(right,BorderLayout.EAST); }
        
      ////////////
      // window //
      ////////////
      this.pack();
      this.setLocationByPlatform(true);
      this.addWindowListener(new MyWindowListener());
      
      if(preferences.containsKey("skin")){
        skinManager.changeSkin(preferences.getValue("skin"));
      } else {
        skinManager.changeSkin(DEFAULT_SKIN);
        preferences.addKey("skin", DEFAULT_SKIN);
      }
      this.repaint();
    }
  
  ////////////////////
  // getMinimumSize //
  ////////////////////
    public Dimension getMinimumSize() {
      return this.minimumSize;
    }
    
  //////////
  // Main //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // The main initializes all base subsystems, and creates the gui.
  // program control then passes on to the gui, and a reaction based interaction system.
  //
  // tasks the main must preform:
  //   enumerating assets
  //   loading preferences
  //   reading tools
  //   initialize actionManager
  //   create and invoke gui
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void main(String[] args){
      
      logger.setLevel(Level.ALL);
      
      //////////////////////
      // enumerate assets //
      //////////////////////
        fileManager = new FileManager();
        
      //////////////////////
      // load preferences //
      //////////////////////
        preferences = new IniInterface();
        try {
          preferences.load(new FileInputStream(fileManager.getIni(PREFERENCES_FILE)));
        } catch (Exception e) {
          logger.log(Level.WARNING, "Could not load preferences");
        }
      
      ////////////////
      // read tools //
      ////////////////
        toolManager = null;
        try {
          toolManager = new ToolManager(fileManager.getXmls());
        } catch (NullPointerException exception){
          logger.log(Level.SEVERE, "No tools definined. Check for valid tool xml definitions in .\\assets", exception);
        }
      
      //////////////////////////////
      // initialize actionManager //
      //////////////////////////////
        actionManager = new ActionManager();
      
      ///////////////////////////
      // create and invoke GUI //
      ///////////////////////////
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
        SwingUtilities.invokeLater(new Runnable(){
          public void run(){
            window = new MySprite(INIT_TITLE);
            window.setMinimumSize(window.getMinimumSize());
            window.setExtendedState(window.getExtendedState() | JFrame.MAXIMIZED_BOTH);
            window.setVisible(true);
            window.updateSpriteComponents();
          }
        });
    }
    
  //////////////////////
  // MyWindowListener //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // This is called whenever the window is interacted with. I.E. opened, closed, minimized, unminimized.
  // 
  // All we use it for is cleaning up when closing, but we really should sleep on iconified to free up CPU cycles
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private class MyWindowListener implements WindowListener{
      public void windowClosing(WindowEvent arg0) {
        try {
          preferences.save(new FileOutputStream(fileManager.getIni(PREFERENCES_FILE)));
        } catch (Exception e) {
          logger.log(Level.WARNING, "Could not save preferences");
        }
        window.dispose();
        System.exit(0);
      }
      public void windowOpened      (WindowEvent arg0) {}
      public void windowClosed      (WindowEvent arg0) {}
      public void windowActivated   (WindowEvent arg0) {}
      public void windowDeactivated (WindowEvent arg0) {}
      public void windowDeiconified (WindowEvent arg0) {}
      public void windowIconified   (WindowEvent arg0) {}
    }
  
  ///////////////////////
  // TabChangeListener //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // This is called whenever the user changes from one tab to the other in Center
  // It is responsible for switching out gui components related to either tab, such as the tools/preview panels
  // 
  // Case  0: is Image tab
  // Case  1: is Animation tab
  // default: twilight zone o_O
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private class TabChangeListener implements ChangeListener{
      public void stateChanged(ChangeEvent event) {
        switch(((JTabbedPane)event.getSource()).getSelectedIndex()){
          case 0:
            updateCurrentComponent();
            imageTabActive = true;
            window.image.repaint();
            window.remove(window.left);
            window.left = new JSplitPane(JSplitPane.VERTICAL_SPLIT, window.imageTools, window.rgbaPanel);
            window.left.setResizeWeight(1);
            if(window.showLeft) { window.add(window.left, BorderLayout.WEST); }
            window.remove(window.right);
            window.right = new JSplitPane(JSplitPane.VERTICAL_SPLIT, window.imagesScrollPane, window.imageLayers);
            window.right.setResizeWeight(.75);
            right.setPreferredSize(rightSize);
            if(window.showRight) { window.add(window.right, BorderLayout.EAST); }
            break;
          case 1:
            updateCurrentComponent();
            imageTabActive = false;
            window.animation.repaint();
            window.remove(window.left);
            window.left = new JSplitPane(JSplitPane.VERTICAL_SPLIT, window.animationPreview,window.animColorPicker);
            window.left.setResizeWeight(1);
            if(window.showLeft) { window.add(window.left, BorderLayout.WEST); }
            window.remove(window.right);
            window.right = new JSplitPane(JSplitPane.VERTICAL_SPLIT, window.spriteButtons, null);
            window.right.setResizeWeight(.75);
            right.setPreferredSize(rightSize);
            if(window.showRight) { window.add(window.right, BorderLayout.EAST); }
            if(window.applySkinToAnimation){
              window.skinManager.changeSkin(window.skinManager.getCurrentSkin());
              window.applySkinToAnimation = false;
            }
            
            break;
          default:
            logger.log(Level.SEVERE, "Invalid tab state: "+((JTabbedPane)event.getSource()).getSelectedIndex(), new NullPointerException());
            break;
        }
      }
    }
  
  /////////////////////////
  // SlideChangeListener //
  /////////////////////////
    private class SliderChangeListener implements ChangeListener{
      public void stateChanged(ChangeEvent event) {
        JSlider source = (JSlider)event.getSource();
        if (!source.getValueIsAdjusting()) {
          int fps = (int)source.getValue();
          animation.rateRefresh(fps);
        }
      }
    }
    
  ///////////////////////////
  // OpacitySliderListener //
  ///////////////////////////
    private class OpacitySliderListener implements ChangeListener{
      public void stateChanged(ChangeEvent event) {
        JSlider source = (JSlider)event.getSource();
        if (!source.getValueIsAdjusting()) {
          int opacity = (int)source.getValue();
          opacityField.setValue(opacity);
          actionManager.setCurrentOpacity((float)opacity/100f);
        }
      }
    }
    
  //////////////////////////
  // OpacityFieldListener //
  //////////////////////////
    private class OpacityFieldListener implements ChangeListener{
      public void stateChanged(ChangeEvent event) {
        JSpinner source = (JSpinner)event.getSource();
        SpinnerNumberModel sm = (SpinnerNumberModel)source.getModel();
        int opacity = sm.getNumber().intValue();
        opacitySlider.setValue(opacity);
        actionManager.setCurrentOpacity((float)opacity/100f);
      }
    }
    
  ////////////////////////
  // ToolActionListener //
  ////////////////////////
    private class ToolActionListener implements ActionListener {
      private Tool tool;
      public ToolActionListener(Tool tool) {
        this.tool = tool;
      }
      public void actionPerformed(ActionEvent e) {
        MySprite.toolManager.setCurrentTool(tool);
        try {
          BufferedImage cursor = MySprite.fileManager.getPng(tool.icon);
          window.image.updateCursor(cursor);
        } catch (NullPointerException exception) {
          logger.log(Level.WARNING, "Unable to find Image cursor", exception);
        }
      }
    }
    
  ///////////////////////////
  // ImagesPreviewListener //
  ///////////////////////////
    public class ImagePreviewListener implements ActionListener {
      public void actionPerformed(ActionEvent e) {
        Component sourceButton = (Component) e.getSource();
        Component[] allButtons = sourceButton.getParent().getParent().getComponents();
        for(int i=0; i < allButtons.length ; i++ ) {
          if(!allButtons[i].equals(sourceButton.getParent())){
            allButtons[i].setBackground(null);
          } else {
            allButtons[i].setBackground(Color.WHITE);
            actionManager.setCurrentImage(i);
          }
        }
      }
    }
    
  ////////////////////////////
  // LayerSelectionListener //
  ////////////////////////////
    public class LayerSelectionListener implements ListSelectionListener {
      public void valueChanged(ListSelectionEvent event){
        if(event.getValueIsAdjusting()){
          return; //Called while value is adjusting
        } else {
          try{
            @SuppressWarnings({ "unchecked", "unused" })
            JList<String> source = (JList<String>)event.getSource();
            // TODO : actionManager.setCurrentLayer(source.getSelectedIndex());
          } catch(Exception exception){
            logger.log(Level.SEVERE, "JList event could not be cast. Bad juju.");
          }
        }
      }
    }
    
  /////////////////////////
  // ColorPickerListener //
  /////////////////////////
    public class ColorPickerListener implements ChangeListener {
      public void stateChanged(ChangeEvent e) {
        if (imageTabActive) {
          actionManager.setCurrentColor(window.colorPicker.getColor());
        }
        else {
          animation.setBackgroundColor(window.animColorPicker.getColor());
        }
      }
    }
    
  ////////////////////////
  // MenuActionListener //
  ////////////////////////
    private class MenuActionListener implements ActionListener{
      public void actionPerformed(ActionEvent e) {
        String command =  e.getActionCommand();
        for(int i=0; i< menuDesign.length; i++){
          for(int j=0; j<menuDesign[i].length; j++){
            if(i==0){
              if(command.equals("New"))
                mFile_New();
              else if(command.equals("Open"))
                mFile_Open();
              else if(command.equals("Save"))
                mFile_Save();
              else if(command.equals("Save As"))
                mFile_SaveAs();
              else if(command.equals("Overwrite Current"))
                mFile_Import(false);
              else if(command.equals("Add to Current"))
                mFile_Import(true);
              else if(command.equals("Images"))
                mFile_ExportAsImages();
              else if(command.equals("Sheet"))
                mFile_ExportAsSheet();
              else if(command.equals("Close"))
                mFile_Close();
              else if(command.equals("Exit"))
                mFile_Exit();
              break;
            }
            else if(i==1){
              if(command.equals("Undo"))
                mEdit_Undo();
              else if(command.equals("Redo"))
                mEdit_Redo();
              else if(command.equals("Cut"))
                mEdit_Cut();
              else if(command.equals("Copy"))
                mEdit_Copy();
              else if(command.equals("Paste"))
                mEdit_Paste();
              else if(command.equals("Delete"))
                mEdit_Delete();
              else if(command.equals("Preferences"))
                mEdit_Preferences();
              break;
            }
            else if(i==2){
              if(command.equals("Deselect"))
                mSelect_Deselect();
              else if(command.equals("Select All"))
                mSelect_SelectAll();
              else if(command.equals("Invert Selection"))
                mSelect_InvertSelection();
              else if(command.equals("Expand"))
                mSelect_Expand();
              else if(command.equals("Shrink"))
                mSelect_Shrink();
              else if(command.equals("To Layer"))
                mSelect_ToLayer();
              break;
            }
            else if(i==3){
              if(command.equals("Zoom In"))
                mView_ZoomIn();
              else if(command.equals("Zoom Out"))
                mView_ZoomOut();
              else if(command.equals("Show Grid"))
                mView_ShowGrid();
              else if(command.equals("Transparency Image"))
                mView_TransparencyImage();
              else if(command.equals("Next Image"))
                mView_AnimationGhostNext();
              else if(command.equals("Previous Image"))
                mView_AnimationGhostPrev();
              else if(command.contains("%"))
                mView_ZoomTo(Integer.parseInt(command.replaceAll("%","")));
              break;
            }
            else if(i==4){
              if(command.equals("Next") || command.equals("Previous"))
                mImage_Goto(command);
              else if(command.equals("Delete"))
                mImage_Delete();
              else if(command.equals("Canvas Size"))
                mImage_CanvasSize();
              else if(command.equals("New Layer"))
                mImage_NewLayer();
              else if(command.equals("Delete Layer"))
                mImage_DeleteLayer();
              else if(command.equals("Up") || command.equals("Down"))
                mImage_MergeLayer(command);
              break;
            }
            else if(i==5){
              if(command.equals("Shift"))
                mMods_Shift();
              else if(command.equals("Flip Sprite Horizontally"))
                mMods_FlipSpriteHorizontal();
              else if(command.equals("Flip Image Horizontally"))
                mMods_FlipImageHorizontal();
              else if(command.equals("Flip Layer Horizontally"))
                mMods_FlipLayerHorizontal();
              else if(command.equals("Flip Sprite Vertically"))
                mMods_FlipSpriteVertical();
              else if(command.equals("Flip Image Vertically"))
                mMods_FlipImageVertical();
              else if(command.equals("Flip Layer Vertically"))
                mMods_FlipLayerVertical();
              else if(command.equals("Rotate Sprite by Degrees"))
                mMods_RotateSpriteByDegrees();
              else if(command.equals("Rotate Sprite 90\u00b0 Clockwise"))
                mMods_RotateSprite90CW();
              else if(command.equals("Rotate Sprite 90\u00b0 Counter-clockwise"))
                mMods_RotateSprite90CCW();
              else if(command.equals("Rotate Image"))
                mMods_RotateImage();
              else if(command.equals("Rotate Layer"))
                mMods_RotateLayer();
              else if(command.equals("Scale Sprite"))
                mMods_ScaleSprite();
              else if(command.equals("Stretch Sprite"))
                mMods_StretchSprite();
              else if(command.equals("Hue/Saturation"))
                mMods_HSV();
              else if(command.equals("Opacity"))
                mMods_Opacity();
              else if(command.equals("Invert"))
                mMods_Invert();
              else if(command.equals("Erase Color"))
                mMods_EraseColor();
              else if(command.equals("Anti-Alias"))
                mMods_AntiAlias();
              else if(command.equals("Crop To Selection"))
                mMods_CropToSelection();
              else if(command.equals("Auto-Crop"))
                mMods_CropAuto();
              break;
            }
            else if(i==6){
              if(command.equals("Left") || command.equals("Right"))
                mAnimation_Cycle(command);
              else if(command.equals("Reverse"))
                mAnimation_Reverse();
              else if(command.equals("Even") || command.equals("Odd"))
                mAnimation_AddReverse(command);
              else if(command.equals("Stretch"))
                mAnimation_Stretch();
              else if(command.equals("Set Length"))
                mAnimation_SetLength();
              break;
            }
            else if (i==7){
              if(command.equals(menuDesign[i][j].value)){
                if(j>=2 && j<=4)
                  mWindow_ToggleToolBar(command);
                else if(j>=8 && j<=34)
                  mWindow_ChangeSkin(command);
                else if(command.equals("About"))
                  mWindow_About();
                else if(command.equals("Help"))
                  mWindow_Help();
                break;
              }
            }
          }
        }
      }
    }
    
  //////////////////////////////////////////////////////////////
  ////*************  BUTTON HANDLER METHODS  ***************////
  //////////////////////////////////////////////////////////////
  private void insertImage()
  {
    int index = actionManager.getCurrentImage();
    sprite.insertImage(index,new LayeredImage(sprite.getName() + index, sprite.getSize()));
    updateSpriteComponents();
  }
  private void editImage()
  {
    center.setSelectedIndex(0);
    
    updateCurrentComponent();
    imageTabActive = true;
    window.image.repaint();
    
    window.remove(window.left);
    window.left = new JSplitPane(JSplitPane.VERTICAL_SPLIT, window.imageTools, window.rgbaPanel);
    
    window.left.setResizeWeight(1);
    if(window.showLeft) { window.add(window.left, BorderLayout.WEST); }
    
    window.remove(window.right);
    window.right = new JSplitPane(JSplitPane.VERTICAL_SPLIT, window.imagesScrollPane, window.imageLayers);
    window.right.setResizeWeight(.75);
    right.setPreferredSize(rightSize);
    window.add(window.right, BorderLayout.EAST);
  }
  private void duplicateImage()
  {
    int index = actionManager.getCurrentImage();
    LayeredImage img = new LayeredImage(sprite.getImages().get(index));
    sprite.insertImage(index,img);
    updateSpriteComponents();
  }
  private void copyImage()
  {
    imageClipboard = new LayeredImage(sprite.getImages().get(actionManager.getCurrentImage()));
  }
  private void pasteImage()
  {
    if (imageClipboard != null) {
      int index = actionManager.getCurrentImage();
      sprite.insertImage(index,imageClipboard);
      updateSpriteComponents();
    }
  }
  private void moveImageRight()
  {
    if (sprite.getImages().size() > 1) {
      int index = actionManager.getCurrentImage();
      LayeredImage img = new LayeredImage(sprite.getImages().get(index));
      sprite.deleteImage(index);
      index = index+1>(sprite.getImages().size()) ? 0 : index+1;
      actionManager.setCurrentImage(index);
      sprite.insertImage(index, img);
      updateSpriteComponents();
    }
  }
  private void moveImageLeft()
  {
    if (sprite.getImages().size() > 1) {
      int index = actionManager.getCurrentImage();
      LayeredImage img = new LayeredImage(sprite.getImages().get(index));
      sprite.deleteImage(index);
      index = index-1<0 ? sprite.getImages().size() : index-1;
      actionManager.setCurrentImage(index);
      sprite.insertImage(index, img);
      updateSpriteComponents();
    }
  }
  private void deleteImage() 
  {
    if (sprite.getImages().size() > 1) {
      int index = actionManager.getCurrentImage();
        sprite.deleteImage(index);
        actionManager.setCurrentImage(Math.max(actionManager.getCurrentImage()-1,0));
        updateSpriteComponents();
    }
  }
    ////////////////////////////////////////////////////////////
    ////*************  MENU HANDLER METHODS  ***************////
    ////////////////////////////////////////////////////////////
    
    ////////////////////////////////////////////////
    //-----------------FILE-----------------------//
    ////////////////////////////////////////////////
    private void mFile_New()
    {
      JTextField nameField = new JTextField(7);
      nameField.setText("newSprite");
      SpinnerNumberModel smw = new SpinnerNumberModel(32,1, 1000, 1);
      SpinnerNumberModel smh = new SpinnerNumberModel(32,1, 1000, 1);
      JSpinner widthField    = new JSpinner(smw);
      JSpinner heightField   = new JSpinner(smh);
      JPanel pnBoxes         = new JPanel();
      JPanel pnFields        = new JPanel();
      JPanel pnLabels        = new JPanel();
      pnLabels.setLayout(new BoxLayout(pnLabels,BoxLayout.LINE_AXIS));
      pnLabels.add(new JLabel("Name:              "));
      pnLabels.add(new JLabel("Width(px):         "));
      pnLabels.add(new JLabel("Height(px):        "));
      pnFields.setLayout(new FlowLayout());
      pnFields.add(nameField);
      pnFields.add(widthField);
      pnFields.add(heightField);
      pnBoxes.setLayout(new BoxLayout(pnBoxes,BoxLayout.Y_AXIS));
      pnBoxes.add(pnLabels);
      pnBoxes.add(pnFields);
      
      Object[] params = {"Enter sprite name, width, and height.",pnBoxes};
      int exportVal = JOptionPane.showConfirmDialog(this,params,"New Sprite",JOptionPane.OK_CANCEL_OPTION);
      
      if(exportVal == JOptionPane.OK_OPTION) {
        int    width  = smw.getNumber().intValue();
        int    height = smh.getNumber().intValue();
        String name   = nameField.getText();
        actionManager.setCurrentImage(0);
        sprite = new Sprite(name, new Dimension(width,height));
        image.updateSprite(sprite);
        
        animation.updateSprite(sprite);
        updateSpriteComponents();
      }
    }
    private void mFile_Open()
    {
      JFileChooser chooser = new JFileChooser();
      FileNameExtensionFilter filter = new FileNameExtensionFilter(".spr","spr");
      chooser.setFileFilter(filter);
      int returnVal = chooser.showOpenDialog(this);
      
      if(returnVal == JFileChooser.APPROVE_OPTION) {
        //TODO: Michael H add here to open spr file
      }
    }
    private void mFile_Save()
    {
      //if new file
      mFile_SaveAs();
      //else
    }
    private void mFile_SaveAs()
    {
      //
    }
    private void mFile_Import(boolean additive) {
      JFileChooser chooser = new JFileChooser();
      FileNameExtensionFilter filter = new FileNameExtensionFilter(".png,.jpg,.gif","png","jpg","gif");
      chooser.setFileFilter(filter);
      chooser.setMultiSelectionEnabled(true);
      chooser.setDialogTitle("Select same-sized images to import");
      int returnVal = chooser.showOpenDialog(this);
      
      if(returnVal == JFileChooser.APPROVE_OPTION) {
        
        String name = chooser.getSelectedFiles()[0].getName();//the first name
        BufferedImage[] files = new BufferedImage[chooser.getSelectedFiles().length];
        Dimension lastSize;
        try {
          for(int i=0; i<files.length; i++)
          {
            files[i] = ImageIO.read(chooser.getSelectedFiles()[i]);
            
            if (i>0){
              lastSize = new Dimension(files[i-1].getWidth(),files[i-1].getHeight());
              if (!lastSize.equals(new Dimension(files[i].getWidth(),files[i].getHeight()))) {
                files[i] = null;
                //change the length of the array
                BufferedImage[] temp = new BufferedImage[i];
                for(int j=0; j<temp.length;j++) {
                  temp[i]=files[i];
                }
                files = temp; 
                break;
                //stop the loop and remove the bad image
              }
            }
          }
          if (!additive) {
            sprite = new Sprite(
              name.substring(
                0, 
                name.lastIndexOf('.')
              ),
              new Dimension(
                files[0].getWidth(),
                files[0].getHeight()
              )
            );
            sprite.setImages(files);
            images.removeAll();
            animationGrid.removeAll();
          } else {
            sprite.addImages(files);
          }
          image.updateSprite(sprite);
          animation.updateSprite(sprite);
          for(BufferedImage image : files) {
            if (new Dimension(image.getWidth(),image.getHeight()).equals(sprite.size)) {
              ImagePreview imagePreview = new ImagePreview(images.getParent().getParent().getWidth()*0.7, image);
              imagePreview.setButtonActionListener(new ImagePreviewListener());
              images.add(imagePreview);
              
              ImagePreview animationPreview = new ImagePreview(animationImageSize.getWidth(),image);
              animationPreview.setButtonActionListener(new ImagePreviewListener());
              
              animationGrid.add(animationPreview);
              
            } else {
              JOptionPane.showMessageDialog(
                this, 
                "Imported images must be the same size as the current sprite.",
                "Error",
                0
              );
              break;
            }
          }
          imagesScrollPane.validate();
        } catch (IOException exception) {
          logger.log(Level.SEVERE, "Unable to read Image file", exception);
        }
      }
    }
    private void mFile_ExportAsImages()
    {
      LinkedList<String> checkedBoxes = new LinkedList<String>(); 
      String[] ext;
      
      JCheckBox bxPng = new JCheckBox("png",true);
      JCheckBox bxGif = new JCheckBox("gif");
      JCheckBox bxJpg = new JCheckBox("jpg");
      JPanel pnBoxes = new JPanel();
      pnBoxes.setLayout(new FlowLayout());
      pnBoxes.add(bxPng);
      pnBoxes.add(bxGif);
      pnBoxes.add(bxJpg);
      Object[] params = {"Select file types to export.",pnBoxes};
      int exportVal = JOptionPane.showConfirmDialog(this,params,"Export as Images",JOptionPane.OK_CANCEL_OPTION);
      
      if(exportVal == JOptionPane.OK_OPTION) {
        if(bxPng.isSelected())
          checkedBoxes.add("png");
        if(bxGif.isSelected())
          checkedBoxes.add("gif");
        if(bxJpg.isSelected())
          checkedBoxes.add("jpg");
        ext = checkedBoxes.toArray(new String[0]);
        
        JFileChooser chooser = new JFileChooser();
          FileNameExtensionFilter filter = new FileNameExtensionFilter(".spr,.png,.jpg,.gif","spr","png","jpg","gif");
          chooser.setFileFilter(filter);
          chooser.setSelectedFile(new File(sprite.getName()));
          chooser.setDialogTitle("Export Images");
        int returnVal = chooser.showSaveDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
          File saveFile;
          
          for(int i=0; i<ext.length; i++) {
            
            int j=0;
              for (LayeredImage image : sprite.getImages()) {
                try {
                  if(chooser.getSelectedFile().getName().contains(".")) {
                  saveFile = new File(
                    chooser.getSelectedFile().getAbsolutePath().substring(
                      0,
                      chooser.getSelectedFile().getAbsolutePath().lastIndexOf('.')
                    )+j+"."+ext[i]
                  );
                }
                else
                  saveFile=new File(chooser.getSelectedFile().getAbsolutePath() +j+ "."+ext[i]);
                    ImageIO.write(image.image(),ext[i], saveFile);
              
            } catch (IOException exception) {
              logger.log(Level.SEVERE, "Unable to write image file", exception);
            }
                j++;
              }
          }
        }
      }
    }
    private void mFile_ExportAsSheet()
    {
      LinkedList<String> checkedBoxes = new LinkedList<String>(); 
      String[] ext;
      
      Dimension sprSize = sprite.getSize();
      int sprLength = sprite.getImages().size();
      System.out.println("sprLength:" + sprLength);
      JCheckBox bxPng = new JCheckBox("png",true);
      JCheckBox bxGif = new JCheckBox("gif");
      JCheckBox bxJpg = new JCheckBox("jpg");
      
      SpinnerNumberModel sm = new SpinnerNumberModel(5,1, 100, 1);
      JSpinner columnsField = new JSpinner(sm);
      JLabel columnsLabel = new JLabel("Columns");
      
      JPanel pnBoxes = new JPanel();
      pnBoxes.setLayout(new FlowLayout());
      pnBoxes.add(bxPng);
      pnBoxes.add(bxGif);
      pnBoxes.add(bxJpg);
      pnBoxes.add(columnsLabel);
      pnBoxes.add(columnsField);
      Object[] params = {"Select file types to export.",pnBoxes};
      int exportVal = JOptionPane.showConfirmDialog(this,params,"Export as Sheet",JOptionPane.OK_CANCEL_OPTION);
      
      if(exportVal == JOptionPane.OK_OPTION) {
        if(bxPng.isSelected())
          checkedBoxes.add("png");
        if(bxGif.isSelected())
          checkedBoxes.add("gif");
        if(bxJpg.isSelected())
          checkedBoxes.add("jpg");
        
        int columns = sm.getNumber().intValue();
        int rows = (int)Math.ceil((double)sprLength/(double)columns);
        int width = Math.min(sprSize.width*columns,sprSize.width*sprLength);
        int height = sprSize.height*rows;
//        System.out.println("--------------"+rows +"/" + columns + "--"+width + "/" + height );
          
        BufferedImage sheet = new BufferedImage(width,height,BufferedImage.TYPE_4BYTE_ABGR);
        
        ext = checkedBoxes.toArray(new String[0]);
        
        JFileChooser chooser = new JFileChooser();
          FileNameExtensionFilter filter = new FileNameExtensionFilter(".spr,.png,.jpg,.gif","spr","png","jpg","gif");
          chooser.setFileFilter(filter);
          chooser.setSelectedFile(new File(sprite.getName()));
          chooser.setDialogTitle("Export as Sheet");
        int returnVal = chooser.showSaveDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
          File saveFile;
          //get sprite images array
          BufferedImage[][] bImages = new BufferedImage[rows][columns];
          for(int i =0; i<rows;i++) {
            for(int k=0; k<columns; k++) {
              if((columns*i)+k > sprLength-1) {
                break;
              }
              bImages[i][k] = sprite.getImages().get((columns*i)+k).image();
//              System.out.println("---addingImage to[" + i+"]["+k+"]:" + ((columns*i)+k));
            }
            }
          //construct sheet
          for (int x = 0; x < width; x++) {
                  for (int y = 0; y < height; y++) {
                    int imageY = (int)Math.floor((double)x/(double)bImages[0][0].getWidth());
                    int imageX = (int)Math.floor((double)y/(double)bImages[0][0].getHeight());
                    int offsetX = x%bImages[0][0].getWidth();
                    int offsetY = y%bImages[0][0].getHeight();
                    if (bImages[imageX][imageY] == null) {
                      continue;
                    }
                    sheet.setRGB( x, y, bImages[imageX][imageY].getRGB(offsetX,offsetY));
                  }
              }
          for(int i=0; i<ext.length; i++) {
            //write the sheet file(s) to disk
            if(chooser.getSelectedFile().getName().contains(".")) {
            saveFile = new File(
              chooser.getSelectedFile().getAbsolutePath().substring(
                0,
                chooser.getSelectedFile().getAbsolutePath().lastIndexOf('.')
              ) + "."+ext[i]
            );
          }
          else
            saveFile=new File(chooser.getSelectedFile().getAbsolutePath() + "."+ext[i]);
            try {
              ImageIO.write(sheet,ext[i], saveFile);
            } catch (IOException exception) {
              logger.log(Level.SEVERE, "Unable to write sprite sheet", exception);
            }
          }
        }
      }
    }
    private void mFile_Close()
    {/*
      sprite = new Sprite("mySprite", NEW_IMAGE_SIZE);
      actionManager.setCurrentImage(0);
      //image
      updateSpriteComponents();
      window.repaint();
      */
    }
    private void mFile_Exit()
    {
      System.exit(0); 
    }
    
    ////////////////////////////////////////////////
    //-----------------EDIT-----------------------//
    ////////////////////////////////////////////////
    private void mEdit_Undo()
    {
      //not implemented
    }
    private void mEdit_Redo()
    {
      //not implemented
    }
    private void mEdit_Cut()
    {
      //not implemented
    }
    private void mEdit_Copy()
    {
      //not implemented
    }
    private void mEdit_Paste()
    {
      //not implemented
    }
    private void mEdit_Delete()
    {
      //not implemented
    }
    private void mEdit_Preferences()
    {
      //not implemented
    }
    
    ////////////////////////////////////////////////
    //-----------------SELECT---------------------//
    ////////////////////////////////////////////////
    private void mSelect_Deselect()
    {
      //not implemented
    }
    private void mSelect_SelectAll()
    {
      //not implemented
    }
    private void mSelect_InvertSelection()
    {
      //not implemented
    }
    private void mSelect_Expand()
    {
      //not implemented
    }
    private void mSelect_Shrink()
    {
      //not implemented
    }
    private void mSelect_ToLayer()
    {
      //not implemented
    }
    
    ////////////////////////////////////////////////
    //-----------------VIEW-----------------------//
    ////////////////////////////////////////////////
    private void mView_ZoomIn()
    {
      //not implemented
    }
    private void mView_ZoomOut()
    {
      //not implemented
    }
    private void mView_ZoomTo(int percent)
    {
//      System.out.println("Zoom To " + percent + "% - Not implemented");
    }
    private void mView_ShowGrid()
    {
      //not implemented
    }
    private void mView_TransparencyImage()
    {
      //not implemented
    }
    private void mView_AnimationGhostNext()
    {/*
      int index = actionManager.getCurrentImage();
      index=index+1>sprite.getImages().size()-1 ? 0 : index+1;
        
      Layer ghost = new Layer(sprite.getName() + "ghost",sprite.getImages().get(index).image()); //neds to be reference
      ghost.updateOpacity(0.25f);
      sprite.getImages().get(actionManager.getCurrentImage()).addLayer(ghost,0);*/
      
    }
    private void mView_AnimationGhostPrev()
    {
      //not implemented
    }
    
    ////////////////////////////////////////////////
    //-----------------IMAGE----------------------//
    ////////////////////////////////////////////////
    private void mImage_Goto(String which)
    {
      if (sprite.getImages().size()<=1)
        return;
      int index = actionManager.getCurrentImage();
      if (which.equals("Next")) {
        index=index+1>sprite.getImages().size()-1 ? 0 : index+1;
      }
      else  {
        index=index-1<0 ? sprite.getImages().size()-1 : index-1;
      }
      actionManager.setCurrentImage(index);
      setSelectedImagePreview(index);
    }
    private void mImage_Delete()
    {
      deleteImage();
    }
    private void mImage_CanvasSize()
    {
      //not implemented
    }
    private void mImage_NewLayer()
    {
      //not implemented
    }
    private void mImage_DeleteLayer()
    {
      //not implemented
    }
    private void mImage_MergeLayer(String direction)
    {
      //direction = "Up" or "Down"
    }
    
    ////////////////////////////////////////////////
    //-----------------MODIFIERS------------------//
    ////////////////////////////////////////////////
    private void mMods_Shift()
    {
      //not implemented
    }
    private void mMods_FlipSpriteVertical()
    {
      //flip sprite
      sprite.flipVertical();
      image.updateSprite(sprite);
      animation.updateSprite(sprite);
     
      int length = sprite.getImages().size();
      BufferedImage[] spriteImages = new BufferedImage[length];
      for(int i=0;i<length;i++){
        spriteImages[i] = sprite.getImages().get(i).image();
      }
      window.setSpriteAndUpdate(spriteImages);
    }
    private void mMods_FlipImageVertical()
    {
      //flip image
      sprite.getImages().get(actionManager.getCurrentImage()).flipVertical();
  
      image.updateSprite(sprite);
      animation.updateSprite(sprite);
     
      int length = sprite.getImages().size();
      BufferedImage[] spriteImages = new BufferedImage[length];
      for(int i=0;i<length;i++){
        spriteImages[i] = sprite.getImages().get(i).image();
      }
      window.setSpriteAndUpdate(spriteImages);
    }
    private void mMods_FlipLayerVertical()
    {
      //flip layer
      sprite.getImages().get(actionManager.getCurrentImage()).getLayers()
                        .get(actionManager.getCurrentLayer()).flipVertical();
      image.updateSprite(sprite);
      animation.updateSprite(sprite);
     
      int length = sprite.getImages().size();
      BufferedImage[] spriteImages = new BufferedImage[length];
      for(int i=0;i<length;i++){
        spriteImages[i] = sprite.getImages().get(i).image();
      }
      window.setSpriteAndUpdate(spriteImages);
    }
    private void mMods_FlipSpriteHorizontal()
    {
      //flip sprite
      sprite.flipHorizontal();
      image.updateSprite(sprite);
      animation.updateSprite(sprite);
     
      int length = sprite.getImages().size();
      BufferedImage[] spriteImages = new BufferedImage[length];
      for(int i=0;i<length;i++){
        spriteImages[i] = sprite.getImages().get(i).image();
      }
      window.setSpriteAndUpdate(spriteImages);
    }
    private void mMods_FlipImageHorizontal()
    {
      //flip image
      sprite.getImages().get(actionManager.getCurrentImage()).flipHorizontal();
  
      image.updateSprite(sprite);
      animation.updateSprite(sprite);
     
      int length = sprite.getImages().size();
      BufferedImage[] spriteImages = new BufferedImage[length];
      for(int i=0;i<length;i++){
        spriteImages[i] = sprite.getImages().get(i).image();
      }
      window.setSpriteAndUpdate(spriteImages);
    }
    private void mMods_FlipLayerHorizontal()
    {
      //flip layer
      sprite.getImages().get(actionManager.getCurrentImage()).getLayers()
                        .get(actionManager.getCurrentLayer()).flipHorizontal();
      image.updateSprite(sprite);
      animation.updateSprite(sprite);
     
      int length = sprite.getImages().size();
      BufferedImage[] spriteImages = new BufferedImage[length];
      for(int i=0;i<length;i++){
        spriteImages[i] = sprite.getImages().get(i).image();
      }
      window.setSpriteAndUpdate(spriteImages);
    }
    private void mMods_RotateSpriteByDegrees()
    {
      SpinnerNumberModel sm = new SpinnerNumberModel(0, 0, 360, 1);
      JSpinner degreesField = new JSpinner(sm);
      JLabel degreesLabel = new JLabel("Degrees");     
      
      JPanel pnBoxes = new JPanel();
      pnBoxes.setLayout(new FlowLayout());
      pnBoxes.add(degreesLabel);
      pnBoxes.add(degreesField);
      int exportVal = JOptionPane.showConfirmDialog(this,pnBoxes,"Rotate by Degrees",JOptionPane.OK_CANCEL_OPTION);
      
      if(exportVal == JOptionPane.OK_OPTION)
      {
        double degrees = sm.getNumber().intValue();
        double radians = Math.toRadians(degrees);
        
        sprite.rotate(radians);
        image.updateSprite(sprite);
        animation.updateSprite(sprite);
        
        int length = sprite.getImages().size();
        BufferedImage[] spriteImages = new BufferedImage[length];
        for(int i=0;i<length;i++){
          spriteImages[i] = sprite.getImages().get(i).image();
        }
        window.setSpriteAndUpdate(spriteImages);
      }
    }
    private void mMods_RotateSprite90CW()
    {
      sprite.rotate(Math.toRadians(90));
      image.updateSprite(sprite);
      animation.updateSprite(sprite);
     
      int length = sprite.getImages().size();
      BufferedImage[] spriteImages = new BufferedImage[length];
      for(int i=0;i<length;i++){
        spriteImages[i] = sprite.getImages().get(i).image();
      }
      window.setSpriteAndUpdate(spriteImages);
    }
    private void mMods_RotateSprite90CCW()
    {
      sprite.rotate(Math.toRadians(-90));
      image.updateSprite(sprite);
      animation.updateSprite(sprite);
     
      int length = sprite.getImages().size();
      BufferedImage[] spriteImages = new BufferedImage[length];
      for(int i=0;i<length;i++){
        spriteImages[i] = sprite.getImages().get(i).image();
      }
      window.setSpriteAndUpdate(spriteImages);
    }
    private void mMods_RotateImage()
    {
      sprite.getImages().get(actionManager.getCurrentImage()).rotate(Math.toRadians(90));
      
      image.updateSprite(sprite);
      animation.updateSprite(sprite);
     
      int length = sprite.getImages().size();
      BufferedImage[] spriteImages = new BufferedImage[length];
      for(int i=0;i<length;i++){
        spriteImages[i] = sprite.getImages().get(i).image();
      }
      window.setSpriteAndUpdate(spriteImages);
    }
    private void mMods_RotateLayer()
    {
      sprite.getImages().get(actionManager.getCurrentImage()).getLayers()
                        .get(actionManager.getCurrentLayer()).rotate(Math.toRadians(90));
      image.updateSprite(sprite);
      animation.updateSprite(sprite);
     
      int length = sprite.getImages().size();
      BufferedImage[] spriteImages = new BufferedImage[length];
      for(int i=0;i<length;i++){
        spriteImages[i] = sprite.getImages().get(i).image();
      }
      window.setSpriteAndUpdate(spriteImages);
    }
    private void mMods_ScaleSprite()
    {
      //not implemented
    }
    private void mMods_StretchSprite()
    {
      //not implemented
    }
    private void mMods_HSV()
    {
      //not implemented
    }
    private void mMods_Opacity()
    {
      //not implemented
    }
    private void mMods_Invert()
    {
      //not implemented
      //rgb = abs(current - 255)
    }
    private void mMods_EraseColor()
    {
      //not implemented
    }
    private void mMods_AntiAlias()
    {
      //not implemented
    }
    private void mMods_CropToSelection()
    {
      //not implemented
    }
    private void mMods_CropAuto()
    {
      //not implemented
    }
    
    ////////////////////////////////////////////////
    //-----------------ANIMATION------------------//
    ////////////////////////////////////////////////
    private void mAnimation_Cycle(String direction)
    {
      int numImages = sprite.getImages().size();
      
      BufferedImage[] cycled = new BufferedImage[numImages];
      
      if (direction.equals("Left")) {
        //Go ahead and grab first image and assign to index [length-1].
        cycled[numImages-1] = sprite.getImages().get(0).image();
        
        for(int i = 0; i < numImages-1; i++) {
          cycled[i] = sprite.getImages().get(i+1).image();  
        }
      }
      else if (direction.equals("Right")) {
        //Go ahead and grab last image and assign to index [0].
        cycled[0] = sprite.getImages().get(numImages-1).image();
        
        for(int i = 1; i < numImages; i++) {
          cycled[i] = sprite.getImages().get(i-1).image();  
        }
      }
      setSpriteAndUpdate(cycled);
    }
    private void mAnimation_Reverse()
    {
      int numImages = sprite.getImages().size();
      BufferedImage[] reversed = new BufferedImage[numImages];
      for(int i = 0; i < numImages; i++){
        reversed[i] = sprite.getImages().get((numImages-1)-i).image();
      }
      setSpriteAndUpdate(reversed);
    }
    
    private void mAnimation_AddReverse(String reverseType) 
    {
      int numImages = sprite.getImages().size();
      int index = 0;
      
      BufferedImage[] revAdd = new BufferedImage[numImages*2];
      
      //populate first half : same order
      for(index = 0; index < numImages; index++) {
        revAdd[index] = sprite.getImages().get(index).image();
      }
      //populate second half : reverse order  
      for(int j = index; index > 0; j++) {
        revAdd[j] = new Layer(new Layer("",sprite.getImages().get(index-1).image())).getData();
        index--;    
      }     
      setSpriteAndUpdate(revAdd);
      //delete duplicate at middle.
      if(reverseType.equals("Odd")) {
        sprite.deleteImage(numImages-1);
        updateSpriteComponents();
      }
    }
    
    private void mAnimation_Stretch()
    {
      //not implemented
    }
    private void mAnimation_SetLength()
    {
      //not implemented
    }
    
    ////////////////////////////////////////////////
    //-----------------WINDOW---------------------//
    ////////////////////////////////////////////////
    private void mWindow_ToggleToolBar(String window)
    {
      //not implemented
    }
    private void mWindow_ChangeSkin(String lookAndFeel)
    {
      window.skinManager.changeSkin(lookAndFeel);
      preferences.setKey("skin", lookAndFeel);
      window.repaint();
    }
    private void mWindow_About()
    {
      JOptionPane.showMessageDialog(this, "MySprite Image Editor\n v0.0.1\n� 5GUISE 2012","About",1);
    }
    private void mWindow_Help()
    {
      //not implemented
    }

    
    ///////////////////////
    //  misc methods     //
    ///////////////////////
    private void setSpriteAndUpdate(BufferedImage[] newImages) {
      
      images.removeAll();
      animationGrid.removeAll();
      
      sprite.setImages(newImages);
      animation.updateSprite(sprite);
      
      for(BufferedImage image : newImages) {
      //update gui elements to reflect sprite structure
          
        ImagePreview imagePreview = new ImagePreview(images.getParent().getParent().getWidth()*0.7, image);
        imagePreview.setButtonActionListener(new ImagePreviewListener());
        images.add(imagePreview);
        
        ImagePreview animationPreview = new ImagePreview(animationImageSize.getWidth(),image);
        animationPreview.setButtonActionListener(new ImagePreviewListener());

        animationGrid.add(animationPreview);
      }
      setSelectedImagePreview(actionManager.getCurrentImage());
      imagesScrollPane.validate();
      animationGrid.validate();
    }
    
    private void updateSpriteComponents() {
          
      BufferedImage[] newImages = new BufferedImage[sprite.getImages().size()];
      for(int i =0; i<newImages.length;i++) {
        newImages[i] = sprite.getImages().get(i).image();
      }
      
      images.removeAll();
      animationGrid.removeAll();
        
      animation.updateSprite(sprite);
          
      for(BufferedImage image : newImages) {
        ImagePreview imagePreview = new ImagePreview(images.getParent().getParent().getWidth()*0.7, image);
        imagePreview.setButtonActionListener(new ImagePreviewListener());
        images.add(imagePreview);
        
        ImagePreview animationPreview = new ImagePreview(animationImageSize.getWidth(),image);
        animationPreview.setButtonActionListener(new ImagePreviewListener());

        animationGrid.add(animationPreview);
      }
      setSelectedImagePreview(actionManager.getCurrentImage()); 
      imagesScrollPane.validate();
      animationGrid.validate();
      animationGrid.repaint();
    }
    private void updateCurrentComponent() {
      int index = actionManager.getCurrentImage();
      BufferedImage newImage = sprite.getImages().get(index).image();
      ImagePreview preview = (ImagePreview)images.getComponent(index);
      preview.updateImage(preview.width, newImage);
      preview.setButtonActionListener(new ImagePreviewListener());
      
      preview = (ImagePreview)animationGrid.getComponent(index);
      preview.updateImage(preview.width, newImage);
      preview.setButtonActionListener(new ImagePreviewListener());
      
      setSelectedImagePreview(index);
      imagesScrollPane.validate();
      animationGrid.validate();
      animationGrid.repaint();
    }
    private void setSelectedImagePreview(int index) {
      Component[] allButtons = animationGrid.getComponents();
      Component[] allButtons2 = images.getComponents();
      
      for(int i=0; i < allButtons.length ; i++ ) {
        if(!(i==index)){
          allButtons[i].setBackground(null);
          allButtons2[i].setBackground(null);
        } else {
          allButtons[i].setBackground(Color.WHITE);
          allButtons2[i].setBackground(Color.WHITE);
        }
      }
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
