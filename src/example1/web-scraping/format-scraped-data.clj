(ns example1.web-scraping.format-scraped-data
  (:require [clojure.string :refer [lower-case replace]]))


(defn return-weight-with-unit
  "function that returns weight and unit from the name of the product or nil"
  [product]
  (let [name (lower-case product)]
    (or (re-find #"[0-9]+ ?k?gr?" name) (re-find #"[0-9]+ ?kom(?:ada)?" name) (re-find #"[0-9]+ ?ka?ps?(?:ula)?" name) (re-find #"[0-9]+ ?m?l" name)
        (re-find #"[0-9]+ ?tableta" name) (re-find #"[0-9]+ ?tb" name) (re-find #"[0-9]+ ?cps" name))))

(defn extract-weight
  "extracts weight without unit"
  [product]
  (if-let [weight (return-weight-with-unit product)]
    (Integer/valueOf (re-find #"\d+" weight))
    100))


(defn extract-unit
  "function extracts unit of product and returns unit name"
  [product]
  (let [name (lower-case (:name product))]
    (cond
      (re-find #"[0-9]+ ?kg" name) "kg"
      (re-find #"[0-9]+ ?gr?" name) "g"
      (re-find #"[0-9]+ ?kom(?:ada)?" name) "komada"
      (or (re-find #"[0-9]+ ?cps" name) (re-find #"[0-9]+ ?ka?ps?(?:ula)?" name)) "kapsula"
      (re-find #"[0-9]+ ?ml" name) "ml"
      (re-find #"[0-9]+ ?l" name) "l"
      (or (re-find #"[0-9]+ ?tableta" name) (re-find #"[0-9]+ ?tb" name)) "tableta"
      :else "g")))

(defn change-name
  "removes weight and unit from product name"
  [product]
  (if-let [weight (return-weight-with-unit product)]
    (replace product (str " " weight) "")
    product))

(defn format-price
  "removes currency from price and returns price as number"
  [price]
  (Double/valueOf (re-find #"\d+" price)))


