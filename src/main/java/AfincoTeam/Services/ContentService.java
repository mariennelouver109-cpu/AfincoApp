package AfincoTeam.Services;

import org.springframework.stereotype.Service;
import AfincoTeam.model.ContentModel;
import AfincoTeam.repository.ContentRepository;
import AfincoTeam.repository.SubjectRepository;
import java.util.List;

// Serviço para orquestrar operações de conteúdo com armazenamento
@Service
public class ContentService {

    private final ContentRepository contentRepository;
    private final SupabaseStorageService storageService;

    public ContentService(ContentRepository contentRepository,
                         SubjectRepository subjectRepository,
                         SupabaseStorageService storageService) {
        this.contentRepository = contentRepository;
        this.storageService = storageService;
    }

    /**
     * Faz download de um conteúdo pelo ID
     * @param contentId ID do conteúdo
     * @return bytes do arquivo
     */
    public byte[] downloadContent(Integer contentId) {
        ContentModel content = contentRepository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("Conteúdo com ID " + contentId + " não encontrado"));

        if (content.getUrl() == null || content.getUrl().isEmpty()) {
            throw new IllegalStateException("Conteúdo não possui arquivo armazenado");
        }

        System.out.println("Downloading content " + contentId + " from path: " + content.getUrl());
        try {
            byte[] data = storageService.downloadFile(content.getUrl());
            System.out.println("Successfully downloaded content, size: " + data.length + " bytes");
            return data;
        } catch (Exception e) {
            System.err.println("Failed to download content from path: " + content.getUrl() + ", error: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Obtém um conteúdo pelo ID
     * @param contentId ID do conteúdo
     * @return ContentModel
     */
    public ContentModel getContent(Integer contentId) {
        return contentRepository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("Conteúdo com ID " + contentId + " não encontrado"));
    }

    /**
     * Obtém todos os conteúdos de uma matéria
     * @param subjectId ID da matéria
     * @return lista de conteúdos
     */
    public List<ContentModel> getContentsBySubject(Integer subjectId) {
        return contentRepository.findBySubjectId(subjectId);
    }

    /**
     * Obtém a URL pública de um conteúdo
     * @param contentId ID do conteúdo
     * @return URL pública do arquivo
     */
    public String getPublicUrl(Integer contentId) {
        ContentModel content = contentRepository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("Conteúdo com ID " + contentId + " não encontrado"));

        if (content.getUrl() == null || content.getUrl().isEmpty()) {
            throw new IllegalStateException("Conteúdo não possui arquivo armazenado");
        }

        return storageService.getPublicFileUrl(content.getUrl());
    }
}
