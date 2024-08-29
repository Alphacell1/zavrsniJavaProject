package sql;

import file_reader.FileContents;
import login_manager.LoginManager;
import user.Role;
import user.User;

import java.sql.*;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseUtils {

    private static final Logger logger = Logger.getLogger(DatabaseUtils.class.getSimpleName());

    private static Connection connectToDatabase() throws SQLException {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            logger.log(Level.WARNING, "Class not found", e);
        }
        Connection veza = DriverManager.getConnection("jdbc:h2:~/baza/test",
                "sa", "");
        return veza;
    }

    public static List<User> getAllUsers() {
        try (Connection connection = connectToDatabase()) {
            List<User> users = new ArrayList<>();
            String sql = "SELECT * FROM users";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Integer userId = resultSet.getInt("id");
                String name = resultSet.getString("name");
                Integer roleId = resultSet.getInt("role_id");
                users.add(new User.UserBuilder().setId(userId).setUsername(name).setRole(Role.getRoleFromId(roleId)).build());
            }
            return users;
        } catch (SQLException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        }
        return null;
    }

    public static Integer storeUser(User user) {
        String sql = "INSERT INTO users (name, role_id) VALUES (?, ?)";
        try (Connection connection = connectToDatabase();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setInt(2, user.getRole().getRoleId());
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }

        } catch (SQLException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        }
        return null;
    }

    public static boolean deleteUser(User user) {
        String sql = "DELETE FROM REVISIONS WHERE last_change_person_id = ?; DELETE FROM users WHERE id = ?";
        LoginManager loginManager = new LoginManager();

        try (Connection connection = connectToDatabase();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, user.getId());
            preparedStatement.setInt(2, user.getId());
            int rowsAffected = preparedStatement.executeUpdate();
            loginManager.deleteUserById(user.getId());
            return rowsAffected > 0;

        } catch (SQLException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        }
        return false;
    }


    public static User getUserFromId(Integer id) {
        try (Connection connection = connectToDatabase()) {
            String sql = "SELECT * FROM users WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Integer userId = resultSet.getInt("id");
                String name = resultSet.getString("name");
                Integer roleId = resultSet.getInt("role_id");
                return new User.UserBuilder().setId(userId).setUsername(name).setRole(Role.getRoleFromId(roleId)).build();
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        }
        return null;
    }

    public static List<FileContents> getAllRevisions() {
        try (Connection connection = connectToDatabase()) {
            List<FileContents> contents = new ArrayList<>();
            String sql = "SELECT * FROM revisions";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            return getFileContents(contents, resultSet);
        } catch (SQLException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        }
        return null;
    }

    private static List<FileContents> getFileContents(List<FileContents> contents, ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            Integer id = resultSet.getInt("id");
            String fileName = resultSet.getString("file_name");
            Timestamp lastModified = resultSet.getTimestamp("date_changed");
            Integer lastModifiedBy = resultSet.getInt("last_change_person_id");
            User previousChangeUser = getUserFromId(lastModifiedBy);
            String content = resultSet.getString("contents");
            contents.add(new FileContents(id, previousChangeUser, lastModified.toLocalDateTime(), content, fileName));
        }
        return contents;
    }


    public static boolean storeRevision(FileContents fileContents) {
        String sql = "INSERT INTO REVISIONS (file_name, date_changed, contents, last_change_person_id) VALUES (?, ?, ?, ?)";

        try (Connection connection = connectToDatabase();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, fileContents.getFileName());
            preparedStatement.setTimestamp(2, Timestamp.from(fileContents.getDateUpdated().toInstant(ZoneOffset.UTC)));
            preparedStatement.setString(3, fileContents.getFileContent());
            preparedStatement.setInt(4, fileContents.getUser().getId());

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
            return false;
        }
    }

    public static boolean deleteRevision(FileContents fileContents) {
        String sql = "DELETE FROM REVISIONS WHERE id = ?";

        try (Connection connection = connectToDatabase();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, fileContents.getId());

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
            return false;
        }
    }


}
