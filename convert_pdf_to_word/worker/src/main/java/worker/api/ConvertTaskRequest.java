package worker.api;

public class ConvertTaskRequest {
    private Integer taskId;
    private String pdfPath;
    private String callbackUrl; // Optional: URL để callback khi hoàn thành

    public ConvertTaskRequest() {}

    public ConvertTaskRequest(Integer taskId, String pdfPath, String callbackUrl) {
        this.taskId = taskId;
        this.pdfPath = pdfPath;
        this.callbackUrl = callbackUrl;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getPdfPath() {
        return pdfPath;
    }

    public void setPdfPath(String pdfPath) {
        this.pdfPath = pdfPath;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }
}

