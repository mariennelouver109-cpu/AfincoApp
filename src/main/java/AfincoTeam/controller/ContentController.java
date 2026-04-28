package AfincoTeam.controller;

// imports
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import AfincoTeam.Services.ContentService;
import AfincoTeam.Services.SupabaseStorageService;
import AfincoTeam.model.ContentModel;

// Controlador para gerenciar operações de conteúdo e armazenamento
@Controller
@RequestMapping("/api/content")
public class ContentController {
    // Injeção do ContentService para orquestrar as operações de conteúdo
    private final ContentService contentService;
    // Construtor para injeção de dependência do ContentService
    public ContentController(ContentService contentService, SupabaseStorageService storageService) {
        this.contentService = contentService;
    }

    // Endpoint para download de conteúdo
    @GetMapping("/download/{contentId}")
    public ResponseEntity<?> downloadContent(@PathVariable Integer contentId) {
        try {
            ContentModel content = contentService.getContent(contentId);
            byte[] fileContent = contentService.downloadContent(contentId);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + content.getContentName() + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                    .body(fileContent); 
        } catch (IllegalArgumentException e) { // conteúdo não encontrado
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage())); 
        } catch (IllegalStateException e) { // erro ao acessar o arquivo no Supabase Storage
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    // Endpoint para obter detalhes de um conteúdo
    @GetMapping("/{contentId}")
    public ResponseEntity<?> getContent(@PathVariable Integer contentId) {
        try {
            ContentModel content = contentService.getContent(contentId);
            String publicUrl = contentService.getPublicUrl(contentId);
            return ResponseEntity.ok()
                    .body(new ContentResponse(content, publicUrl));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    // ----------------respostas da API-----------------


    // Resposta para erros
    public static class ErrorResponse {
        public String error; // mensagem de erro

        public ErrorResponse(String error) {
            this.error = error; // mensagem de erro
        }
    }

    // Resposta para mensagens genéricas
    public static class MessageResponse {
        public String message; // mensagem de resposta

        public MessageResponse(String message) {
            this.message = message; // mensagem de resposta
        }
    }

    // Resposta para detalhes de conteúdo
    public static class ContentResponse {
        public Integer id; // ID do conteúdo
        public String contentName; // nome do conteúdo
        public String type; // tipo do conteúdo (pdf, video, imagem, etc)
        public Integer subjectId; // ID da matéria relacionada
        public String publicUrl; // URL pública para acesso ao arquivo no Supabase Storage

        // Construtor para resposta de detalhes de conteúdo
        public ContentResponse(ContentModel content, String publicUrl) {
            this.id = content.getId();
            this.contentName = content.getContentName();
            this.type = content.getType();
            this.subjectId = content.getSubjectId();
            this.publicUrl = publicUrl;
        }
    }
}
