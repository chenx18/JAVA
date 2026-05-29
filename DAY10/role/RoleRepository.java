import java.util.ArrayList;
import java.util.List;

public class RoleRepository {
  private final List<Role> roles = new ArrayList<>();

  public void save(Role r) {
    roles.add(r);
  }

  public Role findById(int id) {
    for(Role r : roles) {
      if(r.getId() == id){
        return r;
      }
    }
    return null;
  }

  public boolean existsById(int id) {
    Role role = findById(id);
    return role != null;
  }

  public boolean updateRoleName(int id, String newName) {
    Role role = findById(id);
    if(role == null){
      return false;
    }
    role.setName(newName);
    return true;
  }

  public boolean deleteById(int id) {
    Role role = findById(id);
    if(role == null) {
      return false;
    }
    roles.remove(role);
    return true;
  }


}