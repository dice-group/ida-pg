package mypackage;

public class Address {
	
	private String location;

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@Override
	public String toString() {
		return "Address [location=" + location + "]";
	}
	

}
