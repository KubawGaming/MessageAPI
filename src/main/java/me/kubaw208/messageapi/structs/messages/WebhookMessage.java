package me.kubaw208.messageapi.structs.messages;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.kubaw208.messageapi.structs.SoundableMessage;
import me.kubaw208.messageapi.structs.embed.Embed;
import me.kubaw208.messageapi.structs.embed.Field;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@JsonTypeName("WEBHOOK")
@Getter
@Setter
@Accessors(chain = true)
public class WebhookMessage extends SoundableMessage {

    @Getter @Setter private static boolean logWebhookErrors = true;

    @JsonProperty("webhook") private String webhookUrl;
    @JsonProperty("message") private String message;
    @JsonProperty("username") private String username;
    @JsonProperty("avatarUrl") private String avatarUrl;
    @JsonProperty("embeds") private List<Embed> embeds;

    @JsonCreator
    public WebhookMessage(@NotNull @JsonProperty("webhook") String webhookUrl,
                          @Nullable @JsonProperty("message") String message,
                          @Nullable @JsonProperty("username") String username,
                          @Nullable @JsonProperty("avatarUrl") String avatarUrl,
                          @Nullable @JsonProperty("embeds") List<Embed> embeds
    ) {
        this.webhookUrl = webhookUrl;
        this.message = message;
        this.embeds = embeds;
        this.username = username;
        this.avatarUrl = avatarUrl;
    }

    @Override
    protected WebhookMessage sendToInternal(@Nullable CommandSender recipient) {
        if(recipient != null) {
            applySound(recipient);
            applyCommands(recipient);
        }

        sendWebhookMessage();
        return this;
    }

    /**
     * Sends the webhook message to Discord.
     */
    public void sendWebhookMessage() {
        try {
            WebhookClient client = WebhookClient.withUrl(webhookUrl);
            WebhookMessageBuilder messageBuilder = new WebhookMessageBuilder();

            if(username != null) messageBuilder.setUsername(username);
            if(avatarUrl != null) messageBuilder.setAvatarUrl(avatarUrl);
            if(message != null) messageBuilder.setContent(message);

            if(embeds != null && !embeds.isEmpty()) {
                for(Embed embed : embeds) {
                    messageBuilder.addEmbeds(convertEmbed(embed));
                }
            }

            client.send(messageBuilder.build());
            client.close();
        } catch(Exception e) {
            if(logWebhookErrors)
                System.err.println("❌ Webhook sending error: " + e.getMessage());
        }
    }

    private WebhookEmbed convertEmbed(Embed embed) {
        WebhookEmbedBuilder builder = new WebhookEmbedBuilder();

        if(embed.getTitle() != null)
            builder.setTitle(new WebhookEmbed.EmbedTitle(embed.getTitle(), embed.getUrl()));

        builder.setDescription(embed.getDescription());
        builder.setColor(embed.getColor());
        builder.setThumbnailUrl(embed.getThumbnailUrl());
        builder.setImageUrl(embed.getImageUrl());

        if(embed.getTimestamp() != null) {
            try {
                builder.setTimestamp(OffsetDateTime.parse(embed.getTimestamp(), DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            } catch(DateTimeParseException ignored) {}
        }

        if(embed.getFooter() != null) {
            builder.setFooter(new WebhookEmbed.EmbedFooter(
                    embed.getFooter().getText(),
                    embed.getFooter().getIconUrl()
            ));
        }

        if(embed.getAuthor() != null) {
            builder.setAuthor(new WebhookEmbed.EmbedAuthor(
                    embed.getAuthor().getName(),
                    embed.getAuthor().getUrl(),
                    embed.getAuthor().getIconUrl()
            ));
        }

        if(embed.getFields() != null) {
            for(Field field : embed.getFields()) {
                if(field.getName() != null && field.getValue() != null)
                    builder.addField(new WebhookEmbed.EmbedField(
                            field.getInline() != null ? field.getInline() : false,
                            field.getName(),
                            field.getValue()
                    ));
            }
        }

        return builder.build();
    }

    @Override
    public WebhookMessage replace(@NotNull String toReplace, @NotNull String replaced) {
        WebhookMessage cloned = this.clone();

        if(cloned.getMessage() != null)
            cloned.setMessage(cloned.getMessage().replace(toReplace, replaced));

        if(cloned.getEmbeds() != null) {
            List<Embed> clonedEmbeds = new ArrayList<>();

            for(Embed embed : cloned.getEmbeds()) {
                clonedEmbeds.add(embed.replace(toReplace, replaced));
            }

            cloned.setEmbeds(clonedEmbeds);
        }

        return cloned;
    }

    @Override
    public WebhookMessage clone() {
        return (WebhookMessage) super.clone();
    }

}