package machinelearning.gui;

/**
 * Explain the interface and listener mechanism.
 */
// Day 1. Define an interface.
interface Flying {
  public void fly();
}

// Day 2. Define a controller to cope with it.
class Controller {
  Flying flying;

  Controller() {
    flying = null;
  }

  void setListener(Flying paraFlying) {
    flying = paraFlying;
  }

  void doIt() {
    flying.fly();
  }
}

// Day 3. Define class Bird for the interface.
class Bird implements Flying {
  double weight = 0.5;

  public void fly() {
    System.out.println("Bird fly, my weight is " + weight + " kg.");
  }
}

// Day 4. Define class Plane for the interface.
class Plane implements Flying {
  double price = 100000000;

  public void fly() {
    System.out.println("Plan fly, my price is " + price + " RMB.");
  }
}

// Day 5. Test the interface.
public class InterfaceTest {
  public static void main(String[] args) {
    Controller tempController = new Controller();
    Flying tempFlying1 = new Bird();
    tempController.setListener(tempFlying1);
    tempController.doIt();

    Flying tempFlying2 = new Plane();
    tempController.setListener(tempFlying2);
    tempController.doIt();
  }
}
