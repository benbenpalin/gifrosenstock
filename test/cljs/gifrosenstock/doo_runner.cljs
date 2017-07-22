(ns gifrosenstock.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [gifrosenstock.core-test]))

(doo-tests 'gifrosenstock.core-test)

