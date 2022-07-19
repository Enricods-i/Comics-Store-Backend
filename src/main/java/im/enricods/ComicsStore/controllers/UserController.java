package im.enricods.ComicsStore.controllers;

import java.util.LinkedList;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import im.enricods.ComicsStore.entities.User;
import im.enricods.ComicsStore.exceptions.UserAlreadyExists;
import im.enricods.ComicsStore.exceptions.UserNotFoundException;
import im.enricods.ComicsStore.services.UserService;
import im.enricods.ComicsStore.utils.InvalidField;

@RestController
@RequestMapping(path = "users")
public class UserController {
    
    @Autowired
    private UserService userService;

    @GetMapping(path = "/searchByFirstName&LastName")
    public List<User> getByFirstNameAndLastName(@RequestParam(value = "fName") String firstName, @RequestParam(value = "lName") String lastName){
        return userService.getUsersByName(firstName, lastName);
    }//getByFirstNameAndLastName

    @GetMapping(path = "/searchByCity")
    public List<User> getByCity(@RequestParam(value = "city") String city){
        return userService.getUsersByCity(city);
    }//getByCity

    @GetMapping(path = "/getByEmail")
    public ResponseEntity<?> getByEmail(@RequestParam(value = "email") String email){
        try{
            User result = userService.getUserByEmail(email);
            return new ResponseEntity<User>(result, HttpStatus.OK);
        }
        catch(UserNotFoundException e){
            return new ResponseEntity<String>("User with email "+email+" not found", HttpStatus.BAD_REQUEST);
        }
    }//getByEmail

    @PostMapping
    public ResponseEntity<?> create(@RequestBody User user){
        try{
            User result = userService.createUser(user);
            return new ResponseEntity<User>(result, HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            List<InvalidField> fieldsViolated = new LinkedList<>();
            for(ConstraintViolation<?> cv : e.getConstraintViolations()){
                fieldsViolated.add(new InvalidField(cv.getInvalidValue(), cv.getMessage()));
            }
            return new ResponseEntity<List<InvalidField>>(fieldsViolated, HttpStatus.BAD_REQUEST);
        }
        catch(UserAlreadyExists e){
            return new ResponseEntity<String>("User \""+user.getFirstName()+" "+user.getLastName()+"\" already exists!", HttpStatus.BAD_REQUEST);
        }
    }//create

    @PutMapping
    public ResponseEntity<?> update(@RequestBody User user){
        try{
            userService.updateUser(user);
            return new ResponseEntity<String>("User "+user.getFirstName()+" "+user.getLastName()+ "/"+user.getId()+" updated successful!", HttpStatus.OK);
        }
        catch(TransactionSystemException e){
            if(e.getRootCause() instanceof ConstraintViolationException){
                ConstraintViolationException cve = (ConstraintViolationException)e.getRootCause();
                List<InvalidField> fieldsViolated = new LinkedList<>();
                for(ConstraintViolation<?> cv : cve.getConstraintViolations()){
                    fieldsViolated.add(new InvalidField(cv.getInvalidValue(), cv.getMessage()));
                }
                return new ResponseEntity<List<InvalidField>>(fieldsViolated, HttpStatus.BAD_REQUEST);
            }//if
            else
                return new ResponseEntity<String>("SERVER ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch(UserNotFoundException e){
            return new ResponseEntity<String>("User \""+user.getId()+"\" not found", HttpStatus.BAD_REQUEST);
        }
    }//update

}//UserController
