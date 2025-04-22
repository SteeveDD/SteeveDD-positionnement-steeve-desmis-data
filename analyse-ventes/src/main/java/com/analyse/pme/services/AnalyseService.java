package com.analyse.pme.services;

import com.analyse.pme.utils.DatabaseUtils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;

public class AnalyseService {

    public static void totalCA() throws SQLException {
        Connection connection = DatabaseUtils.connect();
        Statement stmt = connection.createStatement();

        String queryDateRange = "SELECT MIN(date) AS date_debut, MAX(date) AS date_fin FROM ventes";
        ResultSet rsDateRange = stmt.executeQuery(queryDateRange);
        String currentDateDebut = null;
        String currentDateFin = null;
        if (rsDateRange.next()) {
            currentDateDebut = rsDateRange.getString("date_debut");
            currentDateFin = rsDateRange.getString("date_fin");
        }
        rsDateRange.close();

        String queryCheckPeriod = "SELECT id FROM total_ca WHERE date_debut = ? AND date_fin = ?";
        PreparedStatement psCheckPeriod = connection.prepareStatement(queryCheckPeriod);
        psCheckPeriod.setString(1, currentDateDebut);
        psCheckPeriod.setString(2, currentDateFin);
        ResultSet rsCheckPeriod = psCheckPeriod.executeQuery();

        String queryTotal = "SELECT SUM(v.quantite * p.prix) AS total_price FROM ventes v JOIN produits p ON v.ref_produit = p.reference";
        ResultSet rsTotal = stmt.executeQuery(queryTotal);
        double totalCA = 0;
        if (rsTotal.next()) {
            totalCA = rsTotal.getDouble("total_price");
        }

        BigDecimal caArrondi = new BigDecimal(totalCA).setScale(2, RoundingMode.HALF_UP);
        if (currentDateDebut != null && currentDateFin != null && !rsCheckPeriod.next()) {

            System.out.println("""
                \n
                -------------------------------------------
                \n
                """);

            rsTotal.close();
            saveCA(Double.parseDouble(String.valueOf(caArrondi)), currentDateDebut, currentDateFin, connection);
            System.out.println("Chiffre d'affaires total (" + currentDateDebut + " au " + currentDateFin + ") : " + caArrondi + "€ (ajoutée en base de donnéés)");
        } else {
            System.out.println("Chiffre d'affaires total (" + currentDateDebut + " au " + currentDateFin + ") : " +  caArrondi + "€");
        }

        rsCheckPeriod.close();
        psCheckPeriod.close();
        stmt.close();
        connection.close();
    }

    private static void saveCA(double ca, String dateDebut, String dateFin, Connection conn) throws SQLException {
        String insert = "INSERT INTO total_ca (ca, date_debut, date_fin, date_enregistrement) VALUES (?, ?, ?, datetime('now'))";
        PreparedStatement ps = conn.prepareStatement(insert);
        ps.setDouble(1, ca);
        ps.setString(2, dateDebut);
        ps.setString(3, dateFin);
        ps.executeUpdate();
        ps.close();
    }

    public static void totalProduct() throws SQLException {
        Connection connection = DatabaseUtils.connect();
        Statement stmt = connection.createStatement();

        String queryDateRange = "SELECT MIN(date) AS date_debut, MAX(date) AS date_fin FROM ventes";
        ResultSet rsDateRange = stmt.executeQuery(queryDateRange);
        String currentDateDebut = null;
        String currentDateFin = null;
        if (rsDateRange.next()) {
            currentDateDebut = rsDateRange.getString("date_debut");
            currentDateFin = rsDateRange.getString("date_fin");
        }
        rsDateRange.close();

        if (currentDateDebut != null && currentDateFin != null) {
            String query = """
            SELECT p.nom, v.ref_produit, SUM(v.quantite * p.prix) AS total_price
            FROM ventes v
            JOIN produits p ON v.ref_produit = p.reference
            WHERE v.date >= ? AND v.date <= ?
            GROUP BY v.ref_produit
        """;
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, currentDateDebut);
            ps.setString(2, currentDateFin);
            ResultSet rs = ps.executeQuery();

            System.out.println("""

            ---------- Chiffre d'affaires par produit (du %s au %s) ----------

        """.formatted(currentDateDebut, currentDateFin));

            while (rs.next()) {
                String nom = rs.getString("nom");
                String refProduit = rs.getString("ref_produit");
                double total = rs.getDouble("total_price");

                BigDecimal arrondi = new BigDecimal(total).setScale(2, RoundingMode.HALF_UP);

                if (!isTotalProductAlreadySaved(nom, refProduit, currentDateDebut, currentDateFin, connection)) {
                    saveTotalProduct(nom, refProduit, arrondi.doubleValue(), currentDateDebut, currentDateFin, connection);
                    System.out.println(nom + " (" + refProduit + ") : " + arrondi + "€ (ajoutée en base de donnéés)");
                } else {
                    System.out.println(nom + " (" + refProduit + ") : " + arrondi + "€");
                }
            }

            rs.close();
            ps.close();
        } else {
            System.out.println("Aucune date de vente trouvée pour déterminer la période d'analyse par produit.");
        }

