public class DepartmentService {
   private final DepartmentRepository repository;

   public DepartmentService(DepartmentRepository repository) {
    this.repository = repository;
   }

   public void addDepartment(Department department) {
    if(department == null) {
        throw new IllegalArgumentException("department null");
    }
    if(department.getId() <= 0) {
        throw new IllegalArgumentException("department id < 0");
    }
    repository.save(department);
   }

    public Department getDepartmentById(int id) {
        Department department = repository.findById(id);
        if(department == null) {
            throw new DepartmentNotFoundException(id);
        }
        return department;
    }

   public void renameDepartment(int id, String newName) {
    if(newName == null || newName.translateEscapes().isEmpty()){
        throw new IllegalArgumentException("new name is error");
    }
    boolean updated = repository.updateNameById(id, newName);
    if(!updated) {
        throw new DepartmentNotFoundException(id);
    }
   }

   public void removeDepartment(int id) {
    boolean delete = repository.deleteById(id);
    if(!delete) {
        throw new DepartmentNotFoundException(id);
    }
   }

}