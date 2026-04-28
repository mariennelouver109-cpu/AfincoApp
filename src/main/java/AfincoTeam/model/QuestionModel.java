package AfincoTeam.model;

// Modelo para questões individuais dentro de um questionário
public class QuestionModel {

    private Integer id;
    private String enunciado;
    private String[] alternativas;
    private Integer respostaCorreta;

    // Constructores
    public QuestionModel() {}

    public QuestionModel(Integer id, String enunciado, String[] alternativas, Integer respostaCorreta) {
        this.id = id;
        this.enunciado = enunciado;
        this.alternativas = alternativas;
        this.respostaCorreta = respostaCorreta;
    }

    // Getters e Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }

    public String[] getAlternativas() {
        return alternativas;
    }

    public void setAlternativas(String[] alternativas) {
        this.alternativas = alternativas;
    }

    public Integer getRespostaCorreta() {
        return respostaCorreta;
    }

    public void setRespostaCorreta(Integer respostaCorreta) {
        this.respostaCorreta = respostaCorreta;
    }
}