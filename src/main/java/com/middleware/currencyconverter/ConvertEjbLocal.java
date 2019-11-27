/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.middleware.currencyconverter;

import javax.ejb.Local;
import org.json.JSONArray;

/**
 *
 * @author Joker
 */
@Local
public interface ConvertEjbLocal {
    /*Getter and setters start*/
    public void setAmount(double amount);

    public void setCustomer(String customer);

    public void setSelected_currency(int selected_currency);

    public double getResult();
    /*Getter and setters end*/
    public void convertAndSave();
    public JSONArray getResutls();
}
