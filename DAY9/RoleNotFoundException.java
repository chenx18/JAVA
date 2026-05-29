public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(int id) {
        super("role not found: " + id);
    }
}