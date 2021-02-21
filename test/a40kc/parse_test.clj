(ns a40kc.parse-test
  (:require [a40kc.parse :as sut]
            [clojure.test :as t]))


(def file (slurp "test.ros"))

(def units (sut/parse-units file))

(t/testing "parse units"
  (t/is (= 2 (count (filter #(= (:unit %) "Infantry Squad") units))))

    )


(t/testing "parse forces"
  (t/is (= 2 (count (sut/parse-forces file))))
  (t/is (= :force (:tag (first (first (sut/parse-forces file))))))
  ;; first patrol
  (t/is (= "Patrol Detachment -2CP" (:name (:attrs (first (first (sut/parse file)))))))
  ;; second patrol
  (t/is (= "Patrol Detachment -2CP" (:name (:attrs (first (second (sut/parse file))))))))
