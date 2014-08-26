
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
//////////////////

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
    /////////////////
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
    ///////////
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
