package im.enricods.ComicsStore.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import im.enricods.ComicsStore.entities.Message;
import im.enricods.ComicsStore.entities.User;
import im.enricods.ComicsStore.repositories.MessageRepository;
import im.enricods.ComicsStore.repositories.UserRepository;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Message> showUnreadMessages(long userId){

        Optional<User> usr = userRepository.findById(userId);
        if(!usr.isPresent())
            throw new IllegalArgumentException("User with id "+userId+" not found!");
        
        return messageRepository.findByUser(usr.get());
        
    }//showUnreadMessages
    
}//MessageService
