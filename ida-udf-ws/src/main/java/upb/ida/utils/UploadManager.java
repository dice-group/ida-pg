package upb.ida.utils;

import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UploadManager {

    public static boolean saveFile (MultipartFile file, byte[] contentInBytes) {
        boolean success = false;
        try {
            if (! Paths.get("/uploads/").toFile().isDirectory()) {
                // If uploads folder did not exists then create it
                new File(System.getProperty("user.dir") + "/uploads").mkdir();
            }
            Path path = Paths.get(System.getProperty("user.dir") + "/uploads/", file.getOriginalFilename() + ".ttl");
            Files.write(path, contentInBytes);
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return success;
    }
}
