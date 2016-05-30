package ru.chatik.web.servlets;

import org.json.simple.JSONObject;
import ru.chatik.web.User;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by andrey on 22.05.2016.
 */
public class ChatServlet extends javax.servlet.http.HttpServlet {
    private static final java.lang.String DB_CONNECTION = "jdbc:oracle:thin:@students.dce.ifmo.ru:1521/xe";
    private static final String DB_USER = "s191978";
    private static final String DB_PASSWORD = "igmaj4";

    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        JSONObject answer = new JSONObject();

        if (request.getParameter("action").equals("auth")) {
            if (request.getParameter("name") != null && !request.getParameter("name").equals("") && request.getParameter("pass") != null && !request.getParameter("pass").equals("")) {
                String name = request.getParameter("name");
                String pass = request.getParameter("pass");
                User user = null;
                Connection dbConnection = null;
                Statement statement = null;

                try {
                    dbConnection = connectToDB();
                    statement = dbConnection.createStatement();
                    ResultSet rs = statement.executeQuery("SELECT * FROM CHATIK_USER WHERE NAME = '" + name + "' AND PASS = '" + pass + "'");

                    // И если что то было получено, то пользователь зарегистрирован
                    if (rs.next()) {
                        user = new User();
                        user.setId(rs.getLong("ID"));
                        user.setName(rs.getString("NAME"));
                        user.setPass(rs.getString("PASS"));
                        user.setLastactivity(new Timestamp(System.currentTimeMillis()));
                    } else {
                        //пользователь с таким именем не зарегистрирован
                        answer.put("auth","failed");
                        answer.put("cause","user_not_reg");
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }

                try {
                    dbConnection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                if (user != null) {
                    //setting session to expiry in 1 min
                    HttpSession session = request.getSession();
                    session.setMaxInactiveInterval(1 * 60);
                    session.setAttribute("authorization_pass", true);
                    session.setAttribute("user", user);
                    answer.put("auth","successful");
                    //response.sendRedirect("chat.html");
                }

            } else {
                //плохие входные данные
                answer.put("auth","failed");
                answer.put("cause","bad_input");
            }
        }

        if (request.getParameter("action").equals("deauth")) {
            HttpSession session = request.getSession();
            session.invalidate();
        }

        if (request.getParameter("action").equals("reg")) {
            if (request.getParameter("name") != null && !request.getParameter("name").equals("") && request.getParameter("pass") != null && !request.getParameter("pass").equals("")) {
                String name = request.getParameter("name");
                String pass = request.getParameter("pass");
                User user = null;
                Connection dbConnection = null;
                Statement statement = null;

                try {
                    dbConnection = connectToDB();
                    statement = dbConnection.createStatement();
                    ResultSet rs = statement.executeQuery("SELECT * FROM CHATIK_USER WHERE NAME = '" + name + "'");

                    // И если что то было получено, то пользователь с таким именем уже зарегистрирован
                    if (rs.next()) {
                        //имя занято
                        answer.put("reg","failed");
                        answer.put("cause","name_is_taken");
                    } else {
                        //записываем нового челика в базу
                        statement = dbConnection.createStatement();
                        rs = statement.executeQuery("INSERT INTO CHATIK_USER (id,name,pass) VALUES (chatik_user_seq.nextval,'" + name + "','" + pass + "')");
                        answer.put("reg","successful");
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }

                try {
                    dbConnection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            } else {
                //плохие входные данные
                answer.put("reg","failed");
                answer.put("cause","bad_input");
            }
        }

        out.println(answer);
        out.close();
    }

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {

        // Отправляем ответ клиенту в формате JSON
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        JSONObject answer = new JSONObject();

        if (request.getParameter("action").equals("getusers")) {
            List<User> list = getUsers();
            for (User u : list) {
                answer.put(u.getId(),u.getName());
            }
        }

        if (request.getParameter("action").equals("getmessages")) {
            HttpSession session = request.getSession();
            if ((boolean)session.getAttribute("authorization_pass")) {
                User user = (User) session.getAttribute("user");
                Timestamp lastdate = user.getLastactivity();
                Timestamp curdate = new Timestamp(System.currentTimeMillis());
                SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                Connection dbConnection = null;
                //PreparedStatement statement = null;
                Statement statement = null;

                try {
                    dbConnection = connectToDB();
                    /*
                    statement = dbConnection.prepareStatement("SELECT me.ID \"ID\", us.NAME \"USER\", me.TEXT \"TEXT\" FROM CHATIK_MESSAGE me, CHATIK_USER us WHERE me.CHATIK_USER = us.ID AND CREATE_DATE >= ?");
                    statement.setTimestamp(1, lastdate);
                    ResultSet rs = statement.executeQuery();
                    */
                    statement = dbConnection.createStatement();
                    ResultSet rs = statement.executeQuery("SELECT me.ID \"ID\", us.NAME \"USER\", me.TEXT \"TEXT\" FROM CHATIK_MESSAGE me, CHATIK_USER us WHERE me.CHATIK_USER = us.ID AND CREATE_DATE >= TO_DATE('" + outputDateFormat.format(lastdate) + "','YYYY-MM-DD HH24:MI:SS')");
                    user.setLastactivity(curdate);

                    while (rs.next()) {
                        JSONObject message = new JSONObject();
                        message.put("user",rs.getString("USER"));
                        message.put("text",rs.getString("TEXT"));
                        answer.put(rs.getString("ID"), message);
                    }
                    //TEST OUTPUT
                    /*
                    JSONObject message = new JSONObject();
                    message.put("user","USER_NAME");
                    message.put("text","MESSAGE_TEXT");
                    answer.put(77, message);
                    */

                } catch (SQLException e) {
                    e.printStackTrace();
                }

                try {
                    dbConnection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        if (request.getParameter("action").equals("checkauth")) {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            if (user == null) {
                answer.put("auth","off");
            } else {
                answer.put("auth","on");
                answer.put("name",user.getName());
            }
        }

        if (request.getParameter("action").equals("sendmessage") && request.getParameter("text") != null) {
            HttpSession session = request.getSession();
            if ((boolean)session.getAttribute("authorization_pass")) {
                User user = (User) session.getAttribute("user");
                SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Timestamp curdate = new Timestamp(System.currentTimeMillis());

                Connection dbConnection = null;
                Statement statement = null;
                //PreparedStatement statement;

                try {
                    dbConnection = connectToDB();
                    //отправляем сообщение
                    statement = dbConnection.createStatement();
                    statement.executeQuery("INSERT INTO CHATIK_MESSAGE (id,text,chatik_user,create_date) VALUES (chatik_message_seq.nextval,'"+request.getParameter("text")+"'," + user.getId() + ", TO_DATE('" + outputDateFormat.format(new Date(System.currentTimeMillis())) + "','YYYY-MM-DD HH24:MI:SS'))");
                    /*
                    statement = dbConnection.prepareStatement("INSERT INTO CHATIK_MESSAGE (id,text,chatik_user,create_date) VALUES (chatik_message_seq.nextval,?,?,?)");
                    statement.setString(1,request.getParameter("text"));
                    statement.setInt(2,(int)user.getId());
                    statement.setTimestamp(3,curdate);
                    statement.executeUpdate();
                    */
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                try {
                    dbConnection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        out.println(answer);
        out.close();
    }

    private Connection connectToDB() {
        Connection dbConnection = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your MySQL JDBC Driver?");
            e.printStackTrace();
        }
        try {
            dbConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
            return dbConnection;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return dbConnection;
    }

    private List<User> getUsers() {
        List<User> list = (List<User>) getServletContext().getAttribute("users");
        return list;
    }

}