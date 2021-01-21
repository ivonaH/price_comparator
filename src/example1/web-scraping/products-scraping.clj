(ns example1.webscraping.products-scraping
  (:require [reaver :refer [parse extract-from text attr select]]
            [example1.db :refer [db create-db insert-db find-in-db insert-all]]
            [example1.web-scraping.format-scraped-data :refer [change-name extract-weight extract-unit format-price]]))

(defn prepare-url [url]
  (parse (slurp url)))

(defn extract-categories-from-url
  "this function extracts categories from specified url"
  [url]
  (-> (select (prepare-url (:link url)) (:categories-html url))
      (attr :href)))


(defn extract-products-from-url
  "this function extracts products from specified url"
  [url category-url]
  (extract-from (prepare-url category-url) (:product-main-html url)
                [:name :price]
                (:product-name url) text
                (:product-price url) text))

(defn get-all-pages
  "this function returns all products from specified category"
  [url category-url]
  (loop [next-page "" all-products []]
    (let [category (str category-url next-page)
          products (extract-products-from-url url category)
          next (-> (select (prepare-url category) (:next-page url)) (attr :href))
          next-page (if (string? next) next
                        (last next))]
      (println (str "NEXT: ") next-page)
      (if (nil? next-page) (into all-products products)
          (recur next-page (into all-products products))))))

(defn get-all-products-from-url
  "this function returns all products (from every category) from website"
  [main-url]
  (let [categories-url-list (extract-categories-from-url main-url)]
    (loop [remaining-url categories-url-list results []]
      (if (empty? remaining-url) results
          (let [[url & remain] remaining-url]
            (println remaining-url)
            (let [products-from-category (get-all-pages main-url url)]
              (if (empty? products-from-category) (recur remain results)
                  (recur remain (into results products-from-category)))))))))


"List of stores from which data will be scraped"
(def stores
  [{:name "maslina" :link "https://www.maslina.rs/" :categories-html ".cat-card"  :product-main-html ".grid" :product-name ".product-card__header> h3 > a" :product-price ".product-card__price" :next-page ".pagination >li >a"}
   {:name "herbasana" :link "http://www.herbasana.rs/" :categories-html ".top-bar-menu >li.menu-item-object-product_cat > a"  :product-main-html " .category > ul >li" :product-name " .title-wrapper" :product-price "> h5" :next-page ".pagination-wrapper .pagination .next"}
   {:name "boneda" :link "https://boneda.rs/online-prodaja/" :categories-html ".cat-item >a" :product-main-html " .box-text-products" :product-name ".name" :product-price ".price" :next-page ".page-numbers .next"}
   {:name "biomarket" :link "https://www.biomarket.rs/" :categories-html ".main_search_dropdown > ul >li >a" :product-main-html ".product-prev__item" :product-name ".product-title" :product-price ".product-price" :next-page ".pagination >li >a"}
   {:name "bio-una" :link "https://shop.bio-una.com/" :categories-html ".category-link" :product-main-html ".product" :product-name ".product-title" :product-price ".price .amount" :next-page ".products-footer >a"}])


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
    (if (empty? remaining-products) (insert-all db "storeprice" results)
        (let [[product & remain] remaining-products
              price (if (string? (:price  product)) (:price product) (first (:price product)))
              formated-product {:name (change-name (:name product)) :weight (extract-weight product)
                                :unit (extract-unit product)}
              id (or (:id (first (find-in-db db "product" formated-product)))
                     (get (first (insert-db db "product" formated-product)) (keyword "last_insert_rowid()")))]
          (recur remain (conj results {:store_id store-id :product_id id :price (format-price price)
                                       :date (.format (java.text.SimpleDateFormat. "dd/MM/yyyy") (new java.util.Date))}))))))


(defn visit-store
  "returns store with given name from db"
  [store-data]
  (let [store (first (find-in-db db "store" {:name (:name store-data)}))
        products (get-all-products-from-url store-data)]
    (println "SCRAPED.....finished")
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
  (create-db)
  (insert-all db "store" (stores-loop stores))
  (visit-all-stores stores))

;(before-starting-server)