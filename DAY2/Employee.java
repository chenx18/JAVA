/**
 * 继承 + 多态
 * extends
 */

public class Employee extends  User {
	private double salary;

	public Employee() {
		super();
	}

	public Employee(int id, String name, String email, double salary) {
		super(id, name, email);  // 调用父类构造器
		this.salary = salary;
	}

	@Override
	public String toString() {
		return "user id:"+ super.toString() + salary ;
	}


	public static void main(String[] arges) {

		Employee e1 = new Employee(1, "zhang", "@123", 12000);
		System.out.println(e1);

		User u = new Employee(2, "li", "c@cc.com", 16000);
		System.out.println(u);

	}
}