package myprog;

import org.springframework.stereotype.Component;

@Component("chutiya")

public class Language {
	
	public String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String tostring()
	{
		return"Language [name=" + name + "]" ;
	}

}
