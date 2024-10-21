import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/register")
public class registration extends HttpServlet {
	private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String firstName = request.getParameter("first_name");
        String lastName = request.getParameter("last_name");
        String dob = request.getParameter("dob");
        String password = request.getParameter("password");

        try {
            Connection con = DatabaseConnection.initializeDatabase();

            // Check if username already exists
            PreparedStatement checkUser = con.prepareStatement("SELECT * FROM users WHERE username = ?");
            checkUser.setString(1, username);
            ResultSet rs = checkUser.executeQuery();

            if (rs.next()) {
                // Username already exists
                request.setAttribute("error", "Username already exists.");
                request.getRequestDispatcher("/register.jsp").forward(request, response);
            } else {
                // Insert new user
                PreparedStatement pst = con.prepareStatement("INSERT INTO users(username, first_name, last_name, dob, password) VALUES(?, ?, ?, ?, ?)");
                pst.setString(1, username);
                pst.setString(2, firstName);
                pst.setString(3, lastName);
                pst.setString(4, dob);
                pst.setString(5, password);

                int result = pst.executeUpdate();
                if (result > 0) {
                    response.sendRedirect("login.jsp");
                } 
                else {
                      request.setAttribute("error", "Registration failed, please try again.");
                      request.getRequestDispatcher("/register.jsp").forward(request, response);
                  }
                pst.close();
            }

            rs.close();
            checkUser.close();
            con.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
