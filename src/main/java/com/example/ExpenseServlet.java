package com.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/addExpense")
public class ExpenseServlet extends HttpServlet {

    private static final String DB_URL =
            "jdbc:mariadb://localhost:3306/expense_tracker";

    private static final String DB_USER = "expenseapp";
    private static final String DB_PASSWORD = "Expense@123";

    @Override
    public void init() throws ServletException {
       try {
           Class.forName("org.mariadb.jdbc.Driver");
       } catch (ClassNotFoundException e) {
        throw new ServletException("MariaDB JDBC Driver not found", e);
       }
    }
    protected void doPost(HttpServletRequest request,
                           HttpServletResponse response)
            throws ServletException, IOException {

        String category = request.getParameter("category");
        String amount = request.getParameter("amount");
        String date = request.getParameter("date");
        String description = request.getParameter("description");

        String sql = "INSERT INTO expenses " +
                "(category, amount, expense_date, description) " +
                "VALUES (?, ?, ?, ?)";

        try (
            Connection connection =
                    DriverManager.getConnection(
                            DB_URL, DB_USER, DB_PASSWORD);

            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setString(1, category);
            statement.setDouble(2, Double.parseDouble(amount));
            statement.setDate(3, Date.valueOf(date));
            statement.setString(4, description);

            statement.executeUpdate();

            response.sendRedirect("addExpense");

        } catch (Exception e) {

            response.setContentType("text/html");

            PrintWriter out = response.getWriter();

            out.println("<h1>Error saving expense</h1>");
            out.println("<pre>");
            e.printStackTrace(out);
            out.println("</pre>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        out.println("<html>");
        out.println("<head>");
        out.println("<title>Saved Expenses</title>");
        out.println("</head>");

        out.println("<body>");

        out.println("<h1>Expense & Food Tracker</h1>");
        out.println("<h2>Saved Expenses</h2>");

        String sql =
                "SELECT id, category, amount, expense_date, description " +
                "FROM expenses ORDER BY id DESC";

        try (
            Connection connection =
                    DriverManager.getConnection(
                            DB_URL, DB_USER, DB_PASSWORD);

            Statement statement = connection.createStatement();

            ResultSet result =
                    statement.executeQuery(sql)
        ) {

            out.println("<table border='1' cellpadding='10'>");

            out.println("<tr>");
            out.println("<th>ID</th>");
            out.println("<th>Category</th>");
            out.println("<th>Amount</th>");
            out.println("<th>Date</th>");
            out.println("<th>Description</th>");
            out.println("</tr>");

            while (result.next()) {

                out.println("<tr>");

                out.println("<td>" +
                        result.getInt("id") +
                        "</td>");

                out.println("<td>" +
                        result.getString("category") +
                        "</td>");

                out.println("<td>₹" +
                        result.getBigDecimal("amount") +
                        "</td>");

                out.println("<td>" +
                        result.getDate("expense_date") +
                        "</td>");

                out.println("<td>" +
                        result.getString("description") +
                        "</td>");

                out.println("</tr>");
            }

            out.println("</table>");

        } catch (Exception e) {

            out.println("<h2>Database Error</h2>");
            out.println("<pre>");
            e.printStackTrace(out);
            out.println("</pre>");
        }

        out.println("<br>");
        out.println("<a href='index.html'>Add Another Expense</a>");

        out.println("</body>");
        out.println("</html>");
    }
}
