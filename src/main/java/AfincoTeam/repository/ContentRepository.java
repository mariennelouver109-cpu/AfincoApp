package AfincoTeam.repository;

//importação das classes necessárias
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import AfincoTeam.model.ContentModel;

// interface para o repositório de conteúdos, estende JpaRepository para fornecer métodos de acesso a dados
public interface ContentRepository extends JpaRepository<ContentModel, Integer> {
    List<ContentModel> findBySubjectId(Integer subjectId);
    
    List<ContentModel> findByPeriod(Integer period);
    
    List<ContentModel> findBySubjectIdAndPeriod(Integer subjectId, Integer period);
}


