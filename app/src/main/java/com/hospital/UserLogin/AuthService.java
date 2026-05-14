package  com.hospital.UserLogin;

import com.hospital.UserLogin.User;
import com.hospital.UserLogin.UserRepository;
import com.hospital.UserLogin.HashUtils;

public class AuthService {

    private UserRepository userRepo;

    public AuthService(UserRepository repo) {
        this.userRepo = repo;
    }

    public User login(String username, String password) {
        User user = userRepo.findByUsername(username);

        if (user == null)
            return null;

        String hashed = HashUtils.sha256(password);

        if (hashed.equals(user.getPasswordHash()))
            return user;

        return null;
    }
}
