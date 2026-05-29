public class User {
  private int id;
  private String name;
  private String email;

  public User(int id, String name, String email) {
    this.id = id;
    this.name = name;
    this.email = email;
  }

  public int getId() {
    return this.id;
  }

  @Override
  public String toString() {
    return "User{id=" + id + ", name=" + name + ", email=" + email;
  }
}