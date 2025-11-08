package model.bo;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import model.bean.Task;
import model.dao.TaskDAO;
import model.enums.TaskStatus;

public class TaskBO {
    private final TaskDAO taskDAO = new TaskDAO();
    
    public Task processAndSaveTask(int userId, String fileName, String contentType, InputStream contentFile, String baseDir) {
        File userUploadDir = new File(baseDir + File.separator + userId);
        if (!userUploadDir.exists()) userUploadDir.mkdirs(); 

        String uniqueFileName = System.currentTimeMillis() + "_" + fileName;
        Path targetPath = Paths.get(userUploadDir.getAbsolutePath() + File.separator + uniqueFileName);

        try {
            Files.copy(contentFile, targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            return null; 
        }

        Task task = new Task();
        task.setUserId(userId);
        task.setPdfName(fileName);
        task.setPdfPath(targetPath.toString());
        task.setStatus(TaskStatus.UPLOADED);
        boolean success = taskDAO.createTask(task); 

        return success ? task : null;
    }
}
