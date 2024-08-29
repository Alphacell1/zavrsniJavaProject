package login_manager;

import exceptions.LoginFailedException;
import user.Role;
import user.User;

import java.util.Optional;

public sealed interface LoginInterface permits LoginManager {
    Optional<User> login(String username, String password) throws LoginFailedException;
}
