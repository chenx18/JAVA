public class DepartmentDemo {
  
  public static void main(String[] args) {
    DepartmentRepository repository = new DepartmentRepository();
    DepartmentService services = new DepartmentService(repository);
  
    services.addDepartment(new Department(1, "生产部"));
    services.addDepartment(new Department(2, "研发中心"));

    System.out.println(services.getDepartment(1));

    services.updateDepartmentNamr(1, "生产中心");
    System.out.println(services.getDepartment(1));

    services.removeDepartment(1);

    try {
      System.out.println(services.getDepartment(1));  
    } catch (DepartmentNotFoundException  e) {
      System.out.println(e.getMessage());
    }
  }

}