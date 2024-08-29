package screens;

import difference.FileHandler;
import file_reader.FileContents;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ViewLocalRevisionsController {

    @FXML
    private TreeView<String> fileTreeView;

    @FXML
    private TextArea fileContentArea;

    @FXML
    public void initialize() {
        File rootDirectory = new File("/files");

        TreeItem<String> rootItem = new TreeItem<>(rootDirectory.getName());
        rootItem.setExpanded(true);

        createTree(rootDirectory, rootItem);

        fileTreeView.setRoot(rootItem);

        fileTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                File selectedFile = new File(rootDirectory, newValue.getValue());
                FileHandler<File> fileHandler = new FileHandler<>(selectedFile);
                fileHandler.processFile();

                displayFileContent(fileHandler.getFile());
            }
        });
    }

    private void createTree(File directory, TreeItem<String> parentItem) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                TreeItem<String> item = new TreeItem<>(file.getName());
                parentItem.getChildren().add(item);
                if (file.isDirectory()) {
                    createTree(file, item);
                }
            }
        }
    }

    private void displayFileContent(File file) {
        try {
            FileContents fileContents = FileContents.updateFileContents(file);
            fileContentArea.setText(fileContents.getFileContent());
        } catch (IOException e) {
            fileContentArea.setText("Unable to read file: " + file.getName());
        }
    }
}
