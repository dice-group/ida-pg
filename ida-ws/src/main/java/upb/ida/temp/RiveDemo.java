package upb.ida.temp;

import com.rivescript.RiveScript;

public class RiveDemo {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// Create a new bot with the default settings.
		RiveScript bot = new RiveScript();
	
		// Load an individual file.
		bot.loadFile("C:\\Users\\Bets\\dice-ida\\ida-rivescript\\ida-chatbot.rive");
		
		// Sort the replies after loading them!
		bot.sortReplies();
		bot.setSubroutine("sayname", new ExampleMacro());
		System.out.println(bot.reply("user", "my name is nikit"));
		System.out.println(bot.reply("user", "what is my name?"));
		// say my name to me in reverse
		System.out.println(bot.reply("user", "say my name to me in reverse"));
	}

	
}
