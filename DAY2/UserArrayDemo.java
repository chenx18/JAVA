
public class UserArrayDemo{
    
    public static void main(String[] arges) {

        User[] users = new User[3];
        users[0] = new User(1, "zhang", "232@123.com");
        users[1] = new User(2, "li", "234@123.com");
        users[2] = new User(3, "LIU", "23@123.com");

        // 遍历打印
        for (User u : users) {
            System.out.println(u);
        }
        
        // 删除 id=2 
        for (int i = 0; i < users.length; i++) {
            if (users[i] != null && users[i].getId() == 2) {
                users[i] = null;
            }
        }

        for (User u : users) {
            if (u != null) {
                System.out.println( u );
            }
        }

        User found = null;
        for (User u : users) {
            if(u != null && u.getId() == 3) {
                found = u;
                break;
            }
        }
        System.out.println("Find id=3:" + found);

    }
}