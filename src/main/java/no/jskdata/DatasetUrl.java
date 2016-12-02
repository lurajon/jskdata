package no.jskdata;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * copy of https://github.com/atlefren/skdata/blob/master/dataset_urls.json
 */
class DatasetUrl {

    private static final Map<String, String> urlPrefixByDatasetName;

    static {
        Map<String, String> u = new HashMap<>();
        u.put("n5000-raster-utm-33-hele-landet",
                "http://data.kartverket.no/download/system/files/kartdata/n5000/landsdekkende");
        u.put("n2000-raster-utm-33-hele-landet",
                "http://data.kartverket.no/download/system/files/kartdata/n2000/landsdekkende");
        u.put("n500-kartdata-utm-33-fylkesvis-inndeling",
                "http://data.kartverket.no/download/system/files/kartdata/n500/fylker");
        u.put("n2000-kartdata-utm33-hele-landet-postgis",
                "http://data.kartverket.no/download/system/files/kartdata/n2000/landsdekkende");
        u.put("n1000-raster-utm33-hele-landet-mrsid",
                "http://data.kartverket.no/download/system/files/kartdata/n1000/landsdekkende");
        u.put("n250-raster-utm-33-tile-inndelt-tiff",
                "http://data.kartverket.no/download/system/files/kartdata/n250/kartblad");
        u.put("n1000-kartdata-utm33-hele-landet-fgdb",
                "http://data.kartverket.no/download/system/files/kartdata/n1000/landsdekkende");
        u.put("n250-kartdata-utm33-hele-landet-postgis",
                "http://data.kartverket.no/download/system/files/kartdata/n250/landsdekkende");
        u.put("n5000-kartdata-utm-33-hele-landet",
                "http://data.kartverket.no/download/system/files/kartdata/n5000/landsdekkende");
        u.put("n250-kartdata-utm-33-hele-landet",
                "http://data.kartverket.no/download/system/files/kartdata/n250/landsdekkende");
        u.put("n50-kartdata-utm33-hele-landet-postgis",
                "http://data.kartverket.no/download/system/files/kartdata/n50/landsdekkende/");
        u.put("n50-kartdata-utm33-hele-landet-fgdb",
                "http://data.kartverket.no/download/system/files/kartdata/n50/landsdekkende/");
        u.put("n500-raster-utm-33-tile-inndelt-tiff",
                "http://data.kartverket.no/download/system/files/kartdata/n500/kartblad");
        u.put("n500-kartdata-utm33-hele-landet-postgis",
                "http://data.kartverket.no/download/system/files/kartdata/n500/landsdekkende/");
        u.put("nasjonal-database-tur-og-friluftsruter-sosi",
                "http://data.kartverket.no/download/system/files/kartdata/tfr");
        u.put("n250-raster-utm33-hele-landet-mrsid",
                "http://data.kartverket.no/download/system/files/kartdata/n250/landsdekkende");
        u.put("n2000-raster-utm33-hele-landet-mrsid",
                "http://data.kartverket.no/download/system/files/kartdata/n2000/landsdekkende");
        u.put("n50-raster-utm-33-tile-inndelt-hele-landet-tiff",
                "http://data.kartverket.no/download/system/files/kartdata/n50/kartblad");
        u.put("n1000-kartdata-utm33-hele-landet-postgis",
                "http://data.kartverket.no/download/system/files/kartdata/n1000/landsdekkende");
        u.put("n50-raster-utm33-fylkesvis-inndeling-mrsid",
                "http://data.kartverket.no/download/system/files/kartdata/n50/fylker");
        u.put("n2000-kartdata-utm-33-hele-landet",
                "http://data.kartverket.no/download/system/files/kartdata/n2000/landsdekkende");
        u.put("nasjonal-database-tur-og-friluftsruter-postgis",
                "http://data.kartverket.no/download/system/files/kartdata/tfr");
        u.put("n50-raster-utm-32-tile-inndelt-s%C3%B8r-norge-tiff",
                "http://data.kartverket.no/download/system/files/kartdata/n50/kartblad");
        u.put("n5000-kartdata-utm33-hele-landet-postgis",
                "http://data.kartverket.no/download/system/files/kartdata/n5000/landsdekkende");
        u.put("n500-raster-utm33-hele-landet-mrsid",
                "http://data.kartverket.no/download/system/files/kartdata/n500/landsdekkende");
        u.put("n250-kartdata-utm33-hele-landet-fgdb",
                "http://data.kartverket.no/download/system/files/kartdata/n250/landsdekkende");
        u.put("n50-kartdata-utm-33-kommunevis-inndeling",
                "http://data.kartverket.no/download/system/files/kartdata/n50/kommuner");
        u.put("n500-kartdata-utm-33-hele-landet",
                "http://data.kartverket.no/download/system/files/kartdata/n500/landsdekkende");
        u.put("n50-raster-utm-35-tile-inndelt-finnmark-tiff",
                "http://data.kartverket.no/download/system/files/kartdata/n50/kartblad");
        u.put("n1000-raster-utm-33-tile-inndelt-tiff",
                "http://data.kartverket.no/download/system/files/kartdata/n1000/kartblad");
        u.put("n250-kartdata-utm-33-fylkesvis-inndeling",
                "http://data.kartverket.no/download/system/files/kartdata/n250/fylker");
        u.put("n1000-kartdata-utm-33-hele-landet",
                "http://data.kartverket.no/download/system/files/kartdata/n1000/landsdekkende/");
        u.put("n5000-kartdata-utm33-hele-landet-fgdb",
                "http://data.kartverket.no/download/system/files/kartdata/n5000/landsdekkende");
        u.put("n2000-kartdata-utm33-hele-landet-fgdb",
                "http://data.kartverket.no/download/system/files/kartdata/n2000/landsdekkende");
        u.put("n1000-kartdata-utm-33-fylkesvis-inndeling",
                "http://data.kartverket.no/download/system/files/kartdata/n1000/fylker");
        u.put("n5000-raster-utm33-hele-landet-mrsid",
                "http://data.kartverket.no/download/system/files/kartdata/n5000/landsdekkende");
        u.put("n500-kartdata-utm33-hele-landet-fgdb",
                "http://data.kartverket.no/download/system/files/kartdata/n500/landsdekkende");
        u.put("stedsnavn-ssr-sosi-utm33", "http://data.kartverket.no/download/system/files/stedsnavn/fylker");
        u.put("stedsnavn-ssr-wgs84-geojson", "http://data.kartverket.no/download/system/files/stedsnavn/landsdekkende");
        u.put("digital-terrengmodell-10-m-utm-35",
                "http://data.kartverket.no/download/system/files/terrengdata/10m/utm35");
        u.put("digital-terrengmodell-50-m-utm-33",
                "http://data.kartverket.no/download/system/files/terrengdata/50m/utm33");
        u.put("digital-terrengmodell-10-m-utm-33",
                "http://data.kartverket.no/download/system/files/terrengdata/10m/utm33");
        u.put("digital-terrengmodell-10-m-utm-32",
                "http://data.kartverket.no/download/system/files/terrengdata/10m/utm32");
        u.put("elveg-adresser-utm-33-hele-landet",
                "http://data.kartverket.no/download/system/files/vegdata/elveg/landsdekkende");
        u.put("vbase-utm-33", "http://data.kartverket.no/download/system/files/vegdata/vbase/kommuner");
        u.put("vbase-utm-32", "http://data.kartverket.no/download/system/files/vegdata/vbase/kommuner");
        u.put("elveg-geometri-utm-33-hele-landet",
                "http://data.kartverket.no/download/system/files/vegdata/elveg/landsdekkende");
        u.put("vbase-utm-35", "http://data.kartverket.no/download/system/files/vegdata/vbase/kommuner");
        u.put("vbase-utm-33-fylkesinndeling", "http://data.kartverket.no/download/system/files/vegdata/vbase/fylker");
        u.put("statistiske-enheter-grunnkretser-utm-33-kommuneinndeling",
                "http://data.kartverket.no/download/system/files/grensedata/kommuner");
        u.put("norges-maritime-grenser-geografiske-koordinater",
                "http://data.kartverket.no/download/system/files/grensedata/landsdekkende");
        u.put("statistiske-enheter-grunnkretser-utm-33-hele-landet",
                "http://data.kartverket.no/download/system/files/grensedata/landsdekkende");
        u.put("valgkretser-lokale-soner-kommunevis-hele-landet-sosi",
                "http://data.kartverket.no/download/system/files/grensedata/landsdekkende");
        u.put("administrative-kommuner-utm-32-kommuneinndeling",
                "http://data.kartverket.no/download/system/files/grensedata/kommuner");
        u.put("administrative-kommuner-utm-35-fylkesinndeling",
                "http://data.kartverket.no/download/system/files/grensedata/fylker");
        u.put("digitale-postnummergrenser-utm33-shape-hele-landet",
                "http://data.kartverket.no/download/system/files/grensedata/landsdekkende");
        u.put("administrative-kommuner-utm-33-kommuneinndeling",
                "http://data.kartverket.no/download/system/files/grensedata/kommuner");
        u.put("digitale-postnummergrenser-utm33-sosi-hele-landet",
                "http://data.kartverket.no/download/system/files/grensedata/landsdekkende");
        u.put("administrative-kommuner-utm-33-fylkesinndeling",
                "http://data.kartverket.no/download/system/files/grensedata/fylker");
        u.put("administrative-enheter-norge-wgs-84-hele-landet-geojson",
                "http://data.kartverket.no/download/system/files/grensedata/landsdekkende");
        u.put("administrative-fylker-utm-33-fylkesinndeling",
                "http://data.kartverket.no/download/system/files/grensedata/fylker");
        u.put("statistiske-enheter-grunnkretser-utm-35-kommuneinndeling",
                "http://data.kartverket.no/download/system/files/grensedata/kommuner");
        u.put("administrative-fylker-utm-35-fylkesinndeling",
                "http://data.kartverket.no/download/system/files/grensedata/fylker");
        u.put("administrative-kommuner-utm-35-kommuneinndeling",
                "http://data.kartverket.no/download/system/files/grensedata/kommuner");
        u.put("statistiske-enheter-grunnkretser-utm-32-kommuneinndeling",
                "http://data.kartverket.no/download/system/files/grensedata/kommuner");
        u.put("administrative-enheter-norge-utm-33-hele-landet",
                "http://data.kartverket.no/download/system/files/grensedata/landsdekkende");
        u.put("administrative-fylker-utm-32-fylkesinndeling",
                "http://data.kartverket.no/download/system/files/grensedata/fylker");
        u.put("administrative-kommuner-utm-32-fylkesinndeling",
                "http://data.kartverket.no/download/system/files/grensedata/fylker");
        u.put("illustrasjonskart-fylkeskart", "http://data.kartverket.no/download/system/files/illustrasjonskart");
        u.put("illustrasjonskart-norgeskart", "http://data.kartverket.no/download/system/files/illustrasjonskart");
        u.put("illustrasjonskart-norges-maritime-grenser",
                "http://data.kartverket.no/download/system/files/illustrasjonskart");
        u.put("illustrasjonskart-svalbard-jan-mayen-og-antarktis",
                "http://data.kartverket.no/download/system/files/illustrasjonskart");
        u.put("illustrasjonskart-relieffkart", "http://data.kartverket.no/download/system/files/illustrasjonskart");
        u.put("illustrasjonskart-nord-europa", "http://data.kartverket.no/download/system/files/illustrasjonskart");
        u.put("illustrasjonskart-europakart", "http://data.kartverket.no/download/system/files/illustrasjonskart");
        u.put("illustrasjonskart-fylkeskart-n2000-raster",
                "http://data.kartverket.no/download/system/files/illustrasjonskart");
        u.put("sj%C3%B8-dybdekurver-utm33-600m-grid-shape",
                "http://data.kartverket.no/download/system/files/sjodata/dybdekurver");
        u.put("sj%C3%B8-dybdekurver-utm33-1000m-grid-sosi",
                "http://data.kartverket.no/download/system/files/sjodata/dybdekurver");
        u.put("dybdedata-utm33-shape-format", "http://data.kartverket.no/download/system/files/sjodata/dybdedata");
        u.put("sj%C3%B8-terrengmodell-25m-utm33",
                "http://data.kartverket.no/download/system/files/sjodata/terrengdata/25m");
        u.put("sj%C3%B8-terrengmodell-5m-utm33",
                "http://data.kartverket.no/download/system/files/sjodata/terrengdata/5m");
        u.put("sj%C3%B8-dybdekurver-utm33-1000m-grid-s57",
                "http://data.kartverket.no/download/system/files/sjodata/dybdekurver");
        u.put("dybdedata-utm33-sosi-format", "http://data.kartverket.no/download/system/files/sjodata/dybdedata");
        u.put("sj%C3%B8-terrengmodell-50m-utm33",
                "http://data.kartverket.no/download/system/files/sjodata/terrengdata/50m");
        u.put("offisielle-adresser-utm33-sosi",
                "http://data.kartverket.no/download/system/files/matrikkeldata/adresser");
        u.put("offisielle-adresser-utm33-csv",
                "http://data.kartverket.no/download/system/files/matrikkeldata/adresser");
        u.put("geodesidata-altimetri-wgs84-textdat-filer",
                "http://data.kartverket.no/download/system/files/geodesi/landsdekkende");
        u.put("geodesidata-geoide-wgs84-textdat-filer",
                "http://data.kartverket.no/download/system/files/geodesi/landsdekkende");
        u.put("geodesidata-hastighetsfelt-norge-itrf2008-textdat-filer",
                "http://data.kartverket.no/download/system/files/geodesi/landsdekkende");

        urlPrefixByDatasetName = Collections.unmodifiableMap(u);

    }

    static String createUrl(String datasetId, String fileName) {
        String urlPrefix = urlPrefixByDatasetName.get(datasetId);
        return urlPrefix == null ? null : urlPrefix + "/" + fileName;
    }

}
