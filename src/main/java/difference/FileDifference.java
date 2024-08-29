package difference;

import exceptions.NotAFileException;
import file_reader.FileContents;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileDifference implements FileDifferencesInterface {
    Logger logger = Logger.getLogger(FileDifference.class.getSimpleName());

    @Override
    public String getFileDifferences(FilePair<FileContents, FileContents> filePair) throws IOException {
        List<String> oldLines = null;
        List<String> newLines = null;
        try {
            oldLines = readFileLines(filePair.getOldFile());
            newLines = readFileLines(filePair.getNewFile());
        } catch (NotAFileException ex) {
            logger.log(Level.WARNING, ex.getMessage());
        }

        StringBuilder differences = new StringBuilder();

        int oldIndex = 0;
        int newIndex = 0;

        while (oldIndex < oldLines.size() || newIndex < newLines.size()) {
            if (oldIndex < oldLines.size() && newIndex < newLines.size()) {
                String oldLine = oldLines.get(oldIndex);
                String newLine = newLines.get(newIndex);

                if (!oldLine.equals(newLine)) {
                    differences.append("Changed at line ").append(oldIndex + 1).append(":\n");
                    differences.append("< ").append(oldLine).append("\n");
                    differences.append("> ").append(newLine).append("\n");
                }
                oldIndex++;
                newIndex++;
            } else if (oldIndex < oldLines.size()) {
                differences.append("Removed at line ").append(oldIndex + 1).append(":\n");
                differences.append("< ").append(oldLines.get(oldIndex)).append("\n");
                oldIndex++;
            } else {
                differences.append("Added at line ").append(newIndex + 1).append(":\n");
                differences.append("> ").append(newLines.get(newIndex)).append("\n");
                newIndex++;
            }
        }

        if (differences.isEmpty()) {
            differences.append("No differences found\n");
        }

        return differences.toString();
    }


    private static List<String> readFileLines(FileContents fileContents){
        return Arrays.asList(fileContents.getFileContent().split("\n"));
    }
}
