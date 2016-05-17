package com.tim.dzenlabtest;

import com.tim.dzenlabtest.entity.TokenAudit;
import com.tim.dzenlabtest.entity.User;

import javax.ejb.EJB;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by Mikhail Titov on 16.05.16.
 */
@WebServlet(urlPatterns = {"/ui/*"})
public class MainServlet extends HttpServlet {
    @EJB
    private DataManager dataManager;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log("Processing request from client: "+req.getPathInfo());
        switch(req.getPathInfo()) {
            case "/create-test-users": handleCreateTestUsers(req, resp); break;
            case "/get-users": handleGetUsers(req, resp); break;
            case "/get-token-history": handleGetTokenHistory(req, resp); break;
        }
    }

    private void handleGetTokenHistory(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_OK);
        List<TokenAudit> tokens = dataManager.listTokenAudit();
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        if (tokens!=null && !tokens.isEmpty())
            for (TokenAudit tokenAudit : tokens)
                arrayBuilder.add(tokenAudit.toJson());
        resp.getWriter().write(arrayBuilder.build().toString());
    }

    private void handleGetUsers(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_OK);
        List<User> users = dataManager.listUsers();
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        if (users!=null && !users.isEmpty())
            for (User user: users)
                arrayBuilder.add(user.toJson());
        resp.getWriter().write(arrayBuilder.build().toString());
    }

    private void handleCreateTestUsers(HttpServletRequest req, HttpServletResponse resp) {
        log("Creating test users");
        dataManager.createTestUsers();
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
