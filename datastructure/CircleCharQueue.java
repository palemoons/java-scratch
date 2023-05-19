package datastructure;

public class CircleCharQueue {
  // Total space. One space can never be used.
  public static final int TOTAL_SPACE = 10;
  // The data.
  char[] data;
  // The index for calculating the head.
  int head;
  // The index for calculating the tail.
  int tail;

  // Constructor.
  public CircleCharQueue() {
    data = new char[TOTAL_SPACE];
    head = 0;
    tail = 0;
  }

  /*
   * Enqueue.
   * 
   * @param paraValue The value of the new node.
   */
  public void enqueue(char paraValue) {
    if ((tail + 1) % TOTAL_SPACE == head) {
      System.out.println("Queue full.");
      return;
    }
    data[tail % TOTAL_SPACE] = paraValue;
    tail++;
  }

  /*
   * Dequeue.
   * 
   * @return The value at the head.
   */
  public char dequeue() {
    if (head == tail) {
      System.out.println("No element in the queue.");
      return '\0';
    }
    char resultValue = data[head % TOTAL_SPACE];
    head++;
    return resultValue;
  }

  // Overrides toString method.
  public String toString() {
    String resultString = "";
    if (head == tail)
      return "empty";
    for (int i = head; i < tail; i++)
      resultString += data[i % TOTAL_SPACE] + ", ";
    return resultString;
  }

  public static void main(String args[]) {
    CircleCharQueue tempQueue = new CircleCharQueue();
    System.out.println("Initalized, the list is: " + tempQueue.toString());

    int tempValue = tempQueue.dequeue();
    System.out.println("Dequeue " + tempValue + ", the queue is: " + tempQueue.toString());

    for (char i = '0'; i < '5'; i++) {
      tempQueue.enqueue(i);
      System.out.println("Enqueue, the queue is: " + tempQueue.toString());
    }

    tempValue = tempQueue.dequeue();
    System.out.println("Dequeue, the queue is: " + tempQueue.toString());

    for (char i = 'a'; i < 'f'; i++) {
      tempQueue.enqueue(i);
      System.out.println("Enqueue, the queue is: " + tempQueue.toString());
    }

    for (int i = 0; i < 3; i++) {
      tempValue = tempQueue.dequeue();
      System.out.println("Dequeue, the queue is: " + tempQueue.toString());
    }

    for (char i = 'A'; i < 'F'; i++) {
      tempQueue.enqueue(i);
      System.out.println("Enqueue, the queue is: " + tempQueue.toString());
    }
  }
}