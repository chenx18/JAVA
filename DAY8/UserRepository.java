import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private final List<User> users = new ArrayList<>();


    public void save(User u){
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

    public boolean updateById(int id, String name, String email) {
        User user = findById(id);
        if (user == null) {
            return false;
        }

        user.setName(name);
        user.setEmail(email);
        return true;
    }

    public boolean deleteById(int id) {
        for(int i = 0; i < users.size(); i++ ) {
            if(users.get(i).getId() == id) {
                users.remove(id);
                return true;
            }
        }
        return false;
    }
}