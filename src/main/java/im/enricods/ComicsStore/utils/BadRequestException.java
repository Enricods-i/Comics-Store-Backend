package im.enricods.ComicsStore.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class BadRequestException extends RuntimeException {

    private Set<Problem> problems = new HashSet<>();

    public BadRequestException(Problem p) {
        this.problems.add(p);
    }

    public BadRequestException(Problem... p) {
        for (Problem problem : p) {
            this.problems.add(problem);
        }
    }

    public BadRequestException(Collection<Problem> p) {
        this.problems.addAll(p);
    }

}// BadRequestException
