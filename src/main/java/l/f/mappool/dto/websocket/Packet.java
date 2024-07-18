package l.f.mappool.dto.websocket;

import lombok.Data;

@Data
public class Packet<T> {
    String channel;
    String command;
    T data;
}
