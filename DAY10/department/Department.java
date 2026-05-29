public class Department {
  private int id;
  private String name;

  public Department(int id,String name){
    this.id = id;
    this.name = name;
  }


  public int getId() {
    return id;
  }
  public String getName() {
    return name;
  }

  public void setName(String n) {
    name = n;
  }

  @Override
  public String toString() {
    return "Department id: " + id + ", name: " + name;
  }

}