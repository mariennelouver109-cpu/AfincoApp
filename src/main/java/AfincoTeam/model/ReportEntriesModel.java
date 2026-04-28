package AfincoTeam.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "report_entries")
public class ReportEntriesModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private ExamModel exam;

    @Column(name = "grade", nullable = true)
    private Double grade;

    @Column(name = "is_approved", nullable = true)
    private Boolean isApproved;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private SubjectModel subject;

    @Column(name = "attempt", nullable = false)
    private String attempt;

    public ReportEntriesModel() {
    }

    public ReportEntriesModel(UserModel user, ExamModel exam, SubjectModel subject, 
                               Double grade, Boolean isApproved, String attempt) {
        this.user = user;
        this.exam = exam;
        this.subject = subject;
        this.grade = grade;
        this.isApproved = isApproved;
        this.attempt = attempt;
    }

    // Getters e Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public ExamModel getExam() {
        return exam;
    }

    public void setExam(ExamModel exam) {
        this.exam = exam;
    }

    public SubjectModel getSubject() {
        return subject;
    }

    public void setSubject(SubjectModel subject) {
        this.subject = subject;
    }

    public Double getGrade() {
        return grade;
    }

    public void setGrade(Double grade) {
        this.grade = grade;
    }

    public Boolean getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(Boolean isApproved) {
        this.isApproved = isApproved;
    }

    public String getAttempt() {
        return attempt;
    }

    public void setAttempt(String attempt) {
        this.attempt = attempt;
    }
}
