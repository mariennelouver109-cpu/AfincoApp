package AfincoTeam.Services;

//imports
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;
import AfincoTeam.model.UserModel;
import AfincoTeam.repository.UserRepository;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service // anotação para indicar que esta classe é um serviço do Spring
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) { //injeção de dependência do UserRepository
        this.userRepository = userRepository;
    }

    // cadastro
    public UserModel register(String username, String senha) {
        validateUsername(username);
        validatePassword(senha);

        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalStateException("Nome de usuário já está em uso");
        }

        UserModel user = new UserModel();
        user.setId(UUID.randomUUID());
        user.setUsername(username);
        user.setPassword(encoder.encode(senha));

        return userRepository.save(user);
    }

    // autenticação
    public Optional<UserModel> authenticate(String username, String password) {
        /*busca o usuário pelo nome de usuário, se encontrado, verifica se a senha fornecida
         corresponde à senha armazenada usando o BCryptPasswordEncoder*/
        return userRepository.findByUsername(username)
                .filter(user -> encoder.matches(password, user.getPassword()));
    }
    
    // login
    public boolean login(String username, String password) {
        return authenticate(username, password).isPresent();
    }

    // procura por nome de usuário
    public Optional<UserModel> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // troca de senha
    public void changePassword(String username, String currentPassword, String newPassword) {
        validatePassword(newPassword);

        // autentica o usuário com a senha atual
        Optional<UserModel> userOpt = authenticate(username, currentPassword);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Senha atual incorreta");
        }
        // Atualiza a senha do usuário
        UserModel user = userOpt.get();
        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);
    }

    // atualização do nome de usuário
    public void update(UUID id, String novoUsername) {
        validateUsername(novoUsername);

        Optional<UserModel> existingUser = userRepository.findByUsername(novoUsername);
        if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
            throw new IllegalStateException("Nome de usuário já está em uso");
        }

        UserModel user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        user.setUsername(novoUsername); // atualiza o nome de usuário
        userRepository.save(user); // salva as alterações no banco de dados
    }

    // deletar usuário
    public void delete(UUID id) { // recebe o ID do usuário a ser deletado
        userRepository.deleteById(id); // chama o método deleteById do DAO para deletar o usuário do banco de dados
    }

    private void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Nome de usuário não pode ser vazio");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Senha não pode ser vazia");
        }
    }
}