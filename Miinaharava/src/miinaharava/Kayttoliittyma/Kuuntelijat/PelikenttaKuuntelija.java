/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package miinaharava.Kayttoliittyma.Kuuntelijat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import miinaharava.Entiteetit.KenttaProfiili;
import miinaharava.Entiteetit.Tulos;
import miinaharava.Kayttoliittyma.AloitusNakyma;
import miinaharava.Kayttoliittyma.SisaltoFrame;
import miinaharava.Pelikentta.Moottori;
import miinaharava.Pelikentta.Solu;
import miinaharava.Kayttoliittyma.Virheilmoitus;
import miinaharava.Aanentoisto.Aanet;
import miinaharava.Tallennus.TallennusLogiikka;

/**
 * Tämä luokka vastaa pelikentässä tapahtuvien painallusten toiminnallisuuden
 * toteuttamisesta, se on vielä kesken.
 *
 * @author virta
 */
public class PelikenttaKuuntelija implements MouseListener {

    /**
     * SisaltoFrame, joka on kaikille näkymä- ja kuuntelijaluokille yhteinen
     * jonka ikkunaan komponentit piirretään.
     */
    private SisaltoFrame nakyma;
    /**
     * Matriisi solupainikkeita, joista varsinainen pelin kulku tapahtuu.
     */
    private JButton[][] soluPainikkeet;
    /**
     * Kellokenttä, johon kellon arvo päivittyy.
     */
    private JLabel kelloKentta;
    /**
     * Miinatietokenttä, johon liputtamattomien miinojen määrä päivittyy.
     */
    private JLabel miinatietoKentta;
    /**
     * Pelimoottori, jota tämä luokka käyttää.
     */
    private Moottori moottori;
    /**
     * KelloPaivittaja-olio, joka käynnistetään tästä luokasta.
     */
    private KelloPaivittaja paivittaja;
    /**
     * Säie johon päivittäjä liitetään, joka käynnistetään tästä luokasta.
     */
    private Thread paivittajaSaie;
    /**
     * Sisäinen muuttuja, jolla ehkäistään säikeen aloittaminen aina uudestaan.
     */
    private boolean paivittajaAloitettu;
    /**
     * Tuloksen muodostamiseen tarvittu profiili.
     */
    private KenttaProfiili profiili;
    /**
     * Takaisin-nappi sisäisenä muuttujana jotta sen toiminta voidaan poistaa
     * hetkellisesti käytöstä.
     */
    private JButton takaisinNappi;
    /**
     * Annetaan mahdollisuus ohjelmalliseen mykistämiseen.
     */
    private JButton mykistysNappi;
    /**
     * Mykistää äänet.
     */
    private boolean mykistysPaalla;

    /**
     * Konstruktorissa alustetaan sisäiset muuttujat.
     *
     * @param nakyma
     * @param solut
     * @param kello
     * @param miinatieto
     * @param moottori
     * @param paivittaja
     * @param profiili
     * @param takaisin
     */
    public PelikenttaKuuntelija(SisaltoFrame nakyma, JButton[][] solut, JLabel kello, JLabel miinatieto, Moottori moottori, KelloPaivittaja paivittaja, KenttaProfiili profiili, JButton takaisin, JButton mykista) {
        this.nakyma = nakyma;
        this.soluPainikkeet = solut;
        this.kelloKentta = kello;
        this.miinatietoKentta = miinatieto;
        this.soluPainikkeet = solut;
        this.moottori = moottori;
        this.paivittaja = paivittaja;
        this.paivittajaSaie = new Thread(this.paivittaja);
        this.paivittajaAloitettu = false;
        this.profiili = profiili;
        this.takaisinNappi = takaisin;
        this.mykistysNappi = mykista;
    }

