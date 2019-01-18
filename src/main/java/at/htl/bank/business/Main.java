package at.htl.bank.business;

import at.htl.bank.model.BankKonto;
import at.htl.bank.model.GiroKonto;
import at.htl.bank.model.SparKonto;
import com.sun.istack.internal.localization.NullLocalizable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Legen Sie eine statische Liste "konten" an, in der Sie die einzelnen Konten speichern
 *
 */
public class Main {

  // die Konstanten sind package-scoped wegen der Unit-Tests
  static final double GEBUEHR = 0.02;
  static final double ZINSSATZ = 3.0;

  static final String KONTENDATEI = "erstellung.csv";
  static final String BUCHUNGSDATEI = "buchungen.csv";
  static final String ERGEBNISDATEI = "ergebnis.csv";
  static List<BankKonto> konten = new ArrayList<>();

  
  /**
   * Führen Sie die drei Methoden erstelleKonten, fuehreBuchungenDurch und
   * findKontoPerName aus
   *
   * @param args
   */
  public static void main(String[] args) {
      erstelleKonten(KONTENDATEI);
      fuehreBuchungenDurch(BUCHUNGSDATEI);
      schreibeKontostandInDatei(ERGEBNISDATEI);
  }

  /**
   * Lesen Sie aus der Datei (erstellung.csv) die Konten ein.
   * Je nach Kontentyp erstellen Sie ein Spar- oder Girokonto.
   * Gebühr und Zinsen sind als Konstanten angegeben.
   *
   * Nach dem Anlegen der Konten wird auf der Konsole folgendes ausgegeben:
   * Erstellung der Konten beendet
   *
   * @param datei KONTENDATEI
   */
  private static void erstelleKonten(String datei) {
        String[] line;
        String name;
        double kontoStand;
        String kontoTyp;

        try(Scanner scanner = new Scanner(new FileReader(datei))) {
            scanner.nextLine();
            while(scanner.hasNextLine()) {
                line = scanner.nextLine().split(";");
                name = line[1];
                kontoStand = Double.parseDouble(line[2]);
                kontoTyp = line[0];

                if(kontoTyp.equalsIgnoreCase("SparKonto")) {
                    SparKonto sk = new SparKonto(name, kontoStand, ZINSSATZ);
                    konten.add(sk);
                } else if(kontoTyp.equalsIgnoreCase("GiroKonto")) {
                    GiroKonto gk = new GiroKonto(name, kontoStand, GEBUEHR);
                    konten.add(gk);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }

      System.out.println("Erstellung der Konten beendet :)");
  }

  /**
   * Die einzelnen Buchungen werden aus der Datei eingelesen.
   * Es wird aus der Liste "konten" jeweils das Bankkonto für
   * kontoVon und kontoNach gesucht.
   * Anschließend wird der Betrag vom kontoVon abgebucht und
   * der Betrag auf das kontoNach eingezahlt
   *
   * Nach dem Durchführen der Buchungen wird auf der Konsole folgendes ausgegeben:
   * Buchung der Beträge beendet
   *
   * Tipp: Verwenden Sie hier die Methode 'findeKontoPerName(String name)'
   *
   * @param datei BUCHUNGSDATEI
   */
  private static void fuehreBuchungenDurch(String datei) {
          String[] line;
          BankKonto vonKonto;
          BankKonto zuKonto;
          double betrag;

        try(Scanner scanner = new Scanner(new FileReader(datei))) {
            scanner.nextLine();
            while (scanner.hasNextLine()) {
                line = scanner.nextLine().split(";");
                vonKonto = findeKontoPerName(line[0]);
                zuKonto = findeKontoPerName(line[1]);
                betrag = Double.parseDouble(line[2]);

                vonKonto.abheben(betrag);
                zuKonto.einzahlen(betrag);
            }
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }

      System.out.println("Buchung der Beträge beendet");
  }

  /**
   * Es werden die Kontostände sämtlicher Konten in die ERGEBNISDATEI
   * geschrieben. Davor werden bei Sparkonten noch die Zinsen dem Konto
   * gutgeschrieben
   *
   * Die Datei sieht so aus:
   *
   * name;kontotyp;kontostand
   * Susi;SparKonto;875.5
   * Mimi;GiroKonto;949.96
   * Hans;GiroKonto;1199.96
   *
   * Vergessen Sie nicht die Überschriftenzeile
   *
   * Nach dem Schreiben der Datei wird auf der Konsole folgendes ausgegeben:
   * Ausgabe in Ergebnisdatei beendet
   *
   * @param datei ERGEBNISDATEI
   */
  private static void schreibeKontostandInDatei(String datei) {
        try(PrintWriter pw = new PrintWriter(new FileWriter(datei))) {
            pw.println("name;kontotyp;kontostand");
            for (BankKonto i:konten) {
                if(i instanceof SparKonto) {
                    ((SparKonto) i).zinsenAnrechnen();
                    pw.println(i.getName() + ";SparKonto;" + i.getKontoStand());
                } else if(i instanceof GiroKonto) {
                    pw.println(i.getName() + ";GiroKonto;" + i.getKontoStand());
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

      System.out.println("Ausgabe in Ergebnisdatei beendet");
  }

  /**
   */
  /**
   * Durchsuchen Sie die Liste "konten" nach dem ersten Konto mit dem als Parameter
   * übergebenen Namen
   * @param name
   * @return Bankkonto mit dem gewünschten Namen oder NULL, falls der Namen
   *         nicht gefunden wird
   */
  public static BankKonto findeKontoPerName(String name) {
      for (int i = 0; i < konten.size(); i++) {
          if (name.equals(konten.get(i).getName())) {
              return konten.get(i);
          }
      }

      return null;
  }

}
