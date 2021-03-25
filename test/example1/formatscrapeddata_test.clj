(ns example1.formatscrapeddata-test
  (:require [example1.formatscrapeddata :refer [return-weight-with-unit extract-unit extract-weight format-price
                                                return-formated-product remove-words-from-name return-new-category]]
            [clojure.test :refer [deftest testing is]]))

(deftest test-return-weight-with-unit
  (testing "unit 100komada"
    (is (= (return-weight-with-unit "apple 100komada") "100komada")))
  (testing "unit 100kom"
    (is (= (return-weight-with-unit "apple 100kom") "100kom")))
  (testing "unit 100gr"
    (is (= (return-weight-with-unit "apple 100gr") "100gr")))
  (testing "unit 100kg"
    (is (= (return-weight-with-unit "apple 100kg") "100kg")))
  (testing "unit 100kapsula"
    (is (= (return-weight-with-unit "apple 100kapsula") "100kapsula")))
  (testing "unit 100kaps"
    (is (= (return-weight-with-unit "apple 100kaps") "100kaps")))
  (testing "unit 100kp"
    (is (= (return-weight-with-unit "apple 100kp") "100kp")))
  (testing "unit 100 kapsula"
    (is (= (return-weight-with-unit "apple 100 kapsula") "100 kapsula")))
  (testing "unit 100 kaps"
    (is (= (return-weight-with-unit "apple 100 kaps") "100 kaps")))
  (testing "unit 100 kp"
    (is (= (return-weight-with-unit "apple 100 kp") "100 kp")))
  (testing "unit 100 ml"
    (is (= (return-weight-with-unit "apple 100 ml") "100 ml")))
  (testing "unit 100ml"
    (is (= (return-weight-with-unit "apple 100ml") "100ml")))
  (testing "unit 100 l"
    (is (= (return-weight-with-unit "apple 100 l") "100 l")))
  (testing "unit 100l"
    (is (= (return-weight-with-unit "apple 100l") "100l")))
  (testing "unit 100 tableta"
    (is (= (return-weight-with-unit "apple 100 tableta") "100 tableta")))
  (testing "unit 100tableta"
    (is (= (return-weight-with-unit "apple 100tableta") "100tableta")))
  (testing "unit 100 tb"
    (is (= (return-weight-with-unit "apple 100 tb") "100 tb")))
  (testing "unit 100tb"
    (is (= (return-weight-with-unit "apple 100tb") "100tb")))
  (testing "unit 100cps"
    (is (= (return-weight-with-unit "apple 100cps") "100cps"))))

(deftest test-extract-unit
  (testing "unit 100komada"
    (is (= (extract-unit {:name "apple 100komada"}) "komada")))
  (testing "unit 100kom"
    (is (= (extract-unit {:name "apple 100kom"}) "komada")))
  (testing "unit 100gr"
    (is (= (extract-unit {:name "apple 100gr"}) "g")))
  (testing "unit 100kg"
    (is (= (extract-unit {:name "apple 100kg"}) "kg")))
  (testing "unit 100kapsula"
    (is (= (extract-unit {:name "apple 100kapsula"}) "kapsula")))
  (testing "unit 100kaps"
    (is (= (extract-unit {:name "apple 100kaps"}) "kapsula")))
  (testing "unit 100kp"
    (is (= (extract-unit {:name "apple 100kp"}) "kapsula")))
  (testing "unit 100 kapsula"
    (is (= (extract-unit {:name "apple 100 kapsula"}) "kapsula")))
  (testing "unit 100 kaps"
    (is (= (extract-unit {:name "apple 100 kaps"}) "kapsula")))
  (testing "unit 100 kp"
    (is (= (extract-unit {:name "apple 100 kp"}) "kapsula")))
  (testing "unit 100 ml"
    (is (= (extract-unit {:name "apple 100 ml"}) "ml")))
  (testing "unit 100ml"
    (is (= (extract-unit {:name "apple 100ml"}) "ml")))
  (testing "unit 100 l"
    (is (= (extract-unit {:name "apple 100 l"}) "l")))
  (testing "unit 100l"
    (is (= (extract-unit {:name "apple 100l"}) "l")))
  (testing "unit 100 tableta"
    (is (= (extract-unit {:name "apple 100 tableta"}) "tableta")))
  (testing "unit 100tableta"
    (is (= (extract-unit {:name "apple 100tableta"}) "tableta")))
  (testing "unit 100 tb"
    (is (= (extract-unit {:name "apple 100 tb"}) "tableta")))
  (testing "unit 100tb"
    (is (= (extract-unit {:name "apple 100tb"}) "tableta")))
  (testing "unit 100cps"
    (is (= (extract-unit {:name "apple 100cps"}) "kapsula")))
  (testing "without unit"
    (is (= (extract-unit {:name "apple 2 kriske"}) "ml"))))

