public class RoleService {
  private final RoleRepository repository;

  public RoleService(RoleRepository r) {
    this.repository = r;
  }

  public void addRole(Role r) {
    if(r == null) {
      throw new IllegalArgumentException("Role is null");
    }

    if(r.getId() <= 0) {
      throw new IllegalArgumentException("Role id is <=0");
    }

    if(r.getName() == null || r.getName().isEmpty()){
      throw new IllegalArgumentException("Role name is null");
    }

    if(repository.existsById(r.getId())){
      throw new IllegalArgumentException("Role has");
    }

    repository.save(r);
  }

  public Role getRoleById(int id) {
    Role role = repository.findById(id);
    if(role == null) {
      throw new RoleNotFoundException(id);
    }
    return role;
  }

  public boolean  removeRoleById(int id) {
    boolean deleted = repository.deleteById(id);
    if(!deleted) {
      throw new RoleNotFoundException(id);
    }
    return true;
  }


  public void updateRoleName(int id, String name){
    if(name == null || name.isEmpty()){
      throw new IllegalArgumentException("Role name error");
    }

    boolean update = repository.updateRoleName(id, name);

    if(!update) {
      throw new RoleNotFoundException(id);
    }
  }

}