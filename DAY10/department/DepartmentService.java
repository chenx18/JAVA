public class DepartmentService {

  private final DepartmentRepository repository;

  public DepartmentService(DepartmentRepository r){
    this.repository = r;
  }

  public void addDepartment(Department d) {
    if(d == null) {
      throw new IllegalArgumentException("Department is null");
    }

    if(d.getId() <= 0) {
      throw new IllegalArgumentException("Id <= 0");
    }

    if(d.getName() == null || d.getName().isEmpty()){
      throw new IllegalArgumentException("Department Name is null");
    }

    if(repository.existsById(d.getId())){
      throw new IllegalArgumentException("Department is has");
    }

    repository.save(d);
  }

  public Department getDepartment(int id){
    Department d = repository.findById(id);
    if(d == null) {
      throw new DepartmentNotFoundException(id);
    }
    return d;
  }

  public boolean updateDepartmentNamr(int id, String name) {
    Department d = repository.findById(id);
    if(d == null) {
      throw new DepartmentNotFoundException(id);
    }
    repository.upadateNameById(id, name);
    return true;
  }

  public void removeDepartment(int id) {
    boolean isHas = repository.existsById(id);
    if(!isHas) {
      throw new DepartmentNotFoundException(id);
    }
    repository.deleteById(id);
  }

}