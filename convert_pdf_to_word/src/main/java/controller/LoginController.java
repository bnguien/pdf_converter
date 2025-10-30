package controller;
import java.io.IOException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.bo.UserBO;
import model.bean.User;

@WebServlet("/login")
public class LoginController extends HttpServlet {
    private UserBO userBO = new UserBO();

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String username = request.getParameter("username");
        boolean success = userBO.login(email, password);

        if (success) {
            HttpSession session = request.getSession();
            session.setAttribute("user_email", email);
            User user = userBO.findUserByEmail(email);
            if (user != null) {
                session.setAttribute("user_username", user.getUsername());
            }
            response.sendRedirect("index.jsp");
        } else {
            response.sendRedirect("login.jsp?error=invalid");
        }
    }
}
