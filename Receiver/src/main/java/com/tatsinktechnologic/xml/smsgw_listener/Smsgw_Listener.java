/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.xml.smsgw_listener;

import java.util.ArrayList;
import java.util.List;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 *
 * @author olivier.tatsinkou
 */
@Root(name="smsgw_listener")
public class Smsgw_Listener {
    
    @ElementList(inline=true, name="smsgwClient" ,required = true) 
    private List<SmsgwClient> listSmsgwClient = new ArrayList<SmsgwClient>();

    public Smsgw_Listener( @ElementList(inline=true, name="smsgwClient" ,required = true) List<SmsgwClient> listSmsgwClient ) {
        this.listSmsgwClient=listSmsgwClient;
    }
    
    
    public  SmsgwClient selectsmsgw_user(String smsgw_user){
        SmsgwClient result = null;
        if (listSmsgwClient != null){
            for (SmsgwClient e : listSmsgwClient){
                if (e.getUser().equals(smsgw_user)){
                    result = e;
                    break;
                }
            }
        }
        return result;
    }

    
    
    
    
    
}
