public class DepartmentNotFoundException extends RuntimeException {
  public DepartmentNotFoundException(int id) {
    super("id is not");
  }
}