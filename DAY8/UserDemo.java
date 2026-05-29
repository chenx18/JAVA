public class UserDemo {
    public static void main(String[] args) {
        UserRepository repository = new UserRepository();
        UserService service = new UserService(repository);

        service.addUser(new User(1, "A", "B"));
        service.addUser(new User(2, "B", "C"));

        System.out.println(service.getUserById(99));

        service.updateUser(1, "a", "b");
        System.out.println(service.getUserById(1));

        service.removeUser(2);

        try {
            service.removeUser(99);
        } catch (UserNotFoundException e) {
            System.err.println("catche error: " + e.getMessage());
        }
    }
}