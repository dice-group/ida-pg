package myprog;

public class Catagories {
	
	private String name;
	private Book bk;
	public Catagories( Book bk)
	{
		this.bk=bk;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public void show()
	{
		System.out.println("name of the book is "+name);
		System.out.println("the name book is :"+bk.getName()+"the price is:"+bk.getPrice());
	}
}
