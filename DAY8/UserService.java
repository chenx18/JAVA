public class  UserService {
    private  final UserRepository repository;

    public UserService(UserRepository r){
        this.repository = r;
    }

    public void addUser(User u) {
        if(u == null) {
            throw new IllegalArgumentException("user is null");
        }

        if(u.getId() <= 0) {
            throw new IllegalArgumentException("id is <= 0");
        }

        if(u.getName() == null || u.getName().isEmpty()) {
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

    public void updateUser(int id, String name, String email) {
        if(name == null || name.trim().isEmpty()){
            throw new IllegalArgumentException("name is blank");
        }

        if(email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("emain is blank");
        }

        boolean update = repository.updateById(id, name, email);

        if(!update) {
            throw new UserNotFoundException(id);
        }
    }

    public void removeUser(int id) {
        boolean delete = repository.deleteById(id);
        if(!delete) {
            throw new UserNotFoundException(id);
        }
    }
}