package work.novablog.mcplugin.discordconnect.util;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

public class ConfigManager {
    private static final int CONFIG_LATEST = 3;

    private static Properties langData;

    public String botToken;
    public List<Long> botChatChannelIds;
    public String botPlayingGameName;
    public String botCommandPrefix;
    public String sendToMinecraftFormat;
    public String sendToDiscordFormat;
    public String lunaChatJapanizeFormat;
    public String botWebhookURL;
    public boolean doUpdateCheck;

    /**
     * configの読み出し、保持を行うインスタンスを生成します
     * @param plugin プラグインのメインクラス
     * @throws IOException 読み出し中にエラーが発生した場合にthrowされます
     */
    public ConfigManager(@NotNull Plugin plugin) throws IOException {
        //設定フォルダの作成
        if(!plugin.getDataFolder().exists() && !plugin.getDataFolder().mkdir()) {
            throw new IOException();
        }

        //バージョンが古ければ古いファイルをバックアップ
        if (getConfigData(plugin).getInt("configVersion", 0) < CONFIG_LATEST) {
            backupOldFile(plugin, "config.yml");
            backupOldFile(plugin, "message.yml");
        }

        //configとlangの取得
        Configuration pluginConfig = getConfigData(plugin);
        langData = getLangData(plugin);

        //configの読み出し
        botToken = pluginConfig.getString("token");
        botChatChannelIds = pluginConfig.getLongList("chatChannelIDs");
        botPlayingGameName = pluginConfig.getString("playingGameName");
        botCommandPrefix = pluginConfig.getString("prefix");
        sendToMinecraftFormat = pluginConfig.getString("toMinecraftFormat");
        sendToDiscordFormat = pluginConfig.getString("toDiscordFormat");
        lunaChatJapanizeFormat = pluginConfig.getString("japanizeFormat");
        botWebhookURL = pluginConfig.getString("webhookURL");
        doUpdateCheck = pluginConfig.getBoolean("updateCheck");
    }

    private Configuration getConfigData(Plugin plugin) throws IOException {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            //存在しなければコピー
            InputStream src = plugin.getResourceAsStream("config.yml");
            Files.copy(src, configFile.toPath());
        }

        return ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
    }

    private Properties getLangData(Plugin plugin) throws IOException {
        File langFile = new File(plugin.getDataFolder(), "message.yml");
        if (!langFile.exists()) {
            //存在しなければコピー
            InputStream src = plugin.getResourceAsStream(Locale.getDefault().toString() + ".properties");
            if(src == null) src = plugin.getResourceAsStream("ja_JP.properties");
            Files.copy(src, langFile.toPath());
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(langFile), StandardCharsets.UTF_8));
        Properties langData = new Properties();
        langData.load(br);
        return langData;
    }

    private void backupOldFile(Plugin plugin, String targetFileName) throws IOException {
        File oldFile = new File(plugin.getDataFolder(), targetFileName + "_old");
        Files.deleteIfExists(oldFile.toPath());
        if(!(new File(plugin.getDataFolder(), targetFileName).renameTo(oldFile))) throw new IOException();
    }

    /**
     * 多言語対応メッセージ
     */
    public enum Message {
        invalidToken,
        invalidWebhookURL,
        mainChannelNotFound,
        shutdownDueToError,
        normalShutdown,
        botIsReady,
        botRestarted,
        configReloaded,

        updateNotice,
        updateDownloadLink,
        updateCheckFailed,
        pluginIsLatest,

        bungeeCommandDenied,
        bungeeCommandNotFound,
        bungeeCommandSyntaxError,

        bungeeCommandHelpLine1,
        bungeeCommandHelpHelpcmd,
        bungeeCommandHelpReloadcmd,

        userActivity,
        serverActivity,

        proxyStarted,
        proxyStopped,
        joined,
        left,
        serverSwitched;

        /**
         * propertiesファイルからメッセージを取ってきます
         * @return 多言語対応メッセージ
         */
        @Override
        public String toString() {
            return langData.getProperty(name());
        }
    }
}
