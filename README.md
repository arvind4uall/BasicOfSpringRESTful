- Adding spring devtools dependency 
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
</dependency>
```
- @PathVariable is use to get the value of path, and @RequestParam is used to get the value from the query string.

1. Reading Path Variables with @PathVariable annotation. path = http://localhost:8080/users/userId
```agsl
  @GetMapping(path = "/{userId}")
  public String getUser(@PathVariable String userId){
    return "get user was called with id: "+userId;
  }
```
2. Reading Query String Request Parameter. path=http://localhost:8080/users?page=1&limit=50

```agsl
@GetMapping
  public String getUsers(@RequestParam(value = "page") int page,@RequestParam(value = "limit") int limit){
    return "get users was called with page: "+page+" ,and limit: "+limit;
 }
```

3. Making parameters optional or required (required=true/false can't be used with premitive data type, because if primitive data type will not be assigned then it can't be converted to null.).
```agsl
1. Use defaultValue to make parameter optional. 

  @GetMapping
  public String getUsers(@RequestParam(value = "page") int page,@RequestParam(value = "limit",defaultValue = "50") int limit){
    return "get users was called with page: "+page+" ,and limit: "+limit;
 }
 
 or 
 
2. Use required=true/false

public String getUsers(@RequestParam(value = "page") int page,
                         @RequestParam(value = "limit",defaultValue = "50") int limit,
                         @RequestParam(value = "sort",required = false) String sort
                         ){
    return "get users was called with page: "+page+" limit: "+limit+", and sort: "+sort;
  }

```

4. Returning Java object as return value. When we return java object then bydefault we get that object as json value in response.
```agsl
@GetMapping(path = "/{userId}")
  public UserRest getUser(@PathVariable String userId) {
    UserRest userRest = new UserRest();
    userRest.setFirstName("Arvind");
    userRest.setLastName("Kumar");
    userRest.setEmail("test@test.com");
    return userRest;
  }
```

If we want to get response as XML then in that case. We will follow these two steps 
```agsl
1. Add produces = {produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE} - For multiple type of response. 
  @GetMapping(path = "/{userId}",
          produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE
          })
          
2. To get XML in response we will have to add this dependency in our pom.xml
<dependency>
    <groupId>com.fasterxml.jackson.dataformat</groupId>
    <artifactId>jackson-dataformat-xml</artifactId>
</dependency>

      
```


5. Set Response Status Code - For setting we will use ResponseEntity<> class. This class has multiple constructor. Either we can send only status code or we can send body along with staus code.

```agsl
  @GetMapping(path = "/{userId}",
          produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<UserRest> getUser(@PathVariable String userId) {
    UserRest returnValue = new UserRest();
    returnValue.setFirstName("Arvind");
    returnValue.setLastName("Kumar");
    returnValue.setEmail("test@test.com");
    // Here we are setting status code and passing object as value also.
    // Based on our requirement we can create object of ResponseEntity class with different-2 argument.
    return new ResponseEntity<UserRest>(returnValue, HttpStatus.CREATED);
  }
```

6. Reading HTTP Post Request Body. 
For this I will first create a class which will convert the json to the java object. 

```agsl
  @PostMapping(consumes = {
          MediaType.APPLICATION_JSON_VALUE,
          MediaType.APPLICATION_XML_VALUE
  })
  
  /*Here without adding consumes= {
          MediaType.APPLICATION_JSON_VALUE,
          MediaType.APPLICATION_XML_VALUE
  } will also work. Because this dependency 
  <dependency>
		<groupId>com.fasterxml.jackson.dataformat</groupId>
		<artifactId>jackson-dataformat-xml</artifactId>
	</dependency>
	is smart enough. It will automatically see the body and will convert them into Java object.
  */
  public ResponseEntity<UserDetailsRequestModel> createUser(@RequestBody UserDetailsRequestModel userDetails) {
    userDetails.setFirstName(userDetails.getFirstName());
    userDetails.setLastName(userDetails.getLastName());
    userDetails.setEmail(userDetails.getEmail());
    userDetails.setPassword(userDetails.getPassword());
    return new ResponseEntity<UserDetailsRequestModel>(userDetails,HttpStatus.CREATED);
  }

```

7. Validating HTTP post request body. 
For validating post request body we will have to follow these two steps.

```agsl
1. Add spring-boot-starter-validation dependency. 

<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-validation</artifactId>
</dependency>

2. Add these two in application.properties
    server.error.include-message=always
    server.error.include-binding-errors=always
```
After following above steps. We will add validation in our bean and then use @Valid just before @RequestBody annotation.
```agsl
1. Validation insdie bean

@NotNull(message = "First Name can't be null") // message attribute will give error according to their value.
  private String firstName;

  @NotNull(message = "Last Name can't be null")
  private String lastName;

  @NotNull(message = "Email can't be null")
  @Email
  private String email;

  @NotNull(message = "Password can't be null")
  @Size(min = 8,max = 12,message = "Password can't be less than 8 characters and more than 12 characters.")
  private String password;
  
  2. Use @Valid before @RequestBody annotation 
  
   public ResponseEntity<UserRest> createUser(@Valid @RequestBody UserDetailsRequestModel userDetails) {
    UserRest returnValue = new UserRest();
    returnValue.setFirstName(userDetails.getFirstName());
    returnValue.setLastName(userDetails.getLastName());
    returnValue.setEmail(userDetails.getEmail());
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }
```

8. Store users temporary. 
```agsl
  Map<String,UserRest> users;
  
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
  
  
```

- Collections.emptyList() is use to create empty list.
- The ResponseEntity.noContent().build() method in Java is used to create a ResponseEntity with an HTTP status of 204 (No Content) and no body



9. Handle an Exception & Create Custom Error message object.
For doing so we will create a separate class.  In case of any error in our application our application will come inside this class and It will look for respective error method. 

```agsl
@ControllerAdvice
public class AppExceptionsHandler extends ResponseEntityExceptionHandler {
  @ExceptionHandler(value = {Exception.class}) // This annotation say that this particular method will handle exception.
  public ResponseEntity<Object> handleAnyException(Exception ex, WebRequest request){
    return new ResponseEntity<>(ex,new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
```
we can add many method in above class which will handle specific exceptions. Above class will give json representation of entire error object.

10. How to return custom json representation of error message? 

```

create this class and make changes in AppExceptionsHandler 

package com.appsdeveloperblog.app.ws.ui.model.response;

import java.util.Date;

public class ErrorMessage {
  private Date timestamp;
  private String message;
  public ErrorMessage() {
  }

  public ErrorMessage(Date timestamp, String message) {
    this.timestamp = timestamp;
    this.message = message;
  }

  public Date getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}

2. Inside AppExceptions Handler 

@ExceptionHandler(value = {Exception.class}) // This annotation say that this particular method will handle exception.
  public ResponseEntity<Object> handleAnyException(Exception ex, WebRequest request){
    ErrorMessage errorMessage = new ErrorMessage(new Date(),ex.getLocalizedMessage());
    return new ResponseEntity<>(errorMessage,new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

```

11. Run web services as standalone application.
> For this run following command 
> 1. mvn install -> This command will create target folder in our project folder. Now go inside this target folder and run this command to run the application. 
> 2. java -jar  nameOfYourApplication-0.0.1-SNAPSHOT.jar -> This command will run your application separately.
> 3. mvn clean -> This command will remove the target folder.



