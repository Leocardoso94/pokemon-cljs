(ns pokemon.core
  (:require
    [devtools.core :as devtools]
    [pokemon.view :as view]
    [pokemon.events]
    [pokemon.subs]
    [reagent.core :as r]
    [re-frame.core :as rf]))

;; -- Debugging aids ----------------------------------------------------------
(devtools/install!);; nós amamos https://github.com/binaryage/cljs-devtools
(enable-console-print!);; agora o println se transformará em `console.log`

(defn render
  []
  (r/render [view/show] (js/document.getElementById "root")))

(defn init
  []
  (rf/dispatch-sync [:initialize])
  (render))
