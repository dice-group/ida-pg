package upb.ida.temp;

import com.rivescript.macro.Subroutine;
import com.rivescript.util.StringUtils;

public class ExampleMacro implements Subroutine {
	public String call (com.rivescript.RiveScript rs, String[] args) {
		String message = StringUtils.join(args, " ");
		System.out.println("subroutine has been called!");
		// To get/set user variables for the user, you can use currentUser
		// to find their ID and then use the usual methods.
		String user = rs.currentUser();
		rs.setUservar(user, "java", "This variable was set by Java when you said 'reverse " + message + "'");
        
		// Reverse their message and return it.
		return new StringBuilder(message).reverse().toString();
	}
}