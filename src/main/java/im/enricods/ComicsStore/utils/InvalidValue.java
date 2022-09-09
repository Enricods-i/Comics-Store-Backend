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
public class InvalidValue implements Serializable {

    private String field;
    private Object value;
    private String motivation;

    public static List<InvalidValue> getAllInvalidValues(ConstraintViolationException cve) {
        List<InvalidValue> valuesViolated = new LinkedList<>();
        String[] pathToField = null;
        for (ConstraintViolation<?> cv : cve.getConstraintViolations()) {
            pathToField = cv.getPropertyPath().toString().split("\\.");
            valuesViolated.add(new InvalidValue(
                    pathToField[pathToField.length - 1],
                    cv.getInvalidValue(),
                    cv.getMessage()));
        }
        return valuesViolated;
    }// getAllInvalidValues

}// FieldInvalid
