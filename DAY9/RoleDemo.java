

public class RoleDemo {
  public static void main(String[] args) {
    RoleRepository repository = new RoleRepository();
    RoleService service = new RoleService(repository);

    service.addRole(new Role(1, "ADMIN"));
    service.addRole(new Role(2, "TEST"));

    System.out.println(service.getRoleById(2));

    service.updateRoleName(1, "SUPER_ADMIN");
    System.out.println(service.getRoleById(1));

    service.removeRole(2);

    try {
      System.out.println(service.getRoleById(99));
    } catch (RoleNotFoundException e) {
      System.out.println("catch error: " + e.getMessage());
    }

  }
}