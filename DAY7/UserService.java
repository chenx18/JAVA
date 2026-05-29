public class UserService {
    private final UserRepository repository;


    public UserService(UserRepository r) {
        this.repository = r;
    }

    public void addUser(User u){
        if(u == null) {
            throw new IllegalArgumentException("user is null");
        }

        if(u.getId() <= 0) {
           throw new IllegalArgumentException("user id <= 0");
        }

        if(u.getName() == null || u.getName().isEmpty()){
            throw new IllegalArgumentException("name is blank");
        }

        if(u.getEmail() == null || u.getEmail().isEmpty()){
            throw new IllegalArgumentException("email is blank");
        }

        repository.save(u);
    }

    public User getUserById(int id) {
        User u = repository.findById(id);
        if(u == null) {
            throw new UserNotFoundException(id);
        }
        return u;
    }

}