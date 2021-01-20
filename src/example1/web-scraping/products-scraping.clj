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
  (loop [page (:page-start url) all-products []]
    (println page)
    (let [products (extract-products-from-url url (str category-url (:page url) page))
          prod-per-page (count products)]
      (if (< prod-per-page (:res-per-page url)) (into all-products products)
          (recur (inc page) (into all-products products))))))

(defn get-all-products-from-url
  "this function returns all products (from every category) from website"
  [main-url]
  (let [categories-url-list (extract-categories-from-url main-url)]
   (loop [remaining-url categories-url-list results []]
    (if (empty? remaining-url) results
        (let [[url & remain] remaining-url]
          (println remaining-url)
          (recur remain (into results (get-all-pages main-url url))))))))

"List of stores from which data will be scraped"
(def stores 
  [{:name "maslina" :link "https://www.maslina.rs/" :categories-html ".cat-card"  :product-main-html ".grid" :product-name ".product-card__header> h3 > a" :product-price ".product-card__price" :page "?page=" :page-start 1 :res-per-page 12}
             {:name "herbasana" :link "http://www.herbasana.rs/" :categories-html ".top-bar-menu > li > a"  :product-main-html " .category > ul >li" :product-name " .title-wrapper" :product-price "> h5" :page "?page=" :page-start 1 :res-per-page 20}
             {:name "boneda" :link "https://boneda.rs/online-prodaja/" :categories-html ".cat-item >a" :product-main-html " .box-text-products" :product-name ".name" :product-price ".price" :page "page/" :page-start 1 :res-per-page 50}
             {:name "biomarket" :link "https://www.biomarket.rs/" :categories-html ".main_search_dropdown > ul >li >a" :product-main-html ".product-prev__item" :product-name ".product-title" :product-price ".product-price" :page "_" :page-start 0 :res-per-page 12}
             {:name "bio-una" :link "https://shop.bio-una.com/" :categories-html ".category-link" :product-main-html ".product" :product-name ".product-title" :product-price ".price .amount" :page "/page/" :page-start 1 :res-per-page 28}])

;(get-all-products-from-url (first stores))

(defn create-products&pricestore
  "inserts products and prices to db"
  [products store-id]
  (loop [remaining-products products results []]
    (if (empty? remaining-products) (insert-all db "storeprice" results)
        (let [[product & remain] remaining-products
              price-no (count (:price  product))
              price (if (> price-no 1) (first (:price product)) (:price product))
              formated-product {:name (change-name (:name product)) :weight (extract-weight product)
                                :unit (extract-unit product)}
              id (or (:id (first (find-in-db db "product" formated-product)))
                     (get (first (insert-db db "product" formated-product)) (keyword "last_insert_rowid()")))]
          (recur remain (conj results {:store_id store-id :product_id id :price (format-price price) :date (.format (java.text.SimpleDateFormat. "dd/MM/yyyy") (new java.util.Date))}))))))
