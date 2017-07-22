(ns gifrosenstock.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [gifrosenstock.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[gifrosenstock started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[gifrosenstock has shut down successfully]=-"))
   :middleware wrap-dev})
