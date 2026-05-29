import java.util.ArrayList;
import java.util.List;

// 模型层：用户实体，保护数据字段 id/name/email
class User {
  private int id;
  private String name;
  private String email;

  // public User 构造器，new User() 时执行，负责初始化一个用户对象。
  public User(int id, String name, String email) {
    this.id = id;
    this.name = name;
    this.email = email;
  }

  public int getId() {
    return id;
  }
  @Override
  public  String toString() {
    return "User{id=" + id + ", Name=" + name + ", Email=" + email;
  }
}

// class UserService 业务层服务，封装“对用户集合的操作” 
// 是“用户业务逻辑层”，负责操作用户数据，比如新增、查询、修改、删除，不是批量生产对象。
class UserService {
  private List<User> users = new ArrayList<>();

  public void addUser(User u) {
    users.add(u);
  }

  public User findById(int id) {
    for (User u : users) {
      if(u.getId() == id) {
        return u;
      }
    }
    throw new RuntimeException("user not found:" + id);
  }
}

// main 调用方法（控制层/测试入口）
public class UserServiceDemo {
  public static void main(String[] args) {
    UserService service = new UserService();
    service.addUser(new User(1, "zhang", "122@163.com"));
    service.addUser(new User(2, "li", "163.COM"));

    System.out.println(service.findById(1));

    try {
      System.out.println(service.findById(99));

    } catch (RuntimeException e) {
      System.out.println("catach error: " + e.getMessage());

    }
  }
}