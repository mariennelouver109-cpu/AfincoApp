package AfincoTeam.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import AfincoTeam.Services.ExamService;
import AfincoTeam.Services.QuestionnaireService;
import AfincoTeam.Services.ReportEntriesService;
import AfincoTeam.model.QuestionModel;
import java.util.UUID;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/questionario")
public class QuestionnaireController {

    private final QuestionnaireService questionnaireService;
    private final ExamService examService;
    private final ReportEntriesService reportEntriesService;

    public QuestionnaireController(QuestionnaireService questionnaireService, ExamService examService,
                                  ReportEntriesService reportEntriesService) {
        this.questionnaireService = questionnaireService;
        this.examService = examService;
        this.reportEntriesService = reportEntriesService;
    }

    @GetMapping("/{examId}")
    public String startQuestionnaire(@PathVariable UUID examId, Model model) {
        try {
            return showQuestion(examId, 0, 0, model);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Erro ao carregar a prova: " + e.getMessage());
            return "error";
        } catch (Exception e) {
            model.addAttribute("error", "Erro inesperado ao carregar a prova: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/proxima")
    public String nextQuestion(
            @RequestParam UUID examId,
            @RequestParam int indiceAtual,
            @RequestParam int resposta,
            @RequestParam int pontuacao,
            Model model,
            HttpSession session) {

        try {
            int currentIndex = Math.max(0, indiceAtual - 1);
            int total = questionnaireService.getTotalQuestions(examId);
            boolean correct = false;
            if (currentIndex < total) {
                correct = questionnaireService.isAnswerCorrect(examId, currentIndex, resposta);
            }
            int score = pontuacao + (correct ? 1 : 0);
            int nextIndex = currentIndex + 1;

            if (nextIndex >= total) {
                int percent = questionnaireService.calculateScorePercent(score, total);
                
                // Salva o resultado do exame para o usuário
                try {
                    String username = (String) session.getAttribute("loggedUser");
                    if (username == null) {
                        return "redirect:/login"; // redireciona para login se não estiver logado
                    }
                    reportEntriesService.saveExamResult(username, examId.toString(), "0", score, total, (double) percent);
                } catch (Exception e) {
                    System.err.println("Erro ao salvar resultado do exame: " + e.getMessage());
                }
                
                model.addAttribute("examName", examService.getExamById(examId).getExamName());
                model.addAttribute("score", score);
                model.addAttribute("total", total);
                model.addAttribute("percent", percent);
                return "questionario-result";
            }

            return showQuestion(examId, nextIndex, score, model);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Erro ao processar resposta: " + e.getMessage());
            return "error";
        } catch (Exception e) {
            model.addAttribute("error", "Erro inesperado ao processar resposta: " + e.getMessage());
            return "error";
        }
    }

    private String showQuestion(UUID examId, int questionIndex, int score, Model model) {
        try {
            int total = questionnaireService.getTotalQuestions(examId);
            QuestionModel question = questionnaireService.getQuestion(examId, questionIndex);

            model.addAttribute("questao", question);
            model.addAttribute("numero", questionIndex + 1);
            model.addAttribute("total", total);
            model.addAttribute("examId", examId);
            model.addAttribute("pontuacao", score);
            model.addAttribute("percentual", questionnaireService.calculateCompletionPercent(questionIndex + 1, total));
            model.addAttribute("examName", examService.getExamById(examId).getExamName());

            return "questionario";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Erro ao carregar a questão: " + e.getMessage());
            return "error";
        } catch (Exception e) {
            model.addAttribute("error", "Erro inesperado ao carregar a questão: " + e.getMessage());
            return "error";
        }
    }
}
