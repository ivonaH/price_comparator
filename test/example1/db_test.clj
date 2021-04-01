(ns example1.db-test
  (:require [example1.db :refer [create-db insert-db find-in-db insert-all]]
            [clojure.java.jdbc :as sql]
            [clojure.test :refer [deftest testing is use-fixtures]]))

(def db_test
  {:classname   "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname     "dbtest.db"})

(defn- clean-up
  "Attempt to drop any test tables before we start a test."
  [t]
    (doseq [table [:storeprice :product :store]]
      (try
        (sql/db-do-commands db_test (sql/drop-table-ddl table))
        (catch java.sql.SQLException _)))
          ;; ignore

  (t))

(use-fixtures
  :each clean-up)

(deftest test-create-db-with-tables
  (create-db db_test)
  (testing "creating table product" (is (= 0 (sql/query db_test ["SELECT * FROM product"] {:result-set-fn count}))))
  (testing "creating table store" (is (= 0 (sql/query db_test ["SELECT * FROM store"] {:result-set-fn count}))))
  (testing "creating table storeprice" (is (= 0 (sql/query db_test ["SELECT * FROM storeprice"] {:result-set-fn count})))))

(deftest test-insert-db
  (create-db db_test)
  (testing "inserting one row to table store"
    (let [r (get (first (insert-db db_test :store {:name "original_parfemi"})) (keyword "last_insert_rowid()"))]
      (is (= r 1))))
  (testing "inserting one row to table product"
    (let [r (get (first (insert-db db_test :product {:name "MON PARIS" :weight 10 :unit "ml" :brend "burberry" :category "edp" :tester "No"})) (keyword "last_insert_rowid()"))]
      (is (= r 1))))
  (testing "inserting one row to table storeprice"
    (let [r (get (first (insert-db db_test :storeprice {:store_id 1 :product_id 1 :gift "No" :price 1999.00 :date "29/02/2020"})) (keyword "last_insert_rowid()"))]
      (is (= r 1))))
  (testing "checking if right values are inserted to table store"
    (let [r (first (find-in-db db_test :store {:id 1}))]
      (is (= r {:id 1, :name "original_parfemi"}))))
  (testing "checking if right values are inserted to table product"
    (let [r (first (find-in-db db_test :product {:id 1}))]
      (is (= r {:id 1 :name "MON PARIS" :weight 10.0 :unit "ml" :brend "burberry" :category "edp" :tester "No"}))))
  (testing "checking if right values are inserted to table storeprice"
    (let [r (first (find-in-db db_test :storeprice {:id 1}))]
      (is (= r {:id 1 :store_id 1 :product_id 1 :gift "No" :price 1999.00 :date "29/02/2020"})))))


(deftest test-insert-all
  (create-db db_test)
  (testing "inserting multiple rows to table STORE, generated indexes"
    (let [r (insert-all db_test :store [{:name "prodaja parfema"} {:name "online prodaja"} {:name "parfemi online"}])
          first (get (first r) (keyword "last_insert_rowid()"))
          second (get (second r) (keyword "last_insert_rowid()"))
          third (get (nth r 2) (keyword "last_insert_rowid()"))]
      (is (= first 1))
      (is (= second 2))
      (is (= third 3))))
  (testing "inserting multiple rows to table STORE, counting number of rows in table"
    (is (= 3 (sql/query db_test ["SELECT * FROM store"] {:result-set-fn count}))))
  (testing "are all added values correctly inserted to table STORE"
    (is (= (first (find-in-db db_test :store {:id 1})) {:id 1 :name "prodaja parfema"}))
    (is (= (first (find-in-db db_test :store {:id 2})) {:id 2 :name "online prodaja"}))
    (is (= (first (find-in-db db_test :store {:id 3})) {:id 3 :name "parfemi online"})))
  (testing "inserting multiple rows to table PRODUCT, generated indexes"
    (let [r (insert-all db_test :product [{:name "MON PARIS" :weight 10.0 :unit "ml" :brend "burberry" :category "edp" :tester "No"}
                                          {:name "LONDON" :weight 100.0 :unit "ml" :brend "kiko" :category "edp" :tester "No"}
                                          {:name "MILAN" :weight 50.0 :unit "ml" :brend "milan.e" :category "edt" :tester "Yes"}])
          first (get (first r) (keyword "last_insert_rowid()"))
          second (get (second r) (keyword "last_insert_rowid()"))
          third (get (nth r 2) (keyword "last_insert_rowid()"))]
      (is (= first 1))
      (is (= second 2))
      (is (= third 3))))
  (testing "inserting multiple rows to table PRODUCT, counting number of rows in table"
    (is (= 3 (sql/query db_test ["SELECT * FROM product"] {:result-set-fn count}))))
  (testing "are all added values correctly inserted to table PRODUCT"
    (is (= (first (find-in-db db_test :product {:id 1})) {:id 1 :name "MON PARIS" :weight 10.0 :unit "ml" :brend "burberry" :category "edp" :tester "No"}))
    (is (= (first (find-in-db db_test :product {:id 2})) {:id 2 :name "LONDON" :weight 100.0 :unit "ml" :brend "kiko" :category "edp" :tester "No"}))
    (is (= (first (find-in-db db_test :product {:id 3})) {:id 3 :name "MILAN" :weight 50.0 :unit "ml" :brend "milan.e" :category "edt" :tester "Yes"})))
  (testing "inserting multiple rows to table STOREPRICE, generated indexes"
    (let [r (insert-all db_test :storeprice [{:store_id 1 :product_id 1 :gift "No" :price 2000.00 :date "29/03/2021"}
                                             {:store_id 2 :product_id 2 :gift "No" :price 1899.00 :date "30/03/2021"}
                                             {:store_id 3 :product_id 3 :gift "No" :price 1009.00 :date "27/02/2021"}])
          first (get (first r) (keyword "last_insert_rowid()"))
          second (get (second r) (keyword "last_insert_rowid()"))
          third (get (nth r 2) (keyword "last_insert_rowid()"))]
      (is (= first 1))
      (is (= second 2))
      (is (= third 3))))
  (testing "inserting multiple rows to table STOREPRICE, counting number of rows in table"
    (is (= 3 (sql/query db_test ["SELECT * FROM storeprice"] {:result-set-fn count}))))
  (testing "are all added values correctly inserted to table STOREPRICE"
    (is (= (first (find-in-db db_test :storeprice {:id 1})) {:id 1 :store_id 1 :product_id 1 :gift "No" :price 2000.00 :date "29/03/2021"}))
    (is (= (first (find-in-db db_test :storeprice {:id 2})) {:id 2 :store_id 2 :product_id 2 :gift "No" :price 1899.00 :date "30/03/2021"}))
    (is (= (first (find-in-db db_test :storeprice {:id 3})) {:id 3 :store_id 3 :product_id 3 :gift "No" :price 1009.00 :date "27/02/2021"}))))

