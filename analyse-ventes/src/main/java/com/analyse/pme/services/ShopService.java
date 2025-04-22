package com.analyse.pme.services;

import com.analyse.pme.models.Shop;
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

public class ShopService {



    public List<Shop> readShops(File file) throws IOException {
        List<Shop> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                Shop sh = new Shop();
                sh.setId(Integer.parseInt(parts[0].trim()));
                sh.setCity( parts[1].trim());
                sh.setEmployees(Integer.parseInt(parts[2].trim()));
                list.add(sh);
            }
        }
        return list;
    }

    public void compareShops(List<Shop> csvShops, Connection conn) throws SQLException {
        for (Shop csvShop : csvShops) {
            String query = "SELECT ville,nb_salaries FROM magasins WHERE id_magasin = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, csvShop.getId());
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String ville = rs.getString("ville");
                    int salaries = rs.getInt("nb_salaries");
                    if (!ville.equals(csvShop.getCity()) || salaries != csvShop.getEmployees()) {
                        String update = "UPDATE magasins SET ville=?, nb_salaries=? WHERE id_magasin = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(update)) {
                            updateStmt.setString(1, csvShop.getCity());
                            updateStmt.setInt(2, csvShop.getEmployees());
                            updateStmt.executeUpdate();
                            System.out.println("Magasin modifié : " + csvShop.toString());
                        }
                    }

                } else {
                    String insert = "INSERT INTO magasins (id_magasin, ville, nb_salaries) VALUES (?, ?, ?)";
                    try (PreparedStatement updateStmt = conn.prepareStatement(insert)) {
                        updateStmt.setInt(1, csvShop.getId());
                        updateStmt.setString(2, csvShop.getCity());
                        updateStmt.setInt(3, csvShop.getEmployees());
                        updateStmt.executeUpdate();
                        System.out.println("Nouveau magasin : " + csvShop.toString());
                    }
                }
                rs.close();
            }
        }
    }

}
