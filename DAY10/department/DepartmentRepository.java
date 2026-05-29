import java.util.ArrayList;
import java.util.List;

public class DepartmentRepository {
  
  private final List<Department> deps = new ArrayList<>();

  public void save(Department d) {
    deps.add(d);
  }

  public Department findById(int id) {
    for(Department d : deps){
      if(d.getId() == id) {
        return d;
      }
    }
    return null;
  }

  public boolean existsById(int id) {
    return findById(id) != null;
  }

  public boolean upadateNameById(int id, String name){
    Department d = findById(id);
    if(d == null) {
      return false;
    }
    d.setName(name);
    return true;
  }

  public boolean deleteById(int id){
    Department d = findById(id);
    if(d == null) {
      return false;
    }
    deps.remove(d);
    return true;
  }

  
}