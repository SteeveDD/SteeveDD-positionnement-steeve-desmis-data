package com.analyse.pme.services;

import com.analyse.pme.models.Product;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductService {


    public List<Product> readProducts(File file) throws IOException {
        List<Product> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                Product p = new Product();
                p.setName(parts[0].trim());
                p.setReference(parts[1].trim());
                p.setPrice(Double.parseDouble(parts[2].trim()));
                p.setStock(Integer.parseInt(parts[3].trim()));
                list.add(p);
            }
        }
        return list;
    }

    public void compareProducts(List<Product> csvProducts, Connection conn) throws SQLException {
        for (Product csvProd : csvProducts) {
            String query = "SELECT nom, prix, stock FROM main.produits WHERE reference = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, csvProd.getReference());
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String name = rs.getString("nom");
                    double price = rs.getDouble("prix");
                    int stock = rs.getInt("stock");

                    if (!name.equals(csvProd.getName()) || price != csvProd.getPrice() || stock != csvProd.getStock()) {

                        String update = "UPDATE produits SET nom=?, prix=?, stock=? WHERE reference=?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(update)) {
                            updateStmt.setString(1, csvProd.getName());
                            updateStmt.setDouble(2, csvProd.getPrice());
                            updateStmt.setInt(3, csvProd.getStock());
                            updateStmt.setString(4, csvProd.getReference());
                            updateStmt.executeUpdate();
                            System.out.println("Produit modifié : " + csvProd.getReference());
                        }
                    }
                } else {

                    String insert = "INSERT INTO produits (nom, reference, prix, stock) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insert)) {
                        insertStmt.setString(1, csvProd.getName());
                        insertStmt.setString(2, csvProd.getReference());
                        insertStmt.setDouble(3, csvProd.getPrice());
                        insertStmt.setInt(4, csvProd.getStock());
                        insertStmt.executeUpdate();
                        System.out.println("Nouveau produit : " + csvProd.getReference());
                    }
                }
                rs.close();
            }
        }
    }

}
