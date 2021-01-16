(ns example1.db
  (:require [clojure.java.jdbc :refer [create-table-ddl db-do-commands insert! query find-by-keys insert-multi!]]))

(def db
  {:classname   "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname     "database.db"})

(defn create-db
  "create db and table"
  []
  (try (db-do-commands db
                       [(create-table-ddl :product
                                          [[:id :integer "PRIMARY KEY AUTOINCREMENT"]
                                           [:name :text]
                                           [:weight :double]
                                           [:unit :text]])
                        (create-table-ddl :store
                                          [[:id :integer "PRIMARY KEY AUTOINCREMENT"]
                                           [:name :text]
                                           [:mindelivery :double]
                                           [:deliverycost :double]
                                           [:freedelivery :double]])
                        (create-table-ddl :storeprice
                                          [[:id :integer "PRIMARY KEY AUTOINCREMENT"]
                                           [:store_id :integer]
                                           [:product_id :integer]
                                           [:price :double]
                                           [:date :date]
                                           ["FOREIGN KEY(store_id) REFERENCES store(id)"]
                                           ["FOREIGN KEY(product_id) REFERENCES products(id)"]])])
       (catch Exception e
         (println (.getMessage e)))))


(defn insert-db
  "function iserts item in specified table of database"
  [db table item]
  (insert! db table item {:return-keys true}))

(comment (defn get-all
  "prints the result set in tabular form"
  [db table]
  (let [q (str "select * from " table)]
    (doseq [row (query db [q])]
      (println row)))))


(defn find-in-db [db table item]
  (find-by-keys db table item))

(defn insert-all [db table items]
  (insert-multi! db table items))

