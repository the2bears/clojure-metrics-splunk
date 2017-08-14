(ns metrics.reporters.splunk
  "Splunk reporter interface"
  (:require [metrics.core  :refer [default-registry]]
            [metrics.reporters :as mrep])
  (:import java.util.concurrent.TimeUnit
           java.net.InetSocketAddress
           [com.codahale.metrics Metric MetricRegistry Clock MetricFilter]
           [com.splunk Service ServiceArgs SSLSecurityProtocol]
           [io.github.zenmoto.metrics SplunkReporter SplunkReporter$Builder]))

(defn- ^InetSocketAddress inet-socket-address
  [^String hostname ^Long port]
  (InetSocketAddress. hostname (int port)))

(defn- ^SplunkReporter$Builder builder-for-registry
  [^MetricRegistry reg]
  (SplunkReporter/forRegistry reg))

(defn- resolve-ssl-security-protocol [ssp]
  (SSLSecurityProtocol/valueOf ssp))

(defn- splunk-service [{:keys [splunk app host owner password port
                               scheme ssl-security-protocol token username] :as opts}]
                                        ;build the splunk service here
  (let [args (ServiceArgs.)]
    (when-let [^String a app]
      (.setApp args a))
    (when-let [^String h host]
      (.setHost args h))
    (when-let [^String o owner]
      (.setOwnder args o))
    (when-let [^String pw password]
      (.setPassword args pw))
    (when-let [^Int p port]
      (.setPort args p))
    (when-let [^String sc scheme]
      (.setScheme args sc))
    (when-let [^String ssp ssl-security-protocol]
      (Service/setSslSecurityProtocol (resolve-ssl-security-protocol ssp)))
    (when-let [^String t token]
      (.setToken args t))
    (when-let [^String u username]
      (.setUsername args u))
    (println args)
    (Service/connect args)))          

(defn ^io.github.zenmoto.metrics.SplunkReporter reporter
  ([opts]
   (reporter default-registry opts))
  ([^MetricRegistry reg {:keys [splunk app host owner password port scheme token username prefix clock rate-unit duration-unit filter] :as opts}]
   (let [sp (or splunk
               (splunk-service opts))
         b (builder-for-registry reg)]
     (when-let [^String s prefix]
       (.prefixedWith b s))
     (when-let [^Clock c clock]
       (.withClock b c))
     (when-let [^TimeUnit ru rate-unit]
       (.convertRatesTo b ru))
     (when-let [^TimeUnit du duration-unit]
       (.convertDurationsTo b du))
     (when-let [^MetricFilter f filter]
       (.filter b f))
     (.build b sp))))

(defn start
  "Report all metrics to graphite periodically."
  [^SplunkReporter r ^long seconds]
  (mrep/start r seconds))

(defn stop
  "Stops reporting."
  [^SplunkReporter r]
  (mrep/stop r))
