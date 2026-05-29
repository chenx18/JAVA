
import java.util.*;
/**
 * 数组 + Map 使用了工具包java.util.
 */
public class UserCollectionDemo {
	public static void main(String[] arges) {
		// List<User> 里的 <User> 叫 泛型 意思是：
		//  这个 List 只能存 User 类型（以及它的子类）
		// 作用：
		// 	编译期类型检查，避免放错对象
		// 	取出来不用强制类型转换
		List<User> users = new ArrayList<>();

		users.add(new User(1, "zhang", "a@123.com"));
		users.add(new User(2, "li", "li@123.com"));
		users.add(new User(3, "liu", "liu@123.com"));

		for (User u : users) {
			System.out.println(u);
		}

		users.removeIf(u -> u.getId() == 2);

		for (User u : users) {
			System.out.println(u);
		}

		// Map 存储并查询
		Map<Integer, User> map = new HashMap<>();
		for (User u : users) {
			map.put(u.getId(), u);
		}

		System.out.println("Find id=3: " + map.get(3));

	}
}