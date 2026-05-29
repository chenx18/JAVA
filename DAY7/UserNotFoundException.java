public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(int id) {
        super("user not found: " + id);
    }
}