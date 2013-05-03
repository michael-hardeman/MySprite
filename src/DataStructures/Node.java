
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                                                                                                                    //
//  Node.java                                                                                                         //
//  Michael Hardeman : Mhardeman2@student.gsu.edu                                                                     //
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
