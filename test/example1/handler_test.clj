(ns example1.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [example1.handler :refer :all]))

(deftest test-app
  (testing "main route"
    (let [response (app (mock/request :get "/"))]
      (is (= (:status response) 200))))

  (testing "not-found route"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= (:status response) 404))))

  (testing "search-form"
    (let [response (app (mock/request :get "/search"))]
      (is (= (:status response) 200))))

  (testing "search products"
    (let [response (app (mock/request :get "/get-submit?name=apple"))]
      (is (= (:status response) 200))))

  (testing "adding product to session products"
    (let [response (app (mock/request :get "/add-product?id=1&name=apple&weight=1&unit=kg"))]
      (is (= (:status response) 200))))

  (testing "testing cart"
    (let [response (app (mock/request :get "/cart"))]
      (is (= (:status response) 200))))

  (testing "testing removing product from cart"
    (let [response (app (mock/request :get "/remove-product?id=1"))]
      (is (= (:status response) 200))))

  (testing "testing reducing product quantity from cart"
    (let [response (app (mock/request :get "/reduce-product?id=1&quantity=5"))]
      (is (= (:status response) 200))
      (str "BODY" (:body response))
     (is (boolean (re-find #"<h2>Product quantity is reduced.</h2>" (:body response)))
))))
