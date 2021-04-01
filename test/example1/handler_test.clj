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

  (testing "search products with name"
    (let [response (app (mock/request :get "/get-submit?name=apple"))]
      (is (= (:status response) 200))))

  (testing "adding product to cart with POST"
    ;id name brend weight unit tester gift o p
    (is (= (:status (app (-> (mock/request :post "/post-submit")
                             (mock/json-body {:id 1
                                              :name "THE ONE"
                                              :brend "d&g"
                                              :weight 50.0
                                              :unit "ml"
                                              :tester "Yes"
                                              :gift "No"
                                              :o 6999.00
                                              :p 8700.00})))) 200)))

  (testing "search products with name and producer"
    (let [response (app (mock/request :get "/get-submit?name=apple&producer=pera"))]
      (is (= (:status response) 200))))

  (testing "testing cart"
    (let [response (app (mock/request :get "/cart"))]
      (is (= (:status response) 200))))

  (testing "testing removing product from cart"
    (let [response (app (mock/request :get "/remove-product?id=1"))]
      (is (= (:status response) 200)))))
