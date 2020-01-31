/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.xml.ussd;

import java.util.ArrayList;
import java.util.List;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 *
 * @author olivier.tatsinkou
 */
@Root(name = "next_menu")
public class NextMenu {
    @ElementList(inline = true, name = "menu", required = true)
    private List<Menu> listMenu = new ArrayList<Menu>();

    public NextMenu(@ElementList(inline = true, name = "menu", required = true) List<Menu> listMenu) {
        this.listMenu=listMenu;
    }

    public List<Menu> getListMenu() {
        return listMenu;
    }

    public void setListMenu(List<Menu> listMenu) {
        this.listMenu = listMenu;
    }
    
    
}
