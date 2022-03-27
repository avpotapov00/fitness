package info.potapov.turnstile.net;

import java.nio.charset.StandardCharsets;

import info.potapov.utils.ConnectionProvider;
import info.potapov.turnstile.command.dao.CommandDaoImpl;
import info.potapov.common.command.CommandProcessor;
import info.potapov.turnstile.command.impl.EnterCommand;
import info.potapov.turnstile.command.impl.ExitCommand;
import com.github.vanbv.num.AbstractHttpMappingHandler;
import com.github.vanbv.num.annotation.PathParam;
import com.github.vanbv.num.annotation.Post;
import com.github.vanbv.num.json.JsonParser;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpVersion;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;

@ChannelHandler.Sharable
public class HttpMappingTurnstileHandler extends AbstractHttpMappingHandler {
    private final CommandProcessor commandProcessor;

    public HttpMappingTurnstileHandler(JsonParser parser) {
        super(parser);
        this.commandProcessor = new CommandProcessor(new CommandDaoImpl(ConnectionProvider.connect()));
    }

    DefaultFullHttpResponse wrapResult(String result) {
        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
                Unpooled.copiedBuffer(result, StandardCharsets.UTF_8));
    }

    @Post("/enter/{id}")
    public DefaultFullHttpResponse enter(@PathParam(value = "id") Long userId) {
        return wrapResult(commandProcessor.process(new EnterCommand(userId, System.currentTimeMillis())));
    }

    @Post("/exit/{id}")
    public DefaultFullHttpResponse exit(@PathParam(value = "id") Long userId) {
        return wrapResult(commandProcessor.process(new ExitCommand(userId, System.currentTimeMillis())));
    }
}
