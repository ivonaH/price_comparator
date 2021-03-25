(ns example1.productsscraping
  (:require [reaver :refer [parse extract-from text attr select]]
            [example1.db :refer [db create-db insert-db find-in-db insert-all drop-all-tables]]
            [example1.formatscrapeddata :refer [ format-price return-formated-product]]
            [clojure.string :as str]))

(defn prepare-url [url]
  (parse (slurp url)))


(defn extract-products-from-url
  "this function extracts products from specified url"
  [url next]
  (extract-from (prepare-url next) (:product-main-html url)
                [:name :price :brend :tester]
                (:product-name url) text
                (:product-price url) text
                (:brend url) text
                (:tester url) text))

(defn get-all-pages
  "this function returns all products from specified url"
  [url]
  (loop [page (:page-start url) all-products []]
    (println page)
    (let [page-to-visit (str (:link url) (:page url) page)
          products (extract-products-from-url url page-to-visit)
          prod-per-page (count products)]
      (if (< prod-per-page (:res-per-page url)) (into all-products products)
          (recur (inc page) (into all-products products))))))


"List of stores from which data will be scraped"
(def stores-data [{:name "original parfemi"
                   :link "https://www.originalniparfemi.rs/parfemi/zenski+dostupnost-da/" :product-main-html ".products .product"
                   :product-name ".title" :product-price ".main-price" :brend ".breand" :category ".category >a"
                   :tester ".item-tester"
                   :page-start 0
                   :res-per-page 12
                   :page "page-"}
                  {:name "parfemi online"
                   :link "https://parfemi-online.com/zenski-parfemi" :product-main-html ".products .product .product-item-info"
                   :product-name ".product-item-link" :product-price ".price-container .price" :brend ".productListBrand"
                   :tester "nil"
                   :category "nill"
                   :page-start 1
                   :res-per-page 20
                   :page "?p="}])


(defn stores-loop
  " extracting fields to save to db from vector of stores"
  [stores]
  (loop [remaining-stores stores results []]
    (if (empty? remaining-stores) results
        (let [[store & remain] remaining-stores]
          (recur remain (conj results {:name (store :name)}))))))

(defn create-products&pricestore
  "inserts products and prices to db"
  [products store-id]
  (loop [remaining-products products results []]
    (if (empty? remaining-products) (insert-all db "storeprice" (set results))
        (let [[product & remain] remaining-products
              gift (or (first (re-find #"\+(.*)" (:name product))) "No")
              price (if (string? (:price  product)) (:price product) (first (:price product)))
              formated-product (return-formated-product (assoc product :name (str/replace (:name product) gift "")))
              id (or (:id (first (find-in-db db "product" formated-product)))
                     (get (first (insert-db db "product" formated-product)) (keyword "last_insert_rowid()")))
                                ]
          (recur remain (conj results {:store_id store-id :product_id id :price (format-price price)
                                       :gift gift
                                     :date (.format (java.text.SimpleDateFormat. "dd/MM/yyyy") (new java.util.Date))}))))))


(defn visit-store
  "returns store with given name from db"
  [store-data]
  (let [store (first (find-in-db db "store" {:name (:name store-data)}))
        products (set (get-all-pages store-data))]
 
    (println (str "SCRAPING" (:name store) "FINISHED"))
    (create-products&pricestore products (store :id))))



(defn visit-all-stores
  "goes through all defined stores and scrapes products from store website"
  [stores]
  (loop [remaining-stores stores results []]
    (if (empty? remaining-stores) results
        (let [[store & remain] remaining-stores]
          (recur remain (into results (visit-store store)))))))

(defn before-starting-server
  "creates db, inserts stores to db and saves all products from all stores to db" []
  (drop-all-tables db [:storeprice :product :store])
  (create-db db)
  (insert-all db "store" (stores-loop stores-data))
  (visit-all-stores stores-data))

;(before-starting-server)