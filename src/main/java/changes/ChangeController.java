package changes;

import screens.LoginController;
import user.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChangeController {

    private static final String FILE_NAME = "changes.ser";

    public static void saveChange(String promjena) {
        try {
            List<Change> svePromjene = getAllChanges();
            Change change = new Change(promjena, LoginController.user.getUsername());
            svePromjene.add(change);

            try (FileOutputStream fileOutputStream = new FileOutputStream(FILE_NAME);
                 ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
                objectOutputStream.writeObject(svePromjene);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Change> getAllChanges() {
        List<Change> changeList = new ArrayList<>();
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return changeList;
        }
        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream objectInputStream = new ObjectInputStream(fis)) {
            changeList = (List<Change>) objectInputStream.readObject();
        } catch (EOFException e) {
            return changeList;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return changeList;
    }

    public static List<Change> getChangesForUser(User user) {
        return getAllChanges().stream()
                .filter(change -> change.username().equals(user.getUsername()))
                .collect(Collectors.toList());
    }
}
