package AfincoTeam.model;

// importação das anotações do JPA para mapear a classe como uma entidade e definir a tabela correspondente no banco de dados
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// anotação para indicar que esta classe é uma entidade do JPA, mapeada para a tabela "contents" no banco de dados
@Entity
// anotação para definir o nome da tabela correspondente a esta entidade no banco de dados
@Table(name = "contents") 


public class ContentModel {

    @Id
    private Integer id;

    @Column(name = "subject_id")
    private Integer subjectId;

    @Column(name = "period")
    private Integer period;

    @Column(name = "content_name")
    private String contentName;

    private String type;
    private String url;

    // getters e setters para acessar e modificar os campos da entidade ContentModel
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getSubjectId() {
        return subjectId;
    }
    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }
    public Integer getPeriod() {
        return period;
    }
    public void setPeriod(Integer period) {
        this.period = period;
    }
    public String getContentName() {
        return contentName;
    }
    public void setContentName(String contentName) {
        this.contentName = contentName;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    
}
