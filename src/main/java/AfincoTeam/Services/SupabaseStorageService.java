package AfincoTeam.Services;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import AfincoTeam.config.SupabaseStorageConfig;

// Serviço para operações de armazenamento no Supabase Storage
@Service
public class SupabaseStorageService {

    private final SupabaseStorageConfig storageConfig;
    private final RestTemplate restTemplate;

    public SupabaseStorageService(SupabaseStorageConfig storageConfig, RestTemplate restTemplate) {
        this.storageConfig = storageConfig;
        this.restTemplate = restTemplate;
    }

    /**
     * Faz download de um arquivo do Supabase Storage
     * @param filePath caminho do arquivo no Supabase Storage
     * @return conteúdo do arquivo em bytes
     */
    public byte[] downloadFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("Caminho do arquivo não pode estar vazio");
        }

        try {
            // Construir URL pública para o arquivo
            String downloadUrl = storageConfig.getUrl() + "/storage/v1/object/public/" 
                               + storageConfig.getBucketName() + "/" + filePath;
            System.out.println("Downloading from: " + downloadUrl);

            // Faz o download (GET request) - sem autenticação necessária para bucket público
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    downloadUrl,
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    byte[].class
            );

            // Verifica se o download foi bem-sucedido
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Erro ao fazer download: " + response.getStatusCode());
            }

            return response.getBody() != null ? response.getBody() : new byte[0];

        } catch (HttpClientErrorException e) {
            System.err.println("HTTP Error during download: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new RuntimeException("Arquivo não encontrado no Supabase Storage: " + filePath, e);
            }
            throw new RuntimeException("Erro na requisição ao Supabase Storage: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            System.err.println("Download error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro ao fazer download do arquivo: " + e.getMessage(), e);
        }
    }

    /**
     * Obtém a URL pública para acessar um arquivo
     * @param filePath caminho do arquivo no Supabase Storage
     * @return URL pública do arquivo
     */
    public String getPublicFileUrl(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("Caminho do arquivo não pode estar vazio");
        }

        // Construir URL pública usando o endpoint correto do Supabase
        return storageConfig.getUrl() + "/storage/v1/object/public/" 
               + storageConfig.getBucketName() + "/" + filePath;
    }
}
