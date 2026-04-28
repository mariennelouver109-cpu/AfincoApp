package AfincoTeam.Services;

import org.springframework.stereotype.Service;
import AfincoTeam.model.ExamModel;
import AfincoTeam.model.SubjectModel;
import AfincoTeam.repository.ExamRepository;
import AfincoTeam.repository.SubjectRepository;
import java.util.List;
import java.util.UUID;

// Serviço para gerenciar operações de leitura de provas (read-only)
// Inserções, atualizações e exclusões são realizadas diretamente no servidor
@Service
public class ExamService {

    private final ExamRepository examRepository;
    private final SubjectRepository subjectRepository;

    public ExamService(ExamRepository examRepository, SubjectRepository subjectRepository) {
        this.examRepository = examRepository;
        this.subjectRepository = subjectRepository;
    }

    /**
     * Obtém uma prova pelo ID
     * @param id ID da prova
     * @return ExamModel correspondente
     */
    public ExamModel getExamById(UUID id) {
        return examRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Prova com ID " + id + " não encontrada"));
    }

    /**
     * Obtém todas as provas
     * @return lista de todas as provas
     */
    public List<ExamModel> getAllExams() {
        return examRepository.findAll();
    }

    /**
     * Obtém todas as provas de uma matéria
     * @param subjectId ID da matéria
     * @return lista de provas da matéria
     */
    public List<ExamModel> getExamsBySubject(Integer subjectId) {
        SubjectModel subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new IllegalArgumentException("Matéria com ID " + subjectId + " não encontrada"));
        return examRepository.findBySubject(subject);
    }

    /**
     * Obtém todas as provas por período
     * @param period número do período
     * @return lista de provas do período
     */
    public List<ExamModel> getExamsByPeriod(Short period) {
        return examRepository.findByPeriod(period);
    }
}
