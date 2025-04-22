package com.analyse.pme.services;

import com.analyse.pme.models.Sale;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SaleService {

    public List<Sale> readSales(File file) throws IOException {
        List<Sale> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                Sale s = new Sale();

                s.setDate(parts[0].trim());
                s.setProductRef(parts[1].trim());
                s.setQuantity(Integer.parseInt(parts[2].trim()));
                s.setShopId(Integer.parseInt(parts[3].trim()));
                list.add(s);
            }
        }
        return list;
    }

    public void compareSales(List<Sale> csvSales, Connection conn) throws SQLException {
        for (Sale csvSale : csvSales) {
            String query = "SELECT * FROM ventes WHERE date = ? AND ref_produit = ? AND quantite = ? AND id_magasin = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, csvSale.getDate());
                stmt.setString(2, csvSale.getProductRef());
                stmt.setInt(3, csvSale.getQuantity());
                stmt.setInt(4, csvSale.getShopId());
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String date = rs.getString("date");
                    String ref = rs.getString("ref_produit");
                    int quantite = rs.getInt("quantite");
                    int idShop = rs.getInt("id_magasin");

                    if (!date.equals(csvSale.getDate()) && !ref.equals(csvSale.getProductRef()) && quantite != csvSale.getQuantity() && idShop != csvSale.getShopId()) {
                        String insert = "INSERT INTO ventes (date, ref_produit, quantite, id_magasin) VALUES (?, ?, ?, ?)";
                        try (PreparedStatement insertStmt = conn.prepareStatement(insert)) {
                            insertStmt.setString(1, csvSale.getDate());
                            insertStmt.setString(2, csvSale.getProductRef());
                            insertStmt.setInt(3, csvSale.getQuantity());
                            insertStmt.setInt(4, csvSale.getShopId());
                            insertStmt.executeUpdate();
                            System.out.println("Nouvelle vente ajouté : " + csvSale.toString());
                        }
                    }
                } else {
                        String insert = "INSERT INTO ventes (date, ref_produit, quantite, id_magasin) VALUES (?, ?, ?, ?)";
                        try (PreparedStatement insertStmt = conn.prepareStatement(insert)) {
                            insertStmt.setString(1, csvSale.getDate());
                            insertStmt.setString(2, csvSale.getProductRef());
                            insertStmt.setInt(3, csvSale.getQuantity());
                            insertStmt.setInt(4, csvSale.getShopId());
                            insertStmt.executeUpdate();
                            System.out.println("Nouvelle vente ajouté : " + csvSale.toString());
                        }
                }
                rs.close();
            }
        }
    }

}
