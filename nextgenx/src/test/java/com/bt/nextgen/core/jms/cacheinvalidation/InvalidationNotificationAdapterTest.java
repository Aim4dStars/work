package com.bt.nextgen.core.jms.cacheinvalidation;

import com.bt.nextgen.core.jms.data.Chunk;
import com.bt.nextgen.integration.xml.extractor.DefaultResponseExtractor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.jms.support.converter.SimpleMessageConverter;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by L054821 on 14/04/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class InvalidationNotificationAdapterTest {

    @InjectMocks
    InvalidationNotificationAdapter invalidationNotificationAdapter;

    @Mock
    Chunk chunk;

    @Mock
    InvalidationNotification invalidationNotification;

    @Mock
    InvalidationNotificationImpl invalidationNotificationImpl;

    @Mock
    SimpleMessageConverter simpleMessageConverter;

    @Mock
    DefaultResponseExtractor defaultResponseExtractor;

    private Message message;

    @Before
    public void setup() throws Exception {
        message = createMessageForTemplateName();
        List<Message> messages = new ArrayList<>();
        messages.add(message);

        Mockito.when(chunk.getItems()).thenReturn(messages);

    }

    @Test
    public void test_transformMessage() throws Exception {
        Mockito.when((String)simpleMessageConverter.fromMessage(message)).thenReturn("xmlns:avqxsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
        Mockito.when(defaultResponseExtractor.extractData("")).thenReturn(invalidationNotificationImpl);
        invalidationNotificationAdapter.transformMessage();

    }

    public Message createMessageForTemplateName() throws Exception {
        String templateName = "";
        Message message = mock(Message.class);
        when(message.getStringProperty(anyString())).thenReturn(templateName);
        return message;
    }
}
