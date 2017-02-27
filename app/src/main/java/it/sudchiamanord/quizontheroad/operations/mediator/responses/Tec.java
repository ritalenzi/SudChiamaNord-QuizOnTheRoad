package it.sudchiamanord.quizontheroad.operations.mediator.responses;

import java.util.ArrayList;
import java.util.List;

public class Tec
{
    // Active matches
    private Integer idpar;
    private String nomep;
    private Integer pubbl;
    private String timef;
    private String timei;

    // Login
    private String sessionKey;
    private String usern;
    private Integer iduse;
    private String cogno;
    private String nomeu;
    private String datan;
    private Integer debug;

    // Actual match
    private Partita partita;
    private Percorso percorso;
    private Boolean gameo;
    private Integer idain;
    private List<Indizi> indizi = new ArrayList<>();


    public Integer getIdpar() {
        return idpar;
    }

    public void setIdpar(Integer idpar) {
        this.idpar = idpar;
    }

    public String getNomep() {
        return nomep;
    }

    public void setNomep(String nomep) {
        this.nomep = nomep;
    }

    public Integer getPubbl() {
        return pubbl;
    }

    public void setPubbl(Integer pubbl) {
        this.pubbl = pubbl;
    }

    public String getTimef() {
        return timef;
    }

    public void setTimef(String timef) {
        this.timef = timef;
    }

    public String getTimei() {
        return timei;
    }

    public void setTimei(String timei) {
        this.timei = timei;
    }

    /**
     *
     * @return
     * The sessionKey
     */
    public String getSessionKey() {
        return sessionKey;
    }

    /**
     *
     * @param sessionKey
     * The sessionKey
     */
    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    /**
     *
     * @return
     * The usern
     */
    public String getUsern() {
        return usern;
    }

    /**
     *
     * @param usern
     * The usern
     */
    public void setUsern(String usern) {
        this.usern = usern;
    }

    /**
     *
     * @return
     * The iduse
     */
    public Integer getIduse() {
        return iduse;
    }

    /**
     *
     * @param iduse
     * The iduse
     */
    public void setIduse(Integer iduse) {
        this.iduse = iduse;
    }

    /**
     *
     * @return
     * The cogno
     */
    public String getCogno() {
        return cogno;
    }

    /**
     *
     * @param cogno
     * The cogno
     */
    public void setCogno(String cogno) {
        this.cogno = cogno;
    }

    /**
     *
     * @return
     * The nomeu
     */
    public String getNomeu() {
        return nomeu;
    }

    /**
     *
     * @param nomeu
     * The nomeu
     */
    public void setNomeu(String nomeu) {
        this.nomeu = nomeu;
    }

    /**
     *
     * @return
     * The datan
     */
    public String getDatan() {
        return datan;
    }

    /**
     *
     * @param datan
     * The datan
     */
    public void setDatan(String datan) {
        this.datan = datan;
    }

    /**
     *
     * @return
     * The debug
     */
    public Integer getDebug() {
        return debug;
    }

    /**
     *
     * @param debug
     * The debug
     */
    public void setDebug(Integer debug) {
        this.debug = debug;
    }

    /**
     *
     * @return
     * The partita
     */
    public Partita getPartita() {
        return partita;
    }

    /**
     *
     * @param partita
     * The partita
     */
    public void setPartita(Partita partita) {
        this.partita = partita;
    }

    /**
     *
     * @return
     * The percorso
     */
    public Percorso getPercorso() {
        return percorso;
    }

    /**
     *
     * @param percorso
     * The percorso
     */
    public void setPercorso(Percorso percorso) {
        this.percorso = percorso;
    }

    /**
     *
     * @return
     * The gameo
     */
    public Boolean getGameo() {
        return gameo;
    }

    /**
     *
     * @param gameo
     * The gameo
     */
    public void setGameo(Boolean gameo) {
        this.gameo = gameo;
    }

    /**
     *
     * @return
     * The idain
     */
    public Integer getIdain() {
        return idain;
    }

    /**
     *
     * @param idain
     * The idain
     */
    public void setIdain(Integer idain) {
        this.idain = idain;
    }

    /**
     *
     * @return
     * The indizi
     */
    public List<Indizi> getIndizi() {
        return indizi;
    }

    /**
     *
     * @param indizi
     * The indizi
     */
    public void setIndizi(List<Indizi> indizi) {
        this.indizi = indizi;
    }
}
