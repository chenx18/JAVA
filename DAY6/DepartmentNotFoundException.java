public class DepartmentNotFoundException extends RuntimeException {
    public DepartmentNotFoundException(int id) {
        super("department not found: " + id);
    }
}