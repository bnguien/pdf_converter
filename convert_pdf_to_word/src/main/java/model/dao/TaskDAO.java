package model.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;

import model.bean.Task;
import util.DBConnection;

public class TaskDAO {
    public int insertTask(Task task) {
        String sql = "INSERT INTO tasks(user_id, pdf_name, pdf_path, docx_path, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            if (task.getUserId() == null) {
                ps.setNull(1, java.sql.Types.INTEGER);
            } else {
                ps.setInt(1, task.getUserId());
            }

            ps.setString(2, task.getPdfName());
            ps.setString(3, task.getPdfPath());
            ps.setString(4, task.getDocxPath());
            ps.setString(5, task.getStatus());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public Optional<Task> getTaskIfUserNull(int taskId) {
        String sql = "SELECT * FROM tasks WHERE id = ? AND user_id IS NULL";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, taskId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Task t = new Task();
                t.setId(rs.getInt("id"));
                t.setPdfName(rs.getString("pdf_name"));
                t.setPdfPath(rs.getString("pdf_path"));
                t.setDocxPath(rs.getString("docx_path"));
                t.setStatus(rs.getString("status"));
                return Optional.of(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public List<Task> getTasksByUserId(int userId) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE user_id = ? ORDER BY uploaded_at DESC";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Task task = new Task();
                task.setId(rs.getInt("id"));
                task.setUserId(rs.getInt("user_id"));
                task.setPdfName(rs.getString("pdf_name"));
                task.setPdfPath(rs.getString("pdf_path"));
                task.setDocxPath(rs.getString("docx_path"));
                task.setStatus(rs.getString("status"));
                task.setUploadedAt(rs.getObject("uploaded_at", LocalDateTime.class));
                task.setCompletedAt(rs.getObject("completed_at", LocalDateTime.class));
                tasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public void updateTaskStatus(int taskId, String status, String docxPath) {
        String sql = "UPDATE tasks SET status=?, docx_path=?, completed_at=NOW() WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            System.out.println("[DEBUG] Running UPDATE for taskId=" + taskId);
            System.out.println("[DEBUG] SQL: " + sql);
            System.out.println("[DEBUG] Values: status=" + status + ", docxPath=" + docxPath);

            ps.setString(1, status);
            ps.setString(2, docxPath);
            ps.setInt(3, taskId);
            int rows = ps.executeUpdate();

            System.out.println("[DEBUG] Rows affected: " + rows);

        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to update task: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Optional<Task> getTaskDetail(int taskId, int userId) {
        String sql = "SELECT * FROM tasks WHERE id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, taskId);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Task task = new Task();
                task.setId(rs.getInt("id"));
                task.setUserId(rs.getInt("user_id"));
                task.setPdfName(rs.getString("pdf_name"));
                task.setPdfPath(rs.getString("pdf_path"));
                task.setDocxPath(rs.getString("docx_path"));
                task.setStatus(rs.getString("status"));
                task.setUploadedAt(rs.getObject("uploaded_at", LocalDateTime.class));
                task.setCompletedAt(rs.getObject("completed_at", LocalDateTime.class));

                return Optional.of(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}