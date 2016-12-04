# jskdata

A java downloader for http://data.kartverket.no/download/ and https://download.geonorge.no/skdl2/

# Maven

```
<repository>
    <id>ECC</id>
    <url>https://github.com/ElectronicChartCentre/ecc-mvn-repo/raw/master/releases</url>
</repository>

<dependency>
    <groupId>no.jskdata</groupId>
    <artifactId>jskdata</artifactId>
    <version>1.0.1</version>
</dependency>
````

## Usage
```
  // download from http://data.kartverket.no/download/
  Downloader kd = new KartverketDownload(username, password);
  kd.login();
  kd.dataset("administrative-fylker-utm-32-fylkesinndeling");
  kd.download((fileName, in) -> { # or implement Receiver
  });
  
  // download from https://download.geonorge.no/skdl2/
  // NB: different username/password than the public http://data.kartverket.no/download/
  Downloader gnd = new GeoNorgeDownload(geonorgeUsername, geonorgePassword);
  gnd.login();
  gnd.setFileNameFilter(n -> n.endsWith("_Ledning.zip"));
  gnd.dataset("FKB-data");
  gnd.download((fileName, in) -> { # or implement Receiver
  });
  
```

## Test
```
mvn -Ddata.kartverket.no.username=... \
    -Ddata.kartverket.no.password=... \
    -Dgeonorge.username=... \
    -Dgeonorge.password=... test
```

## Thanks
* [@atlefren](https://github.com/atlefren/) for [skdata](https://github.com/atlefren/skdata) that jskdatas `KartverketDownload` is mostly a java variant of.
* [@jhy](https://github.com/jhy/) for [jsoup](https://github.com/jhy/jsoup) that jskdata uses for HTML parsing.
* Google for Gson and Guava.
* Kartverket.

