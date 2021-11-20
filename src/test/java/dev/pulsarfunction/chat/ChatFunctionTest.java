package dev.pulsarfunction.chat;

import dev.pulsarfunction.chat.ChatFunction;
import org.apache.pulsar.common.functions.FunctionConfig;
import org.apache.pulsar.common.io.SourceConfig;
import org.apache.pulsar.functions.LocalRunner;
import org.junit.Assert;
import org.junit.Test;
import org.apache.pulsar.functions.api.Context;

import java.util.Collections;

import static org.mockito.Mockito.mock;

public class ChatFunctionTest {

    protected Context ctx;

    protected void init(Context ctx) {
        this.ctx = ctx;
    }

    protected void log(String msg) {
        if (ctx != null && ctx.getLogger() != null) {
            ctx.getLogger().info(String.format("Function: [%s, id: %s, instanceId: %d of %d] %s",
                    ctx.getFunctionName(), ctx.getFunctionId(), ctx.getInstanceId(), ctx.getNumInstances(), msg));
        }
    }

    @Test
    public void testChatFunction() {
        ChatFunction func = new ChatFunction();
        String output = func.process("this is great.", mock(Context.class));
        Assert.assertEquals(output, "Positive");
    }

    @Test
    public void testChatFunctionBad() {
        ChatFunction func = new ChatFunction();
        String output = func.process("This is horrible, it makes me angry, what is everything bad and wrong.", mock(Context.class));
        Assert.assertEquals(output, "Negative");
    }

    /**
     * @param args
     * @throws Exception
     */
        public static void main(String[] args) throws Exception {

            FunctionConfig functionConfig = FunctionConfig.builder()
                    .className(ChatFunction.class.getName())
                    .inputs(Collections.singleton("persistent://public/default/chat"))
                    .name("Chat")
                    .tenant("public")
                    .namespace("default")
                    .runtime(FunctionConfig.Runtime.JAVA)
                    .cleanupSubscription(true)
                    .build();

            // nvidia-desktop
            //192.168.1.210
            LocalRunner localRunner = LocalRunner.builder()
                    .brokerServiceUrl("pulsar://127.0.0.1:6650")
                    .functionConfig(functionConfig)
                    .build();

            localRunner.start(false);

            Thread.sleep(30000);
            localRunner.stop();
            System.exit(0);
        }
}
