
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