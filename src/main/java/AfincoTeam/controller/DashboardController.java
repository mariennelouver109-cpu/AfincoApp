package AfincoTeam.controller;

/**
responsável pela exibição do dashboard do usuário na aplicação.
gerencia a apresentação dos dados do usuário logado, incluindo matérias,
conteúdos e informações de progresso acadêmico.
 */
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import AfincoTeam.Services.SubjectService;
import AfincoTeam.Services.ReportEntriesService;
import AfincoTeam.dto.BoletimDTO;
import AfincoTeam.model.SubjectModel;
import java.util.List;
import jakarta.servlet.http.HttpSession;

@Controller
public class DashboardController {

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    private final SubjectService subjectService;
    private final ReportEntriesService reportEntriesService;

    public DashboardController(SubjectService subjectService,
                               ReportEntriesService reportEntriesService) {
        this.subjectService = subjectService;
        this.reportEntriesService = reportEntriesService;
    }

    /**
     * Exibe o menu com todas as matérias disponíveis
     * @param model modelo para passar dados à view
     * @param session sessão HTTP para obter o usuário logado
     * @return template do menu
     */
    @GetMapping("/menu")
    public String menu(Model model, HttpSession session) {
        // obtém o usuário logado da sessão
        String username = (String) session.getAttribute("loggedUser");
        if (username == null) {
            return "redirect:/login"; // redireciona para login se não estiver logado
        }
        
        // Obtém todas as matérias disponíveis
        List<SubjectModel> subjects = subjectService.getAllSubjects();
        
        logger.info("Fetching subjects from database...");
        logger.info("Number of subjects found: {}", subjects.size());
        if (!subjects.isEmpty()) {
            subjects.forEach(subject -> logger.info("Subject: {} (ID: {}, Slug: {})", 
                subject.getSubjectName(), subject.getId(), subject.getSlug()));
        } else {
            logger.warn("No subjects found in database!");
        }
        
        model.addAttribute("username", username);
        model.addAttribute("subjects", subjects);
        
        return "menu";
    }

    /**
     * Exibe o boletim (relatório de notas) do usuário
     * @param model modelo para passar dados à view
     * @param session sessão HTTP para obter o usuário logado
     * @return template do boletim
     */
    @GetMapping("/boletim")
    public String boletim(Model model, HttpSession session) {
        // obtém o usuário logado da sessão
        String username = (String) session.getAttribute("loggedUser");
        if (username == null) {
            return "redirect:/login"; // redireciona para login se não estiver logado
        }

        try {
            BoletimDTO boletimDTO = reportEntriesService.getBoletim(username);
            model.addAttribute("boletim", boletimDTO.getBoletim());
            model.addAttribute("media", String.format("%.2f", boletimDTO.getMedia()));
            logger.info("Boletim loaded successfully for user: {}", username);
        } catch (IllegalArgumentException e) {
            logger.error("Error loading boletim: {}", e.getMessage());
            model.addAttribute("error", "Erro ao carregar o boletim: " + e.getMessage());
            return "error";
        }

        return "boletim";
    }
}
