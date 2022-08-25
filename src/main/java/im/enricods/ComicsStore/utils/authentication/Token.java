package im.enricods.ComicsStore.utils.authentication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;


@UtilityClass
public class Token {


    public Jwt getPrincipal() {
        return (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public String getAuthServerId() {
        return getTokenNode().get("subject").asText();
    }

    public String getName() {
        return getTokenNode().get("sub").asText();
    }

    public String getEmail() {
        return getTokenNode().get("claims").get("preferred_username").asText();
    }

    public long getId() {
        return 0;
    }

    private JsonNode getTokenNode() {
        Jwt jwt = getPrincipal();
        ObjectMapper objectMapper = new ObjectMapper();
        String jwtAsString;
        JsonNode jsonNode;
        try {
            jwtAsString = objectMapper.writeValueAsString(jwt);
            jsonNode = objectMapper.readTree(jwtAsString);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to retrieve user's info!");
        }
        return jsonNode;
    }


}
