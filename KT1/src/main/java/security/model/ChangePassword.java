package security.model;

public class ChangePassword {
	
	private String email;
	private String newPass;
	private String confirmPass;
	
	public ChangePassword() {}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNewPass() {
		return newPass;
	}

	public void setNewPass(String newPass) {
		this.newPass = newPass;
	}

	public String getConfirmPass() {
		return confirmPass;
	}

	public void setConfirmPass(String confirmPass) {
		this.confirmPass = confirmPass;
	}

	@Override
	public String toString() {
		return "ChangePassword [email=" + email + ", newPass=" + newPass + ", confirmPass=" + confirmPass + "]";
	}
	
	
}
