(ns example1.formatscrapeddata
  (:require [clojure.string :refer [lower-case replace]]))


(defn return-weight-with-unit
  "function that returns weight and unit from the name of the product or nil"
  [name]
    (or (re-find #"[0-9]+ ?(?i)k?gr?" name) (re-find #"[0-9]+ ?(?i)kom(?:ada)?" name) (re-find #"[0-9]+ ?(?i)ka?ps?(?:ula)?" name)(re-find #"[0-9]+\,[0-9]+ ?(?i)m?l" name)(re-find #"[0-9]+\,[0-9]+" name) (re-find #"[0-9]+\.[0-9]+ ?(?i)m?l" name)
        (re-find #"[0-9]+\.[0-9]+" name)(re-find #"[0-9]+ ?(?i)m?l" name)
        (re-find #"[0-9]+ ?(?i)tableta" name) (re-find #"[0-9]+ ?(?i)tb" name) (re-find #"[0-9]+ ?(?i)cps" name) ""))

(defn extract-weight1
  "extracts weight without unit"
  [product]
  (if-let [ex (return-weight-with-unit (:name product))]
       (let [tr (replace ex "," ".")]
      (Double/valueOf  (or (re-find #"[0-9]+\.[0-9]+" tr)
                        (re-find #"[0-9]+\,[0-9]+" tr)
                        (re-find #"\d+" tr)
                        1)))
    1))
(defn extract-weight
  "extracts weight without unit"
  [product]
  (let [ex (return-weight-with-unit (:name product))]
    (if (empty? ex) 1
    (let [tr (replace ex "," ".")]
      (Double/valueOf  (or (re-find #"[0-9]+\.[0-9]+" tr)
                           (re-find #"[0-9]+\,[0-9]+" tr)
                           (re-find #"\d+" tr)
                           1))))))
 
 
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
      :else "ml")))

(defn return-new-category
  "returns new category for product"
  [product]
  (let [category (:category product)
        name (:name product)]
  (if (nil? category) 
    (or  (re-find #"EDP" name)  (re-find #"EDC" name)  (re-find #"EDT" name) (re-find #"SET" name) "EDP")
    (cond
     (= "PARFEMSKA VODA" category) "EDP"
     (= "TOALETNA VODA" category) "EDT"
     (= "KOLONJSKA VODA" category) "EDC"
     (= "SET" category) "SET"
     :else "EDP"))))

(defn remove-words-from-name
  "remove words from product name"
  [words-to-remove prod-name]
  (loop [remain-words words-to-remove name prod-name]
    (if (empty? remain-words) name
        (let [[word & remain] remain-words]
          (recur remain (clojure.string/replace-first name word ""))))))

(defn return-formated-product
  "formats product, makes all fields upper case, removes weight, brend name, giftsm category, and other unwanter words
   from product name, extracts unit and weight from product name"
  [product]
  (let [brend (clojure.string/upper-case (:brend product))
        new-cat (return-new-category product)
        category (or (:category product) new-cat)
        weight (return-weight-with-unit (:name product))
        tester (cond (not (nil? (:tester product))) "Yes"
                     (re-find #"(?i)TESTER" (:name product)) "Yes"
                     :else "No")
        name-upper (clojure.string/upper-case (:name product))
       ;b-count (count (re-seq (re-pattern brend) name-upper))
        words-to-remove [weight  "-" category "²" "TESTER" brend]
        ;final-words-to-remove (if (= b-count 1) words-to-remove (conj words-to-remove brend))
        product-name (clojure.string/trim
                      (remove-words-from-name (mapv #(clojure.string/upper-case %) words-to-remove) name-upper))]
    {:name (if (clojure.string/blank? product-name) brend product-name)
     :category (clojure.string/upper-case new-cat)
     :weight (extract-weight product)
     :unit (extract-unit product)
     :brend brend
     :tester tester}))

(defn format-price
  "removes currency from price and returns price as number"
  [price]
  (let [price-value (cond 
                      (re-find #"[0-9]+\,[0-9]+\.[0-9]+" price) (clojure.string/replace price "," "")
                     (re-find #"[0-9]+\.[0-9]{3}" price) (clojure.string/replace price "." "")
                      (re-find #"[0-9]+\.00" price) price
                      :else price
                      )
        price-f (re-find #"\d+" price-value)]
  (if (nil? price-value) 0 (Double/valueOf price-f))))



