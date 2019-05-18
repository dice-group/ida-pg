package upb.ida.rest;

public class UserData {
	private String name;
	private String username,password,newpassword;
	

	public String getName() {
		return name;
	}


	public String getNewpassword() {
		return newpassword;
	}


	public void setNewpassword(String newpassword) {
		this.newpassword = newpassword;
	}


	public void setName(String name) {
		this.name = name;
	}

	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}


	
}
