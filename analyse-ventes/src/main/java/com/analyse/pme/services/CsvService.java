package com.analyse.pme.services;

import com.analyse.pme.models.Product;
import com.analyse.pme.models.Sale;
import com.analyse.pme.models.Shop;
import com.analyse.pme.utils.DatabaseUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class CsvService {

    private static final String productsUrl = "https://docs.google.com/spreadsheets/d/e/2PACX-1vSawI56WBC64foMT9pKCiY594fBZk9Lyj8_bxfgmq-8ck_jw1Z49qDeMatCWqBxehEVoM6U1zdYx73V/pub?gid=0&single=true&output=csv&urp=gmail_link";
    private static final String shopsUrl = "https://docs.google.com/spreadsheets/d/e/2PACX-1vSawI56WBC64foMT9pKCiY594fBZk9Lyj8_bxfgmq-8ck_jw1Z49qDeMatCWqBxehEVoM6U1zdYx73V/pub?gid=714623615&single=true&output=csv&urp=gmail_link";
    private static final String salesUrl = "https://docs.google.com/spreadsheets/d/e/2PACX-1vSawI56WBC64foMT9pKCiY594fBZk9Lyj8_bxfgmq-8ck_jw1Z49qDeMatCWqBxehEVoM6U1zdYx73V/pub?gid=760830694&single=true&output=csv&urp=gmail_link";

    private static final ProductService productService = new ProductService();
    private static final SaleService saleService = new SaleService();
    private static final ShopService shopService = new ShopService();


    public static File downloadCsvFromUrl(String urlString) throws IOException, URISyntaxException {
        String fileName = switch (urlString) {
            case productsUrl -> "produits.csv";
            case salesUrl -> "ventes.csv";
            case shopsUrl -> "magasins.csv";
            default -> "unknown.csv";
        };
        System.out.println("Téléchargement  du fichier : '" + fileName + "'");
        File tempFile = new File(fileName);
        System.out.println("Téléchargement.... Veuillez patienter");
        if (!fileName.equals("unknown.csv")) {
            URL url = new URI(urlString).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(10000);
            int status = connection.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                System.err.println("Erreur de téléchargement. Code HTTP : " + status);
                return null;
            }
            try (InputStream in = connection.getInputStream();
                 OutputStream out = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            System.out.println("Fichier téléchargé : " + tempFile.getName());
            return tempFile;
        } else {
            return null;
        }
    }

    public static void processCsvFile(File file) throws IOException, SQLException {
        Connection connection = DatabaseUtils.connect();
        System.out.println("Traitement du fichier : " + file.getName());
        switch (file.getName()) {
            case "produits.csv" -> {
                List<Product> productList = productService.readProducts(file);
                productService.compareProducts(productList, connection);
            }
            case "ventes.csv" -> {
                List<Sale> saleList = saleService.readSales(file);
                saleService.compareSales(saleList, connection);
            }
            case "magasins.csv" -> {
                List<Shop> shopList = shopService.readShops(file);
                shopService.compareShops(shopList, connection);
            }
        }
        file.delete();
        connection.close();
    }

    public static void handleCsvFile(String urlString) {
        try {
            File tempFile = downloadCsvFromUrl(urlString);

            if (tempFile != null) {
                processCsvFile(tempFile);
            } else {
                System.err.println("Le téléchargement du fichier a échoué.");
            }
        } catch (IOException | URISyntaxException | SQLException e) {
            System.err.println("Erreur du fichier : " + e.getMessage());
        }
    }

    public static void execute() {
        List<String> csvUrls = List.of(
                productsUrl,
                salesUrl,
                shopsUrl
        );
        for (String url : csvUrls) {
            handleCsvFile(url);
        }
    }
}