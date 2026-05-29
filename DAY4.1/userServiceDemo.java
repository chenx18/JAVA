public class userServiceDemo {
    public static void main(String[] args) {
        UserRepository repo = new UserRepository();
        UserService service = new UserService(repo);


        service.addUser(new User(1, "co", "123@163.com"));
        service.addUser(new User(2, "fo", "136@163.com"));

        System.out.println(service.getUserById(1));


        try {
            System.out.println(service.getUserById(999));
        } catch (Exception e) {
            System.out.println("catch error:" + e.getMessage());
        }
    }
}