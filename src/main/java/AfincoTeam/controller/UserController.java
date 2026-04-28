package AfincoTeam.controller;

/**
 * Controlador responsável pelo gerenciamento de usuários na aplicação AfincoApp.
 * Esta classe lida com operações de autenticação, cadastro, atualização e exclusão de usuários,
 * utilizando sessões HTTP para manter o estado de login.
 */
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import AfincoTeam.Services.UserService;
import jakarta.servlet.http.HttpSession;

// anotação para indicar que esta classe é um controlador do Spring MVC, responsável por lidar com as requisições relacionadas aos usuários
@Controller
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    //------------ login -----------
    // método para exibir a página de login, mapeado para a URL "/login" usando o método GET
    @GetMapping("/login") 
    public String loginPage() {
        return "login";
    }

    // método para processar o login, mapeado para a URL "/login" usando o método POST, recebe o nome de usuário e a senha como parâmetros
    @PostMapping("/login")
    public String login(@RequestParam String username, // recebe o nome de usuário do formulário de login
                        @RequestParam String password, // recebe a senha do formulário de login
                        Model model,
                        HttpSession session) { // recebe a sessão HTTP para armazenar o usuário logado

        // chama o método login do UserService para verificar se as credenciais são válidas, armazena o resultado em uma variável boolean
        boolean ok = service.login(username, password);

        if (ok) { // se o login for bem-sucedido, armazena o usuário na sessão e redireciona para /menu
            session.setAttribute("loggedUser", username);
            return "redirect:/menu";
        // se o login falhar, adiciona uma mensagem de erro ao modelo e retorna a view "login" para que o usuário possa tentar novamente
        } else {
            model.addAttribute("erro", "Login inválido");
            return "login";
        }
    }

    // --------- cadastro ---------
    // método para exibir a página de cadastro, mapeado para a URL "/cadastro" usando o método GET
    @GetMapping("/cadastro")
    public String cadastroPage() { // retorna a view "cadastro" para exibir o formulário de cadastro
        return "cadastro";
    }

    // método para processar o cadastro, mapeado para a URL "/cadastro" usando o método POST, recebe o nome de usuário e senha como parâmetros
    @PostMapping("/cadastro")
    public String cadastro(@RequestParam String username,
                           @RequestParam String password,
                           Model model) {
        try {
            service.register(username, password);
            model.addAttribute("mensagem", "Cadastro realizado com sucesso. Agora faça login.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("erro", ex.getMessage());
        }
        return "cadastro";
    }

    // ----------------- delete -----------
    // método para processar a exclusão do usuário logado, mapeado para a URL "/delete" usando o método POST
    @PostMapping("/delete")
    public String delete(HttpSession session) { // usa o usuário logado
        String username = (String) session.getAttribute("loggedUser");
        if (username == null) {
            return "redirect:/login";
        }

        var userOpt = service.findByUsername(username);
        if (userOpt.isPresent()) {
            service.delete(userOpt.get().getId());
        }
        session.invalidate();
        return "redirect:/login";
    }

    // ----------------- update -----------
    // método para processar a atualização do nome de usuário, mapeado para a URL "/update" usando o método POST, recebe o novo nome de usuário como parâmetro
    @PostMapping("/update")
    public String update(@RequestParam String username, // recebe o novo nome de usuário do formulário de atualização
                         Model model,
                         HttpSession session) {

        String currentUsername = (String) session.getAttribute("loggedUser");
        if (currentUsername == null) {
            return "redirect:/login";
        }

        try {
            // obtém o usuário atual
            var userOpt = service.findByUsername(currentUsername);
            if (userOpt.isEmpty()) {
                model.addAttribute("erro", "Usuário não encontrado");
                return "perfil";
            }
            UUID id = userOpt.get().getId();

            // chama o método update do UserService para atualizar o nome de usuário
            service.update(id, username);
            // atualiza a sessão com o novo username
            session.setAttribute("loggedUser", username);
            model.addAttribute("mensagem", "Nome de usuário atualizado com sucesso");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("erro", ex.getMessage());
        }
        model.addAttribute("username", currentUsername);
        return "perfil";
    }

    // ------------------ index -----------
    // método para redirecionar a página inicial para a página de login, mapeado para a URL "/" usando o método GET
    @GetMapping("/")
    public String index() {
        return "redirect:/login"; // redireciona para a URL "/login" para exibir a página de login quando o usuário acessar a raiz do aplicativo
    }

    // ------------------ logout -----------
    // método para fazer logout, mapeado para a URL "/logout" usando o método GET
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // invalida a sessão, removendo todos os atributos
        return "redirect:/login"; // redireciona para a página de login
    }

    // ------------------ perfil -----------
    // método para exibir a página de perfil do usuário, mapeado para a URL "/perfil" usando o método GET
    @GetMapping("/perfil")
    public String perfilPage(Model model, HttpSession session) {
        String username = (String) session.getAttribute("loggedUser");
        if (username == null) {
            return "redirect:/login";
        }
        model.addAttribute("username", username);
        return "perfil";
    }

    // ----------------- change password -----------
    // método para processar a alteração de senha, mapeado para a URL "/change-password" usando o método POST
    @PostMapping("/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Model model,
                                 HttpSession session) {

        String username = (String) session.getAttribute("loggedUser");
        if (username == null) {
            return "redirect:/login";
        }

        try {
            if (!newPassword.equals(confirmPassword)) {
                throw new IllegalArgumentException("Nova senha e confirmação não coincidem");
            }

            service.changePassword(username, currentPassword, newPassword);
            model.addAttribute("mensagem", "Senha alterada com sucesso");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("erro", ex.getMessage());
        }
        model.addAttribute("username", username);
        return "perfil";
    }
}