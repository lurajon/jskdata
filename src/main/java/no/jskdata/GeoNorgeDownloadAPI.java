package no.jskdata;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import no.jskdata.data.geonorge.Area;
import no.jskdata.data.geonorge.Capabilities;
import no.jskdata.data.geonorge.File;
import no.jskdata.data.geonorge.Format;
import no.jskdata.data.geonorge.Order;
import no.jskdata.data.geonorge.OrderArea;
import no.jskdata.data.geonorge.OrderLine;
import no.jskdata.data.geonorge.OrderReceipt;
import no.jskdata.data.geonorge.Projection;

/**
 * A way to access
 * https://www.geonorge.no/for-utviklere/APIer-og-grensesnitt/nedlastingsapiet/
 * from java.
 */
public class GeoNorgeDownloadAPI extends Downloader {

	private final Set<String> datasetIds = new LinkedHashSet<>();

	private final Gson gson = new Gson();

	@Override
	public void dataset(String datasetId) {
		datasetIds.add(datasetId);
	}

	private DatasetInfo datasetInfo(String datasetId) throws IOException {

		String capabilitiesUrl = "https://nedlasting.geonorge.no/api/capabilities/" + datasetId;
		Capabilities capabilities = fetchAndParse(capabilitiesUrl, Capabilities.class);
		if (capabilities == null) {
			throw new IllegalArgumentException("Invalid dataset: " + datasetId);
		}

		String orderUrl = capabilities.getOrderUrl();

		@SuppressWarnings("serial")
		List<Format> formats = fetchAndParse(capabilities.getFormatUrl(), new TypeToken<ArrayList<Format>>() {
		}.getType());
		if (formats == null || formats.isEmpty()) {
			throw new IllegalArgumentException("Dataset does not have any formats: " + datasetId);
		}

		@SuppressWarnings("serial")
		List<Projection> projections = fetchAndParse(capabilities.getProjectionUrl(),
				new TypeToken<ArrayList<Projection>>() {
				}.getType());
		if (projections == null || projections.isEmpty()) {
			throw new IllegalArgumentException("Dataset does not have any projections: " + datasetId);
		}

		@SuppressWarnings("serial")
		List<Area> areas = fetchAndParse(capabilities.getAreaUrl(), new TypeToken<ArrayList<Area>>() {
		}.getType());
		if (projections == null || projections.isEmpty()) {
			throw new IllegalArgumentException("Dataset does not have any areas: " + datasetId);
		}

		DatasetInfo datasetInfo = new DatasetInfo();
		datasetInfo.orderUrl = orderUrl;
		datasetInfo.formats = formats;
		datasetInfo.projections = projections;
		datasetInfo.areas = areas;

		return datasetInfo;
	}

	@Override
	public void download(Receiver receiver) throws IOException {

		Map<String, Order> orderByOrderUrl = new HashMap<>();

		for (String datasetId : datasetIds) {
			DatasetInfo info = datasetInfo(datasetId);

			Order order = orderByOrderUrl.get(info.orderUrl);
			if (order == null) {
				order = new Order();
				orderByOrderUrl.put(info.orderUrl, order);
			}

			OrderLine orderLine = new OrderLine();
			orderLine.areas = info.orderAreasForCountry();
			orderLine.metadataUuid = datasetId;
			orderLine.formats = info.formats;
			orderLine.projections = info.projections;

			order.addOrderLine(orderLine);
		}

		if (orderByOrderUrl.isEmpty()) {
			return;
		}

		for (Map.Entry<String, Order> e : orderByOrderUrl.entrySet()) {
			String orderUrl = e.getKey();
			Order order = e.getValue();

			HttpURLConnection conn = (HttpURLConnection) new URL(orderUrl).openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			OutputStream out = conn.getOutputStream();
			out.write(gson.toJson(order).getBytes("UTF-8"));
			out.flush();

			Reader reader = new InputStreamReader(conn.getInputStream());
			OrderReceipt reciept = gson.fromJson(reader, OrderReceipt.class);

			for (File file : reciept.getFiles()) {
				if (!fileNameFilter.test(file.name)) {
					continue;
				}

				currentDownloadUrl = file.downloadUrl;
				HttpURLConnection fileConn = (HttpURLConnection) new URL(file.downloadUrl).openConnection();
				receiver.receive(file.name, fileConn.getInputStream());
				currentDownloadUrl = null;
			}
		}

	}

	private <T> T fetchAndParse(String url, Class<T> type) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		if (conn.getResponseCode() == 404) {
			return null;
		}
		Reader reader = new InputStreamReader(conn.getInputStream());
		return gson.fromJson(reader, type);
	}

	private <T> T fetchAndParse(String url, Type type) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		if (conn.getResponseCode() == 404) {
			return null;
		}
		Reader reader = new InputStreamReader(conn.getInputStream());
		return gson.fromJson(reader, type);
	}

	@Override
	public void clear() {
		datasetIds.clear();
	}

	private static class DatasetInfo {

		String orderUrl;
		List<Format> formats;
		List<Projection> projections;
		List<Area> areas;

		public List<OrderArea> orderAreasForCountry() {
			if (areas == null) {
				return Collections.emptyList();
			}

			// look for country wide
			for (Area area : areas) {
				if (area.isCountryWide()) {
					return Collections.singletonList(area.asOrderArea());
				}
			}

			List<OrderArea> countys = new ArrayList<>();
			for (Area area : areas) {
				if (area.isCounty()) {
					countys.add(area.asOrderArea());
				}
			}
			if (!countys.isEmpty()) {
				return countys;
			}

			// give up
			return Collections.emptyList();

		}

	}

}
