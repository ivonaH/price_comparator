(ns example1.handler
  (:require [compojure.core :refer [GET defroutes]]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.session]
            [ring.util.response :as response]
            [example1.views :as v]))

(defroutes app-routes
  (route/resources "/")
  (GET "/" [] (v/welcome))
  (GET "/search" [] (v/get-form-products))
  (GET "/get-submit" [name] (v/search name))
  (GET "/add-product" [id name weight unit :as {session :session}] (v/add-to-cart id name weight unit session))
  (GET "/cart" {session :session} (v/my-cart session))
  (GET "/remove-product" [id :as {session :session}] (v/remove-from-cart session id))
  (GET "/reduce-product" [id quantity :as {session :session}] (v/reduce-product session id quantity))

  (route/not-found "Not Found"))
;((:headers request) "referer")

(def app
  (wrap-defaults app-routes site-defaults))
