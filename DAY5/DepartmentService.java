public class DepartmentService {
    private final DepartmentRepository repository;

    public DepartmentService(DepartmentRepository rp) {
        this.repository = rp;
    }

    public void addDepartment(Department departmnet) {
        repository.save(departmnet);
    }

    public Department getDepartmentById(int id) {
        Department d = repository.finById(id);
        if(d == null) {
            throw new DepartmentNotFoundException(id);
        }
        return d;
    }
}