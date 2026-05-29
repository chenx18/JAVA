import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    
    private final List<User> users = new ArrayList<>();

    public void save(User user) {
        users.add(user);
    }

    public User findById(int id) {
        for(User u : users) {
            if(u.getId() == id) {
                return u;
            }
        }
        return null;
    }
}