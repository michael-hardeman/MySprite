
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                                                                                                                   //
//  Queue.java                                                                                                       //
//  Michael Hardeman : Mhardeman2@student.gsu.edu                                                                    //
//                                                                                                                   //
//  It's a Queue. Generic.                                                                                           //
//                                                                                                                   //
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package DataStructures;

///////////
// Queue //
///////////
  public class Queue<E>{
    
    ////////////////////////
    // Instance Variables //
    ////////////////////////
      public  int     size;
      private Node<E> top;
      private Node<E> bottom;
    
    /////////////////
    // Constructor //
    /////////////////
      public Queue(){
        size   = 0;
        top    = null;
        bottom = null;
      }
    
    /////////////
    // enqueue //
    /////////////
      public void enqueue(E input){
        if(size == 0){
          top = new Node<E>(input, null);
          bottom = top;
        } else {
          bottom.setNext(new Node<E>(input,null));
          bottom = bottom.getNext();
        }
        size++;
      }
    
    /////////////
    // dequeue //
    /////////////
      public E dequeue() throws NullPointerException {
        if (size == 0) 
          throw new NullPointerException();
        Node<E> temp = top;
        top = top.getNext();
        size--;
        return temp.getData();
      }
    
    //////////
    // peek //
    //////////
      public E peek() throws NullPointerException {
        return top.getData();
      }
    
    /////////
    // get //
    /////////
      public E get(int index) {
        Node<E> current = top;
        for( int i = 0; i < index; i++ ){
          current = current.getNext();
        }
        return current.getData();
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
