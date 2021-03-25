(ns example1.views-test
  (:require [example1.views :refer [head get-form-products calculate-total store-vectore show-column-names columns]]
            [clojure.test :refer [deftest testing is]]))

(deftest test-menu
  (testing "Does menu contains link for home page"
    (is (clojure.string/includes? head "Home page")))
  (testing "Does menu contains link for product search"
    (is (clojure.string/includes? head "Search for product")))
  (testing "Does menu contains link for cart"
    (is (clojure.string/includes? head "Cart"))))


(deftest test-form-for-searching-products
  (testing "Does form contains input for parfume name"
    (is (clojure.string/includes? (get-form-products) "<input name=\"name\" type=\"text\">")))
  (testing "Does form contains input for parfume producer"
    (is (clojure.string/includes? (get-form-products) "<input name=\"producer\" type=\"text\">")))
  (testing "Does form contains menu"
    (is (clojure.string/includes? (get-form-products) "<ul><li><a href=\"/\">Home page</a></li><li><a href=\"./search\">Search for product</a></li><li><a href=\"./cart\">Cart</a></li></ul>"))))


(deftest test-calculate-total
  (testing "calculating total price for certain store when there is 1 article"
    (is (= (calculate-total [{:id "15", :name "Cedevita pomorandza", :weight "19.0", :unit "g", :no 1, :bastapromet "381.4", :neretvakomerc "3822.4", :tim99 "0"}] :neretvakomerc) 3822.4)))
  (testing "calculating total price for certain store when there is 2 same articles (number of items is 2)"
    (is (= (calculate-total [{:id "15", :name "Cedevita pomorandza", :weight "19.0", :unit "g", :no 2, :bastapromet "381.4", :neretvakomerc "3822.4", :tim99 "0"}] :neretvakomerc) 7644.8)))
  (testing "calculating total price for certain store when there is 2 different articles"
    (is (= (calculate-total [{:id "15", :name "Cedevita pomorandza", :weight "19.0", :unit "g", :no 1, :bastapromet "381.4", :neretvakomerc "3822.4", :tim99 "0"}
                             {:id "16", :name "Nar", :weight "19.0", :unit "g", :no 1, :bastapromet "381.4", :neretvakomerc "6000.0", :tim99 "0"}] :neretvakomerc) 9822.4)))
  (testing "calculating total price for certain store when there is 2 different articles, 2 of each kind"
    (is (= (calculate-total [{:id "15", :name "Cedevita pomorandza", :weight "19.0", :unit "g", :no 2, :bastapromet "381.4", :neretvakomerc "200", :tim99 "0"}
                             {:id "16", :name "Nar", :weight "19.0", :unit "g", :no 2, :bastapromet "381.4", :neretvakomerc "300", :tim99 "0"}] :neretvakomerc) 1000.0))))

(deftest test-store-vectore
  (testing "does it returns 0 if there is no price"
    (is (= 0 (store-vectore "prodavnica1" {:id "15", :name "Cedevita pomorandza", :weight "19.0", :unit "g", :no 2, :bastapromet "381.4", :neretvakomerc "200", :tim99 "0"}))))
  (testing "does it returns correct price"
    (is (= 0 (store-vectore "prodavnica1" {:id "15", :name "Cedevita pomorandza", :weight "19.0", :unit "g", :no 2, :prodavnica1 "381.4", :neretvakomerc "200", :tim99 "0"})))))



