package controller;
import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.bo.UserBO;
import util.Validator;

@WebServlet("/forgotPassword")
public class ForgotPasswordController extends HttpServlet {
     private final UserBO userBO = new UserBO();

     @Override
     protected void doGet(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
          // Redirect GET requests to forgotPassword.jsp
          response.sendRedirect("forgotPassword.jsp");
     }

     @Override
     protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
          request.setCharacterEncoding("UTF-8");
          response.setCharacterEncoding("UTF-8");
          
          String email = request.getParameter("email");
          String username = request.getParameter("username");
          String newPassword = request.getParameter("password");
          String confirmPassword = request.getParameter("confirmPassword");

          if (Validator.isPasswordResetDataInvalid(email, username, newPassword, confirmPassword)) {
               response.sendRedirect("forgotPassword.jsp?error=invalid");
               return;
          }

          if (!Validator.isValidEmail(email)) {
               response.sendRedirect("forgotPassword.jsp?error=invalid");
               return; 
          }

          if (!userBO.isAccountExist(email, username)) {
               response.sendRedirect("forgotPassword.jsp?error=notExist"); //Email/Username không tồn tại
               return;
          }

          boolean success = userBO.forgotPassword(email, username, newPassword);
          if (success) {
               response.sendRedirect("login.jsp?success=2");
          } else {
               response.sendRedirect("forgotPassword.jsp?error=exists"); //Lỗi khi đổi mật khẩu
          }
     }
}
