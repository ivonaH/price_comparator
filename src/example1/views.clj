(ns example1.views
  (:require [hiccup.page :refer [html5 include-css]]
            [example1.db :as db]
            [example1.productsscraping :refer [stores-data]]))

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
    [:h1 (str "Welcome to the price comparator")]
    [:h2 "Find your product"]]))


(defn get-form-products
  "shows form for searching products"
  []
  (html5
   head
   [:div
    [:h1 "Search your product"]
    [:form {:method "get" :action "get-submit"}
     [:label "Parfume name:"]
     [:input {:type "text" :name "name"}]
     [:br]
     [:br]
     [:label "Parfume brend:"]
     [:input {:type "text" :name "producer"}]
     [:br]
     [:br]
     [:input {:type "submit" :value "Search"}]]]))

(def cart-search-buttons
  (list [:a {:href "./cart"} [:button {:class "remove"} "Back to cart"]]
        [:a {:href "./search"} [:button {:class "add"} "Back to search"]]))

(defn add-to-cart
  "adds products to users cart"
  [id name weight o p unit session]
  (let [data  (:data session [])
        session (if (some #(= id (:id %)) data) (assoc session :data (map (fn [x] (update x :no #(if (= id (:id x)) (inc %) %))) data))
                    (assoc session :data (conj data {:id id :name name :weight weight :unit unit :no 1 :original-parfemi o :prodaja-parfema p})))]
    {:headers {"Content-Type" "text/html"}
     :body (html5
            head
            [:div
             [:h2 "Product is added to cart."]
             cart-search-buttons])
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
             [:h2 "Product is removed from cart."]
             cart-search-buttons])
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
             [:h2 "Product quantity is reduced."]
             cart-search-buttons])
     :session session}))

(defn calculate-total
"calculates total price of all the parfumes from that store"
  [data storename]
  (loop [remaining-products data sum 0]
    (if (empty? remaining-products) sum
        (let [[p & remain] remaining-products]
          (recur remain (+ sum (* (:no p) (Double/valueOf (storename p)))))))))


(defn store-vectore
  "Returns price of product in certain store"
  [store products]
  (let [pr (:price (first (filter #(= store (:store_name %)) products)))]
    (if (nil? pr) 0 pr)))

(def columns [:id :name :brend :tester :weight :unit :gift])

(defn show-product-with-prices
  "Shows parfume data with compared prices in different stores"
  [products columns]
  (let [rep (first products)
        original-parfemi (if (empty? (:original-parfemi rep)) (store-vectore "original parfemi" products) (:original-parfemi rep))
        prodaja-parfema (if (empty? (:prodaja-parfema rep)) (store-vectore "parfemi online" products) (:prodaja-parfema rep))]
    (for [item products]
      [:tr (if (= (:tester rep) "Yes") {:class "tester"} {:class "row-data"})
       [:form {:method "post" :action "post-submit"}
        (for [col columns]
          [:td [:input {:type "text" :name col :value (item col) :readonly true}]])
        [:td [:input {:type "text" :name "o" :value original-parfemi :readonly true}]]
        [:td [:input {:type "text" :name "p" :value prodaja-parfema :readonly true}]]
        [:td [:input {:type "submit" :value "Add to cart" :class "add"}]]]
       [:td [:a {:href (str "./reduce-product?id=" (:id item) "&quantity=" (:no item))} [:button {:class "reduce"} "Reduce quantity"]]]
       [:td [:a {:href (str "./remove-product?id=" (:id item))} [:button {:class "remove"} "Remove from cart"]]]])))



(defn show-stores-as-columns 
  "shows store names as columns the table"[]
  (let [store-names (map :name stores-data)]
    (for [store-name store-names]
      [:th (str store-name "<br>price")])))

(defn show-column-names 
  "shows fields from products as columns in the table"
  [columns]
  [:tr
   (for [column columns]
     [:th column])
   (show-stores-as-columns)])

(defn my-cart
  "shows products in user cart"
  [session]
  (let [data  (:data session [])
        sum-op (calculate-total data :original-parfemi)
        sum-pp (calculate-total data :prodaja-parfema)
        columns (conj columns :no)]
    (html5
     head
     (if (empty? data)
       [:div
        [:h1 "Your cart is empty."]
        [:a {:href "./search"} [:button "Back to search"]]]
       [:div  [:div
               [:h1 "Your CART:"]
               [:table
                (show-column-names columns)
                (show-product-with-prices data columns)]]
        [:br]
        [:br]
        [:h1 "Total"]
        [:table
         (show-stores-as-columns)

         [:tr
          [:td sum-op]
          [:td sum-pp]]]]))))

(defn search
  "shows search results for perfumes"
  [name brend]
  (let [items (if (empty? brend) (db/results db/db name) (db/results db/db name brend))
        ids (set (map :id items))]
    (html5
     head
     [:h2  "You searched for: " name "!" brend]
     [:h2 "Results"]
     [:hr]
     (if (empty? items) [:h3 "No results"]
         [:table
          (show-column-names columns)
          (for [id ids]
            (show-product-with-prices  (filter #(= id (:id %)) items) columns))]))))




(defn post-s 
  "adds products to cart (session) with post method"
  [id name brend weight unit tester gift o p session]
  (let [data  (:data session [])
        session (if (some #(= id (:id %)) data) (assoc session :data (map (fn [x] (update x :no #(if (= id (:id x)) (inc %) %))) data))
                    (assoc session :data (conj data {:id id :name name :brend brend :tester tester :weight weight :unit unit :gift gift :no 1 :original-parfemi o :prodaja-parfema p})))]
    {:headers {"Content-Type" "text/html"}
     :body (html5
            head
            [:div
             [:h2 "Product is added to cart."]]
            cart-search-buttons)
     :session session}))

