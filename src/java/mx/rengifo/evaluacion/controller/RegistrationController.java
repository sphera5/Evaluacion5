package mx.rengifo.evaluacion.controller;

import mx.rengifo.evaluacion.util.DatabaseConnectionFactory;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

/**
 * Servlet implementation class RegistrationController
 */
@WebServlet("/checkRegister")
public class RegistrationController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegistrationController() {
        super();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String exam = request.getParameter("exam");

        boolean valido = validateParameters(username, email, password, exam);

        if (valido) {
            Connection con = DatabaseConnectionFactory.createConnection();
            PreparedStatement ps = null;
            try {
                con.setAutoCommit(false);
                ps = con.prepareStatement("INSERT INTO users (`username`,`email`,`password`,`exam`,`calificacion`) values (?,?,?,?,?)");
                ps.setString(1, username);
                ps.setString(2, email);
                ps.setString(3, password);
                ps.setString(4, exam);
                ps.setString(5, null);
                ps.executeUpdate();
                con.commit();

            } catch (SQLException sqe) {
                sqe.printStackTrace();
                System.out.println("Error : Mientras se insertaba el registro en la base de datos");
            } finally {
                try {
                    con.close();
                } catch (SQLException se) {
                    System.out.println("Error : Mientras se cerraba la conexion");
                }
            }

            request.setAttribute("newUser", username);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsps/regSuccess.jsp");
            dispatcher.forward(request, response);
        } else {
            System.out.println("parametros invalidos");
            request.setAttribute("errorMessage", "<br><br>Todos los campos son obligatorios.<br>Debe utilizar un correo valido y seleccionar un rol.");
            RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsps/register.jsp");
            rd.forward(request, response);
        }

    }

    /**
     * Valida los parametros para el registro
     * @param username
     * @param email
     * @param password
     * @param exam
     * @return 
     */
    private boolean validateParameters(String username, String email, String password, String exam) {
        boolean valido = false;
        if (StringUtils.isNotBlank(username) 
                && StringUtils.isNotBlank(email) 
                && StringUtils.isNotBlank(password) 
                && StringUtils.contains(email, "@") && StringUtils.contains(email, ".") 
                && StringUtils.isNotBlank(exam)) {
            valido = true;
        }
        return valido;
    }

}