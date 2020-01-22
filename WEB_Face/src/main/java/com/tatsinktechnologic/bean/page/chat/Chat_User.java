package com.tatsinktechnologic.bean.page.chat;

import java.io.Serializable;

public class Chat_User implements Serializable{

	private static final long serialVersionUID = -7351729135012380019L;
	
	private String nickname;
	
	private String dateOfLogin;
	
	public Chat_User() {
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getDateOfLogin() {
		return dateOfLogin;
	}

	public void setDateOfLogin(String dateOfLogin) {
		this.dateOfLogin = dateOfLogin;
	}

	@Override
	public String toString() {
		return "User [nickname=" + nickname + "]";
	}

}
