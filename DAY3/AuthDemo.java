interface Authorizable {
  boolean hasPermission(String perm);
}

class AdminUser implements Authorizable {
  @Override

  public boolean hasPermission(String perm) {
    return true;
  }
}

class NormalUser implements Authorizable {
  @Override
  public boolean hasPermission(String perm) {
    return  "READ".equals(perm);
  }
}

public class AuthDemo {
  public static void main(String[] args) {
    Authorizable a1 = new AdminUser();
    Authorizable a2 = new NormalUser();

    System.out.println(a1.hasPermission("WRITE"));
    System.out.println(a2.hasPermission("WRITE"));
    System.out.println(a2.hasPermission("READ"));
  }
}