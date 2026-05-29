// ExceptionDemo.java
public class ExceptionDemo {
  public static int parseAge(String s) {
    try {
      return Integer.parseInt(s);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("age format error:" + s);

    }
  }

  public static void main(String[] args) {
    try {
      System.out.println(parseAge("18"));
      System.out.println(parseAge("abc"));
    } catch (IllegalArgumentException e) {
      System.out.println("catch error:" + e.getMessage());
    }
  }
}