(deftest test-return-new-category
  (testing "EDP"
    (is (= (return-new-category  {:name "BOSS HUGO BOSS THE SCENT FOR HER EDP 50ml", :price "5.880 RSD", :category nil, :brend "Hugo Boss"}) "EDP")))
  (testing "PARFEMSKA VODA"
    (is (= (return-new-category  {:name "BOSS HUGO BOSS THE SCENT FOR HER 50ml", :price "5.880 RSD", :category " PARFEMSKA VODA", :brend "Hugo Boss"}) "EDP")))
  (testing "EDT"
    (is (= (return-new-category  {:name "BOSS HUGO BOSS THE SCENT FOR HER EDT 50ml", :price "5.880 RSD", :category nil, :brend "Hugo Boss"}) "EDT")))
  (testing "TOALETNA VODA"
    (is (= (return-new-category  {:name "xyz 50ml", :category "TOALETNA VODA" :price "5.880 RSD", :brend "Hugo Boss"}) "EDT")))
  (testing "EDC"
    (is (= (return-new-category  {:name "BOSS HUGO BOSS THE SCENT FOR HER EDC 50ml", :price "5.880 RSD", :category nil, :brend "Hugo Boss"}) "EDC")))
  (testing "KOLONJSKA VODA"
    (is (= (return-new-category  {:name "xyz 50ml", :category "KOLONJSKA VODA" :price "5.880 RSD", :brend "Hugo Boss"}) "EDC")))
  (testing "SET"
    (is (= (return-new-category  {:name "BOSS HUGO BOSS THE SCENT FOR HER SET 50ml", :price "5.880 RSD", :category nil, :brend "Hugo Boss"}) "SET")))
  (testing "SET"
    (is (= (return-new-category  {:name "xyz 50ml", :category "SET" :price "5.880 RSD", :brend "Hugo Boss"}) "SET")))
  (testing "without any category"
    (is (= (return-new-category  {:name "xyz 50ml", :category nil :price "5.880 RSD", :brend "Hugo Boss"}) "EDP")))
  )

(deftest test-extract-weight
  (testing "extracting weight"
    (is (= (extract-weight {:name "Crna čokolada 76% sa divljim jagodama Arriba nacional organic 40 gr", :price "370.00 rsd"}) 40.0)))
  (testing "no weight, default weight"
    (is (= (extract-weight {:name "Crna čokolada 76% sa divljim jagodama Arriba nacional organic"}) 1))))

(deftest test-remove-words-from-name
 (testing "are all words removed from name"
   (is (= (remove-words-from-name ["23ml" "xyz" "TESTER" "EDP"] "parfem xyz 23ml EDP TESTER") "parfem    ")))
 (testing "are all words removed from name"
   (is (= (remove-words-from-name ["23ml" "xyz" "TESTER" "EDP" "f(x)"] "parfem xyz 23ml EDP TESTER") "parfem    "))))



(deftest test-return-formated-product
  (testing "is product correctly formated, not tester"
    (is (= (return-formated-product  {:name "Yves Saint Laurent Mon Paris 100ml "
                                      :price "10,800.00 rsd"
                                      :category "SET"
                                      :brend "Yves Saint Laurent"
                                      :tester nil})
           {:name "MON PARIS", :category "SET", :weight 100.0, :unit "ml", :brend "YVES SAINT LAURENT", :tester "No"})))
  (testing "is product correctly formated no name, except brend name"
    (is (= (return-formated-product  {:name "Yves Saint Laurent 100ml "
                                      :price "10,800.00 rsd"
                                      :category "SET"
                                      :brend "Yves Saint Laurent"
                                      :tester nil})
           {:name "YVES SAINT LAURENT", :category "SET", :weight 100.0, :unit "ml", :brend "YVES SAINT LAURENT", :tester "No"})))
  (testing "is product correctly formated, when tester"
    (is (= (return-formated-product  {:name "Yves Saint Laurent Mon Paris 100ml TESTER"
                                      :price "10,800.00 rsd"
                                      :category "SET"
                                      :brend "Yves Saint Laurent"
                                      :tester nil})
           {:name "MON PARIS", :category "SET", :weight 100.0, :unit "ml", :brend "YVES SAINT LAURENT", :tester "Yes"}))))



(deftest test-format-price
  (testing "price with . and less then two numbers after"
    (is (= (format-price "34.00rsd") 34.0)))
  (testing "price with . and more then two numbers after"
    (is (= (format-price "6.700 RSD") 6700.0)))
  (testing "price with , and ."
    (is (= (format-price "4,200.00 rsd") 4200.0)))
  (testing "price without , and ."
    (is (= (format-price "4200 rsd") 4200.0))))

