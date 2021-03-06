package miinaharava.Entiteetit;

/**
 * Luokka jonka olioina kaikki tulokset ilmenevät, olioita ei suoraan tallenneta tiedostoon, vaan ne muunnetaan H(uman)R(eadable)-muotoon ja tallennetaan tiedostoon.
 *
 * @author virta
 */
public class Tulos implements Comparable<Object>{
    /**
     * Aika tallennetaan suoritus aikana tässä vaiheessa String-oliona.
     */
    private String aika;
    /**
     * Kenttäprofiili tallennetaan suoritus aikana sellaisenaan.
     */
    private KenttaProfiili profiili;
    /**
     * Käyttäjä tallennetaan suoritus aikana sellaisenaan.
     */
    private String pelaaja;
    /**
     * Tallennetaan totuusarvona päättyikö peli onnistuneesti.
     */
    private boolean loppuikoOnnistuneesti;
    
    /**
     * Konstruktori määritteleee parametreina annetut tiedot tuloksen sisäisiin muuttujiin.
     * @param aika Missä ajassa kentän pelaaminen pääättyi, muotoa min:sekuntia, esim 1:20.
     * @param profiili Millä kenttäprofiililla pelattiin.
     * @param pelaaja Kuka pelasi, anon jos käyttäjä ei kirjautunut.
     * @param loppuikoOnnistuneesti Tallennetaan, jotta pelaaja voi tarkastella erikseen epäonnistumisia ja onnistumisia.
     */
    public Tulos(String aika, KenttaProfiili profiili, String pelaaja, boolean loppuikoOnnistuneesti){
        this.aika = aika;
        this.pelaaja = pelaaja;
        this.profiili = profiili;
        this.loppuikoOnnistuneesti = loppuikoOnnistuneesti;
    }

    public String getAika() {
        return this.aika;
    }

    public KenttaProfiili getProfiili() {
        return this.profiili;
    }

    public String getPelaaja() {
        return this.pelaaja;
    }
    
    public boolean getOnnistuiko(){
        return this.loppuikoOnnistuneesti;
    }

    /**
     * Comparable toteutus, jotta tulokset voidaan järjestää ajan mukaiseen järjestykseen.
     * Tuloksien järjestäminen profiilin tai muun mukaan jätetään kutsuvalle metodille.
     * @param o mikä tahansa olio, muutetaan Tulos-olioksi.
     * @return erotus joko verrattavan ja tämän minuuteista, tai jos samat, erotus sekunneista.
     */
    @Override
    public int compareTo(Object o) {
        Tulos verrattava = (Tulos) o;
        String verrattavanAikaString = verrattava.getAika().trim();
        String[] verrattavanAikaSplit = verrattavanAikaString.split(":");
        int verrattavanMinuutit = Integer.parseInt(verrattavanAikaSplit[0]);
        int verrattavanSekunnit = Integer.parseInt(verrattavanAikaSplit[1]);
        
        String tamanAikaString = this.aika.trim();
        String[] tamanAikaSplit = tamanAikaString.split(":");
        int tamanMinuutit = Integer.parseInt(tamanAikaSplit[0]);
        int tamanSekunnit = Integer.parseInt(tamanAikaSplit[1]);
        
        if (verrattavanMinuutit==tamanMinuutit){
            return tamanSekunnit-verrattavanSekunnit;
        } else {
            return tamanMinuutit-verrattavanMinuutit;
        }
    }
    
}
