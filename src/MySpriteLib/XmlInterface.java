
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                                                                                                                    //
//  XmlInterface.java                                                                                                 //
//  Michael Hardeman : Mhardeman2@student.gsu.edu                                                                     //
//                                                                                                                    //
//  Abstracts XmlParsing Library with standard function calls                                                         //
//                                                                                                                    //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package MySpriteLib;
import  java.io.File;
import  java.io.IOException;
import  java.util.LinkedList;
import  java.util.logging.Level;
import  java.util.logging.Logger;
import  javax.xml.parsers.DocumentBuilder;
import  javax.xml.parsers.DocumentBuilderFactory;
import  javax.xml.parsers.ParserConfigurationException;
import  org.w3c.dom.Document;
import  org.w3c.dom.Element;
import  org.w3c.dom.Node;
import  org.w3c.dom.NodeList;
import  org.xml.sax.SAXException;

//////////////////
// XmlInterface //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// I am not a fan of java's document Builder.
// I'm baffled by it's obtuse design, and wonder why it parses out such useless information as
// blank spaces and new lines, and why it's structure is so nested and nodes don't have relevant data in them,
// the data is stored in children nodes along with other tags and blank spaces and junk.
//
// This tries to remedy that by making getting tags, and info out of tags easier.
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  public class XmlInterface {

    ////////////////////////
    // Instance Variables //
    ////////////////////////
      private static Logger                 logger = Logger.getLogger(XmlInterface.class.getName());
      private        Node                   rootNode;
      private        Document               currentDocument;
      private        DocumentBuilder        documentBuilder;
      private        DocumentBuilderFactory documentBuilderFactory;

    /////////////////
    // Constructor //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // God damn it, who thought that it was a good idea to have a documentBuilder factory, that you use to build
    // documentBuilders, that you then use to buildDocuments... That's some of the most nonsensical design i've
    // ever seen.
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      public XmlInterface() throws ParserConfigurationException{
        try {
          logger.setLevel(Level.ALL);
          rootNode        = null;
          currentDocument = null;
          documentBuilderFactory = DocumentBuilderFactory.newInstance();
          documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException exception) {
          logger.log(Level.SEVERE, "Parser unable to be configured");
          throw exception; // notify calling program of error
        }
      }

    ///////////
    // parse //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Asswiper myAssWiper = new Asswiper();
    // myAssWiper.wipeAss(this);
    //
    // that's basically the jist of this function. God damn it java, you make things too easy. Nobody will ever learn
    // anything using you because you do everything for them.
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      public void parse(File file) throws Exception {
        try {
          if(file.exists() && file.canRead() && file.isFile() && file.getName().endsWith(".xml")) {
            currentDocument = documentBuilder.parse(file);
            NodeList nodeList = currentDocument.getChildNodes();
            for(int i=0; i<nodeList.getLength(); i++){
              Node current = nodeList.item(i);
              if(current.getNodeType() == Node.ELEMENT_NODE){
                rootNode = current;
                break;
              }
            }
          }
        } catch(IOException exception){
          logger.log(Level.SEVERE, "Parser unable to read file:"+file.getPath());
          throw exception;
        } catch (SAXException exception) {
          logger.log(Level.SEVERE, "Parser unable to parse file:"+file.getPath());
          throw exception;
        }
      }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // These are getters that try and extract data from the retarded tree structure that the dom parser makes
    // They attempt to discards dummy data, which for some reason, is included in the tree; and
    // they extract the info contained in the tags from the obtuse design.
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      
    ////////////////
    // getRootTag //
    ////////////////
      public String getRootTag(){
        return rootNode.getNodeName();
      }
    
    ////////////////////////
    // getAllNodesWithTag //
    ////////////////////////
      public LinkedList<Node> getAllNodesWithTag(String tag){
        LinkedList<Node> output = new LinkedList<Node>();
        search(output, tag, rootNode);
        return output;
      }
      
    ////////////////////////
    // getAllNodesWithTag //
    ////////////////////////
      public LinkedList<Node> getAllNodesWithTag(String tag, Node start){
          LinkedList<Node> output = new LinkedList<Node>();
        search(output, tag, start);
        return output;
      }

    ////////////
    // search //
    ////////////
      public void search(LinkedList<Node> nodes, String tag, Node current){
        NodeList children = current.getChildNodes();
        for(int i=0; i<children.getLength(); i++){
          if(children.item(i).getNodeType() == Node.ELEMENT_NODE){
            if(children.item(i).getNodeName().equals(tag)){
              nodes.add(children.item(i));
            }
            search(nodes, tag, children.item(i));
          }
        }
      }
    
    ////////////////////
    // getNodeWithTag //
    ////////////////////
      public Node getNodeWithTag(String tag, Node start){
        return search(tag, start);
      }
      
    ////////////////////
    // getNodeWithTag //
    ////////////////////
      public Node getNodeWithTag(String tag){
        return search(tag, rootNode);
      }
    
    ////////////
    // search //
    ////////////
      public Node search(String tag, Node current){
        NodeList children = current.getChildNodes();
        for(int i=0; i<children.getLength(); i++){
          if(children.item(i).getNodeType() == Node.ELEMENT_NODE){
            if(children.item(i).getNodeName().equals(tag)){
              return children.item(i);
            }
            search(tag, children.item(i));
          }
        }
        return null;
      }
      
    /////////////////
    // getTagValue //
    /////////////////
      public String getTagValue(String tag) {
        NodeList list = ((Element)rootNode).getElementsByTagName(tag).item(0).getChildNodes();
        Node value = (Node) list.item(0);
        return value.getNodeValue();
      }
    
    /////////////////
    // getTagValue //
    /////////////////
      public String getTagValue(String tag, Node node) {
        Element element = (Element) node;
        NodeList list = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node value = (Node) list.item(0);
        return value.getNodeValue();
      }
  
    //////////////////
    // getNodeValue //
    //////////////////
      public String getNodeValue(Node node){
        Element element = (Element) node;
        NodeList list = element.getChildNodes();
        Node value = (Node) list.item(0);
        return value.getNodeValue();
      }
  
    ///////////////
    // printNode //
    ///////////////
      public void printNode(Node node){
        System.out.println(node.getNodeName()+" : "+getNodeValue(node));
      }
}