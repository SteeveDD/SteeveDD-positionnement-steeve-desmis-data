package com.analyse.pme;

import com.analyse.pme.utils.DatabaseUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSetup {

    public static void createTables() throws SQLException {
        String createProduitsTable = "CREATE TABLE IF NOT EXISTS produits (" +
                "reference TEXT PRIMARY KEY, " +
                "nom TEXT, " +
                "prix REAL, " +
                "stock INTEGER)";

        String createMagasinsTable = "CREATE TABLE IF NOT EXISTS magasins (" +
                "id_magasin INTEGER PRIMARY KEY, " +
                "ville TEXT, " +
                "nb_salaries INTEGER)";

        String createVentesTable = "CREATE TABLE IF NOT EXISTS ventes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "date TEXT, " +
                "ref_produit TEXT, " +
                "quantite INTEGER, " +
                "id_magasin INTEGER, " +
                "FOREIGN KEY (ref_produit) REFERENCES produits(reference) " +
                "ON DELETE CASCADE ON UPDATE CASCADE, " +
                "FOREIGN KEY (id_magasin) REFERENCES magasins(id_magasin) " +
                "ON DELETE CASCADE ON UPDATE CASCADE)";

        String createTotalCA = "CREATE TABLE IF NOT EXISTS total_ca (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ca DOUBLE, " +
                "date_debut TEXT, " +
                "date_fin TEXT, " +
                "date_enregistrement TEXT)";

        String createTotalRegion = "CREATE TABLE IF NOT EXISTS total_region (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nom_region TEXT, " +
                "region_ca DOUBLE, " +
                "date_debut TEXT, " +
                "date_fin TEXT, " +
                "date_enregistrement TEXT)";

        String createTotalProduct = "CREATE TABLE IF NOT EXISTS total_produit (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nom_produit TEXT, " +
                "ref TEXT, " +
                "total DOUBLE, " +
                "date_debut TEXT, " +
                "date_fin TEXT, " +
                "date_enregistrement TEXT)";

        Connection connection = DatabaseUtils.connect();
        Statement stmt = connection.createStatement();
        try{
            stmt.executeUpdate(createProduitsTable);
            System.out.println("Created produits table");
        } catch (SQLException e){
            System.out.println("Create produits table Error : " + e.getMessage());
        }
        try{
            stmt.executeUpdate(createMagasinsTable);
            System.out.println("Created magasin table");
        } catch (SQLException e){
            System.out.println("Create magasin table Error : " + e.getMessage());
        }
        try{
            stmt.executeUpdate(createVentesTable);
            System.out.println("Created ventes table");
        } catch (SQLException e){
            System.out.println("Create ventes table Error : " + e.getMessage());
        }
        try{
            stmt.executeUpdate(createTotalCA);
            System.out.println("Created total_ca table");
        } catch (SQLException e){
            System.out.println("Create total_ca table Error : " + e.getMessage());
        }
        try{
            stmt.executeUpdate(createTotalRegion);
            System.out.println("Created total_region table");
        } catch (SQLException e){
            System.out.println("Create total_region table Error : " + e.getMessage());
        }
        try{
            stmt.executeUpdate(createTotalProduct);
            System.out.println("Created total_produit table");
        } catch (SQLException e){
            System.out.println("Create total_produit table Error : " + e.getMessage());
        }
        stmt.close();
        connection.close();
    }
}