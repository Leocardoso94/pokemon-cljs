(ns pokemon.subs
  (:require
    [re-frame.core :as rf]))

(rf/reg-sub :app-name
  (fn [db]
    (:app-name db)))
