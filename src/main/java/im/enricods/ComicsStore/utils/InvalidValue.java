package im.enricods.ComicsStore.utils;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class InvalidValue implements Serializable{
    
    private Object value;
    private String motivation;

    public static List<InvalidValue> getAllInvalidValues(ConstraintViolationException cve){
        List<InvalidValue> valuesViolated = new LinkedList<>();
        for(ConstraintViolation<?> cv : cve.getConstraintViolations()){
            valuesViolated.add(new InvalidValue(cv.getInvalidValue(), cv.getMessage()));
        }
        return valuesViolated;
    }//getAllInvalidValues
    
}//FieldInvalid
