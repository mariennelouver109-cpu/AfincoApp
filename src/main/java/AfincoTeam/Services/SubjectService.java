package AfincoTeam.Services;

import org.springframework.stereotype.Service;
import AfincoTeam.model.SubjectModel;
import AfincoTeam.repository.SubjectRepository;
import java.util.List;

// Serviço para gerenciar operações de matérias
@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;

    public SubjectService(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    /**
     * Obtém uma matéria pelo slug
     * @param slug identificador único da matéria (ex: "quimica", "biologia")
     * @return SubjectModel correspondente
     */
    public SubjectModel getSubjectBySlug(String slug) {
        return subjectRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Matéria com slug '" + slug + "' não encontrada"));
    }

    /**
     * Obtém uma matéria pelo ID
     * @param id ID da matéria
     * @return SubjectModel correspondente
     */
    public SubjectModel getSubjectById(Integer id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Matéria com ID " + id + " não encontrada"));
    }

    /**
     * Obtém todas as matérias
     * @return lista de todas as matérias
     */
    public List<SubjectModel> getAllSubjects() {
        return subjectRepository.findAll();
    }

    /**
     * Cria uma nova matéria
     * @param subject SubjectModel a ser criada
     * @return matéria criada
     */
    public SubjectModel createSubject(SubjectModel subject) {
        return subjectRepository.save(subject);
    }

    /**
     * Atualiza uma matéria
     * @param id ID da matéria a atualizar
     * @param subject dados atualizados
     * @return matéria atualizada
     */
    public SubjectModel updateSubject(Integer id, SubjectModel subject) {
        SubjectModel existing = getSubjectById(id);
        existing.setSubjectName(subject.getSubjectName());
        if (subject.getSlug() != null) {
            existing.setSlug(subject.getSlug());
        }
        return subjectRepository.save(existing);
    }

    /**
     * Deleta uma matéria
     * @param id ID da matéria a deletar
     */
    public void deleteSubject(Integer id) {
        SubjectModel subject = getSubjectById(id);
        subjectRepository.delete(subject);
    }
}
