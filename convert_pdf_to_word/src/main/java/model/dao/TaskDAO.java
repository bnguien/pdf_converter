package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import model.bean.Task;

public class TaskDAO {
     public boolean createTask(Task task) {
          String sql = "INSERT INTO tasks (user_id, pdf_name, pdf_path, status) VALUES (?, ?, ?, ?)";

          try (Connection conn = DBConnection.getConnection();
               PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
               ps.setInt(1, task.getUserId());
               ps.setString(2, task.getPdfName());
               ps.setString(3, task.getPdfPath());
               ps.setString(4, task.getStatus().name());
               
               int rowsAffected = ps.executeUpdate();
            
               if (rowsAffected > 0) {
                    try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                         if (generatedKeys.next()) {
                         task.setId(generatedKeys.getInt(1));
                         return true;
                         }
                    }
               }
          } catch(Exception e) {
               e.printStackTrace();
          }
          return false;
     }
}