        stmt.close();
        connection.close();
    }

    private static boolean isTotalProductAlreadySaved(String nomProduit, String ref, String dateDebut, String dateFin, Connection conn) throws SQLException {
        String queryCheck = "SELECT id FROM total_produit WHERE nom_produit = ? AND ref = ? AND date_debut = ? AND date_fin = ?";
        PreparedStatement psCheck = conn.prepareStatement(queryCheck);
        psCheck.setString(1, nomProduit);
        psCheck.setString(2, ref);
        psCheck.setString(3, dateDebut);
        psCheck.setString(4, dateFin);
        ResultSet rsCheck = psCheck.executeQuery();
        boolean exists = rsCheck.next();
        rsCheck.close();
        psCheck.close();
        return exists;
    }

    private static void saveTotalProduct(String nomProduit, String ref, double total, String dateDebut, String dateFin, Connection conn) throws SQLException {
        String insert = "INSERT INTO total_produit (nom_produit, ref, total, date_debut, date_fin, date_enregistrement) VALUES (?, ?, ?, ?, ?, datetime('now'))";
        PreparedStatement ps = conn.prepareStatement(insert);
        ps.setString(1, nomProduit);
        ps.setString(2, ref);
        ps.setDouble(3, total);
        ps.setString(4, dateDebut);
        ps.setString(5, dateFin);
        ps.executeUpdate();
        ps.close();
    }

    public static void totalCountry() throws SQLException {
        Connection connection = DatabaseUtils.connect();
        Statement stmt = connection.createStatement();

        String queryDateRange = "SELECT MIN(date) AS date_debut, MAX(date) AS date_fin FROM ventes";
        ResultSet rsDateRange = stmt.executeQuery(queryDateRange);
        String currentDateDebut = null;
        String currentDateFin = null;
        if (rsDateRange.next()) {
            currentDateDebut = rsDateRange.getString("date_debut");
            currentDateFin = rsDateRange.getString("date_fin");
        }
        rsDateRange.close();

        if (currentDateDebut != null && currentDateFin != null) {
            String query = """
            SELECT m.ville, SUM(v.quantite * p.prix) AS total_price
            FROM ventes v
            JOIN produits p ON v.ref_produit = p.reference
            JOIN magasins m ON v.id_magasin = m.id_magasin
            WHERE v.date >= ? AND v.date <= ?
            GROUP BY ville
        """;
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, currentDateDebut);
            ps.setString(2, currentDateFin);
            ResultSet rs = ps.executeQuery();

            System.out.println("""

            ---------- Chiffre d'affaires par région (du %s au %s) ----------

        """.formatted(currentDateDebut, currentDateFin));

            while (rs.next()) {
                String ville = rs.getString("ville");
                double total = rs.getDouble("total_price");

                BigDecimal arrondi = new BigDecimal(total).setScale(2, RoundingMode.HALF_UP);

                if (!isTotalCountryAlreadySaved(ville, currentDateDebut, currentDateFin, connection)) {
                    saveTotalCountry(ville, arrondi.doubleValue(), currentDateDebut, currentDateFin, connection);
                    System.out.println(ville + " : " + arrondi + "€  ajoutée en base de données");
                } else {
                    System.out.println(ville + " : " + arrondi + "€");
                }
            }

            rs.close();
            ps.close();
        } else {
            System.out.println("Aucune date de vente trouvée pour déterminer la période d'analyse par région.");
        }

        stmt.close();
        connection.close();
    }

    private static boolean isTotalCountryAlreadySaved(String ville, String dateDebut, String dateFin, Connection conn) throws SQLException {
        String queryCheck = "SELECT id FROM total_region WHERE nom_region = ? AND date_debut = ? AND date_fin = ?";
        PreparedStatement psCheck = conn.prepareStatement(queryCheck);
        psCheck.setString(1, ville);
        psCheck.setString(2, dateDebut);
        psCheck.setString(3, dateFin);
        ResultSet rsCheck = psCheck.executeQuery();
        boolean exists = rsCheck.next();
        rsCheck.close();
        psCheck.close();
        return exists;
    }

    private static void saveTotalCountry(String ville, double total, String dateDebut, String dateFin, Connection conn) throws SQLException {
        String insert = "INSERT INTO total_region (nom_region, region_ca, date_debut, date_fin, date_enregistrement) VALUES (?, ?, ?, ?, datetime('now'))";
        PreparedStatement ps = conn.prepareStatement(insert);
        ps.setString(1, ville);
        ps.setDouble(2, total);
        ps.setString(3, dateDebut);
        ps.setString(4, dateFin);
        ps.executeUpdate();
        ps.close();
    }
}

