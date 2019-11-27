package com.middleware.currencyconverter;

import java.io.IOException;
import java.io.PrintWriter;
import javax.ejb.EJB;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Joker
 */
@WebServlet(urlPatterns = {"/Converter", "/converter"})
public class Converter extends HttpServlet {
    @EJB
    private ConvertEjbRemote convert;
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        setAccessControlHeaders(response);
        double amount;
        String customer;
        int selected_currency;
        try (PrintWriter out = response.getWriter()) {
            amount = Double.parseDouble(request.getParameter("amount"));
            customer = request.getParameter("customer");
            selected_currency = Integer.parseInt(request.getParameter("selected_currency"));
            convert.setAmount(amount);
            convert.setCustomer(customer);
            convert.setSelected_currency(selected_currency);
            convert.convertAndSave();
            out.print(convert.getResult()); 
        }
    }

    private void setAccessControlHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Methods", "x-requested-with");
    }
    private ConvertEjbLocal lookupConvertEjbBeanLocal() {
        try {
            Context c = new InitialContext();
            System.out.println("Bean ");
            return (ConvertEjbLocal) c.lookup("java:global/currencyconverter/ConvertEjb!servlet.ConvertEjbLocal");
        } catch (NamingException ne) {
            throw new RuntimeException(ne);
        }
    }
}
