package com.mkyong.common;

public class Customer {
	private String custName;
	private Address address;
	// ...

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	@Override
	public String toString() {
		return "Customer [custName=" + custName + ", address=" + address + "]";
	}
	
	
	
}
