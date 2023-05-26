package datastructure.tree;

public class Hanoi {
  /*
   * Move a number of plates.
   * 
   * @param paraSource The source pole.
   * 
   * @param paraIntermedium The intermediary pole.
   * 
   * @paraDestination The destination pole.
   * 
   * @param paraNumber The number of plates.
   */
  public static void hanoi(char paraSource, char paraIntermediary, char paraDestination, int paraNumber) {
    if (paraNumber == 1) {
      System.out.println(paraSource + "->" + paraDestination + " ");
      return;
    }
    hanoi(paraSource, paraDestination, paraIntermediary, paraNumber - 1);
    System.out.println(paraSource + "->" + paraDestination + " ");
    hanoi(paraIntermediary, paraSource, paraDestination, paraNumber - 1);
  }

  public static void main(String args[]){
    hanoi('a','b','c', 3);
  }
}