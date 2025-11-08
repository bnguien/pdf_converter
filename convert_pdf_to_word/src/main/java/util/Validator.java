package util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {
     private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
     
     private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

     private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
     private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);
     
     public static boolean isValidEmail(String email) {
          if (email == null) return false;
          Matcher matcher = EMAIL_PATTERN.matcher(email);
          return matcher.matches();
     }

     public static boolean isStrongPassword(String password) {
          if (password == null) return false;
          Matcher matcher = PASSWORD_PATTERN.matcher(password);
          return matcher.matches();
     }

     public static boolean isPasswordResetDataInvalid(String email, String username, String newPassword, String confirmPassword) {
          return    email == null || email.trim().isEmpty() || 
                    newPassword == null || newPassword.trim().isEmpty() ||
                    confirmPassword == null || confirmPassword.trim().isEmpty() ||
                    !confirmPassword.equals(newPassword);
     }

     public static boolean isInvalidPDFFile(String contentType, String fileName) {
          return    contentType == null || !contentType.equals("application/pdf") || 
                    !fileName.toLowerCase().endsWith(".pdf");
     }
     
     private Validator() {
     }
}
