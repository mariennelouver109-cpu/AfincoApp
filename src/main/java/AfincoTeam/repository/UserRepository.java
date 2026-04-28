package AfincoTeam.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import AfincoTeam.model.UserModel;

// interface para o repositório de usuários, estende JpaRepository para fornecer métodos de acesso a dados
public interface UserRepository extends JpaRepository<UserModel, UUID> {

    // método para encontrar um usuário pelo nome de usuário, retorna um Optional<UserModel> para lidar com a possibilidade de o usuário não ser encontrado
    Optional<UserModel> findByUsername(String username);
    // método para encontrar um usuário pelo ID, retorna um Optional<UserModel> para lidar com a possibilidade de o usuário não ser encontrado
    Optional<UserModel> findById(UUID id);
    // método para deletar um usuário pelo ID, recebe o ID do usuário a ser deletado
    void deleteById(UUID id);
}

