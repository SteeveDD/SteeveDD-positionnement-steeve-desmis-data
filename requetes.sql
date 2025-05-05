-- Requêtes de création des tables

CREATE TABLE IF NOT EXISTS produits (
    reference TEXT PRIMARY KEY,
    nom TEXT,
    prix REAL,
    stock INTEGER
);

CREATE TABLE IF NOT EXISTS magasins (
    id_magasin INTEGER PRIMARY KEY,
    ville TEXT,
    nb_salaries INTEGER
);

CREATE TABLE IF NOT EXISTS ventes (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    date TEXT,
    ref_produit TEXT,
    quantite INTEGER,
    id_magasin INTEGER,
    FOREIGN KEY (ref_produit) REFERENCES produits(reference)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (id_magasin) REFERENCES magasins(id_magasin)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS total_ca (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    ca DOUBLE,
    date_debut TEXT,
    date_fin TEXT,
    date_enregistrement TEXT
);

CREATE TABLE IF NOT EXISTS total_region (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nom_region TEXT,
    region_ca DOUBLE,
    date_debut TEXT,
    date_fin TEXT,
    date_enregistrement TEXT
);

CREATE TABLE IF NOT EXISTS total_produit (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nom_produit TEXT,
    ref TEXT,
    total DOUBLE,
    date_debut TEXT,
    date_fin TEXT,
    date_enregistrement TEXT
);






-- Requêtes d'analyse extraites de AnalyseService.java

-- Requête pour obtenir la période des ventes
SELECT MIN(date) AS date_debut, MAX(date) AS date_fin FROM ventes;

-- Requête pour vérifier si le CA total pour une période existe déjà 
SELECT id FROM total_ca WHERE date_debut = ? AND date_fin = ?;

-- Requête pour obtenir le chiffre d'affaires total
SELECT SUM(v.quantite * p.prix) AS total_price FROM ventes v JOIN produits p ON v.ref_produit = p.reference;

-- Requête pour enregistrer le CA total
INSERT INTO total_ca (ca, date_debut, date_fin, date_enregistrement) VALUES (?, ?, ?, datetime('now'));

-- Requête pour obtenir le chiffre d'affaires par produit pour une période
SELECT p.nom, v.ref_produit, SUM(v.quantite * p.prix) AS total_price
FROM ventes v
JOIN produits p ON v.ref_produit = p.reference
WHERE v.date >= ? AND v.date <= ?
GROUP BY v.ref_produit;

-- Requête pour vérifier si le CA par produit pour une période existe déjà
SELECT id FROM total_produit WHERE nom_produit = ? AND ref = ? AND date_debut = ? AND date_fin = ?;

-- Requête pour enregistrer le CA par produit
INSERT INTO total_produit (nom_produit, ref, total, date_debut, date_fin, date_enregistrement) VALUES (?, ?, ?, ?, ?, datetime('now'));

-- Requête pour obtenir le chiffre d'affaires par région pour une période
SELECT m.ville, SUM(v.quantite * p.prix) AS total_price
FROM ventes v
JOIN produits p ON v.ref_produit = p.reference
JOIN magasins m ON v.id_magasin = m.id_magasin
WHERE v.date >= ? AND v.date <= ?
GROUP BY ville;
-- Requête pour vérifier si le CA par région pour une période existe déjà
SELECT id FROM total_region WHERE nom_region = ? AND date_debut = ? AND date_fin = ?;

-- Requête pour enregistrer le CA par région
INSERT INTO total_region (nom_region, region_ca, date_debut, date_fin, date_enregistrement) VALUES (?, ?, ?, ?, datetime('now'));





-- Remplacer les ? par les valeurs attendues
-- Requête pour sélectionner un produit par référence
SELECT nom, prix, stock FROM main.produits WHERE reference = ?;

-- Requête pour mettre à jour un produit existant
UPDATE produits SET nom=?, prix=?, stock=? WHERE reference=?;

-- Requête pour insérer un nouveau produit
INSERT INTO produits (nom, reference, prix, stock) VALUES (?, ?, ?, ?);




-- Requête pour sélectionner une vente par date, référence produit, quantité et ID magasin [date(yyyy-mm-dd)]
SELECT * FROM ventes WHERE date = ? AND ref_produit = ? AND quantite = ? AND id_magasin = ?;
--Exemple :
SELECT * FROM ventes WHERE date = '2023-05-27' AND ref_produit = 'REF001' AND quantite = 5 AND id_magasin = 1;

-- Requête pour insérer une nouvelle vente
INSERT INTO ventes (date, ref_produit, quantite, id_magasin) VALUES (?, ?, ?, ?);




-- Requête pour sélectionner un magasin par ID
SELECT ville, nb_salaries FROM magasins WHERE id_magasin = ?;

-- Requête pour mettre à jour un magasin existant
UPDATE magasins SET ville=?, nb_salaries=? WHERE id_magasin = ?;

-- Requête pour insérer un nouveau magasin
INSERT INTO magasins (id_magasin, ville, nb_salaries) VALUES (?, ?, ?);