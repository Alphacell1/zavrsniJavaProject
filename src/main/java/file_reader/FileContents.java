package file_reader;

import screens.LoginController;
import screens.WelcomeScreen;
import user.Role;
import user.User;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class FileContents {
    Integer id;
    User user;
    LocalDateTime dateUpdated;
    String fileContent;
    String fileName;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public FileContents(Integer id, User user, LocalDateTime dateUpdated, String fileContent, String fileName) {
        this.id = id;
        this.user = user;
        this.dateUpdated = dateUpdated;
        this.fileContent = fileContent;
        this.fileName = fileName;
    }

    public FileContents(User user, LocalDateTime dateUpdated, String fileContent, String fileName) {
        this.user = user;
        this.dateUpdated = dateUpdated;
        this.fileContent = fileContent;
        this.fileName = fileName;
    }

    public User getUser() {
        return user;
    }


    public String getFileName() {
        return fileName;
    }


    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getDateUpdated() {
        return dateUpdated;
    }


    public String getFileContent() {
        return fileContent;
    }


    public static FileContents parseFileContents(String fileContent, String fileName) {
        String[] lines = fileContent.split("\n");

        if (lines.length < 3) {
            throw new IllegalArgumentException("File content does not have enough lines for user, role, and date updated.");
        }

        try {
            if (!lines[0].startsWith("user: ")) {
                throw new IllegalArgumentException("Invalid format for user.");
            }
            String username = lines[0].split(": ")[1].trim();

            if (!lines[1].startsWith("role: ")) {
                throw new IllegalArgumentException("Invalid format for role.");
            }
            String role = lines[1].split(": ")[1].trim();
            Role userRole = Role.getRoleFromString(role);

            if (!lines[2].startsWith("date updated: ")) {
                throw new IllegalArgumentException("Invalid format for date updated.");
            }
            String dateUpdatedStr = lines[2].split(": ")[1].trim();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            LocalDateTime dateUpdated;
            try {
                dateUpdated = LocalDateTime.parse(dateUpdatedStr, formatter);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid date format for date updated.");
            }

            StringBuilder fileContentBuilder = new StringBuilder();
            for (int i = 3; i < lines.length; i++) {
                fileContentBuilder.append(lines[i]);
                if (i < lines.length - 1) {
                    fileContentBuilder.append("\n");
                }
            }
            String actualFileContent = fileContentBuilder.toString();

            User user = new User.UserBuilder()
                    .setUsername(username)
                    .setRole(userRole)
                    .build();
            return new FileContents(user, dateUpdated, actualFileContent, fileName);
        } catch (Exception e) {
            System.err.println("Error parsing file content: " + e.getMessage());
            return null;
        }
    }

    public static FileContents updateFileContents(File file) throws IOException {
        String fileContent = new String(Files.readAllBytes(file.toPath()));

        User currentUser = LoginController.user;
        String username = currentUser.getUsername();
        Role role = currentUser.getRole();
        String dateUpdatedStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

        StringBuilder updatedContent = new StringBuilder();

        String[] lines = fileContent.split("\n");

        // Flag variables to check if certain fields were found
        boolean userFound = false;
        boolean roleFound = false;
        boolean dateUpdatedFound = false;

        // Iterate through the lines and update the necessary fields
        int i = 0;
        for (; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.startsWith("user: ")) {
                updatedContent.append("user: ").append(username).append("\n");
                userFound = true;
            } else if (line.startsWith("role: ")) {
                updatedContent.append("role: ").append(role).append("\n");
                roleFound = true;
            } else if (line.startsWith("date updated: ")) {
                updatedContent.append("date updated: ").append(dateUpdatedStr).append("\n");
                dateUpdatedFound = true;
            } else {
                break;
            }
        }

        if (!userFound) {
            updatedContent.append("user: ").append(username).append("\n");
        }
        if (!roleFound) {
            updatedContent.append("role: ").append(role).append("\n");
        }
        if (!dateUpdatedFound) {
            updatedContent.append("date updated: ").append(dateUpdatedStr).append("\n");
        }

        for (; i < lines.length; i++) {
            updatedContent.append(lines[i]).append("\n");
        }
        Files.write(file.toPath(), updatedContent.toString().getBytes(), StandardOpenOption.TRUNCATE_EXISTING);

        return parseFileContents(updatedContent.toString(), file.getName());
    }


        public static void createFileFromContents(FileContents fileContents) throws IOException {
            File rootDirectory = new File("/files");
            if (!rootDirectory.exists()) {
                rootDirectory.mkdirs();
            }

            File file = new File(rootDirectory, fileContents.getFileName());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String formattedDateUpdated = fileContents.getDateUpdated().format(formatter);

            User user = fileContents.getUser();
            String username = user.getUsername();
            String role = user.getRole().toString();

            StringBuilder contentBuilder = new StringBuilder();
            contentBuilder.append("user: ").append(username).append("\n");
            contentBuilder.append("role: ").append(role).append("\n");
            contentBuilder.append("date updated: ").append(formattedDateUpdated).append("\n");
            contentBuilder.append(fileContents.getFileContent());

            try (FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.write(contentBuilder.toString());
            }
        }



}
