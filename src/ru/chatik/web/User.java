package ru.chatik.web;

import org.json.simple.JSONObject;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by andrey on 22.05.2016.
 */
public class User implements HttpSessionBindingListener {
    public static final long AFK_TIME = 5000;
    private long id;
    private String name;
    private String pass;
    private Date lastactivity;

    public User(){};

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public Date getLastactivity() {
        return lastactivity;
    }

    public void setLastactivity(Date lastactivity) {
        this.lastactivity = lastactivity;
    }

    @Override
    public void valueBound(HttpSessionBindingEvent httpSessionBindingEvent) {
        List<User> logins = (List<User>) httpSessionBindingEvent.getSession().getServletContext().getAttribute("users");
        if (logins == null) {
            logins = new ArrayList<User>();
            httpSessionBindingEvent.getSession().getServletContext().setAttribute("users",logins);
        }
        logins.add(this);
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent httpSessionBindingEvent) {
        List<User> logins = (List<User>) httpSessionBindingEvent.getSession().getServletContext().getAttribute("users");
        logins.remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (!o.getClass().equals(this.getClass())) {
            return false;
        }
        User fUser = (User)o;
        if (fUser.getId() != this.getId()) {
            return false;
        }
        if (!fUser.getName().equals(this.getName())) {
            return false;
        }
        if (!fUser.getPass().equals(this.getPass())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 37;
        hash = hash*17 + (id == 0 ? 0 : (int)id);
        hash = hash*17 + (name == null ? 0 : name.hashCode());
        hash = hash*17 + (pass == null ? 0 : pass.hashCode());
        return hash;
    }
}
