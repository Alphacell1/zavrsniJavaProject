package difference;

import file_reader.FileContents;

import java.io.IOException;

public interface FileDifferencesInterface {
    String getFileDifferences(FilePair<FileContents,FileContents> filePair) throws IOException;
}
