(ns pokemon.events
  (:require
    [clojure.walk :as walk]
    [pokemon.queries :as q]
    [re-frame.core :as rf]))

(defn- on-reponse
  [response]
  (if-not (.-ok response)
    (throw (js/Error (.-statusText response)))
    (.json response)))

(rf/reg-fx :fetch
  (fn [{:keys [query variables on-error on-success]}]
    (rf/dispatch [:add-one-request])
    (let [body                (-> {:query     query
                                   :variables variables}
                                  clj->js
                                  js/JSON.stringify)
          on-unexpected-error (fn [e]
                                (rf/dispatch (conj on-error e)))
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
          (.catch on-unexpected-error)
          (.finally #(rf/dispatch [:drop-one-request]))))))

(rf/reg-event-fx :handle-error
  (fn [_ [_ data]]
    (js/console.log "Error: " data)
    {}))

(rf/reg-event-db :drop-one-request
  (fn [db _]
    (update db :requests drop-last)))

(rf/reg-event-db :add-one-request
  (fn [db _]
    (update db :requests #(cons :foo %))))

(rf/reg-event-db :handle-get-pokemon
  (fn [db [_ {:keys [pokemon]}]]
    (assoc db :pokemon pokemon)))

(rf/reg-event-fx :get-pokemon
  (fn [_ [_ pokemon-name]]
    {:fetch {:query      q/pokemon
             :variables  {:name pokemon-name}
             :on-success [:handle-get-pokemon]
             :on-error   [:handle-error]}}))

(rf/reg-event-db :handle-get-list-of-pokemons
  (fn [db [_ {:keys [pokemons]}]]
    (assoc db :pokemons pokemons)))

(rf/reg-event-fx :get-list-of-pokemons
  (fn [{:keys [db]} _]
    {:fetch {:query      q/pokemons
             :variables  {:first (+ (count (:pokemons db)) 10)}
             :on-success [:handle-get-list-of-pokemons]
             :on-error   [:handle-error]}}))

(def ^:private default-db
  {:pokemon-name "Bulbasaur"
   :pokemon      nil
   :requests     []
   :pokemons     []})

(rf/reg-event-fx :initialize
  (fn [_ _]
    {:db         default-db
     :dispatch-n [[:get-pokemon (:pokemon-name default-db)] [:get-list-of-pokemons]]}))