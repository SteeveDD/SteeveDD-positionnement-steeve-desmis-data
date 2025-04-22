package com.analyse.pme;

import com.analyse.pme.services.AnalyseService;
import com.analyse.pme.services.CsvService;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException {
        DatabaseSetup.createTables();
        CsvService.execute();
        AnalyseService.totalCA();
        AnalyseService.totalProduct();
        AnalyseService.totalCountry();
        System.out.println("Analyses terminées. L'application va se terminer.");

    }
}
