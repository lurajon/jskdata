## jskdata

A java downloader for http://data.kartverket.no/download/

# Usage
```
  KartverketDownload kd = new KartverketDownload(username, password);
  kd.dataset("administrative-fylker-utm-32-fylkesinndeling");
  for (String url : kd.urls()) {
      HttpURLConnection conn = kd.openConnection(url);
  }
```

# Test
```
mvn -Ddata.kartverket.no.username=... -Ddata.kartverket.no.password=... test
```

# Thanks
* @atlefren for [skdata](https://github.com/atlefren/skdata) that jskdata is mostly a java variant of.
* @jhy for [jsoup](https://github.com/jhy/jsoup) that jskdata uses for HTML parsing.
* Google for Guava and Gson
* Kartverket

