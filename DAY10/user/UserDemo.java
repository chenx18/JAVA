public class UserDemo {

 public static void main(String[] args) {
    UserRepository repository = new UserRepository();
    UserService service = new UserService(repository);

    service.addUser(new User(1, "Jack", "Jack@123.com"));
    service.addUser(new User(2, "LiLi", "LiLi@123.com"));

    System.out.println(service.getUserById(1));
 }
  
}