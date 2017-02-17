package it.sudchiamanord.quizontheroad.operations.mediator.responses;

public class Percorso
{
    private Integer idper;
    private String nomep;
    private String notep;

    /**
     *
     * @return
     * The idper
     */
    public Integer getIdper() {
        return idper;
    }

    /**
     *
     * @param idper
     * The idper
     */
    public void setIdper(Integer idper) {
        this.idper = idper;
    }

    /**
     *
     * @return
     * The nomep
     */
    public String getNomep() {
        return nomep;
    }

    /**
     *
     * @param nomep
     * The nomep
     */
    public void setNomep(String nomep) {
        this.nomep = nomep;
    }

    /**
     *
     * @return
     * The notep
     */
    public String getNotep() {
        return notep;
    }

    /**
     *
     * @param notep
     * The notep
     */
    public void setNotep(String notep) {
        this.notep = notep;
    }
}
