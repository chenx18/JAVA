
public class User {
  // 字段：id、name、email
  // private 修饰
  private int id;
  private String name;
  private String email;
   
  // 构造器  同名 + 参数不同 = 不同方法
  public User() {
    
  }
  // 构造器  同名 + 参数不同 = 不同方法
  public User(int id, String name, String email) {
    this.id = id;
    this.name = name;
    this.email = email;
  }

  public int getId() {
    return id;
  }

  // 重写 toString() 让对象被 System.out.println(u) 打印时，输出清晰内容
  @Override
  public String toString() {
    return "User id:"+ id + ", name:" + name + ", email:" + email;
  }


	public static void main(String[] args) {
    
    // 无参构造器：让你了解对象默认创建
		User u1 = new User();

    // 全参构造器：用 this.id = id 这种方式赋值
    User u2 = new User(1, "zhang", "sdfsd@123.com");

		System.out.println(u1);
    System.out.println(u2);
	}
}