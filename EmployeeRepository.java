import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class EmployeeRepository {
    private final Path dataFile;

    public EmployeeRepository(String filePath) {
        this.dataFile = Path.of(filePath);
        ensureFile();
    }

    private void ensureFile() {
        try {
            if (Files.notExists(dataFile)) {
                Files.createDirectories(dataFile.getParent() != null ? dataFile.getParent() : Path.of("."));
                saveAll(new ArrayList<>());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize data file: " + e.getMessage(), e);
        }
    }

    public synchronized List<Employee> loadAll() {
        if (Files.notExists(dataFile) || dataFile.toFile().length() == 0) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(dataFile)))) {
            Object obj = ois.readObject();
            return (List<Employee>) obj;
        } catch (EOFException eof) {
            return new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Warning: Could not read employees. Starting with empty list. " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public synchronized void saveAll(List<Employee> employees) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(Files.newOutputStream(dataFile)))) {
            oos.writeObject(employees);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save employees: " + e.getMessage(), e);
        }
    }
}

