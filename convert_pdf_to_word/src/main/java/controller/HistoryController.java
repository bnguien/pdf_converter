package controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/history")
public class HistoryController extends HttpServlet {
     @Override
     protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
          response.sendRedirect("history.jsp?message=conversion_started");
     }
     @Override
     protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
          doGet(request, response);
     }
}
