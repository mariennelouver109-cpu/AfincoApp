package AfincoTeam.model;

import jakarta.persistence.*;
import java.util.UUID;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Entity
@Table(name = "exams")
public class ExamModel {

    // Atributos do modelo de exame
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "exam_name", nullable = false)
    private String examName;

    @Column(name = "period", nullable = false)
    private Short period;

    @Column(name = "questions_json", nullable = false, columnDefinition = "TEXT")
    private String questionsJson;

    // Relacionamento Many-to-One com SubjectModel
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private SubjectModel subject;

    public ExamModel() {}
    public ExamModel(String examName, Short period, String questionsJson, SubjectModel subject) {
        this.examName = examName;
        this.period = period;
        this.questionsJson = questionsJson;
        this.subject = subject;
    }

    // --- Getters and Setters ---

    public UUID getId() {
        return id;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public Short getPeriod() {
        return period;
    }

    public void setPeriod(Short period) {
        this.period = period;
    }

    public String getQuestionsJson() {
        return questionsJson;
    }

    public void setQuestionsJson(String questionsJson) {
        this.questionsJson = questionsJson;
    }

    public SubjectModel getSubject() {
        return subject;
    }

    public void setSubject(SubjectModel subject) {
        this.subject = subject;
    }

    // JSON parsing methods
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Método para converter o JSON de perguntas em um objeto QuestionnaireModel
    public QuestionnaireModel getQuestionnaire() throws JsonProcessingException {
        if (questionsJson == null || questionsJson.isEmpty()) {
            throw new IllegalStateException("Questionário JSON não pode ser vazio");
        }
        return objectMapper.readValue(questionsJson, QuestionnaireModel.class);
    }
}