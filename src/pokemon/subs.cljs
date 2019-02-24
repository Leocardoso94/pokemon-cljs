(ns pokemon.subs
  (:require
    [re-frame.core :as rf]))

(rf/reg-sub :name
  (fn [db]
    (:name db)))

(rf/reg-sub :pokemon-name
  (fn [db]
    (:pokemon-name db)))

(rf/reg-sub :pokemon
  (fn [db]
    (:pokemon db)))

(rf/reg-sub :pokemons
  (fn [db]
    (:pokemons db)))

(rf/reg-sub :loading?
  (fn [db]
    (pos? (count (:requests db)))))