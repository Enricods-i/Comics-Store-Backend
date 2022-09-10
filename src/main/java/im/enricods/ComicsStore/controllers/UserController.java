package im.enricods.ComicsStore.controllers;

import java.util.List;

import javax.validation.ConstraintViolationException;

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
import im.enricods.ComicsStore.services.UserService;
import im.enricods.ComicsStore.utils.InvalidValue;

@RestController
@RequestMapping(path = "users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(path = "/byFirstNameAndLastName")
    public ResponseEntity<?> showByFirstNameAndLastName(@RequestParam(value = "fName") String firstName,
            @RequestParam(value = "lName") String lastName) {
        try {
            List<User> result = userService.getByName(firstName, lastName);
            return new ResponseEntity<List<User>>(result, HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
        }
    }// showByFirstNameAndLastName

    @GetMapping(path = "/byCity")
    public ResponseEntity<?> showByCity(@RequestParam String city) {
        try {
            List<User> result = userService.getByCity(city);
            return new ResponseEntity<List<User>>(result, HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
        }
    }// showByCity

    @GetMapping(path = "/byEmail")
    public ResponseEntity<?> showByEmail(@RequestParam String email) {
        try {
            User result = userService.getByEmail(email);
            return new ResponseEntity<User>(result, HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }// showByEmail

    @GetMapping(path = "/byPhoneNumber")
    public ResponseEntity<?> showByPhoneNumber(@RequestParam String phoneNumber) {
        try {
            List<User> result = userService.getByPhoneNumber(phoneNumber);
            return new ResponseEntity<List<User>>(result, HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
        }
    }// showByPhoneNumber

    @PostMapping(path = "/create")
    public ResponseEntity<?> create(@RequestBody User user) {
        try {
            User result = userService.add(user);
            return new ResponseEntity<User>(result, HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }// create

    @PutMapping(path = "/update")
    public ResponseEntity<?> update(@RequestBody User user) {
        try {
            userService.modify(user);
            return new ResponseEntity<String>("User \"" + user.getFirstName() + " " + user.getLastName() + "\" ("
                    + user.getId() + ") updated successful!", HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }// update

}// UserController
