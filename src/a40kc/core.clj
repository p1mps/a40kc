(ns a40kc.core
  (:gen-class))

(require '[clojure.xml :as xml])
(require '[clojure.java.io :as io])
(require '(xenopath [xpath :as xpath] [dom :as dom]))

(def xml-file (slurp "test.ros"))

;; (defn parse-xml
;;   []
;;   (->
;;    (io/file xml-file)
;;    (xml/parse)
;;    (xml-seq)))

;; (def xml-file-parsed (parse-xml))

;; (defn get-attrs
;;   [xml]
;;   (get-in xml [:tag]))

;; (defn parse-xpath []
;;   (xpath/xml->doc (slurp "Mordians.ros")))

(defn parse-xml []
  (dom/name (xpath/lookup-node "//selection" xml-file)))

(defn -main
  "I don't do a whole lot."
  [& args]
  (print parse-xml))
