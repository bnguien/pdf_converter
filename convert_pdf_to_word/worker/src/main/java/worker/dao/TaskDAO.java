package worker.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import worker.util.DBConnection;

public class TaskDAO {
    
    public void updateTaskStatus(int taskId, String status, String docxPath) {
        String sql = "UPDATE tasks SET status=?, docx_path=?, completed_at=NOW() WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            System.out.println("[WORKER-DAO] Updating task " + taskId);
            System.out.println("[WORKER-DAO] Status: " + status);
            System.out.println("[WORKER-DAO] DOCX Path: " + docxPath);

            ps.setString(1, status);
            ps.setString(2, docxPath);
            ps.setInt(3, taskId);
            
            int rows = ps.executeUpdate();
            System.out.println("[WORKER-DAO] Rows affected: " + rows);

        } catch (SQLException e) {
            System.err.println("[WORKER-DAO] Failed to update task: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to update task status", e);
        }
    }
}

