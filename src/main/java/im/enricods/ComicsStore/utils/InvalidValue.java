package im.enricods.ComicsStore.utils;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class InvalidValue implements Serializable{
    
    private Object value;
    private String motivation;
    
}//FieldInvalid
