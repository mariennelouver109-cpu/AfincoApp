package AfincoTeam.dto;

import java.util.List;

public class BoletimDTO {
    private List<BoletimEntryDTO> boletim; // Lista de entradas do boletim, cada uma representando um exame e seu resultado
    private Double media; // Média geral do boletim

    public BoletimDTO(List<BoletimEntryDTO> boletim, Double media) {
        this.boletim = boletim;
        this.media = media;
    }

    public List<BoletimEntryDTO> getBoletim() {
        return boletim;
    }

    public void setBoletim(List<BoletimEntryDTO> boletim) {
        this.boletim = boletim;
    }

    public Double getMedia() {
        return media;
    }

    public void setMedia(Double media) {
        this.media = media;
    }
}
