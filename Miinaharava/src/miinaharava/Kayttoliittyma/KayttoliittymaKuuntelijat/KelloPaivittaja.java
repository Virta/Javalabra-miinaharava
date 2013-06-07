/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package miinaharava.Kayttoliittyma.KayttoliittymaKuuntelijat;

import javax.swing.JLabel;
import miinaharava.Pelikentta.Moottori;

/**
 * Tämä luokka vastaa pelikentän kellon ja miinetiedon päivittämisestä.
 * Tästä luokasta luotu olio käynnistetään omassa säikeessää, jotta em. tiedot päivittyvät jatkuvasti pelin käynnistymisen jälkeen.
 * @author virta
 */
public class KelloPaivittaja implements Runnable {
    
    /**
     * JLabel-olio, johon päivitetään pelin aloituksesta kulunut aika sekunteina.
     */
    private JLabel kello;
    
    /**
     * Moottori, jota pelikenttä käyttää, josta kysellään kellon ja miinatiedon informaatio.
     */
    private Moottori moottori;
    
    /**
     * JLabel-olio, johon päivitetään kentän liputtamattomien miinojen määrä, oli liputus oikein tai väärin.
     */
    private JLabel miinojaJaljella;
    
    /**
     * Sisäinen totuusarvo, jota käytetään säikeen lopetuksen yhteydessä jos pelistä poistutaan ennen kuin peli loppui onnistuneesti.
     */
    private boolean keskeyta;
    
    /**
     * Konstruktori ottaa vastaan JLabel-oliot kelloksi ja miinatietokentäksi, sekä moottorin jolta informaatio päivityksen yhteydessä haetaan.
     * @param kello
     * @param moottori
     * @param miinoja 
     */
    public KelloPaivittaja(JLabel kello, Moottori moottori, JLabel miinoja){
        this.kello = kello;
        this.moottori = moottori;
        this.miinojaJaljella = miinojaJaljella;
        this.keskeyta = false;
    }

    /**
     * Kun tämän luokan olio luodaan säikeeseen ja säie käynnistetään aloitetaan while-silmukka joka toistuu kunnes peli loppui
     * onnistuneesti tai kunnes sisäinen muuttuja keskeyta saa arvon true, eli tätä luokkaa käyttävä luokka on kutsunut
     * tämän luokan metodia keskeytaPaivitys().
     */
    @Override
    public void run() {
        while (true){
            
            try {
                Thread.sleep(500);
            } catch (Exception e){
                
            }
            
            int aika = (int) moottori.getAika();
            String aikaString = (aika/60)+":"+(aika-((aika/60)*60));
            kello.setText(aikaString);
            
            if (moottori.peliLoppuiOnnistuneesti()){
                break;
            }
            
            if (keskeyta){
                break;
            }
            
        }
    }
    
    /**
     * Päivittää miinatiedon asianmukaiseen kenttään.
     */
    public void paivitaMiinaTieto(){
        miinojaJaljella.setText(moottori.getKentta().getMiinojaJaljella()+"");
    }
    
    /**
     * Asettaa sisäisen muuttujan keskeyta-arvoksi true, jolloin run()-metodin while silmukka voidaan keskeyttää tappamatta säiettä
     * jossa tämä luokka on ajossa.
     */
    public void keskeytaPaivitys(){
        this.keskeyta=true;
    }
    
}
