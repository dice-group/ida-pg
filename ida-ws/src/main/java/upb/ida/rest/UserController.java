package upb.ida.rest;

/**
 * Exposes CRUD REST controllers
 *
 * @author Deepak Garg
 */

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import upb.ida.bean.ResponseBean;
import upb.ida.constant.IDALiteral;
import upb.ida.dao.UserRepository;
import upb.ida.domains.User;

@Controller
@CrossOrigin(origins = "*", allowCredentials = "true")
@RequestMapping("/user")
public class UserController {

	private static String datasetName = "users";

	@Autowired
	private ResponseBean responseBean;

	@Autowired
	private UserRepository userRepository;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ResponseBody
	public ResponseBean listUsers() {
		try {
			String jsonOutput = userRepository.listAllUsers(datasetName);
			Map<String, Object> returnMap = new HashMap<>();
			returnMap.put("users", jsonOutput);
			responseBean.setPayload(returnMap);
		} catch (Exception ex) {
			responseBean.setErrMsg("Users list could not be fetched.");
			ex.printStackTrace();
		}
		return responseBean;
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	@ResponseBody
	public ResponseBean updateUser(@RequestBody final User updatedrecord) throws NoSuchAlgorithmException {
		if (updatedrecord == null) {
			responseBean.setErrCode(IDALiteral.FAILURE_UPDATEUSER);
			return responseBean;
		} else {
			try {
				User oldRecord = userRepository.getUserByUsername(datasetName, updatedrecord.getUsername());
				if (oldRecord == null) {
					responseBean.setErrMsg("user not found");
					return responseBean;
				}
				updatedrecord.setUsername(oldRecord.getUsername());
				updatedrecord.setPassword(oldRecord.getPassword());
				if (userRepository.updateUser(datasetName, oldRecord, "DELETE DATA", false) == IDALiteral.RESP_PASS_ROUTINE && userRepository.updateUser("users", updatedrecord, "INSERT DATA", false) == IDALiteral.RESP_PASS_ROUTINE) {
					Map<String, Object> returnMap = new HashMap<String, Object>();
					returnMap.put("updatedUser", updatedrecord.getUsername());
					responseBean.setPayload(returnMap);
				} else {
					responseBean.setErrMsg("User details could not be updated");
				}
			} catch (Exception ex) {
				responseBean.setErrMsg("User details could not be updated");
				ex.printStackTrace();
			}
		}
		return responseBean;
	}

	@RequestMapping(value = "/new", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public ResponseBean createNewUser(@RequestBody final User record) throws Exception {

		if (record == null) {
			responseBean.setErrMsg("Invalid Input");
			return responseBean;
		}
		try {
			if (userRepository.getUserByUsername("users", record.getUsername()) != null) {
				responseBean.setErrCode(IDALiteral.FAILURE_USEREXISTS);
				return responseBean;
			}
			if (userRepository.updateUser(datasetName, record, "INSERT DATA", true) == IDALiteral.RESP_PASS_ROUTINE) {
				Map<String, Object> returnMap = new HashMap<>();
				returnMap.put("newUser", record.getUsername());
				responseBean.setPayload(returnMap);
			} else {
				responseBean.setErrCode(IDALiteral.FAILURE_USEREXISTS);
				responseBean.setErrMsg("Sign up was not successful");
			}
			return responseBean;
		} catch (Exception ex) {
			responseBean.setErrCode(IDALiteral.FAILURE_USEREXISTS);
			responseBean.setErrMsg("Sign up was not successful");
			ex.printStackTrace();
		}
		return responseBean;
	}

	@RequestMapping(value = "/delete/{usernames:.+}", method = RequestMethod.POST)
	@ResponseBody
	public ResponseBean deleteUser(@PathVariable String usernames) {
		try {
			User record = userRepository.getUserByUsername("users", usernames);
			if (record == null) {
				responseBean.setErrMsg("user not found");
				return responseBean;
			} else if (userRepository.updateUser(datasetName, record, "DELETE DATA", false) == IDALiteral.RESP_PASS_ROUTINE) {
				responseBean.setErrMsg("User Deleted");
			}
		} catch (Exception ex) {
			responseBean.setErrMsg("User could not be deleted");
			ex.printStackTrace();
		}
		return responseBean;
	}

	public static User list(String clientUserName) {
		UserRepository userRepository = new UserRepository();
		User user = null;
		try {
			user = userRepository.getUserByUsername(datasetName, clientUserName);
		}catch (Exception ex){
			ex.printStackTrace();
		}
		return user;
	}

	public static boolean checkPassword(String dbPass, String userInputPassword) throws NoSuchAlgorithmException {
		return BCrypt.checkpw(userInputPassword, dbPass);
	}
}
