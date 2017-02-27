
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                                                                                                                    //
//  Node.java                                                                                                         //
//  Michael Hardeman                                                                                                  //
//                                                                                                                    //
//  Basic Linked List data component. Generic.                                                                        //
//                                                                                                                    //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package DataStructures;

//////////
// Node //
//////////
  public class Node<E> {

    ////////////////////////
    // Instance Variables //
    ////////////////////////
      private E       data;
      private Node<E> next;
    
    /////////////////
    // Constructor //
    /////////////////
      public Node(){
        this.data = null;
        this.next = null;
      } 
      public Node(E data, Node<E> next){
        this.data = data;
        this.next = next;
      }
    
    /////////////
    // getData //
    /////////////
      public E getData(){
        return this.data;
      }

    /////////////
    // getNext //
    /////////////
      public Node<E> getNext(){
        return this.next;
      }

    /////////////
    // setData //
    /////////////
      public void setData(E data){
        this.data = data;
      }

    /////////////
    // setNext //
    /////////////
      public void setNext(Node<E> next){
        this.next = next;
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