    /**
     * Otetaan kiinni vain tapahtuma jossa käyttäjä vapauttaa hiiren painikkeen.
     * Voi olla että käyttäjä huomaa painaessaan, että hän painaa väärää solua,
     * joten annamme tällä menetelmällä hieman pelivaraa. Jos tuloksen
     * tallennuksessa taphatuu virhe se propagoi stackiä ylös tähän metodiin
     * jossa poikkeus käsitellään näyttämällä tallennuksen virheilmoitus
     * spesifioituna tälle kuuntelijalle, keskeytetään päivityssäie ja palataan
     * aloitusnäkymään.
     *
     * @param e
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        try {
            int[] koords = haeKoordinaatit(e.getSource());

            if (koords != null) {
                int mouseEvent = e.getButton();
                switch (mouseEvent) {
                    case 1:
                        avaaYksi(koords[0], koords[1]);
                        break;
                    case 2:
                        avaaMonta(koords[0], koords[1]);
                        break;
                    case 3:
                        asetaLippu(koords[0], koords[1]);
                        break;
                }
            }

            if (e.getSource() == mykistysNappi) {
                mykistaAanet();
            }
        } catch (Exception ex) {
            kasittelePoikkeus(ex);
        }
    }

    private void mykistaAanet() {
        if (this.mykistysPaalla) {
            this.mykistysPaalla = false;
            this.mykistysNappi.setText("((< >))");
        } else {
            this.mykistysPaalla = true;
            this.mykistysNappi.setText(" </> ");
        }
    }

    /**
     * Käyttäjän avatessa solun vapauttamalla hiiren vasemman painikkeen
     * kutsutaan tätä metodia.
     *
     * @param x
     * @param y
     * @throws Exception Heittää poikkeuksen jos pelin päättyessä tuloksen
     * tallennuksessa tapahtuu virhe, heitetään kutsuvalle metodille.
     */
    private void avaaYksi(int x, int y) throws Exception {
        boolean palaute = moottori.aukaiseYksi(x, y);
        toimiPalautteesta(palaute);
    }

    /**
     * Käyttäjän vapauttaessa hiiren oikean painikkeen kutsutaan tätä metodia.
     *
     * @param x
     * @param y
     */
    private void asetaLippu(int x, int y) throws FileNotFoundException, IOException, InterruptedException {
        if (!moottori.getKentta().getSolu(x, y).isAuki()) {
            moottori.getKentta().asetaFlagi(x, y);
            this.miinatietoKentta.setText("     Miinoja: " + moottori.getKentta().getMiinojaJaljella());
            paivitaNakyma();
            if (moottori.getKentta().getSolu(x, y).getFlagi() == 1 && !mykistysPaalla) {
                Thread aaniSaie = new Thread(new Aanet("flag"));
                aaniSaie.start();
            }
        }
    }

    /**
     * Käyttäjän avatessa solun vapauttamalla hiiren keskimmäisen painikkeen
     * kutsutaan tätä metodia.
     *
     * @param x
     * @param y
     * @throws Exception Heittää poikkeuksen jos pelin päättyessä tuloksen
     * tallennuksessa tapahtuu virhe, heitetään kutsuvalle metodille.
     */
    private void avaaMonta(int x, int y) throws Exception {
        boolean palaute = moottori.aukaiseMonta(x, y);
        toimiPalautteesta(palaute);
    }

    /**
     * Pelin päättyessä kutsutaan tätä metodia; luodaan uusi Tulos-olio joka
     * tallennetaan SisaltoFramen sisältämään tuloslistaan.
     *
     * @param loppuikoOnnistuneesti
     * @throws Exception Heittää poikkeuksen jos tuloksen tallennuksessa
     * tapahtuu virhe, heitetään kutsuvalle metodille.
     */
    private void luoTulos(boolean loppuikoOnnistuneesti) throws Exception {
        Tulos tulos = new Tulos(kelloKentta.getText().trim(), profiili, nakyma.getKirjautunutNimimerkki(), loppuikoOnnistuneesti);
        this.nakyma.getTulokset().add(tulos);
        TallennusLogiikka.tallenna(this.nakyma.getTulokset());
    }

    /**
     * Pelin päättyessä kutsutaan tätä metodia; avataan kaikki solut ja
     * poistetaan kentän nappien toimivuus, jos käyttäjä liputti solun väärin
     * tämäkin ilmenee kentässä.
     */
    private void naytaLopetusNakyma() {
        for (int i = 0; i < soluPainikkeet.length; i++) {
            for (int j = 0; j < soluPainikkeet.length; j++) {
                JButton soluPainike = soluPainikkeet[i][j];
                Solu solu = moottori.getKentta().getSolu(i, j);
                soluPainike.setEnabled(false);
                if (solu.isMiina() && solu.getFlagi() != 1) {
                    soluPainike.setText("O");
                } else if (!solu.isMiina() && solu.getFlagi() > 0) {
                    soluPainike.setText("X");
                }
            }
        }
    }

