package model.bo;
import java.util.List;
import model.bean.Task;
import model.dao.TaskDAO;
public class TaskBO {
    private TaskDAO taskDAO = new TaskDAO();

    public int addTask(Task task){
        return taskDAO.insertTask(task);
    }
    public List<Task> getUserTasks(int userId){
        return taskDAO.getTasksByUserId(userId);
    }
    public void updateTask(int taskId, String status, String docxPath){
        taskDAO.updateTaskStatus(taskId, status, docxPath);
    }
    public void saveHistory(String username, String originalFileName, String storedFileName){
        taskDAO.saveConversionHistory(username, originalFileName, storedFileName);
    }
}
