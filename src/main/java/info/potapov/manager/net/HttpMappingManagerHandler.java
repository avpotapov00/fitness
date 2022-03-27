package info.potapov.manager.net;

import java.nio.charset.StandardCharsets;
import java.sql.Date;

import info.potapov.utils.ConnectionProvider;
import info.potapov.manager.command.impl.AddNewUserCommand;
import info.potapov.common.command.CommandProcessor;
import info.potapov.manager.command.impl.ExtendMembershipCommand;
import info.potapov.manager.command.dao.CommandDaoImpl;
import info.potapov.manager.query.queries.GetMembershipInfoQuery;
import info.potapov.manager.query.QueryDaoImpl;
import info.potapov.common.query.QueryProcessor;
import com.github.vanbv.num.AbstractHttpMappingHandler;
import com.github.vanbv.num.annotation.Get;
import com.github.vanbv.num.annotation.PathParam;
import com.github.vanbv.num.annotation.Post;
import com.github.vanbv.num.annotation.QueryParam;
import com.github.vanbv.num.json.JsonParser;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpVersion;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;

@ChannelHandler.Sharable
public class HttpMappingManagerHandler extends AbstractHttpMappingHandler {
    private final CommandProcessor commandProcessor;
    private final QueryProcessor queryProcessor;

    public HttpMappingManagerHandler(JsonParser parser) {
        super(parser);
        this.commandProcessor = new CommandProcessor(new CommandDaoImpl(ConnectionProvider.connect()));
        this.queryProcessor = new QueryProcessor(new QueryDaoImpl(ConnectionProvider.connect()));
    }

    DefaultFullHttpResponse wrapResult(String result) {
        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
                Unpooled.copiedBuffer(result, StandardCharsets.UTF_8));
    }

    @Post("/user/new")
    public DefaultFullHttpResponse userNew() {
        return wrapResult(commandProcessor.process(new AddNewUserCommand()));
    }

    @Post("/membership/extend")
    public DefaultFullHttpResponse membershipExtend(@QueryParam(value = "user_id") Long userId,
                                        @QueryParam(value = "expiry_date_ts") String expiryDate) {
        return wrapResult(commandProcessor.process(new ExtendMembershipCommand(userId, Date.valueOf(expiryDate))));
    }

    @Get("/user/{id}")
    public DefaultFullHttpResponse userGet(@PathParam(value = "id") Long userId) {
        return wrapResult(queryProcessor.process(new GetMembershipInfoQuery(userId)));
    }
}
