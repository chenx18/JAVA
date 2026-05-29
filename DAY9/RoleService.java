public class RoleService {

  private final RoleRepository repository;

  public RoleService(RoleRepository r) {
    this.repository = r;
  }

  public void addRole(Role e){
    if(e == null) {
      throw new IllegalArgumentException("role is  null");
    }
    if(e.getId() <= 0) {
      throw new IllegalArgumentException("role id is <= 0");
    }
    if(e.getName() == null || e.getName().isEmpty()) {
      throw new IllegalArgumentException("role name is null");
    }
    if(repository.existsById(e.getId())){
      throw new IllegalArgumentException("role is has");
    }

    repository.save(e);
  }

  public Role getRoleById(int id) {
    Role r = repository.findById(id);
    if(r == null) {
      throw new RoleNotFoundException(id);
    }
    return r;
  }


  public void updateNameById(int id, String name){
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("name is blank");
    }
    boolean update = repository.updateNameById(id, name);
    
    if(!update) {
      throw new RoleNotFoundException(id);
    }
  }


  public boolean  removeRole(int id) {
    boolean deleted = repository.deleteById(id);
    if(!deleted) {
      throw new RoleNotFoundException(id);
    }
    return true;
  }

}