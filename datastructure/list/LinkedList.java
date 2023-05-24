package datastructure.list;

public class LinkedList {
  // An inner class.
  class Node {
    // The data.
    int data;

    // The reference to the next node;
    Node next;

    // Constructor.
    public Node(int paraValue) {
      data = paraValue;
      next = null;
    }
  }

  // The dummy head.
  Node header;

  // Construct an empty linked list.
  public LinkedList() {
    header = new Node(0);
  }

  // Overrides toString method
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

  // Reset to empty.
  public void reset() {
    header.next = null;
  }

  /*
   * Locate the given value.
   * 
   * @param paraValue The given value.
   * 
   * @return The position or -1 if not found.
   */
  public int locate(int paraValue) {
    int tempPosition = -1;
    Node tempNode = header.next;
    int tempCurrentPosition = 0;
    while (tempNode != null) {
      if (tempNode.data == paraValue) {
        tempPosition = tempCurrentPosition;
        break;
      }
      tempNode = tempNode.next;
      tempCurrentPosition++;
    }
    return tempPosition;
  }

  /*
   * Insert a value to a position.
   * 
   * @param paraPosition The given position.
   * 
   * @param paraValue The given value.
   * return Success or not.
   */
  public boolean insert(int paraPosition, int paraValue) {
    Node tempNode = header;
    Node tempNewNode;

    for (int i = 0; i < paraPosition; i++) {
      if (tempNode.next == null) {
        System.out.println("The position " + paraPosition + " is illegal.");
        return false;
      }
      tempNode = tempNode.next;
    }
    // Construct a new node.
    tempNewNode = new Node(paraValue);
    // Link.
    tempNewNode.next = tempNode.next;
    tempNode.next = tempNewNode;

    return true;
  }

  /*
   * Delete a value at a position.
   * 
   * @param paraPosition The given position.
   * 
   * @return Success or not.
   */
  public boolean delete(int paraPosition) {
    if (header.next == null) {
      System.out.println("Cannot delete element from an empty list.");
      return false;
    }

    Node tempNode = header;
    for (int i = 0; i < paraPosition; i++, tempNode = tempNode.next) {
      if (tempNode.next.next == null) {
        System.out.println("The position " + paraPosition + " is illegal.");
        return false;
      }
    }
    tempNode.next = tempNode.next.next;
    return true;
  }

  public static void main(String args[]) {
    LinkedList tempFirstList = new LinkedList();
    System.out.println("Initalized, the list is: " + tempFirstList.toString());

    for (int i = 0; i < 5; i++)
      tempFirstList.insert(0, i);
    System.out.println("Inserted, the list is: " + tempFirstList.toString());

    tempFirstList.insert(6, 9);

    tempFirstList.delete(4);

    tempFirstList.delete(2);
    System.out.println("Deleted, the list is: " + tempFirstList.toString());

    for (int i = 0; i < 5; i++) {
      tempFirstList.delete(0);
      System.out.println("Looped delete, the list is: " + tempFirstList.toString());
    }
  }
}
