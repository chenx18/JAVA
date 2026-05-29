
import java.util.ArrayList;
import java.util.List;

public class RoleRepository {

  private final List<Role> roles = new ArrayList<>();

  public void save(Role role) {
    roles.add(role);
  }

  public Role findById(int id) {
    for (Role role : roles) {
        if(role.getId() == id){
          return  role;
        }
    }
    return null;
  }

  public boolean existsById(int id) {
    return findById(id) != null;
  }

  public boolean updateNameById(int id, String newName){
    Role role = findById(id);
    if(role == null) {
      return false
    }
    role.setName(newName);
    return true;
  }


  public boolean deleteById(int id) {
    Role role = findById(id);
    if(role == null) {
      return false
    }
    roles.remove(id);
    return true;
  }
}