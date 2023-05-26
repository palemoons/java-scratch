package datastructure.tree;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Huffman {
  // Inner class for Huffman nodes.
  class HuffmanNode {
    // The char.
    char character;
    // Weight.
    int weight;
    // The left child.
    HuffmanNode leftChild;
    // The right child.
    HuffmanNode rightChild;
    // The parent node.
    HuffmanNode parent;

    // Constructor.
    public HuffmanNode(char paraCharacter, int paraWeight, HuffmanNode paraLeftChild, HuffmanNode paraRigthChild,
        HuffmanNode paraParent) {
      character = paraCharacter;
      weight = paraWeight;
      leftChild = paraLeftChild;
      rightChild = paraRigthChild;
      parent = paraParent;
    }

    public String toString() {
      String resultString = "(" + character + ", " + weight + ")";
      return resultString;
    }
  }

  // The number of characters. 256 for ASCII.
  public static final int NUM_CHARS = 256;
  // The input text.
  String inputText;
  // The length of the alphabet, also the number of leaves.
  int alphabetLength;
  // The alphabet.
  char[] alphabet;
  // The count of chars. The length is 2 * alphabetLength - 1 to include non-leaf
  // nodes.
  int[] charCounts;
  // The mapping of chars to the indices in the alphabet.
  int[] charMapping;
  // Codes for each char in the alphabet. It should have the same length a
  // alphabet.
  String[] huffmanCodes;
  // All nodes. The last node is the root.
  HuffmanNode[] nodes;

  /*
   * Constructor.
   * 
   * @param paraFilename The text filename.
   */
  public Huffman(String paraFilename) {
    charMapping = new int[NUM_CHARS];
    readText(paraFilename);
  }

  /*
   * Read text.
   * 
   * @param paraFilename The text filename.
   */
  public void readText(String paraFilename) {
    try {
      inputText = Files.newBufferedReader(Paths.get(paraFilename), StandardCharsets.UTF_8).lines()
          .collect(Collectors.joining("\n"));
    } catch (Exception ee) {
      System.out.println(ee);
      System.exit(0);
    }

    System.out.println("The text is:\r\n" + inputText);
  }

  // Construct the alphabet. The result are stored in the number variables
  // charMapping and alphabet.
  public void constructAlphabet() {
    // Intialize.
    Arrays.fill(charMapping, -1);
    // The count for each char. At most NUM_CHARS chars.
    int[] tempCharCounts = new int[NUM_CHARS];
    // The index of the char in the ASCII charset.
    int tempCharIndex;

    // Step 1. Scan the string to obtain the counts.
    char tempChar;
    for (int i = 0; i < inputText.length(); i++) {
      tempChar = inputText.charAt(i);
      tempCharIndex = (int) tempChar;
      System.out.print("" + tempCharIndex + " ");
      tempCharCounts[tempCharIndex]++;
    }

    // Step 2. Scan to determine the size of the alphabet.
    for (int i = 0; i < 255; i++) {
      if (tempCharCounts[i] > 0)
        alphabetLength++;
    }

    // Step 3. Compress to the alphabet.
    alphabet = new char[alphabetLength];
    charCounts = new int[2 * alphabetLength - 1];

    int tempCounter = 0;
    for (int i = 0; i < NUM_CHARS; i++) {
      if (tempCharCounts[i] > 0) {
        alphabet[tempCounter] = (char) i;
        charCounts[tempCounter] = tempCharCounts[i];
        charMapping[i] = tempCounter;
        tempCounter++;
      }
    }

    System.out.println("\nThe alphabet is: " + Arrays.toString(alphabet));
    System.out.println("Their count are: " + Arrays.toString(charCounts));
    System.out.println("The char mappings are: " + Arrays.toString(charMapping));
  }

  // Construct the tree.
  public void constructTree() {
    // Step 1. Allocate space.
    nodes = new HuffmanNode[alphabetLength * 2 - 1];
    boolean[] tempProcessed = new boolean[alphabetLength * 2 - 1];

    // Step 2. Initialize leaves.
    for (int i = 0; i < alphabetLength; i++)
      nodes[i] = new HuffmanNode(alphabet[i], charCounts[i], null, null, null);

    // Step 3. Construct the tree.
    int tempLeft, tempRight, tempMinimal;
    for (int i = alphabetLength; i < 2 * alphabetLength - 1; i++) {
      // Step 3.1. Select the first minimal as the left child.
      tempLeft = -1;
      tempMinimal = Integer.MAX_VALUE;
      for (int j = 0; j < i; j++) {
        if (tempProcessed[j])
          continue;

        if (tempMinimal > charCounts[j]) {
          tempMinimal = charCounts[j];
          tempLeft = j;
        }
      }
      tempProcessed[tempLeft] = true;

      // Step 3.2. Select the second minimal as the right child.
      tempRight = -1;
      tempMinimal = Integer.MAX_VALUE;
      for (int j = 0; j < i; j++) {
        if (tempProcessed[j])
          continue;
        if (tempMinimal > charCounts[j]) {
          tempMinimal = charCounts[j];
          tempRight = j;
        }
      }
      tempProcessed[tempRight] = true;
      System.out.println("Selecting " + tempLeft + " and " + tempRight);

      // Step 3.3. Construct the new node.
      charCounts[i] = charCounts[tempLeft] + charCounts[tempRight];
      nodes[i] = new HuffmanNode('*', charCounts[i], nodes[tempLeft], nodes[tempRight], null);

      // Step 3.4. Link with children.
      nodes[tempLeft].parent = nodes[i];
      nodes[tempRight].parent = nodes[i];
      System.out.println("The children of " + i + " are " + tempLeft + " and " + tempRight);
    }
  }

  /*
   * Get the root of the binary tree.
   * 
   * @return The root.
   */
  public HuffmanNode getRoot() {
    return nodes[nodes.length - 1];
  }

  // Pre-order visit.
  public void preOrderVisit(HuffmanNode paraNode) {
    System.out.println("(" + paraNode.character + ", " + paraNode.weight + ") ");
    if (paraNode.leftChild != null)
      preOrderVisit(paraNode.leftChild);
    if (paraNode.rightChild != null)
      preOrderVisit(paraNode.rightChild);
  }

  // Generate codes for each character in the alphabet.
  public void generateCodes() {
    huffmanCodes = new String[alphabetLength];
    HuffmanNode tempNode;
    for (int i = 0; i < alphabetLength; i++) {
      tempNode = nodes[i];
      String tempCharCode = "";
      while (tempNode.parent != null) {
        if (tempNode == tempNode.parent.leftChild)
          tempCharCode = "0" + tempCharCode;
        else
          tempCharCode = "1" + tempCharCode;

        tempNode = tempNode.parent;
      }
      huffmanCodes[i] = tempCharCode;
      System.out.println("The code of " + alphabet[i] + " is " + tempCharCode);
    }
  }

  /*
   * Encode the given string.
   * 
   * @param paraString The given string.
   */
  public String coding(String paraString) {
    String resultCodeString = "";
    int tempIndex;
    for (int i = 0; i < paraString.length(); i++) {
      // From the original char to the location in the alphabet.
      tempIndex = charMapping[(int) paraString.charAt(i)];
      // From the location in the alphabet to the code.
      resultCodeString += huffmanCodes[tempIndex];
    }
    return resultCodeString;
  }

  /*
   * Decode the given string.
   * 
   * @param paraString The given string.
   */
  public String decoding(String paraString) {
    String resultCodeString = "";
    HuffmanNode tempNode = getRoot();
    for (int i = 0; i < paraString.length(); i++) {
      if (paraString.charAt(i) == '0') {
        tempNode = tempNode.leftChild;
        System.out.println(tempNode);
      } else {
        tempNode = tempNode.rightChild;
        System.out.println(tempNode);
      }

      if (tempNode.leftChild == null) {
        System.out.println("Decode one:" + tempNode);
        // Decode one char.
        resultCodeString += tempNode.character;
        // Return to the root.
        tempNode = getRoot();
      }
    }
    return resultCodeString;
  }

  public static void main(String args[]){
    Huffman tempHuffman=new Huffman("./sample-data/huffmantext-small.txt");
    tempHuffman.constructAlphabet();
    tempHuffman.constructTree();

    HuffmanNode tempRoot=tempHuffman.getRoot();
    System.out.println("The root is: "+tempRoot);
    System.out.println("Pre-order visit:");
    tempHuffman.preOrderVisit(tempRoot);

    tempHuffman.generateCodes();

    String tempCoded=tempHuffman.coding("abcdb");
    System.out.println("Coded: "+tempCoded);
    String tempDecoded=tempHuffman.decoding(tempCoded);
    System.out.println("Decoded: "+tempDecoded);
  }
}