package AfincoTeam.Services;

import org.springframework.stereotype.Service;
import AfincoTeam.dto.BoletimDTO;
import AfincoTeam.dto.BoletimEntryDTO;
import AfincoTeam.model.ReportEntriesModel;
import AfincoTeam.model.UserModel;
import AfincoTeam.model.SubjectModel;
import AfincoTeam.model.ExamModel;
import AfincoTeam.repository.ReportEntriesRepository;
import AfincoTeam.repository.UserRepository;
import AfincoTeam.repository.SubjectRepository;
import AfincoTeam.repository.ExamRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReportEntriesService {

    private final ReportEntriesRepository reportEntriesRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final ExamRepository examRepository;

    public ReportEntriesService(ReportEntriesRepository reportEntriesRepository,
                                 UserRepository userRepository,
                                 SubjectRepository subjectRepository,
                                 ExamRepository examRepository) {
        this.reportEntriesRepository = reportEntriesRepository;
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
        this.examRepository = examRepository;
    }

    /**
     * Salva o resultado de um exame para um usuário
     */
    public ReportEntriesModel saveExamResult(String username, String examId, String subjectId,
                                             Integer score, Integer totalQuestions, Double percentage) {
        UserModel user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + username));

        // Valida o exame e a matéria
        UUID examUUID = UUID.fromString(examId);
        ExamModel exam = examRepository.findById(examUUID)
                .orElseThrow(() -> new IllegalArgumentException("Exame não encontrado: " + examId));

        // Valida a matéria associada ao exame
        SubjectModel subject = exam.getSubject();
        if (subject == null) {
            throw new IllegalArgumentException("Exame não possui matéria associada: " + examId);
        }

        // Busca as tentativas anteriores do usuário para o mesmo exame e matéria
        List<ReportEntriesModel> previousAttempts = reportEntriesRepository
                .findByUserAndExamAndSubject(user, exam, subject);
        
        // Determina o número da próxima tentativa com base nas tentativas anteriores
        int nextAttempt = 1;
        if (!previousAttempts.isEmpty()) {
            // Tenta extrair o número da tentativa das entradas anteriores e calcula a próxima tentativa
            nextAttempt = previousAttempts.stream()
                    .mapToInt(result -> {
                        try {
                            return Integer.parseInt(result.getAttempt());
                        } catch (NumberFormatException e) {
                            return 0;
                        }
                    })
                    .max()
                    .orElse(0) + 1;
        }

        // Calcula a nota com base no percentual (convertendo para escala de 0 a 10)
        Double grade = (percentage / 100) * 10;
        Boolean isApproved = grade >= 6.0; // Assuming 6.0 is passing grade

        ReportEntriesModel result = new ReportEntriesModel();
        result.setUser(user);
        result.setExam(exam);
        result.setSubject(subject);
        result.setGrade(grade);
        result.setIsApproved(isApproved);
        result.setAttempt(String.valueOf(nextAttempt));

        return reportEntriesRepository.save(result);
    }

    /**
     * Obtém o boletim (relatório de notas) de um usuário
     */
    public BoletimDTO getBoletim(String username) {
        UserModel user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + username));

        // Busca todos os resultados do usuário
        List<ReportEntriesModel> results = reportEntriesRepository.findByUser(user);

        // Agrupa por matéria e calcula a média de cada uma
        Map<SubjectModel, List<ReportEntriesModel>> resultsBySubject = results.stream()
                .collect(Collectors.groupingBy(ReportEntriesModel::getSubject));

        // Cria o boletim com a média de cada matéria
        List<BoletimEntryDTO> boletimEntries = subjectRepository.findAll().stream()
                .map(subject -> {
                    List<ReportEntriesModel> subjectResults = resultsBySubject.getOrDefault(subject, List.of());

                    if (subjectResults.isEmpty()) {
                        // Se não há resultados para a matéria, coloca 0
                        return new BoletimEntryDTO(subject.getSubjectName(), 0.0, 0.0);
                    }

                    // Agrupa os resultados por exame e pega a maior nota de cada exame
                    Map<ExamModel, Double> bestGradesByExam = subjectResults.stream()
                            .collect(Collectors.groupingBy(
                                ReportEntriesModel::getExam,
                                Collectors.mapping(
                                    result -> result.getGrade() != null ? result.getGrade() : 0.0,
                                    Collectors.maxBy(Double::compare)
                                )
                            ))
                            .entrySet().stream()
                            .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> entry.getValue().orElse(0.0)
                            ));

                    // Calcula a média das melhores notas de cada exame na matéria
                    Double averageGrade = bestGradesByExam.values().stream()
                            .mapToDouble(Double::doubleValue)
                            .average()
                            .orElse(0.0);

                    // Converte nota para percentual (grade * 10)
                    Double percentual = averageGrade;

                    return new BoletimEntryDTO(subject.getSubjectName(), averageGrade, percentual);
                })
                .collect(Collectors.toList());

        // Calcula a média geral de todas as matérias
        Double mediaGeral = boletimEntries.stream()
                .mapToDouble(BoletimEntryDTO::getNota)
                .average()
                .orElse(0.0);

        return new BoletimDTO(boletimEntries, mediaGeral);
    }
}