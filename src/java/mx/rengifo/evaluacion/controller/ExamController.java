package mx.rengifo.evaluacion.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import mx.rengifo.evaluacion.Exam;
import mx.rengifo.evaluacion.QuizQuestion;
import mx.rengifo.evaluacion.util.Constante;
import mx.rengifo.evaluacion.util.Message;
import javax.servlet.RequestDispatcher;

/**
 * Servlet implementation class ExamController
 */
@WebServlet(urlPatterns = { "/exam", "/exam2", "/exam3" })
public class ExamController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    
    /** Accion */
    public enum Accion {SIGUIENTE, ANTERIOR, FIN}; 

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        boolean finish = false;
        
        String applicationContextPath = request.getContextPath();
        if(request.getSession().getAttribute("background")==null) {
            request.getSession().setAttribute("background", Constante.BACKGROUND);
        }

        HttpSession session = request.getSession();
        try {
            if (session.getAttribute("currentExam") == null) {
                session = request.getSession();
                String selectedExam = (String) request.getSession().getAttribute("exam");
                System.out.println("Setting Exam " + selectedExam);
                Exam newExam = new Exam(selectedExam);
                session.setAttribute("currentExam", newExam);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss a");
                Date date = new Date();
                String started = dateFormat.format(date);
                session.setAttribute("started", started);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        String username = (String) session.getAttribute("user");
        System.out.println("username = [" + username + "]");

        Exam exam = (Exam) request.getSession().getAttribute("currentExam");

        if (exam.currentQuestion == 0) {
            exam.setQuestion(exam.currentQuestion);
            QuizQuestion q = exam.questionList.get(exam.currentQuestion);
            session.setAttribute("quest", q);
        }

        String action = request.getParameter("action");
        
        if (request.getRequestURI().equals(applicationContextPath + "/exam")) {
            
            System.out.println("/exam");
            
            String radio = request.getParameter("answer");
            int selectedRadio = -1;
            exam.selections.put(exam.currentQuestion, selectedRadio);
            if ("1".equals(radio)) {
                selectedRadio = 1;
                exam.selections.put(exam.currentQuestion, selectedRadio);
                System.out.println(Message.PRINT_SELECTED + selectedRadio);
            } else if ("2".equals(radio)) {
                selectedRadio = 2;
                exam.selections.put(exam.currentQuestion, selectedRadio);
                System.out.println(Message.PRINT_SELECTED + selectedRadio);
            } else if ("3".equals(radio)) {
                selectedRadio = 3;
                exam.selections.put(exam.currentQuestion, selectedRadio);
                System.out.println(Message.PRINT_SELECTED + selectedRadio);
            } else if ("4".equals(radio)) {
                selectedRadio = 4;
                exam.selections.put(exam.currentQuestion, selectedRadio);
                System.out.println(Message.PRINT_SELECTED + selectedRadio);
            }

            if (null != action) {
                Accion a  = Accion.valueOf(action);
                switch (a) {
                    case SIGUIENTE:{
                        System.out.println(Message.CLICK_BUTTON + "SIGUIENTE");
                        exam.currentQuestion++;
                        exam.setQuestion(exam.currentQuestion);
                        QuizQuestion q = exam.questionList.get(exam.currentQuestion);
                        session.setAttribute("quest", q);
                            break;
                        }
                    case ANTERIOR:{
                        System.out.println(Message.CLICK_BUTTON + "ANTERIOR");
                        exam.currentQuestion--;
                        exam.setQuestion(exam.currentQuestion);
                        QuizQuestion q = exam.questionList.get(exam.currentQuestion);
                        session.setAttribute("quest", q);
                            break;
                        }
                    case FIN:
                        System.out.println(Message.CLICK_BUTTON + "FIN");
                        finish = true;
                        int result = exam.calculateResult(exam, username);
                        request.setAttribute("result", result);
                        //request.getSession().setAttribute("currentExam",null);
                        request.getRequestDispatcher("/WEB-INF/jsps/result.jsp").forward(request, response);
                        break;
                    default:
                        break;
                }
            }

            if (finish != true) {
                request.getRequestDispatcher("/WEB-INF/jsps/exam.jsp").forward(request, response);
            }

        } else if(request.getRequestURI().equals(applicationContextPath + "/exam2")){
            System.out.println("/exam2");
            request.getRequestDispatcher("/WEB-INF/jsps/result2.jsp").forward(request, response);
        }else if (request.getRequestURI().equals(applicationContextPath + "/exam3")) {
            System.out.println("/exam3");
            request.getSession().setAttribute("currentExam",null);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsps/result3.jsp");
            dispatcher.forward(request, response);
        }

    }

}