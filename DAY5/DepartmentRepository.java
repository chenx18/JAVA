import java.util.ArrayList;
import java.util.List;


public class DepartmentRepository {
    private final List<Department> departments = new ArrayList<>();

    public void  save(Department dep) {
        departments.add(dep);
    }

    public Department finById(int id) {
        for(Department d : departments) {
            if(d.getId() == id) {
                return d;
            }
        }
        return null;
    }
}