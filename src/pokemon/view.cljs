(ns pokemon.view
  (:require
    [re-frame.core :as rf]))

(defn show
  []
  (let [app-name @(rf/subscribe [:app-name])]
    [:div
     {:style {:background-color "#282c34"
              :min-height       "100vh"
              :display          "flex"
              :flex-direction   "column"
              :align-items      "center"
              :justify-content  "center"
              :font-size        "calc(10px + 2vmin)"
              :color            "white"}}
     [:p (str "Hello from " app-name)]]))