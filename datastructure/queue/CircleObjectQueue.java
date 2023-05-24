package datastructure.queue;

public class CircleObjectQueue {
  // The total space.
  public static final int TOTAL_SPACE = 10;
  // The data.
  Object[] data;
  // The index of the head.
  int head;
  // The index of the tail.
  int tail;

  // Constructor
  public CircleObjectQueue() {
    data = new Object[TOTAL_SPACE];
    head = 0;
    tail = 0;
  }

  /*
   * Enqueue
   * 
   * @param paraValue The value of the new node.
   */
  public void enqueue(Object paraValue) {
    if ((tail + 1) % TOTAL_SPACE == head) {
      System.out.println("Queue full.");
      return;
    }
    data[tail % TOTAL_SPACE] = paraValue;
    tail++;
  }

  public Object dequeue() {
    if (head == tail) {
      System.out.println("No element in the queue");
      return null;
    }
    Object resultValue = data[head % TOTAL_SPACE];
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
    return;
  }
}
