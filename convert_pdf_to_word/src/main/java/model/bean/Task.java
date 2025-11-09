package model.bean;

import java.time.LocalDateTime;

public class Task {
    private int id;
    private int userId;
    private String pdfName;
    private String pdfPath;
    private String docxPath;
    private String status;
    private LocalDateTime uploadedAt;
    private LocalDateTime completedAt;
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
	public String getPdfName() {
		return pdfName;
	}
	public void setPdfName(String pdfName) {
		this.pdfName = pdfName;
	}
	public String getPdfPath() {
		return pdfPath;
	}
	public void setPdfPath(String pdfPath) {
		this.pdfPath = pdfPath;
	}
	public String getDocxPath() {
		return docxPath;
	}
	public void setDocxPath(String docxPath) {
		this.docxPath = docxPath;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public LocalDateTime getUploadedAt() {
		return uploadedAt;
	}
	public void setUploadedAt(LocalDateTime uploadedAt) {
		this.uploadedAt = uploadedAt;
	}
	public LocalDateTime getCompletedAt() {
		return completedAt;
	}
	public void setCompletedAt(LocalDateTime completedAt) {
		this.completedAt = completedAt;
	}

    public Task() {}
    public Task(int id, int userId, String pdfName, String pdfPath, String docxPath, String status, LocalDateTime uploadedAt, LocalDateTime completedAt) {
        this.id = id;
        this.userId = userId;
        this.pdfName = pdfName;
        this.pdfPath = pdfPath;
        this.docxPath = docxPath;
        this.status = status;
        this.uploadedAt = uploadedAt;
        this.completedAt = completedAt;
    }
}
