package AfincoTeam.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

// Configuração para conectar com o Supabase Storage
@Configuration
@ConfigurationProperties(prefix = "supabase.storage")
public class SupabaseStorageConfig {
    
    private String url;
    private String apiKey;
    private String bucketName;

    // Bean para RestTemplate que será injetado nos serviços
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    // Getters e Setters
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }
}
