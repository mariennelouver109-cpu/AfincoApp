package AfincoTeam.dto;

public class BoletimEntryDTO { // DTO para representar uma entrada do boletim, contendo a matéria, a nota e o percentual de acertos
    private String materia;
    private Double nota;
    private Double percentual;

    public BoletimEntryDTO(String materia, Double nota, Double percentual) {
        this.materia = materia;
        this.nota = nota;
        this.percentual = percentual;
    }

    public String getMateria() {
        return materia;
    }

    public void setMateria(String materia) {
        this.materia = materia;
    }

    public Double getNota() {
        return nota;
    }

    public void setNota(Double nota) {
        this.nota = nota;
    }

    public Double getPercentual() {
        return percentual;
    }

    public void setPercentual(Double percentual) {
        this.percentual = percentual;
    }
}
