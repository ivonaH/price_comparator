(ns example1.views
  (:require [hiccup.page :refer [html5 include-css]]
            [example1.db :as db]))


(def head
  (list
   [:head
    (include-css "/css/style.css")]
   [:ul
    [:li [:a {:href "/"} "Home page"]]
    [:li [:a {:href "./search"} "Search for product"]]
    [:li [:a {:href "./cart"} "Cart"]]]))

(defn welcome
  "shows home page"
  []
  (html5
   head
   [:div
    [:h1 (str "Welcome to the price comparator CAO")]
    [:h2 "Find your product"]]))


(defn get-form-products
  "shows form for searching products"
  []
  (html5
   head
   [:div
    [:h1 "Search your product"]
    [:form {:method "get" :action "get-submit"}
     [:input {:type "text" :name "name"}]
     [:input {:type "submit" :value "Search"}]]]))



(defn show-as-table
  "function creates html table from list of items, it extracts column names from items and names columns"
  [items]
  (let [columns (keys (first items))
        col-keywords (map keyword columns)]
    [:hr]
    [:table
     [:tr
      (for [column columns]
        [:th column])
      (for [item items]
        [:div [:tr
               (for [keyword col-keywords]
                 [:td (item keyword)])
               [:td [:a {:href (str "./add-product?id=" (:id item) "&name=" (:name item) "&weight=" (:weight item) "&unit=" (:unit item))} "Add to cart"]]
               [:td [:a {:href (str "./reduce-product?id=" (:id item) "&quantity=" (:no item))} "Reduce quantity"]]
               [:td [:a {:href (str "./remove-product?id=" (:id item))} "Remove from cart"]]]])]]))

(defn add-to-cart
  "adds products to users cart"
  [id name weight unit session]
  (let [data  (:data session [])
        session (if (some #(= id (:id %)) data) (assoc session :data (map (fn [x] (update x :no #(if (= id (:id x)) (inc %) %))) data))
                    (assoc session :data (conj data {:id id :name name :weight weight :unit unit :no 1})))]
    {:headers {"Content-Type" "text/html"}
     :body (html5
            head
            [:div
             [:h2 "Product is added to cart."]])
     :session session}))

(defn remove-from-cart
  "removes product from users cart"
  [session id]
  (let [data  (:data session [])
        session (assoc session :data (remove #(= (:id %) id) data))]
    {:headers {"Content-Type" "text/html"}
     :body (html5
            head
            [:div
             [:h2 "Product is removed from cart."]])
     :session session}))


(defn reduce-product
  "reduces product quantity from users cart"
  [session id quantity]
  (let [data  (:data session [])
        session (if (= (Integer. quantity) 1)
                  (assoc session :data (remove #(= (:id %) id) data))
                  (assoc session :data (map (fn [x] (update x :no #(if (= id (:id x)) (- % 1) %))) data)))]
    {:headers {"Content-Type" "text/html"}
     :body (html5
            head
            [:div
             [:h2 "Product quantity is reduced."]])
     :session session}))

(defn my-cart
  "shows products in user cart"
  [session]
  (let [data  (:data session [])]
    (html5
     head
     [:div
      [:h1 "Your CART:"]
      (show-as-table data)])))

(defn search
  "shows search results"
  [name]
  (let [items (db/find-in-db db/db "product" {:name name})]
    (html5
     head
     [:h2  "You searched for: " name "!"]
     [:h2 "Results"]
     [:hr]
     (if (empty? items) [:h3 "No results"]
         (show-as-table items)))))


