public class UserService{
  private final UserRepository repository;

  public UserService(UserRepository r) {
    this.repository = r;
  }

  public void addUser(User e) {
    if(e == null) {
      throw new IllegalArgumentException("User is null");
    }
    if(e.getId() <= 0) {
      throw new IllegalArgumentException("User id null");
    }

    if(e.getName() == null || e.getName().isEmpty()) {
      throw new IllegalArgumentException("User name null");
    }

    if(e.getEmail() == null || e.getEmail().isEmpty()) {
      throw new IllegalArgumentException("User email null");
    }

    repository.save(e);
  }

  public User getUserById(int id) {
    User u = repository.findById(id);
    if(u == null){
      throw new UserNotFoundException(id);
    }
    return u;
  }



}