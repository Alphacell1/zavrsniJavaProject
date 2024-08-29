package difference;

public class FileHandler<T> {
    private T file;

    public FileHandler(T file) {
        this.file = file;
    }

    public T getFile() {
        return file;
    }

    public void setFile(T file) {
        this.file = file;
    }

    public void processFile() {
        System.out.println("Processing file: " + file.toString());
    }
}