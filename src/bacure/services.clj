(ns bacure.services
  (:require [bacure.coerce :as c]
            [bacure.local-device :as ld])
  (:import (com.serotonin.bacnet4j.service.unconfirmed WhoIsRequest
                                                       WhoHasRequest
                                                       WhoHasRequest$Limits)))

(defn send-who-is
  [local-device-id {:keys [min-range max-range]
                    :as   args}]

  (doto (ld/local-device-object local-device-id)
    (.sendGlobalBroadcast (if (or min-range max-range)
                            (WhoIsRequest.
                             (c/clojure->bacnet :unsigned-integer (or min-range 0))
                             (c/clojure->bacnet :unsigned-integer (or max-range 4194304)))
                            (WhoIsRequest.)))))

(defn send-who-has
  [local-device-id object-identifier-or-name
   {:keys [min-range max-range] :or {min-range 0 max-range 4194303}
    :as   args}]

  (let [local-device   (ld/local-device-object local-device-id)
        min-range      (c/clojure->bacnet :unsigned-integer min-range)
        max-range      (c/clojure->bacnet :unsigned-integer max-range)
        object-id-type (if (string? object-identifier-or-name)
                         :character-string
                         :object-identifier)

        object-identifier-or-name (c/clojure->bacnet object-id-type
                                                     object-identifier-or-name)

        limits  (WhoHasRequest$Limits. min-range max-range)
        request (WhoHasRequest. limits object-identifier-or-name)]

    (doto local-device (.sendGlobalBroadcast request))))
