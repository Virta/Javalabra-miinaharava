/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package miinaharava.Kayttoliittyma.KayttoliittymaKuuntelijat;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import miinaharava.Kayttoliittyma.AloitusNakyma;
import miinaharava.Kayttoliittyma.SisaltoFrame;

/**
 *
 * @author virta
 */
public class UudenPelinAloitusKuuntelija implements ActionListener {

    private SisaltoFrame nakyma;
    private JButton helppoNappi;
    private JButton keskivaikeaNappi;
    private JButton vaikeaNappi;
    private JButton geneerinen;

    public UudenPelinAloitusKuuntelija(JButton helppoN, JButton keskivN, JButton vaikaN, SisaltoFrame nakyma) {
        this.nakyma = nakyma;
        this.helppoNappi = helppoN;
        this.keskivaikeaNappi = keskivN;
        this.vaikeaNappi = vaikaN;
    }

    public UudenPelinAloitusKuuntelija(JButton nappi, SisaltoFrame nakyma) {
        this.geneerinen = nappi;
        this.nakyma = nakyma;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == helppoNappi) {
            uusiHelppoPeli();
        } else if (source == keskivaikeaNappi) {
            uusiKeskivaikeaPeli();
        } else if (source == vaikeaNappi) {
            uusiVaikeaPeli();
        } else if (source == geneerinen) {
            uusiCustomPeli();
        }
    }

    private void uusiHelppoPeli() {
//        AloitusNakyma nak = new AloitusNakyma(nakyma);
//        SwingUtilities.invokeLater(nak);
    }

    private void uusiKeskivaikeaPeli() {
    }

    private void uusiVaikeaPeli() {
    }

    private void uusiCustomPeli() {
        AloitusNakyma nak = new AloitusNakyma(nakyma);
        SwingUtilities.invokeLater(nak);
    }
}