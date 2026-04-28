package AfincoTeam.Services;

import org.springframework.stereotype.Service;
import AfincoTeam.model.ExamModel;
import AfincoTeam.model.QuestionModel;
import AfincoTeam.model.QuestionnaireModel;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Service
public class QuestionnaireService {

    private final ExamService examService;
    private final SupabaseStorageService storageService;

    public QuestionnaireService(ExamService examService, SupabaseStorageService storageService) {
        this.examService = examService;
        this.storageService = storageService;
    }

    public QuestionnaireModel getQuestionnaireByExamId(UUID examId) {
        ExamModel exam = examService.getExamById(examId);
        try {
            String questionsJson = exam.getQuestionsJson();
            System.out.println("Loading questionnaire for exam " + examId + ", questionsJson: " + questionsJson);

            if (isSupabaseFilePath(questionsJson)) {
                System.out.println("Detected Supabase file path: " + questionsJson + ", attempting download...");
                try {
                    byte[] fileBytes = storageService.downloadFile(questionsJson);
                    questionsJson = new String(fileBytes, StandardCharsets.UTF_8);
                    System.out.println("Successfully downloaded JSON from Supabase, length: " + questionsJson.length());
                    exam.setQuestionsJson(questionsJson);
                } catch (Exception e) {
                    System.err.println("Failed to download from Supabase: " + e.getMessage());
                    e.printStackTrace();
                    throw new IllegalArgumentException("Arquivo de questionário não encontrado no Supabase Storage: " + questionsJson, e);
                }
            } else {
                System.out.println("Using inline JSON data, length: " + questionsJson.length());
            }

            QuestionnaireModel questionnaire = parseQuestionnaire(exam);
            System.out.println("Successfully parsed questionnaire with " + questionnaire.getTotalQuestions() + " questions");
            return questionnaire;

        } catch (Exception e) {
            System.err.println("Error loading questionnaire: " + e.getMessage());
            throw new IllegalArgumentException("Não foi possível carregar o questionário: " + e.getMessage(), e);
        }
    }

    private QuestionnaireModel parseQuestionnaire(ExamModel exam) throws Exception {
        return exam.getQuestionnaire();
    }

    private boolean isSupabaseFilePath(String text) {
        if (text == null || text.isBlank()) {
            return false;
        }
        String trimmed = text.trim();
        if (trimmed.startsWith("{") || trimmed.startsWith("[") || trimmed.startsWith("\"")) {
            return false;
        }
        return trimmed.contains("/") || trimmed.endsWith(".json");
    }

    public QuestionModel getQuestion(UUID examId, int index) {
        QuestionnaireModel questionnaire = getQuestionnaireByExamId(examId);
        QuestionModel question = questionnaire.getQuestao(index);
        if (question == null) {
            throw new IllegalArgumentException("Questão não encontrada no índice " + index);
        }
        return question;
    }

    public int getTotalQuestions(UUID examId) {
        QuestionnaireModel questionnaire = getQuestionnaireByExamId(examId);
        return questionnaire.getTotalQuestions();
    }

    public boolean isAnswerCorrect(UUID examId, int questionIndex, Integer chosenOption) {
        if (chosenOption == null) {
            return false;
        }
        QuestionModel question = getQuestion(examId, questionIndex);
        Integer correct = question.getRespostaCorreta();
        return correct != null && correct.equals(chosenOption);
    }

    public int calculateCompletionPercent(int currentQuestionNumber, int totalQuestions) {
        if (totalQuestions <= 0) {
            return 0;
        }
        int percent = (int) Math.round((currentQuestionNumber / (double) totalQuestions) * 100);
        return Math.min(100, Math.max(0, percent));
    }

    public int calculateScorePercent(int correctAnswers, int totalQuestions) {
        if (totalQuestions <= 0) {
            return 0;
        }
        int percent = (int) Math.round((correctAnswers / (double) totalQuestions) * 100);
        return Math.min(100, Math.max(0, percent));
    }

    public int grade(UUID examId, List<Integer> answers) {
        QuestionnaireModel questionnaire = getQuestionnaireByExamId(examId);
        int total = questionnaire.getTotalQuestions();
        int score = 0;
        if (answers == null) {
            return score;
        }
        for (int i = 0; i < answers.size() && i < total; i++) {
            QuestionModel question = questionnaire.getQuestao(i);
            if (question != null && question.getRespostaCorreta() != null && question.getRespostaCorreta().equals(answers.get(i))) {
                score++;
            }
        }
        return score;
    }
}
