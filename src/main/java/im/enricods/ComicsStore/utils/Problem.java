package im.enricods.ComicsStore.utils;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import lombok.Getter;

@Getter
public class Problem implements Serializable {

    private int code;
    private List<String> invalidFields;

    public Problem(ProblemCode code, String... invalidFields) {
        this.code = code.ordinal();
        this.invalidFields = new LinkedList<>();
        for (String invalidField : invalidFields) {
            this.invalidFields.add(invalidField);
        }
    }

    public boolean add(String invalidField) {
        return this.invalidFields.add(invalidField);
    }

    public static Set<Problem> getProblemFromConstraintViolationException(ConstraintViolationException cve) {
        Set<Problem> result = new HashSet<>();
        Problem p = new Problem(ProblemCode.VALIDATION);
        for (ConstraintViolation<?> cv : cve.getConstraintViolations()) {
            String[] pathToInvalidField = cv.getPropertyPath().toString().split("\\.");
            p.add(pathToInvalidField[pathToInvalidField.length - 1]);
        }
        result.add(p);
        return result;
    }

}// Invalid
