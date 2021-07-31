package me.neiizun.lightdrop.lightdrop.bot;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.managers.DirectAudioController;
import net.dv8tion.jda.api.managers.Presence;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;
import net.dv8tion.jda.api.requests.restaction.CommandEditAction;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.requests.restaction.GuildAction;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.api.utils.cache.CacheView;
import net.dv8tion.jda.api.utils.cache.SnowflakeCacheView;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

public class BotWrapper implements JDA {


    JDA innerJDA;

    /**
     * never use this constructor manually it exist only for injection
     */

    public BotWrapper(){
    }
    public BotWrapper(JDA jda){
        this.innerJDA = jda;
    }
    // for lazy initialization by hand
    public void setInnerJDA(JDA jda){
        this.innerJDA = jda;
    }

    @NotNull
    @Override
    public Status getStatus() {
        return innerJDA.getStatus();
    }

    @NotNull
    @Override
    public EnumSet<GatewayIntent> getGatewayIntents() {
        return innerJDA.getGatewayIntents();
    }

    @NotNull
    @Override
    public EnumSet<CacheFlag> getCacheFlags() {
        return innerJDA.getCacheFlags();
    }

    @Override
    public boolean unloadUser(long userId) {
        return innerJDA.unloadUser(userId);
    }

    @Override
    public long getGatewayPing() {
        return innerJDA.getGatewayPing();
    }

    @NotNull
    @Override
    public JDA awaitStatus(@NotNull JDA.Status status, @NotNull Status... failOn) throws InterruptedException {
        return innerJDA.awaitStatus(status,failOn);
    }

    @Override
    public int cancelRequests() {
        return innerJDA.cancelRequests();
    }

    @NotNull
    @Override
    public ScheduledExecutorService getRateLimitPool() {
        return innerJDA.getRateLimitPool();
    }

    @NotNull
    @Override
    public ScheduledExecutorService getGatewayPool() {
        return innerJDA.getGatewayPool();
    }

    @NotNull
    @Override
    public ExecutorService getCallbackPool() {
        return innerJDA.getCallbackPool();
    }

    @NotNull
    @Override
    public OkHttpClient getHttpClient() {
        return innerJDA.getHttpClient();
    }

    @NotNull
    @Override
    public DirectAudioController getDirectAudioController() {
        return innerJDA.getDirectAudioController();
    }


    @Override
    public void setEventManager(@Nullable IEventManager manager) {
        innerJDA.setEventManager(manager);
    }


    @Override
    public void addEventListener(@NotNull Object... listeners) {
        innerJDA.addEventListener(listeners);
    }


    @Override
    public void removeEventListener(@NotNull Object... listeners) {
        innerJDA.removeEventListener(listeners);
    }

    @NotNull
    @Override
    public List<Object> getRegisteredListeners() {
        return innerJDA.getRegisteredListeners();
    }

    @NotNull
    @Override
    public RestAction<List<Command>> retrieveCommands() {
        return innerJDA.retrieveCommands();
    }

    @NotNull
    @Override
    public RestAction<Command> retrieveCommandById(@NotNull String id) {
        return innerJDA.retrieveCommandById(id);
    }

    @NotNull
    @Override
    public CommandCreateAction upsertCommand(@NotNull CommandData command) {
        return innerJDA.upsertCommand(command);
    }

    @NotNull
    @Override
    public CommandListUpdateAction updateCommands() {
        return innerJDA.updateCommands();
    }

    @NotNull
    @Override
    public CommandEditAction editCommandById(@NotNull String id) {
        return innerJDA.editCommandById(id);
    }

    @NotNull
    @Override
    public RestAction<Void> deleteCommandById(@NotNull String commandId) {
        return innerJDA.deleteCommandById(commandId);
    }

    @NotNull
    @Override
    public GuildAction createGuild(@NotNull String name) {
        return innerJDA.createGuild(name);
    }

    @NotNull
    @Override
    public RestAction<Void> createGuildFromTemplate(@NotNull String code, @NotNull String name, @Nullable Icon icon) {
        return innerJDA.createGuildFromTemplate(code,name,icon);
    }

    @NotNull
    @Override
    public CacheView<AudioManager> getAudioManagerCache() {
        return innerJDA.getAudioManagerCache();
    }

    @NotNull
    @Override
    public SnowflakeCacheView<User> getUserCache() {
        return innerJDA.getUserCache();
    }

    @NotNull
    @Override
    public List<Guild> getMutualGuilds(@NotNull User... users) {
        return innerJDA.getMutualGuilds(users);
    }

