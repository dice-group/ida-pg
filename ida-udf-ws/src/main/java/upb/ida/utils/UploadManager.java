package upb.ida.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UploadManager {

    public static boolean saveFile (String fileName, byte[] contentInBytes) {
        boolean success = false;
        try {
            if (! Paths.get("/uploads/").toFile().isDirectory()) {
                // If uploads folder did not exists then create it
                new File(System.getProperty("user.dir") + "/uploads").mkdir();
            }
            Path path = Paths.get(System.getProperty("user.dir") + "/uploads/", fileName + ".ttl");
            Files.write(path, contentInBytes);
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return success;
    }

    public static String getFileExtension (String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        return dotIndex != -1 ? fileName.substring(dotIndex+1) : "";
    }
}
