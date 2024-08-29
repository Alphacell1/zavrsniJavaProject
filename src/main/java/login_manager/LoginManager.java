package login_manager;

import exceptions.LoginFailedException;
import sql.DatabaseUtils;
import user.Role;
import user.User;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public non-sealed class LoginManager implements LoginInterface {

    private static final String LOGIN_FILE = "infoLogin.txt";

    public LoginManager() {
        initializeFile();
    }

    private void initializeFile() {
        File file = new File(LOGIN_FILE);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public Integer addUser(String username, String password, Role role) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOGIN_FILE, true))) {
            String hashedPassword = hashPassword(password);
            Integer userId = DatabaseUtils.storeUser(new User.UserBuilder().setUsername(username).setRole(role).build());
            writer.write(userId + "," + username + "," + hashedPassword + "," + role.name());
            writer.newLine();
            return userId;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Optional<User> login(String username, String password) throws LoginFailedException {
        String hashedPassword = hashPassword(password);
        Optional<User> user = findUserInFile(username, hashedPassword);
        if (user.isEmpty()) {
            throw new LoginFailedException("Username or password is incorrect");
        }
        return user;
    }


    private Optional<User> findUserInFile(String username, String hashedPassword) {
        try (BufferedReader reader = new BufferedReader(new FileReader(LOGIN_FILE))) {
            return reader.lines()
                    .map(line -> line.split(","))
                    .filter(loginInfo -> loginInfo.length == 4)
                    .filter(loginInfo -> loginInfo[1].equals(username) && loginInfo[2].equals(hashedPassword))
                    .map(loginInfo -> {
                        Integer userId = Integer.valueOf(loginInfo[0]);
                        Role role = Role.getRoleFromString(loginInfo[3]);
                        return new User.UserBuilder().setId(userId).setUsername(username).setRole(role).build();
                    })
                    .findFirst();
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public void deleteUserById(int userIdToDelete) {
        File file = new File(LOGIN_FILE);
        List<String> remainingLines;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            remainingLines = reader.lines()
                    .filter(line -> {
                        String[] loginInfo = line.split(",");
                        return loginInfo.length == 4 && Integer.parseInt(loginInfo[0]) != userIdToDelete;
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : remainingLines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
