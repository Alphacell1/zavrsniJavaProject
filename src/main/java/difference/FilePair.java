package difference;

import exceptions.NotAFileException;
import file_reader.FileContents;

public record FilePair<T, K>(T oldFile, K newFile) {

    public FileContents getOldFile() throws NotAFileException {
        if (oldFile instanceof FileContents) {
            return (FileContents) oldFile;
        }
        throw new NotAFileException("The first given variable is not a file");
    }

    public FileContents getNewFile() throws NotAFileException {
        if (newFile instanceof FileContents) {
            return (FileContents) newFile;
        }
        throw new NotAFileException("The second given variable is not a file");
    }

}
