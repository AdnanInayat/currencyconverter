package com.middleware.currencyconverter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import javax.ejb.Stateless;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Joker
 */
@Stateless
public class ConvertEjb implements ConvertEjbRemote, ConvertEjbLocal {
    private String customer;
    private double amount;
    private int selected_currency;
    private double result;
    private Connection con;
    
    @Override
    public void setCustomer(String customer) {
        this.customer = customer;
    }

    @Override
    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public void setSelected_currency(int selected_currency) {
        this.selected_currency = selected_currency;
    }
    @Override
    public double getResult() {
        return result;
    }
    /**
     *
     */
    @Override
    public void convertAndSave(){
        calculate();
        try {
            con = getcon();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM tuser where username='" + customer + "'");
            if(rs.next()) {
                PreparedStatement ps = con
                        .prepareStatement("insert into tconversions(amount,selected_currency,converted_amount,userid) values(?,?,?,?)");
                ps.setString(1, String.valueOf(amount));
                ps.setInt(2, selected_currency);
                ps.setDouble(3, result);
                ps.setInt(4, rs.getInt("id"));
                ps.executeUpdate();
            }
            else{
                PreparedStatement ps = con
                        .prepareStatement("insert into tuser(username) values (?)", Statement.RETURN_GENERATED_KEYS);
                ps.setString(1,customer);
                ps.executeUpdate();
                ResultSet rs2 = ps.getGeneratedKeys();
                if(rs2.next())
                {
                    int lastInsertedId = rs2.getInt(1);
                    PreparedStatement ps2 = con
                        .prepareStatement("insert into tconversions(amount,selected_currency,converted_amount,userid) values(?,?,?,?)");
                    ps2.setString(1, String.valueOf(amount));
                    ps2.setInt(2, selected_currency);
                    ps2.setDouble(3, result);
                    ps2.setInt(4, lastInsertedId);
                    ps2.executeUpdate();
                }
            }
            con.close();
        } catch (SQLException e) {
        }
    }
    /**
     *
     */
    private void calculate() {
        if (selected_currency == 0) {
            result = amount * 1.11;
        }
        if (selected_currency == 1) {
            result = amount * 0.86;
        }
        if (selected_currency == 2) {
            result = amount * 172;
        }
    }

    @Override
    public JSONArray getResutls() {
        try {
            con = getcon();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT tc.id as id, tc.amount, tc.selected_currency, tc.converted_amount, tu.id FROM tconversions tc join tuser tu on tc.userid = tu.id\n" +
                    "where tu.username ='" + customer + "'");
            JSONArray json = new JSONArray();
            ResultSetMetaData rsmd = rs.getMetaData();

            while(rs.next()) {
                JSONObject obj = new JSONObject();
                int numColumns = rsmd.getColumnCount();
                for (int i=numColumns; i>=1; i--) {
                    String column_name = rsmd.getColumnName(i);
                    obj.put(column_name, rs.getObject(column_name));
                }
                json.put(obj);
            }
            con.close();
            return json;
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
    public Connection getcon(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            String unicode="useSSL=false&autoReconnect=true&useUnicode=yes&characterEncoding=UTF-8";
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/converter?"+unicode, "root", "root");
        }catch(Exception ex){
            System.out.println(ex.getMessage());
            System.out.println("couldn't connect!");
            throw new RuntimeException(ex);
        }
    }
}
