package upb.ida.bean;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class User {

    @GraphId
    private Long id;
    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private String userrole;
    
    public User() {
		this.userrole = "USER";
	}
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }
    
    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
    
    public String getUserRole() {
        return userrole;
    }

    public void setUserRole(String userrole) {
        this.userrole = userrole;
    }
}
