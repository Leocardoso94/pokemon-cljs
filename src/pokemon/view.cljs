(ns pokemon.view
  (:require
    [clojure.string :as str]
    [re-frame.core :as rf]
    ["react-loader-spinner" :default spinner])
  (:import
    [goog.async Debouncer]))

(defn debounce
  [f interval]
  (let [dbnc (Debouncer. f interval)]
    (fn [& args]
      (.apply (.-fire dbnc) dbnc (to-array args)))))

(defn- loader
  []
  (when @(rf/subscribe [:loading?])
    [:div
     {:style {:bottom           0
              :top              0
              :left             0
              :right            0
              :display          "flex"
              :flex-direction   "column"
              :align-items      "center"
              :justify-content  "center"
              :position         "fixed"
              :background-color "rgba(0,0,0,1)"}}
     [:>
      spinner
      {:type   "Puff"
       :color  "#61dafb"
       :width  "100"
       :height "100"}]]))

(def ^:private btn-style
  {:background-color "#fff"
   :border-radius    100
   :display          "block"
   :margin           "20px auto"
   :font-weight      "500"
   :font-family      "inherit"
   :border-color     "transparent"
   :cursor           "pointer"
   :padding          "10px 15px"})

(defn- pokemon-link
  [name]
  [:a
   {:on-click #(rf/dispatch [:get-pokemon name])
    :style    {:color           "#61dafb"
               :text-decoration "underline"
               :cursor          "pointer"}}
   name])

(defn- load-more-button
  [pokemons]
  (let [number-of-pokemons 151]
    (when-not (or (= number-of-pokemons (count pokemons)) (zero? (count pokemons)))
      [:button
       {:on-click #(rf/dispatch [:get-list-of-pokemons])
        :style    btn-style}
       "Load more..."])))

(defn- pokemon-list
  [pokemons]
  [:div
   {:style {:width        "50%"
            :padding-left 20}}
   [:p "List of Pokémon..."]
   [:ul
    (for [{:keys [id name]} pokemons]
      [:li {:key id} [pokemon-link name]])]
   [load-more-button pokemons]])

(defn- evolutions-list
  [evolutions]
  (if (zero? (count evolutions))
    [:p {:style {:text-align "center"}} "No evolutions"]
    [:div
     {:style {:padding-left "20%"}}
     [:p {:style {:margin-bottom 0}} "Evolutions"]
     [:ul
      {:style {:margin-top 5}}
      (map (fn [{:keys [name]}] [:li {:key [name]} [pokemon-link name]])
        evolutions)]]))

(defn- pokemon-info
  [{:keys [image name number types evolutions]
    :as   pokemon}]
  [:div
   {:style {:width "50%"}}
   (if pokemon
     [:div
      [:h4 {:style {:text-align "center"}} (str name " #" number)]
      [:img
       {:src   image
        :style {:border-radius "10px"
                :margin        "0 auto"
                :display       "block"
                :height        "100px"}}]
      [:p {:style {:text-align "center"}} "Types: " (str/join ", " types)]
      [evolutions-list evolutions]]
     [:h4 {:style {:text-align "center"}} "Pokemon not found"])])

(defn- title
  []
  [:h2 "Pokémons"])

(defn- search
  []
  (let [find-pokemon!           (fn [event]
                                  (rf/dispatch [:get-pokemon
                                                (-> event
                                                    .-target
                                                    .-value)]))
        find-pokemon-debounced! (debounce find-pokemon! 500)]
    [:div
     [:input
      {:placeholder "Search pokemon..."
       :on-change   (fn [e]
                      (.persist e)
                      (find-pokemon-debounced! e))
       :style       {:padding       "8px 15px"
                     :border        "none"
                     :border-radius 100
                     :font-family   "inherit"}}]]))

(defn show
  []
  (let [pokemons (rf/subscribe [:pokemons])
        pokemon  (rf/subscribe [:pokemon])]
    [:div
     {:style {:background-color "#282c34"
              :min-height       "100vh"
              :display          "flex"
              :flex-direction   "column"
              :align-items      "center"
              :justify-content  "center"
              :font-size        "calc(10px + 2vmin)"
              :color            "white"}}
     [loader]
     [title]
     [search]
     [:div
      {:style {:display         "flex"
               :width           "100%"
               :justify-content "space-between"}}
      [pokemon-list @pokemons]
      [pokemon-info @pokemon]]]))