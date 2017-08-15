# metrics-clojure-splunk

A [metrics-clojure](https://github.com/sjl/metrics-clojure) reporter to use with [Splunk](http://www.splunk.com). Mostly copied from an existing reporter, and largely untested yet. Uses the [metrics-splunk](https://github.com/zenmoto/metrics-splunk) code, which does not have any maven presence. I have to figure out how to package that.

```clojure

(def registry (new-registry))
(def test-meter (meter registry ["test" "meters"  "metrics-test"]))
(def sp-reporter (splunk/reporter registry {:host "localhost" :port 8089
                                            :username "admin" :password "changeme"
                                            :scheme "https" :ssl-security-protocol "TLSv1_2"}
                                            :app "app-context" :owner "owner-context"))

```

## License

Copyright © William Swaney and Steve Losh

Distributed under the MIT/X11 license, same as the original metrics-clojure library.