    /**
     * Kun peli päättyy kutsutaan tätä metodia; luodaan uusi ikkuna jossa
     * näytetään pelin tulos, samalla poistetaan toiminnallisuus
     * pelikenttä-näkymästä kunnes käyttäjä on sulkenut tulosikkunan "sulje"
     * napista.
     *
     * @param loppuikoOnnistuneesti
     */
    private void naytaPeliTulosIkkuna(boolean loppuikoOnnistuneesti) {
        JFrame peliTulosIkkuna = new JFrame("Peli loppui!");
        peliTulosIkkuna.setLayout(new BorderLayout());
        peliTulosIkkuna.setPreferredSize(new Dimension(200, 100));
        peliTulosIkkuna.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        JLabel onnistui = new JLabel();
        if (loppuikoOnnistuneesti) {
            onnistui.setText("Onneksi olkoon!");
        } else {
            onnistui.setText("Voi HARMI!");
        }

        JLabel aika = new JLabel("Aikasi: " + kelloKentta.getText());
        JButton suljeNappula = new JButton("Sulje");
        SuljeNappulanKuuntelija kuuntelija = new SuljeNappulanKuuntelija(this.nakyma.getFrame(), peliTulosIkkuna, suljeNappula, takaisinNappi);
        suljeNappula.addActionListener(kuuntelija);

        peliTulosIkkuna.getContentPane().add(onnistui, BorderLayout.NORTH);
        peliTulosIkkuna.getContentPane().add(aika, BorderLayout.CENTER);
        peliTulosIkkuna.getContentPane().add(suljeNappula, BorderLayout.SOUTH);

        this.takaisinNappi.setEnabled(false);
        this.nakyma.getFrame().setFocusable(false);
        peliTulosIkkuna.pack();
        peliTulosIkkuna.setVisible(true);
    }

    /**
     * Pelaajan avatessa solun pävitetään näkymä ja kentän painikkeet tällä
     * metodilla.
     */
    private void paivitaNakyma() throws FileNotFoundException, IOException, InterruptedException {
        for (int i = 0; i < soluPainikkeet.length; i++) {
            for (int j = 0; j < soluPainikkeet.length; j++) {
                JButton soluPainike = soluPainikkeet[i][j];
                Solu solu = moottori.getKentta().getSolu(i, j);
                if (solu.isAuki()) {
                    if (solu.getVieressaMiinoja() > 0) {
                        int vieressaMiinoja = moottori.getKentta().getSolu(i, j).getVieressaMiinoja();
                        soluPainike.setText(vieressaMiinoja + "");
                        soluPainike.setForeground(getSolulleVari(vieressaMiinoja));
                        soluPainike.setBackground(Color.LIGHT_GRAY);
                    } else {
                        soluPainike.setEnabled(false);
                    }
                } else {
                    asetaNapilleFlagi(solu, soluPainike);
                }
            }
        }
    }

    /**
     * Muutetaan vieressä olevien miinojen määrä väriksi tällä metodilla.
     *
     * @param miinoja
     * @return Color-väriarvo (määrätty arvo) joka vastaa miinojen määrää.
     */
    private Color getSolulleVari(int miinoja) {
        switch (miinoja) {
            case 1:
                return Color.BLUE;
            case 2:
                return Color.GREEN;
            case 3:
                return Color.RED;
            case 4:
                return Color.BLACK;
            case 5:
                return Color.getHSBColor(0, 1, 0.5f);
            case 6:
                return Color.ORANGE;
            case 7:
                return Color.YELLOW;
            default:
                return Color.CYAN;
        }
    }

    /**
     * Pelaajan avatessa solun kutsutaan tätä metodia painikkeen
     * identifioimiseksi.
     *
     * @param source
     * @return palautetaan kahden pituinen lista koordinaateista, jossa [0] = x
     * ja [1] = y, ei palauteta mitään jos painike ei ollut päällä.
     */
    private int[] haeKoordinaatit(Object source) {
        for (int i = 0; i < soluPainikkeet.length; i++) {
            boolean keskeyta = false;
            for (int j = 0; j < soluPainikkeet.length; j++) {
                if (source == soluPainikkeet[i][j]) {
                    JButton painike = (JButton) source;
                    if (painike.isEnabled()) {
                        return new int[]{i, j};
                    }
                }
            }
        }
        return null;
    }

    /**
     * Moottorin antama palaute käsitellään tässä metodissa ja toimitaan sen
     * mukaisesti.
     *
     * @param palaute
     * @throws Exception Heittää poikkeuksen jos tuloksen tallennuksessa
     * tapahtuu virhe, heitetään eteenpäin.
     */
    private void toimiPalautteesta(boolean palaute) throws Exception {
        if (palaute) {
            aloitaPaivittaja();

            paivitaNakyma();

            tarkistaLoppuikoPeli(palaute);

        } else {
            peliPaattyiEpaonnistuneesti(palaute);
        }
    }

