package com.ervelus.service.defaultimpl;

import com.ervelus.infrastructure.annotations.Component;
import com.ervelus.infrastructure.annotations.InjectByType;
import com.ervelus.model.Message;
import com.ervelus.model.User;
import com.ervelus.repository.MessageRepository;
import com.ervelus.service.MessageService;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

/**
 * Default implementation of UserService.
 * Is annotated with @Component, thus will be saved in context
 */
@Component
public class MessageServiceImpl implements MessageService {
    /**
     * Instance of MessageRepository for persisting messages.
     * Setter is only used for tests, is automatically injected
     */
    @InjectByType
    @Setter
    private MessageRepository messageRepository;

    /**
     * Persists the message into storage
     * @param message message, containing sender, receiver and text
     */
    @Override
    public void send(Message message) {
        messageRepository.save(message);
    }

    /**
     * Loads chat between two users, then makes sure that the last element is the most recent message
     */
    @Override
    public List<Message> loadChat(User userFrom, User userTo) {
        List<Message> res = messageRepository.loadChat(userFrom, userTo);
        if (res != null && res.size() > 1) {
            Collections.reverse(res);
        }
        return res;
    }

}
