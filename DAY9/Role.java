public class Role {
  
  private int id;
  private String name;

  public Role(int id, String name) {
    this.id = id;
    this.name = name;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setNaem(String n) {
    this.name = n;
  }

  @Override
  public String toString() {
    return "Role{id=" + id + ";name=" + name + "}";
  }
}