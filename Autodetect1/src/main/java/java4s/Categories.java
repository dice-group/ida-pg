package java4s;
public class Categories {

	   private String name;
	   private Book bk;
	   
	   public Categories(Book bk)
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
		System.out.println("Categories name :"+name);
		System.out.println("Book name :"+bk.getBookname()+" and Book Price :"+bk.getBookprice());
	}
		
}