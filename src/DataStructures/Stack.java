
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                                                                                                                   //
//  Stack.java                                                                                                       //
//  Michael Hardeman : Mhardeman2@student.gsu.edu                                                                    //
//                                                                                                                   //
//  It's a Stack. Generic.                                                                                           //
//                                                                                                                   //
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package DataStructures;

///////////
// Stack //
///////////
  public class Stack<E>{  

    ////////////////////////
    // Instance Variables //
    ////////////////////////
      public  int     size;
      private Node<E> top;
    
    /////////////////
    // Constructor //
    /////////////////
      public Stack(){
        size = 0;
        top = null;
      }
    
    //////////
    // push //
    //////////
      public void push(E input){
        top = new Node<E>(input,top);
        size++;
      }
    
    /////////
    // pop //
    /////////
      public E pop() throws NullPointerException {      
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
  }