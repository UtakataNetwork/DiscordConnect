package work.novablog.mcplugin.discordconnect.listener;

import com.gmail.necnionch.myapp.markdownconverter.MarkComponent;
import com.gmail.necnionch.myapp.markdownconverter.MarkdownConverter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;
import work.novablog.mcplugin.discordconnect.DiscordConnect;
import work.novablog.mcplugin.discordconnect.util.DiscordSender;

import java.util.regex.Pattern;

public class DiscordListener extends ListenerAdapter {
    private final String prefix;

    public DiscordListener(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent message) {
        if(message.getAuthor().isBot()) return;
        if(!DiscordConnect.getInstance().getBotManager().getChatChannelIds().contains(message.getChannel().getIdLong())) return;

        if (message.getMessage().getContentRaw().startsWith(prefix)) {
            //コマンド TODO
            String command = message.getMessage().getContentRaw().replace(prefix, "").split("\\s+")[0];
            String[] args = message.getMessage().getContentRaw().replaceAll(Pattern.quote(prefix + command) + "\\s*", "").split("\\s+");
            if(args[0].equals("")) {
                args = new String[0];
            }

            //DiscordConnect.getInstance().embed(Color.RED, "coming soon...", null);
        }else {
            //メッセージ
            MarkComponent[] components = MarkdownConverter.fromDiscordMessage(message.getMessage().getContentRaw());
            TextComponent[] convertedMessage = MarkdownConverter.toMinecraftMessage(components);

            TextComponent[] send = new TextComponent[convertedMessage.length + 1];
            send[0] = new TextComponent(message.getAuthor().getName() + " : ");
            System.arraycopy(convertedMessage, 0, send, 1, convertedMessage.length);

            ProxyServer.getInstance().broadcast(send);
        }
    }
}