    /**
     * Käyttäjän asettaessa lipun miinalle kutsutaan tätä metodia.
     *
     * @param solu
     * @param soluPainike
     */
    private void asetaNapilleFlagi(Solu solu, JButton soluPainike) throws FileNotFoundException, IOException {
        if (soluPainike.isEnabled()) {
            int flagi = solu.getFlagi();
            String soluText = "";
            Color soluVari = null;

            switch (flagi) {
                case 0:
                    soluText = " ";
                    break;
                case 1:
                    soluText = "!";
                    soluVari = Color.RED;
                    break;
                case 2:
                    soluText = "?";
                    soluVari = Color.CYAN;
            }
            soluPainike.setBackground(soluVari);
            soluPainike.setText(soluText);
        }
    }

    /**
     * Tämä metodi käsittelee poikkeuksen, joka johtuu tallennuksessa
     * tapahtuneesta virheestä. Näytetään käyttäjäystävällinen virheilmoitus ja
     * palataan aloitusnäkymään.
     *
     * @param ex
     */
    private void kasittelePoikkeus(Exception ex) {
        if (ex.equals(FileNotFoundException.class)) {
            Virheilmoitus.naytaVirheilmoitus("Pelikenttäkuuntelijassa virhe: " + ex.toString());
        } else {
            Virheilmoitus.naytaVirheilmoitus("Äänentoistossa virhe: " + ex.toString());
        }
        paivittaja.keskeytaPaivitys();
        AloitusNakyma aloitusNakyma = new AloitusNakyma(this.nakyma);
        SwingUtilities.invokeLater(aloitusNakyma);
    }

    /**
     * Pelin kulussa kutsutaan tätä metodia, ja käynnistetään kellon
     * päivittäjäsäie vain jos peli alkoi juuri.
     */
    private void aloitaPaivittaja() {
        if (!paivittajaAloitettu) {
            paivittajaSaie.start();
            paivittajaAloitettu = true;
        }
    }

    /**
     * Pelin kulussa kutsutaan tätä metodia tarkistamaan moottorilta päättyikö
     * peli onnistuneesti.
     *
     * @param palaute
     * @throws Exception Heittää poikkeuksen jos tuloksen tallennuksessa
     * tapahtuu virhe, heitetään eteenpäin.
     */
    private void tarkistaLoppuikoPeli(boolean palaute) throws Exception {
        if (moottori.peliLoppuiOnnistuneesti()) {
            paivittaja.keskeytaPaivitys();
            luoTulos(palaute);
            naytaLopetusNakyma();
            naytaPeliTulosIkkuna(palaute);
            
            if (!mykistysPaalla) {
                Thread aaniSaie = new Thread(new Aanet("levelComplete"));
                aaniSaie.start();
            }
        } else {
            if (!mykistysPaalla) {
                Thread aaniSaie = new Thread(new Aanet("singleOpen"));
                aaniSaie.start();
            }
        }
    }

    /**
     * Jos moottorin palaute on kutsuvassa metodissa false peli päättyi
     * epäonnistuneesti ja kutsutaan tätä metodia.
     *
     * @param palaute
     * @throws Exception Heittää poikkeuksen jos tuloksen tallennuksessa
     * tapahtui virhe, heitetään eteenpäin.
     */
    private void peliPaattyiEpaonnistuneesti(boolean palaute) throws Exception {
        paivittaja.keskeytaPaivitys();
        luoTulos(palaute);
        naytaLopetusNakyma();
        naytaPeliTulosIkkuna(palaute);
        if (!mykistysPaalla) {
            Thread aaniSaie = new Thread(new Aanet("explosion"));
            aaniSaie.start();
        }
    }

    /**
     * Ei käytössä.
     *
     * @param e
     */
    @Override
    public void mousePressed(MouseEvent e) {
    }

    /**
     * Ei käytössä.
     *
     * @param e
     */
    @Override
    public void mouseEntered(MouseEvent e) {
    }

    /**
     * Ei käytössä.
     *
     * @param e
     */
    @Override
    public void mouseExited(MouseEvent e) {
    }

    /**
     * Ei käytössä.
     *
     * @param e
     */
    @Override
    public void mouseClicked(MouseEvent e) {
    }
}
