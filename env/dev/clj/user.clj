(ns user
  (:require [mount.core :as mount]
            [gifrosenstock.figwheel :refer [start-fw stop-fw cljs]]
            gifrosenstock.core))

(defn start []
  (mount/start-without #'gifrosenstock.core/repl-server))

(defn stop []
  (mount/stop-except #'gifrosenstock.core/repl-server))

(defn restart []
  (stop)
  (start))


