package com.ervelus.service.defaultimpl;

import com.ervelus.infrastructure.annotations.Component;
import com.ervelus.infrastructure.annotations.InjectByType;
import com.ervelus.model.Message;
import com.ervelus.model.User;
import com.ervelus.repository.MessageRepository;
import com.ervelus.service.MessageService;
import com.ervelus.service.UserService;

import java.util.Collections;
import java.util.List;

@Component
public class MessageServiceImpl implements MessageService {
    @InjectByType
    private MessageRepository messageRepository;

    @Override
    public void send(Message message) {
        messageRepository.save(message);
    }

    @Override
    public List<Message> loadChat(User userFrom, User userTo) {
        List<Message> res = messageRepository.loadChat(userFrom, userTo);
        Collections.reverse(res);
        return res;
    }

}
