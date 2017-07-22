(ns ^:figwheel-no-load gifrosenstock.app
  (:require [gifrosenstock.core :as core]
            [devtools.core :as devtools]))

(enable-console-print!)

(devtools/install!)

(core/init!)
