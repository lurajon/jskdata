package no.jskdata;

import junit.framework.TestCase;

public class DatasetUrlTest extends TestCase {

    public void testCreateUrl() {
        assertEquals(
                "http://data.kartverket.no/download/system/files/kartdata/n50/kommuner/Kartdata_441_Os_UTM33_N50_SOSI.zip",
                DatasetUrl.createUrl("n50-kartdata-utm-33-kommunevis-inndeling", "Kartdata_441_Os_UTM33_N50_SOSI.zip"));
        assertEquals(
                "http://data.kartverket.no/download/system/files/grensedata/fylker/Grensedata_5_Oppland_UTM32_Adm_enheter_SOSI.zip",
                DatasetUrl.createUrl("administrative-fylker-utm-33-fylkesinndeling",
                        "Grensedata_5_Oppland_UTM32_Adm_enheter_SOSI.zip"));
        assertEquals(
                "http://data.kartverket.no/download/system/files/kartdata/n5000/landsdekkende/Kartdata_Norge_UTM33_N5000_SOSI.zip",
                DatasetUrl.createUrl("n5000-kartdata-utm-33-hele-landet", "Kartdata_Norge_UTM33_N5000_SOSI.zip"));
    }

}
