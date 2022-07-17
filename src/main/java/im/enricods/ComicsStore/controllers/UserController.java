package im.enricods.ComicsStore.controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import im.enricods.ComicsStore.entities.User;
import im.enricods.ComicsStore.exceptions.UserNotFoundException;
import im.enricods.ComicsStore.services.UserService;

@RestController
@RequestMapping(path = "users")
public class UserController {
    
    @Autowired
    private UserService userService;

    @GetMapping(path = "firstName&lastName")
    public List<User> getByFirstNameAndLastName(@RequestParam(value = "fName") String firstName, @RequestParam(value = "lName") String lastName){
        return userService.getUsersByName(firstName, lastName);
    }//getByFirstNameAndLastName

    @GetMapping(path = "city")
    public List<User> getByCity(@RequestParam(value = "city") String city){
        return userService.getUsersByCity(city);
    }//getByCity

    @GetMapping(path = "email")
    public ResponseEntity getByEmail(@RequestParam(value = "email") String email){
        try{
            User result = userService.getUserByEmail(email);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        catch(UserNotFoundException e){
            return new ResponseEntity<>("User with email "+email+" not found", HttpStatus.BAD_REQUEST);
        }
    }//getByEmail

    @PostMapping
    public User create(@RequestBody @Valid User user){
        return userService.createUser(user);
    }//create

    @PutMapping
    public ResponseEntity update(@RequestBody @Valid User user){
        try{
            userService.updateUser(user);
            return new ResponseEntity<>("User "+user.getFirstName()+" "+user.getLastName()+ "/"+user.getId()+" updated successful!", HttpStatus.OK);
        }
        catch(UserNotFoundException e){
            return new ResponseEntity<>("User \""+user.getId()+"\" not found", HttpStatus.BAD_REQUEST);
        }
    }//getByEmail

}//UserController
