package myprog;

import org.springframework.stereotype.Component;

@Component("efg")
public class Developer {
	
	private Language language;
	
	public Developer(Language language)
	{
		this.language= language;
	}
	
	public String toString()
	{
		return "Developer [language=" + language + "]" ;
	}

}
