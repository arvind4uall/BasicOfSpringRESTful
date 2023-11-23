package com.appsdeveloperblog.app.ws.ui.controller;

import com.appsdeveloperblog.app.ws.ui.model.request.UserDetailsRequestModel;
import com.appsdeveloperblog.app.ws.ui.model.response.UserRest;
import jakarta.validation.Valid;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("users") // http://localhost:8080/users
public class UserController {

  Map<String,UserRest> users;

  @GetMapping
  public List<UserRest> getUsers(@RequestParam(value = "page",defaultValue = "1") int page,
                                 @RequestParam(value = "limit", defaultValue = "50") int limit,
                                 @RequestParam(value = "sort", required = false) String sort
  ) {
    return users==null?Collections.emptyList():users.values().stream().toList();
  }

  @GetMapping(path = "/{userId}",
          produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<UserRest> getUser(@PathVariable String userId) {

   if(users.containsKey(userId))
     return new ResponseEntity<>(users.get(userId), HttpStatus.OK);
   else
     return new ResponseEntity<>(HttpStatus.NO_CONTENT);

  }

  @PostMapping(consumes = {
          MediaType.APPLICATION_JSON_VALUE,
          MediaType.APPLICATION_XML_VALUE
  })
  public ResponseEntity<UserRest> createUser(@Valid @RequestBody UserDetailsRequestModel userDetails) {
    UserRest returnValue = new UserRest();
    returnValue.setFirstName(userDetails.getFirstName());
    returnValue.setLastName(userDetails.getLastName());
    returnValue.setEmail(userDetails.getEmail());

    // to generate random userId
    String userId = UUID.randomUUID().toString();
    returnValue.setUserId(userId);

    // To check whe users is present or not
    if(users==null) users = new HashMap<>();
    users.put(userId,returnValue);

    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  @PutMapping(path = "/{userId}")
  public ResponseEntity<UserRest> updateUser(@PathVariable String userId, @RequestBody UserDetailsRequestModel userDetails){
    if(users.get(userId)==null)
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    UserRest updatedUser = users.get(userId);
    updatedUser.setFirstName(userDetails.getFirstName());
    updatedUser.setLastName(userDetails.getLastName());
    users.put(userId,updatedUser);
    return new ResponseEntity<>(updatedUser,HttpStatus.OK);
  }

  @DeleteMapping(path = "/{userId}")
  public ResponseEntity deleteUser(@PathVariable String userId) {
    users.remove(userId);
    return ResponseEntity.noContent().build();
  }

}
