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
        u.put("n5000-raster-utm-33-hele-landet", "kartdata/n5000/landsdekkende");
        u.put("n2000-raster-utm-33-hele-landet", "kartdata/n2000/landsdekkende");
        u.put("n500-kartdata-utm-33-fylkesvis-inndeling", "kartdata/n500/fylker");
        u.put("n2000-kartdata-utm33-hele-landet-postgis", "kartdata/n2000/landsdekkende");
        u.put("n1000-raster-utm33-hele-landet-mrsid", "kartdata/n1000/landsdekkende");
        u.put("n250-raster-utm-33-tile-inndelt-tiff", "kartdata/n250/kartblad");
        u.put("n1000-kartdata-utm33-hele-landet-fgdb", "kartdata/n1000/landsdekkende");
        u.put("n250-kartdata-utm33-hele-landet-postgis", "kartdata/n250/landsdekkende");
        u.put("n5000-kartdata-utm-33-hele-landet", "kartdata/n5000/landsdekkende");
        u.put("n250-kartdata-utm-33-hele-landet", "kartdata/n250/landsdekkende");
        u.put("n50-kartdata-utm33-hele-landet-postgis", "kartdata/n50/landsdekkende/");
        u.put("n50-kartdata-utm33-hele-landet-fgdb", "kartdata/n50/landsdekkende/");
        u.put("n500-raster-utm-33-tile-inndelt-tiff", "kartdata/n500/kartblad");
        u.put("n500-kartdata-utm33-hele-landet-postgis", "kartdata/n500/landsdekkende/");
        u.put("nasjonal-database-tur-og-friluftsruter-sosi", "kartdata/tfr");
        u.put("n250-raster-utm33-hele-landet-mrsid", "kartdata/n250/landsdekkende");
        u.put("n2000-raster-utm33-hele-landet-mrsid", "kartdata/n2000/landsdekkende");
        u.put("n50-raster-utm-33-tile-inndelt-hele-landet-tiff", "kartdata/n50/kartblad");
        u.put("n1000-kartdata-utm33-hele-landet-postgis", "kartdata/n1000/landsdekkende");
        u.put("n50-raster-utm33-fylkesvis-inndeling-mrsid", "kartdata/n50/fylker");
        u.put("n2000-kartdata-utm-33-hele-landet", "kartdata/n2000/landsdekkende");
        u.put("nasjonal-database-tur-og-friluftsruter-postgis", "kartdata/tfr");
        u.put("n50-raster-utm-32-tile-inndelt-s%C3%B8r-norge-tiff", "kartdata/n50/kartblad");
        u.put("n5000-kartdata-utm33-hele-landet-postgis", "kartdata/n5000/landsdekkende");
        u.put("n500-raster-utm33-hele-landet-mrsid", "kartdata/n500/landsdekkende");
        u.put("n250-kartdata-utm33-hele-landet-fgdb", "kartdata/n250/landsdekkende");
        u.put("n50-kartdata-utm-33-kommunevis-inndeling", "kartdata/n50/kommuner");
        u.put("n500-kartdata-utm-33-hele-landet", "kartdata/n500/landsdekkende");
        u.put("n50-raster-utm-35-tile-inndelt-finnmark-tiff", "kartdata/n50/kartblad");
        u.put("n1000-raster-utm-33-tile-inndelt-tiff", "kartdata/n1000/kartblad");
        u.put("n250-kartdata-utm-33-fylkesvis-inndeling", "kartdata/n250/fylker");
        u.put("n1000-kartdata-utm-33-hele-landet", "kartdata/n1000/landsdekkende/");
        u.put("n5000-kartdata-utm33-hele-landet-fgdb", "kartdata/n5000/landsdekkende");
        u.put("n2000-kartdata-utm33-hele-landet-fgdb", "kartdata/n2000/landsdekkende");
        u.put("n1000-kartdata-utm-33-fylkesvis-inndeling", "kartdata/n1000/fylker");
        u.put("n5000-raster-utm33-hele-landet-mrsid", "kartdata/n5000/landsdekkende");
        u.put("n500-kartdata-utm33-hele-landet-fgdb", "kartdata/n500/landsdekkende");
        u.put("stedsnavn-ssr-sosi-utm33", "stedsnavn/fylker");
        u.put("stedsnavn-ssr-wgs84-geojson", "stedsnavn/landsdekkende");
        u.put("digital-terrengmodell-10-m-utm-35", "terrengdata/10m/utm35");
        u.put("digital-terrengmodell-50-m-utm-33", "terrengdata/50m/utm33");
        u.put("digital-terrengmodell-10-m-utm-33", "terrengdata/10m/utm33");
        u.put("digital-terrengmodell-10-m-utm-32", "terrengdata/10m/utm32");
        u.put("elveg-adresser-utm-33-hele-landet", "vegdata/elveg/landsdekkende");
        u.put("vbase-utm-33", "vegdata/vbase/kommuner");
        u.put("vbase-utm-32", "vegdata/vbase/kommuner");
        u.put("elveg-geometri-utm-33-hele-landet", "vegdata/elveg/landsdekkende");
        u.put("vbase-utm-35", "vegdata/vbase/kommuner");
        u.put("vbase-utm-33-fylkesinndeling", "vegdata/vbase/fylker");
        u.put("statistiske-enheter-grunnkretser-utm-33-kommuneinndeling", "grensedata/kommuner");
        u.put("norges-maritime-grenser-geografiske-koordinater", "grensedata/landsdekkende");
        u.put("statistiske-enheter-grunnkretser-utm-33-hele-landet", "grensedata/landsdekkende");
        u.put("valgkretser-lokale-soner-kommunevis-hele-landet-sosi", "grensedata/landsdekkende");
        u.put("administrative-kommuner-utm-32-kommuneinndeling", "grensedata/kommuner");
        u.put("administrative-kommuner-utm-35-fylkesinndeling", "grensedata/fylker");
        u.put("digitale-postnummergrenser-utm33-shape-hele-landet", "grensedata/landsdekkende");
        u.put("administrative-kommuner-utm-33-kommuneinndeling", "grensedata/kommuner");
        u.put("digitale-postnummergrenser-utm33-sosi-hele-landet", "grensedata/landsdekkende");
        u.put("administrative-kommuner-utm-33-fylkesinndeling", "grensedata/fylker");
        u.put("administrative-enheter-norge-wgs-84-hele-landet-geojson", "grensedata/landsdekkende");
        u.put("administrative-fylker-utm-33-fylkesinndeling", "grensedata/fylker");
        u.put("statistiske-enheter-grunnkretser-utm-35-kommuneinndeling", "grensedata/kommuner");
        u.put("administrative-fylker-utm-35-fylkesinndeling", "grensedata/fylker");
        u.put("administrative-kommuner-utm-35-kommuneinndeling", "grensedata/kommuner");
        u.put("statistiske-enheter-grunnkretser-utm-32-kommuneinndeling", "grensedata/kommuner");
        u.put("administrative-enheter-norge-utm-33-hele-landet", "grensedata/landsdekkende");
        u.put("administrative-fylker-utm-32-fylkesinndeling", "grensedata/fylker");
        u.put("administrative-kommuner-utm-32-fylkesinndeling", "grensedata/fylker");
        u.put("illustrasjonskart-fylkeskart", "illustrasjonskart");
        u.put("illustrasjonskart-norgeskart", "illustrasjonskart");
        u.put("illustrasjonskart-norges-maritime-grenser", "illustrasjonskart");
        u.put("illustrasjonskart-svalbard-jan-mayen-og-antarktis", "illustrasjonskart");
        u.put("illustrasjonskart-relieffkart", "illustrasjonskart");
        u.put("illustrasjonskart-nord-europa", "illustrasjonskart");
        u.put("illustrasjonskart-europakart", "illustrasjonskart");
        u.put("illustrasjonskart-fylkeskart-n2000-raster", "illustrasjonskart");
        u.put("sj%C3%B8-dybdekurver-utm33-600m-grid-shape", "sjodata/dybdekurver");
        u.put("sj%C3%B8-dybdekurver-utm33-1000m-grid-sosi", "sjodata/dybdekurver");
        u.put("dybdedata-utm33-shape-format", "sjodata/dybdedata");
        u.put("sj%C3%B8-terrengmodell-25m-utm33", "sjodata/terrengdata/25m");
        u.put("sj%C3%B8-terrengmodell-5m-utm33", "sjodata/terrengdata/5m");
        u.put("sj%C3%B8-dybdekurver-utm33-1000m-grid-s57", "sjodata/dybdekurver");
        u.put("dybdedata-utm33-sosi-format", "sjodata/dybdedata");
        u.put("sj%C3%B8-terrengmodell-50m-utm33", "sjodata/terrengdata/50m");
        u.put("offisielle-adresser-utm33-sosi", "matrikkeldata/adresser");
        u.put("offisielle-adresser-utm33-csv", "matrikkeldata/adresser");
        u.put("geodesidata-altimetri-wgs84-textdat-filer", "geodesi/landsdekkende");
        u.put("geodesidata-geoide-wgs84-textdat-filer", "geodesi/landsdekkende");
        u.put("geodesidata-hastighetsfelt-norge-itrf2008-textdat-filer", "geodesi/landsdekkende");

        urlPrefixByDatasetName = Collections.unmodifiableMap(u);

    }

    static String createUrl(String datasetId, String fileName) {
        String urlPrefix = urlPrefixByDatasetName.get(datasetId);
        return urlPrefix == null ? null
                : "http://data.kartverket.no/download/system/files/" + urlPrefix + "/" + fileName;
    }

    static String createAnonUrl(String datasetId, String fileName) {
        String urlPrefix = urlPrefixByDatasetName.get(datasetId);
        return urlPrefix == null ? null : "http://data.kartverket.no/data/" + urlPrefix + "/" + fileName;
    }

}
