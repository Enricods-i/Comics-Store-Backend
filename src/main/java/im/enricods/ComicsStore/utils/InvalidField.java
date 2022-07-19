package im.enricods.ComicsStore.utils;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class InvalidField implements Serializable{
    
    private Object field;
    private String motivation;
    
}//FieldInvalid
