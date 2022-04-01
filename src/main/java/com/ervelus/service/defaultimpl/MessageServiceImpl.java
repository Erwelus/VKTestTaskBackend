package com.ervelus.service.defaultimpl;

import com.ervelus.infrastructure.annotations.Component;
import com.ervelus.infrastructure.annotations.InjectByType;
import com.ervelus.model.Message;
import com.ervelus.model.User;
import com.ervelus.repository.MessageRepository;
import com.ervelus.service.MessageService;

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
        return messageRepository.loadChat(userFrom, userTo);
    }
}
