package model.bean;

import java.time.LocalDateTime;

public class Log {
    private int id;
    private int userId;
    private String action;
    private String message;
    private LocalDateTime createdAt;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
    public Log() {}

    public Log(int id, int userId, String action, String message, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.action = action;
        this.message = message;
        this.createdAt = createdAt;
    }
}
