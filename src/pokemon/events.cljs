(ns pokemon.events
  (:require
    [clojure.walk :as walk]
    [re-frame.core :as rf]))


; Code used to fetch data from api
; Usage:
; (rf/reg-event-fx :get-something
;                  (fn [_ _]
;                    {:fetch {:query      some-query
;                             :variables  {:name "foo"}
;                             :on-success [:some-event]
;                             :on-error   [:some-other-event]}}))
(rf/reg-fx :fetch
  (fn [{:keys [query variables on-error on-success]}]
    (let [body                (-> {:query     query
                                   :variables variables}
                                  clj->js
                                  js/JSON.stringify)
          on-unexpected-error (fn [e]
                                (rf/dispatch (conj on-error e)))
          on-reponse          (fn [response]
                                (if-not (.-ok response)
                                  (throw (js/Error (.-statusText response)))
                                  (.json response)))
          on-json             (fn [json]
                                (let [{:keys [data errors]} (-> json
                                                                js->clj
                                                                walk/keywordize-keys)]
                                  (if errors
                                    (rf/dispatch (conj on-error errors))
                                    (rf/dispatch (conj on-success data)))))]
      (-> (js/fetch "https://graphql-pokemon.now.sh/"
                    (clj->js {:method  "POST"
                              :headers {"Content-Type" "application/json"}
                              :body    body}))
          (.then on-reponse)
          (.then on-json)
          (.catch on-unexpected-error)))))

(def ^:private default-db
  {:app-name "Xerpa"})

(rf/reg-event-fx :initialize
  (fn [_ _]
    {:db default-db}))