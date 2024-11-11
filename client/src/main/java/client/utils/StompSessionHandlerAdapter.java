package client.utils;

import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;

import java.lang.reflect.Type;

public abstract class StompSessionHandlerAdapter implements StompSessionHandler {
    /**
     * This implementation returns String as the expected payload type
     * for STOMP ERROR frames.
     */
    @Override
    public Type getPayloadType(StompHeaders headers) {
        return String.class;
    }

    /**
     * This implementation is empty
     */
    @Override
    public void handleFrame(StompHeaders headers, Object payload) {

    }
    /**
     * This implementation is empty
     */
    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {

    }
    /**
     * This implementation is empty
     */
    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers,
                                byte[] payload, Throwable exception) {

    }
    /**
     * This implementation is empty
     */
    @Override
    public void handleTransportError(StompSession session, Throwable exception) {

    }
}
