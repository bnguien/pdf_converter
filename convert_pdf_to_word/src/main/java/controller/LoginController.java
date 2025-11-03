package controller;
import java.io.IOException;
import java.util.Optional;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.bean.User;
import model.bo.UserBO;
import util.Validator;

@WebServlet("/login")
public class LoginController extends HttpServlet {
    private final UserBO userBO = new UserBO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirect GET requests to login.jsp
        response.sendRedirect("login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        if (email == null || email.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            response.sendRedirect("login.jsp?error=invalid");
            return;
        }

        if (!Validator.isValidEmail(email)) {
            response.sendRedirect("login.jsp?error=invalid");
            return; 
        }

        Optional<User> userOpt = userBO.login(email, password);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            HttpSession session = request.getSession();
            session.setAttribute("user_id", user.getId());
            session.setAttribute("user_email", user.getEmail());
            session.setAttribute("user_username", user.getUsername());
            session.setAttribute("user_role", user.getRole());
            response.sendRedirect("index.jsp?success=login");
        } else {
            response.sendRedirect("login.jsp?error=invalid");
        }
    }
}
