package datastructure;

public class LinkedQueue {
  class Node {
    // The data.
    int data;
    // The reference to the next node.
    Node next;

    // Constructor.
    public Node(int paraValue) {
      data = paraValue;
      next = null;
    }
  }

  // The header of the queue.
  Node header;
  // The tail of the queue.
  Node tail;

  // Constructor
  public LinkedQueue() {
    header = new Node(-1);
    tail = header;
  }

  /*
   * Enqueue.
   * 
   * @param paramValue The value of the new node.
   */
  public void enqueue(int paraValue) {
    Node tempNode = new Node(paraValue);
    tail.next = tempNode;
    tail = tempNode;
  }

  /*
   * Dequeue.
   * 
   * @return The value at the header.
   */
  public int dequeue() {
    if (header == tail) {
      System.out.println(" No element in the queue");
      return -1;
    }
    int resultValue = header.next.data;
    header.next = header.next.next;
    // Handle when queue becomes empty.
    if (header.next == null)
      tail = header;

    return resultValue;
  }

  // Overrides toString method.
  public String toString() {
    String resultString = "";
    if (header.next == null)
      return "empty";

    Node tempNode = header.next;
    while (tempNode != null) {
      resultString += tempNode.data + ", ";
      tempNode = tempNode.next;
    }
    return resultString;
  }

  public static void main(String args[]) {
    LinkedQueue tempQueue = new LinkedQueue();
    System.out.println("Initialized, the list is: " + tempQueue.toString());

    for (int i = 0; i < 5; i++)
      tempQueue.enqueue(i + 1);
    System.out.println("Enqueue, the queue is: " + tempQueue.toString());

    tempQueue.dequeue();
    System.out.println("Dequeue, the queue is: " + tempQueue.toString());

    int tempValue;
    for (int i = 0; i < 5; i++) {
      tempValue = tempQueue.dequeue();
      System.out.println("Loop delete  " + tempValue + ", the new queue is: " + tempQueue.toString());
    }

    for (int i = 0; i < 3; i++)
      tempQueue.enqueue(i + 10);
    System.out.println("Enqueue, the queue is: " + tempQueue.toString());
  }
}
