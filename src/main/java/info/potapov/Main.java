package info.potapov;

import info.potapov.manager.net.HttpMappingManagerHandler;
import info.potapov.report.net.HttpMappingReportHandler;
import info.potapov.turnstile.net.HttpMappingTurnstileHandler;
import com.github.vanbv.num.json.JsonParserDefault;
import lombok.SneakyThrows;

import static info.potapov.common.net.ServerRunner.runServer;

public class Main {

    @SneakyThrows
    public static void main(String[] args) {
        runServer(8100, new HttpMappingManagerHandler(new JsonParserDefault()));
        runServer(8101, new HttpMappingTurnstileHandler(new JsonParserDefault()));
        runServer(8102, new HttpMappingReportHandler(new JsonParserDefault()));
    }

}
