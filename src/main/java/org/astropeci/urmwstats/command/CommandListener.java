package org.astropeci.urmwstats.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.astropeci.urmwstats.DoggoUriProvider;
import org.astropeci.urmwstats.SecretProvider;
import org.astropeci.urmwstats.auth.NotStaffException;
import org.astropeci.urmwstats.auth.RoleManager;
import org.astropeci.urmwstats.export.ChannelExporter;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.awt.*;

@Component
public class CommandListener extends ListenerAdapter {

    private final String prefix;
    private final RoleManager roleManager;
    private final ChannelExporter channelExporter;
    private final DoggoUriProvider doggoUriProvider;

    public CommandListener(
            JDA jda,
            SecretProvider secretProvider,
            RoleManager roleManager,
            ChannelExporter channelExporter,
            DoggoUriProvider doggoUriProvider
    ) {
        this.roleManager = roleManager;
        this.channelExporter = channelExporter;
        this.doggoUriProvider = doggoUriProvider;
        jda.addEventListener(this);
        prefix = String.format("<@!%s>", secretProvider.getDiscordClientId());
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }

        String message = event.getMessage().getContentRaw().trim();

        if (!message.startsWith(prefix)) {
            return;
        }

        message = message.substring(prefix.length()).trim();

        switch (message) {
            case "doggo":
                doggo(event);
                break;
            case "export":
                export(event);
                break;
            default:
                event.getChannel().sendMessage("Unknown command").queue();
        }
    }

    private void doggo(MessageReceivedEvent event) {
        MessageEmbed embed = new EmbedBuilder()
                .setColor(new Color(155, 89, 182))
                .setImage(doggoUriProvider.randomUri().toString())
                .build();

        event.getChannel().sendMessage(embed).queue();
    }

    private void export(MessageReceivedEvent event) {
        try {
            roleManager.authenticate(event.getAuthor().getId());
        } catch (NotStaffException e) {
            event.getChannel().sendMessage("Not permitted").queue();
            return;
        }

        event.getChannel().sendMessage("Beginning export").queue();
    }
}
