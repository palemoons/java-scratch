# Day 20: Summary
根据写程序的体会回答：
## Q1
> 面向对象与面向过程相比，有哪些优势？  

面向对象编程以对象（Object）为程序的基本单元，具有封装、多态和继承等特性，提高了软件的重用性、灵活性和扩展性。

## Q2
> 比较顺序表和链表的异同。  

顺序表和链表都属于数据结构中的线性表，是由n个数据元素组成的有限序列。不同点在于顺序表在物理空间中是依次存储的，而链表通过指针联系，数据被分散存储在物理空间中。

## Q3
> 分析顺序表和链表的优缺点。

顺序表的优点在于随机访问，缺点是在声明时需要预先申请一块连续的存储空间，且在后续不能再申请空间。  
链表的优缺点与顺序表相反。

## Q4
> 分析调试程序常见的问题和解决方案。

多打断点，二分查找bug。

## Q5
> 分析链队列与循环队列的优缺点。

链队列的优点在于没有队列最大长度的限制，可以不断申请新的空间；缺点在于无法随机访问。

循环队列的优点在可以进行随机访问；缺点在于队列声明时设置了队列的最大长度`MAX_LENGTH`，后续不可以随意增加，同时循环队列中最多只能存储`MAX_LENGTH - 1`个结点，造成了空间的浪费。

## Q6
> 第18天建立的两个队列，其区别仅在于基础数据不同，一个是int，一个是char。按这种思路，对于不同的基础数据类型，都需要重写一个类，这合理吗？如何解决？

面向对象编程中提供了泛型解决该问题：
```java
public class Box<T> {
   
  private T t;
 
  public void add(T t) {
    this.t = t;
  }
 
  public T get() {
    return t;
  }
 
  public static void main(String[] args) {
    Box<Integer> integerBox = new Box<Integer>();
    Box<String> stringBox = new Box<String>();
 
    integerBox.add(new Integer(0));
    stringBox.add(new String("字符串"));
 
    System.out.printf("整型值为 :%d\n\n", integerBox.get());
    System.out.printf("字符串为 :%s\n", stringBox.get());
  }
}
```