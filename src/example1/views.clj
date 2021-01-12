(ns example1.views
  (:require
   [hiccup.core :refer [h]]
   [hiccup.page :refer [html5 include-css]]))


(def head
  (list
   [:head
    (include-css "/css/style.css")]
   [:ul
    [:li [:a {:href "/"} "Home page"]]
    [:li [:a {:href "./search-product"} "Search for product"]]
    [:li [:a {:href "./get-form.html"} "Search for product"]]]))

(defn welcome [req]
  (html5
   head
   [:div
    [:h1 "Welcome to the price comparator"]
    [:p "Find your product"]]))

(defn get-form [req]
  (html5
   head
   [:div
    [:h1 "Search your product"]
    [:form {:method "get" :action "get-submit"}
     [:input {:type "text" :name "name"}]
     [:input {:type "submit" :value "submit"}]]]))



(defn search [name]
  (let [items]
    (html5
     head
     [:div
      [:h2  "<i> Your searched for: " name "!</i>"]
      [:ul {:class "groceries"}
       (for [item items]
         [:li item])]])))