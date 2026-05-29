public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void addUser(User user) {
        userRepository.save(user);
    }

    public User getUserById(int id) {
        User user = userRepository.findById(id);
        if(user == null) {
            throw new UserNotFoundException(id);
        }
        return user;
    }
}