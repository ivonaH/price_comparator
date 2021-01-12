(ns example1.handler
  (:require [compojure.core :refer [GET POST defroutes]]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [example1.views :as v]))

(defroutes app-routes
  (route/resources "/")
  (GET "/" req (v/welcome req))
  (GET "/search-product" req (v/get-form req))
  (GET "/get-submit" [name] (v/search name))

  (GET "/" [] "Hello Ivona")


  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
