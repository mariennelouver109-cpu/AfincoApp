package AfincoTeam.model;

import java.util.List;

// Modelo de questionário com várias questões
public class QuestionnaireModel {

    private List<QuestionModel> questoes;

    // Constructors
    public QuestionnaireModel() {}

    public QuestionnaireModel(List<QuestionModel> questoes) {
        this.questoes = questoes;
    }

    // Getters and Setters
    public List<QuestionModel> getQuestoes() {
        return questoes;
    }

    public void setQuestoes(List<QuestionModel> questoes) {
        this.questoes = questoes;
    }

    // Utility methods
    public int getTotalQuestions() {
        return questoes != null ? questoes.size() : 0;
    }

    public QuestionModel getQuestao(int index) {
        if (questoes != null && index >= 0 && index < questoes.size()) {
            return questoes.get(index);
        }
        return null;
    }
}