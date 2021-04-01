(ns example1.db
  (:require [clojure.java.jdbc :refer [create-table-ddl db-do-commands insert! drop-table-ddl query find-by-keys insert-multi!]]))

(def db
  {:classname   "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname     "database.db"})

(defn create-db
  "create db and table"
  [db]
  (try (db-do-commands db
                       [(create-table-ddl :product
                                          [[:id :integer "PRIMARY KEY AUTOINCREMENT"]
                                           [:name :text]
                                           [:weight :double]
                                           [:unit :text]
                                           [:brend :text]
                                           [:category :text]
                                           [:tester :text]])
                        (create-table-ddl :store
                                          [[:id :integer "PRIMARY KEY AUTOINCREMENT"]
                                           [:name :text]])
                        (create-table-ddl :storeprice
                                          [[:id :integer "PRIMARY KEY AUTOINCREMENT"]
                                           [:store_id :integer]
                                           [:product_id :integer]
                                           [:gift :text]
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


(defn find-in-db [db table item]
  (find-by-keys db table item))

(defn insert-all [db table items]
  (insert-multi! db table items))

(defn results
  "execute query and return lazy sequence"
  ([db word]
  (query db [(str "select product.*, s.name as store_name, storeprice.price as price, storeprice.gift as gift from product join storeprice on product.id=storeprice.product_id join store s on s.id=storeprice.store_id where product.name like '%" word
                  "%'")]))
  ([db word brend]
  (query db [(str "select product.*, s.name as store_name, storeprice.price as price, storeprice.gift as gift from product join storeprice on product.id=storeprice.product_id join store s on s.id=storeprice.store_id where product.name like '%" word
                  "%' and product.brend like '%" brend "%'")])))

(defn drop-all-tables
  "drops all the tables in db"
  [db table-names]
  (doseq [table table-names]
    (try
      (db-do-commands db (drop-table-ddl table))
      (catch java.sql.SQLException _))))