package im.enricods.ComicsStore.services;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.enricods.ComicsStore.entities.Message;
import im.enricods.ComicsStore.entities.User;
import im.enricods.ComicsStore.entities.WishList;
import im.enricods.ComicsStore.repositories.MessageRepository;
import im.enricods.ComicsStore.repositories.UserRepository;
import im.enricods.ComicsStore.repositories.WishListRepository;

@Service
@Transactional
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WishListRepository wishListRepository;


    @Transactional(readOnly = true)
    public Set<Message> showAllMessages(long userId){

        Optional<User> usr = userRepository.findById(userId);
        if(!usr.isPresent())
            throw new IllegalArgumentException("User with id "+userId+" not found!");
        
        return usr.get().getMessages();
        
    }//showUnreadMessages


    @Transactional(readOnly = true)
    public Set<Message> showCartMessages(long userId){

        Optional<User> usr = userRepository.findById(userId);
        if(!usr.isPresent())
            throw new IllegalArgumentException("User with id "+userId+" not found!");

        return messageRepository.findCartMessages(usr.get());

    }//showCartMessages


    @Transactional(readOnly = true)
    public Set<Message> showListMessages(long userId, long listId){

        Optional<User> usr = userRepository.findById(userId);
        if(!usr.isPresent())
            throw new IllegalArgumentException("User with id "+userId+" not found!");

        Optional<WishList> list = wishListRepository.findById(listId);
        if(!list.isPresent())
            throw new IllegalArgumentException("Wish list with id "+listId+" not found!");

        return messageRepository.findListMessages(usr.get(), list.get());
        
    }//showListMessages


    public void removeMessages(Set<Long> messageIds){

        for(long id : messageIds){
            Optional<Message> msg = messageRepository.findById(id);
            if(!msg.isPresent()) { /*DO SOMETHING*/ }

            Message message = msg.get();

            //remove bidirectional relations
            message.getTargetUser().getMessages().remove(message);
            for(WishList wl : message.getInvolvedLists()){
                wl.getMessages().remove(message);
            }

            //remove message
            messageRepository.delete(message);

        }

    }//removeMessages

    
}//MessageService
