/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.xml.ussd;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 *
 * @author olivier.tatsinkou
 */
@Root(name = "menu")
public class Menu {

    @Attribute(name = "input", required = true)
    private String input;

    @Attribute(name = "status", required = true)
    private String status;

    @Element(name = "resp", required = false)
    private String resp;

    @Element(name = "action", required = false)
    private String action;

    @Element(name = "desc", required = false)
    private String desc;

    @Element(name = "next_menu", required = false)
    private NextMenu nextMenu;

    public Menu(@Attribute(name = "input", required = true) String input,
            @Attribute(name = "status", required = true) String status,
            @Element(name = "resp", required = false) String resp,
            @Element(name = "action", required = false) String action,
            @Element(name = "desc", required = false) String desc,
            @Element(name = "next_menu", required = false) NextMenu nextMenu) {
        this.input = input;
        this.status = status;
        this.resp = resp;
        this.action = action;
        this.desc = desc;
        this.nextMenu = nextMenu;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResp() {
        return resp;
    }

    public void setResp(String resp) {
        this.resp = resp;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public NextMenu getNextMenu() {
        return nextMenu;
    }

    public void setNextMenu(NextMenu nextMenu) {
        this.nextMenu = nextMenu;
    }

    
    
}
