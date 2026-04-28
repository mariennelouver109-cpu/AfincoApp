package AfincoTeam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import AfincoTeam.model.ExamModel;
import AfincoTeam.model.ReportEntriesModel;
import AfincoTeam.model.UserModel;
import AfincoTeam.model.SubjectModel;

import java.util.List;
import java.util.UUID;

// Repositório para gerenciar as entradas de relatório de desempenho dos usuários
@Repository
public interface ReportEntriesRepository extends JpaRepository<ReportEntriesModel, UUID> {

    // Métodos personalizados para consultar as entradas de relatório com base no usuário, matéria e exame
    List<ReportEntriesModel> findByUser(UserModel user);

    // Consulta para obter as entradas de relatório de um usuário específico em uma matéria específica
    List<ReportEntriesModel> findByUserAndSubject(UserModel user, SubjectModel subject);

    // Consulta para obter as entradas de relatório de um usuário específico em um exame específico
    List<ReportEntriesModel> findByUserAndExamAndSubject(UserModel user, ExamModel exam, SubjectModel subject);

    // Consulta para obter a última entrada de relatório de um usuário específico em uma matéria específica, ordenada pela tentativa mais recente
    ReportEntriesModel findTopByUserAndSubjectOrderByAttemptDesc(UserModel user, SubjectModel subject);
}