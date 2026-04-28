package AfincoTeam.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import AfincoTeam.Services.ContentService;
import AfincoTeam.Services.SubjectService;
import AfincoTeam.Services.ExamService;
import AfincoTeam.model.ContentModel;
import AfincoTeam.model.SubjectModel;
import AfincoTeam.model.ExamModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

// Controlador para gerenciar matérias e exibir conteúdos relacionados
@Controller
@RequestMapping("/materia")
public class SubjectController {

    private final SubjectService subjectService;
    private final ContentService contentService;
    private final ExamService examService;

    public SubjectController(SubjectService subjectService, ContentService contentService, ExamService examService) {
        this.subjectService = subjectService;
        this.contentService = contentService;
        this.examService = examService;
    }

    @GetMapping("/{slug}")
    public String getSubjectContent(@PathVariable String slug, Model model) {
        try {
            // Busca a matéria pelo slug
            SubjectModel subject = subjectService.getSubjectBySlug(slug);

            // Busca todos os conteúdos da matéria
            List<ContentModel> allContents = contentService.getContentsBySubject(subject.getId());

            // Busca todos os exames da matéria
            List<ExamModel> allExams = examService.getExamsBySubject(subject.getId());

            // Agrupa conteúdos por período (1, 2, 3, 4), mantendo ordem
            Map<Integer, List<ContentModel>> contentsByPeriod = allContents.stream()
                    .collect(Collectors.groupingBy(
                            ContentModel::getPeriod,
                            TreeMap::new,  // TreeMap mantém chaves ordenadas
                            Collectors.toList()
                    ));

            // Agrupa exames por período (1, 2, 3, 4), mantendo ordem
            Map<Integer, List<ExamModel>> examsByPeriod = allExams.stream()
                    .collect(Collectors.groupingBy(
                            exam -> exam.getPeriod().intValue(),
                            TreeMap::new,  // TreeMap mantém chaves ordenadas
                            Collectors.toList()
                    ));

            // Combina períodos de conteúdos e exames
            Set<Integer> allPeriods = new TreeSet<>();
            allPeriods.addAll(contentsByPeriod.keySet());
            allPeriods.addAll(examsByPeriod.keySet());

            // Constrói lista de períodos com seus conteúdos e exames
            List<PeriodDTO> periodDTOs = new ArrayList<>();
            for (Integer periodNumber : allPeriods) {
                List<ContentModel> contentsInPeriod = contentsByPeriod.getOrDefault(periodNumber, Collections.emptyList());
                List<ExamModel> examsInPeriod = examsByPeriod.getOrDefault(periodNumber, Collections.emptyList());
                
                // Converte Contents em formato esperado por conteúdos.html
                List<ContentDTO> contentDTOs = contentsInPeriod.stream()
                        .map(content -> {
                            String url = null;
                            if (content.getUrl() != null) {
                                if (content.getUrl().startsWith("http://") || content.getUrl().startsWith("https://")) {
                                    // External URL, use directly
                                    url = content.getUrl();
                                } else {
                                    // Supabase path, get public URL
                                    try {
                                        url = contentService.getPublicUrl(content.getId());
                                    } catch (Exception e) {
                                        // If unable to get public URL, leave as null
                                    }
                                }
                            }
                            return new ContentDTO(
                                content.getId(),
                                content.getContentName(),
                                content.getType(),
                                url
                            );
                        })
                        .collect(Collectors.toList());
                
                // Converte Exams em formato esperado por conteúdos.html
                List<ExamDTO> examDTOs = examsInPeriod.stream()
                        .map(exam -> new ExamDTO(
                            exam.getId(),
                            exam.getExamName(),
                            "/questionario/" + exam.getId()
                        ))
                        .collect(Collectors.toList());
                
                String periodName = getPeriodName(periodNumber);
                periodDTOs.add(new PeriodDTO(periodNumber, periodName, contentDTOs, examDTOs));
            }

            // Passa dados para a view
            model.addAttribute("materia", subject.getSubjectName());
            model.addAttribute("periods", periodDTOs);

            return "conteudos";

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Matéria não encontrada: " + e.getMessage());
            return "error";
        }
    }

    /**
     * Converte número de período em nome legível
     * @param periodNumber número do período (1, 2, 3, 4)
     * @return nome do período (e.g., "1º Bimestre")
     */
    private String getPeriodName(Integer periodNumber) {
        if (periodNumber == null) return "Sem período";
        String[] names = {"", "1º Bimestre", "2º Bimestre", "3º Bimestre", "4º Bimestre"};
        return (periodNumber > 0 && periodNumber < names.length) ? names[periodNumber] : periodNumber + "º Período";
    }

    /**
     * DTO para representar um período com seus conteúdos e exames
     */
    public static class PeriodDTO {
        public Integer number;
        public String nome;
        public List<ContentDTO> conteudos;
        public List<ExamDTO> exames;

        public PeriodDTO(Integer number, String nome, List<ContentDTO> conteudos, List<ExamDTO> exames) {
            this.number = number;
            this.nome = nome;
            this.conteudos = conteudos;
            this.exames = exames;
        }
    }

    /**
     * DTO para representar um conteúdo adaptado ao formato esperado por conteúdos.html
     */
    public static class ContentDTO {
        public Integer id;
        public String nome;
        public String tipo;
        public String openUrl;

        public ContentDTO(Integer id, String nome, String tipo, String openUrl) {
            this.id = id;
            this.nome = nome;
            this.tipo = tipo;
            this.openUrl = openUrl;
        }
    }

    /**
     * DTO para representar um exame adaptado ao formato esperado por conteúdos.html
     */
    public static class ExamDTO {
        public UUID id;
        public String nome;
        public String questionarioUrl;

        public ExamDTO(UUID id, String nome, String questionarioUrl) {
            this.id = id;
            this.nome = nome;
            this.questionarioUrl = questionarioUrl;
        }
    }
}
