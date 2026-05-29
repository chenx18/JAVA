import java.util.ArrayList;
import java.util.List;

public class DepartmentRepository {
    private final List<Department> departments = new ArrayList<>();

    public void save(Department department) {
        departments.add(department);
    };

    public Department findById(int id) {
        for (Department d : departments) {
            if(d.getId() == id) {
                return d;
            }
        }
        return null;
    };

    public boolean existsById(int id) {
        return findById(id) != null;
    };

    public boolean updateNameById(int d, String newName) {
        Department department = findById(d);
        if(department == null) {
            return false;
        }
        department.setName(newName);
        return true;
    }

    public boolean deleteById(int id) {
        for(int i = 0; i < departments.size(); i++) {
            if(departments.get(i).getId() == id) {
                departments.remove(i);
                return true;
            }
        }
        return false;
    }
}