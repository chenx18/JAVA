// FileDemo.java

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileDemo {

  public static void main(String[] args) {
    String fileName = "users.text";

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
      writer.write("1, zhang, 12@163.com");
      writer.newLine();

      writer.write("2, li, 123@163.com");
      writer.newLine();

      writer.write("3, liu, 123@163.com");
      writer.newLine();

    } catch (IOException e) {
      System.out.println("wite error:" + e.getMessage());
    }

    try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
      String line;
      while ((line = reader.readLine()) != null) {
        System.out.print(line);
      }
    } catch (IOException e) {
      System.out.println("read error:" + e.getMessage());
    }
  }
}
