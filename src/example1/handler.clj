(ns example1.handler
  (:require [compojure.core :refer [GET POST defroutes]]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [ wrap-defaults site-defaults]]
            [ring.middleware.session]
            [example1.views :as v]))

(defroutes app-routes
  (route/resources "/")
  (GET "/" [] (v/welcome))
  (GET "/search" [] (v/get-form-products))
  (GET "/get-submit" [name producer] (v/search name producer))
  (POST "/post-submit" [id name brend weight unit tester gift o p :as {session :session}] (v/post-s id name brend weight unit tester gift o p session))
  (GET "/cart" {session :session} (v/my-cart session))
  (GET "/remove-product" [id :as {session :session}] (v/remove-from-cart session id))
  (GET "/reduce-product" [id quantity :as {session :session}] (v/reduce-product session id quantity))

  (route/not-found "Not Found"))
;

(def app
  (wrap-defaults app-routes (assoc-in site-defaults [:security :anti-forgery] false)))



