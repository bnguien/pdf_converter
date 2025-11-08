package model.enums;

public enum TaskStatus {
     UPLOADED,
     PENDING,
     PROCESSING,
     DONE,
     FAILED;
    
     public static TaskStatus fromString(String status) {
          for (TaskStatus s : TaskStatus.values()) {
               if (s.name().equalsIgnoreCase(status)) {
                    return s;
               }
          }
          throw new IllegalArgumentException("Unknown Task Status: " + status);
     }
}