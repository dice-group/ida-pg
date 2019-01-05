package com.poc.repositories;

import com.poc.model.User;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends Neo4jRepository<User, Long> {
	
    @Query("MATCH (n:User {username: {username}}) RETURN n")
    User findUserByUsername(@Param("username") String username);
}
