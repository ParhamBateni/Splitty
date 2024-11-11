package server;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class StartCleanUp {

    private final String filename;

    public StartCleanUp() {
        this.filename = System.getProperty("user.home") + File.separator + "recentEvents.ser";
    }

    @PreDestroy
    public void cleanup() {
        File file = new File(filename);
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("Serialized file erased successfully.");
            } else {
                System.err.println("Failed to delete serialized file.");
            }
        }
    }
}

