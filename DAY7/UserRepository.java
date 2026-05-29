
import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private final  List<User> users = new ArrayList<>();

    public void save(User u) {
        users.add(u);
    }

    public User findById(int id) {
        for(User u : users) {
            if(u.getId() == id) {
                return u;
            }
        }
        return null;
    }

    public boolean existsById(int id) {
        return findById(id) != null;
    }

}