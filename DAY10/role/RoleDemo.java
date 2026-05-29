public class RoleDemo {
  public static void main(String[] args) {
    
    RoleRepository repository = new RoleRepository();
    RoleService service = new RoleService(repository);

    service.addRole(new Role(1, "admin"));
    service.addRole(new Role(2, "test"));

    System.out.println(service.getRoleById(1));
    System.out.println(service.getRoleById(99));

    service.updateRoleName(1, "Admin");   
    System.out.println(service.getRoleById(1));

    service.removeRoleById(2);
    System.out.println(service.getRoleById(2));
    
  }
}