    @NotNull
    @Override
    public List<Guild> getMutualGuilds(@NotNull Collection<User> users) {
        return innerJDA.getMutualGuilds(users);
    }

    @NotNull
    @Override
    public RestAction<User> retrieveUserById(long id, boolean update) {
        return innerJDA.retrieveUserById(id,update);
    }

    @NotNull
    @Override
    public SnowflakeCacheView<Guild> getGuildCache() {
        return innerJDA.getGuildCache();
    }

    @NotNull
    @Override
    public Set<String> getUnavailableGuilds() {
        return innerJDA.getUnavailableGuilds();
    }

    @Override
    public boolean isUnavailable(long guildId) {
        return innerJDA.isUnavailable(guildId);
    }

    @NotNull
    @Override
    public SnowflakeCacheView<Role> getRoleCache() {
        return innerJDA.getRoleCache();
    }

    @NotNull
    @Override
    public SnowflakeCacheView<Category> getCategoryCache() {
        return innerJDA.getCategoryCache();
    }

    @NotNull
    @Override
    public SnowflakeCacheView<StoreChannel> getStoreChannelCache() {
        return innerJDA.getStoreChannelCache();
    }

    @NotNull
    @Override
    public SnowflakeCacheView<TextChannel> getTextChannelCache() {
        return innerJDA.getTextChannelCache();
    }

    @NotNull
    @Override
    public SnowflakeCacheView<VoiceChannel> getVoiceChannelCache() {
        return innerJDA.getVoiceChannelCache();
    }

    @NotNull
    @Override
    public SnowflakeCacheView<PrivateChannel> getPrivateChannelCache() {
        return innerJDA.getPrivateChannelCache();
    }

    @NotNull
    @Override
    public RestAction<PrivateChannel> openPrivateChannelById(long userId) {
        return innerJDA.openPrivateChannelById(userId);
    }

    @NotNull
    @Override
    public SnowflakeCacheView<Emote> getEmoteCache() {
        return innerJDA.getEmoteCache();
    }

    @NotNull
    @Override
    public IEventManager getEventManager() {
        return innerJDA.getEventManager();
    }

    @NotNull
    @Override
    public SelfUser getSelfUser() {
        return innerJDA.getSelfUser();
    }

    @NotNull
    @Override
    public Presence getPresence() {
        return innerJDA.getPresence();
    }

    @NotNull
    @Override
    public ShardInfo getShardInfo() {
        return innerJDA.getShardInfo();
    }

    @NotNull
    @Override
    public String getToken() {
        return innerJDA.getToken();
    }

    @Override
    public long getResponseTotal() {
        return innerJDA.getResponseTotal();
    }

    @Override
    public int getMaxReconnectDelay() {
        return innerJDA.getMaxReconnectDelay();
    }


    @Override
    public void setAutoReconnect(boolean reconnect) {
        innerJDA.setAutoReconnect(reconnect);
    }


    @Override
    public void setRequestTimeoutRetry(boolean retryOnTimeout) {
        innerJDA.setRequestTimeoutRetry(retryOnTimeout);
    }

    @Override
    public boolean isAutoReconnect() {
        return innerJDA.isAutoReconnect();
    }

    @Override
    public boolean isBulkDeleteSplittingEnabled() {
        return innerJDA.isBulkDeleteSplittingEnabled();
    }


    @Override
    public void shutdown() {
        innerJDA.shutdown();
    }


    @Override
    public void shutdownNow() {
        innerJDA.shutdownNow();
    }

    @NotNull
    @Override
    public AccountType getAccountType() {
        return innerJDA.getAccountType();
    }

    @NotNull
    @Override
    public RestAction<ApplicationInfo> retrieveApplicationInfo() {
        return innerJDA.retrieveApplicationInfo();
    }

    @NotNull
    @Override
    public JDA setRequiredScopes(@NotNull Collection<String> scopes) {
        return innerJDA.setRequiredScopes(scopes);
    }

    @NotNull
    @Override
    public String getInviteUrl(@Nullable Permission... permissions) {
        return innerJDA.getInviteUrl(permissions);
    }

    @NotNull
    @Override
    public String getInviteUrl(@Nullable Collection<Permission> permissions) {
        return innerJDA.getInviteUrl(permissions);
    }

    @Nullable
    @Override
    public ShardManager getShardManager() {
        return innerJDA.getShardManager();
    }

    @NotNull
    @Override
    public RestAction<Webhook> retrieveWebhookById(@NotNull String webhookId) {
        return innerJDA.retrieveWebhookById(webhookId);
    }
}
