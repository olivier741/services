/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.xml.user_listener;

import java.util.ArrayList;
import java.util.List;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 *
 * @author olivier.tatsinkou
 */
@Root(name="sender_user")
public class Sender_User_Listener {
    
    @ElementList(inline=true, name="user_connection" ,required = true)
    private List<Send_User_Connection> listUser_Connection = new ArrayList<Send_User_Connection>();

    public Sender_User_Listener(@ElementList(inline=true, name="user_connection" ,required = true) List<Send_User_Connection> listUser_Connection) {
        this.listUser_Connection = listUser_Connection;     
    }

    public List<Send_User_Connection> getListUser_Connection() {
        return listUser_Connection;
    }

    public  Send_User_Connection selectUser_con(String user){
        Send_User_Connection result = null;
        if (listUser_Connection != null){
            for (Send_User_Connection e : listUser_Connection){
                if (e.getUser().equals(user)){
                    result = e;
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "User_Listener{" + "listUser_Connection=" + listUser_Connection + '}';
    }
    
    
    
    
}
