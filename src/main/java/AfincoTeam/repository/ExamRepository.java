package AfincoTeam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import AfincoTeam.model.ExamModel;
import AfincoTeam.model.SubjectModel;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

// interface para o repositório de provas, estende JpaRepository para fornecer métodos de acesso a dados
public interface ExamRepository extends JpaRepository<ExamModel, UUID> {

    // método para encontrar todas as provas de uma matéria específica
    List<ExamModel> findBySubject(SubjectModel subject);

    // método para encontrar todas as provas de uma matéria por ID
    List<ExamModel> findBySubjectId(Integer subjectId);

    // método para encontrar uma prova pelo nome
    Optional<ExamModel> findByExamName(String examName);

    // método para encontrar provas por período
    List<ExamModel> findByPeriod(Short period);
}
