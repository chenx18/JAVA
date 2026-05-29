public class UserDemo {
    public static void main(String[] args) {
        UserRepository repository = new UserRepository();
        UserService service = new UserService(repository);

        service.addUser(new User(1,"Jay","123@163.com"));
        service.addUser(new User(2, "CO" , "234@123.com"));

        System.out.println(service.getUserById(1));

    }
}