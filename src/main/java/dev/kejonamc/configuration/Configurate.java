package dev.kejonamc.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class Configurate {

    /**
     * Load EcoBase config
     *
     * @param dataDirectory The config's directory
     */
    public static Configurate configuration(@NotNull Path dataDirectory) throws IOException {

        File folder = dataDirectory.toFile();
        File file = new File(folder, "config.yml");

        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try (InputStream input = Configurate.class.getResourceAsStream("/" + file.getName())) {
                if (input != null) {
                    Files.copy(input, file.toPath());
                } else {
                    file.createNewFile();
                }
            } catch (IOException ignored) {
            }
        }

        final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        return mapper.readValue(dataDirectory.resolve("config.yml").toFile(), Configurate.class);
    }

    @JsonProperty("api-key")
    private String apiKey;

    public String getApiKey() {
        return apiKey;
    }

    @JsonProperty("auto-purge")
    private boolean autoPurge;
    public boolean getAutoPurge() {
        return autoPurge;
    }

    @JsonProperty("xuid")
    private String xuid;
    public String getXUID() {
        return xuid;
    }

    @JsonProperty("timer")
    private int time;
    public int getTime() {
        return time;
    }
}