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

@Component
public class MessageServiceImpl implements MessageService {
    @InjectByType
    @Setter
    private MessageRepository messageRepository;

    @Override
    public void send(Message message) {
        messageRepository.save(message);
    }

    @Override
    public List<Message> loadChat(User userFrom, User userTo) {
        List<Message> res = messageRepository.loadChat(userFrom, userTo);
        if (res != null && res.size() > 1) {
            Collections.reverse(res);
        }
        return res;
    }

}
