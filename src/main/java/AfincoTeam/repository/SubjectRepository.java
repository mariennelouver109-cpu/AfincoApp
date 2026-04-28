package AfincoTeam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import AfincoTeam.model.SubjectModel;
import java.util.Optional;

// interface para o repositório de matérias, estende JpaRepository para fornecer métodos de acesso a dados
public interface SubjectRepository extends JpaRepository<SubjectModel, Integer> {
    Optional<SubjectModel> findBySlug(String slug);
}

