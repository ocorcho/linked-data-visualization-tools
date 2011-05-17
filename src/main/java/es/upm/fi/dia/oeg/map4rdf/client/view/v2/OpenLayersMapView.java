/**
 * Copyright (c) 2011 Alexander De Leon Battista
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package es.upm.fi.dia.oeg.map4rdf.client.view.v2;

import org.gwtopenmaps.openlayers.client.LonLat;
import org.gwtopenmaps.openlayers.client.Map;
import org.gwtopenmaps.openlayers.client.MapOptions;
import org.gwtopenmaps.openlayers.client.MapWidget;
import org.gwtopenmaps.openlayers.client.control.LayerSwitcher;
import org.gwtopenmaps.openlayers.client.layer.Google;
import org.gwtopenmaps.openlayers.client.layer.Layer;
import org.gwtopenmaps.openlayers.client.layer.TransitionEffect;
import org.gwtopenmaps.openlayers.client.layer.WMS;
import org.gwtopenmaps.openlayers.client.layer.WMSOptions;
import org.gwtopenmaps.openlayers.client.layer.WMSParams;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

import es.upm.fi.dia.oeg.map4rdf.client.widget.LoadingWidget;
import es.upm.fi.dia.oeg.map4rdf.share.BoundingBox;
import es.upm.fi.dia.oeg.map4rdf.share.OpenLayersAdapter;
import es.upm.fi.dia.oeg.map4rdf.share.TwoDimentionalCoordinate;

/**
 * @author Alexander De Leon
 */
public class OpenLayersMapView implements MapView {

	/**
	 * By default the map is centered in Puerta del Sol, Madrid
	 */
	private static final LonLat DEFAULT_CENTER = new LonLat(-3.703637, 40.416645);
	private static final int DEFAULT_ZOOM_LEVEL = 6;
	public static final String WMS_URL = "http://www.idee.es/wms-c/IDEE-Base/IDEE-Base";

	private final LoadingWidget loadingWidget;
	private Map map;
	private final OpenLayersMapLayer defaultLayer;
	private AbsolutePanel panel;

	public OpenLayersMapView(LoadingWidget loadingWidget) {
		this.loadingWidget = loadingWidget;
		createUi();

		defaultLayer = (OpenLayersMapLayer) createLayer("default");
	}

	@Override
	public Widget asWidget() {
		return panel;
	}

	@Override
	public void startProcessing() {
		loadingWidget.center();
	}

	@Override
	public void stopProcessing() {
		loadingWidget.hide();
	}

	@Override
	public TwoDimentionalCoordinate getCurrentCenter() {
		return OpenLayersAdapter.getTwoDimentionalCoordinate(map.getCenter());
	}

	@Override
	public BoundingBox getVisibleBox() {
		return OpenLayersAdapter.getBoundingBox(map.getExtent());
	}

	@Override
	public void setVisibleBox(BoundingBox boundingBox) {
		map.zoomToExtent(OpenLayersAdapter.getLatLngBounds(boundingBox));
	}

	@Override
	public MapLayer getDefaultLayer() {
		return defaultLayer;
	}

	@Override
	public MapLayer createLayer(String name) {
		// TODO save layer
		return new OpenLayersMapLayer(this, map, name);
	}

	@Override
	public AbsolutePanel getContainer() {
		return panel;
	}

	/* ----------------------------- helper methods -- */
	private void createUi() {
		panel = new AbsolutePanel() {
			@Override
			protected void onLoad() {
				defaultLayer.bind();
			};
		};

		MapOptions options = new MapOptions();
		double[] resolutions = new double[] { 0.703125, 0.3515625, 0.17578125, 0.087890625, 0.0439453125,
				0.02197265625, 0.010986328125, 0.0054931640625, 0.00274658203125, 0.001373291015625,
				0.0006866455078125, 0.00034332275390625, 0.000171661376953125, 8.58306884765625e-005,
				4.291534423828125e-005, 2.1457672119140625e-005, 1.0728836059570313e-005, 5.3644180297851563e-006,
				2.6822090148925781e-006, 1.3411045074462891e-006 };
		options.setProjection("EPSG:4326");
		options.setResolutions(resolutions);

		MapWidget mapWidget = new MapWidget("100%", "100%", options);
		map = mapWidget.getMap();

		// Defining a WMSLayer and adding it to a Map
		WMSParams wmsParams = new WMSParams();
		wmsParams.setFormat("image/png");
		wmsParams.setLayers("Todas");

		WMSOptions wmsLayerParams = new WMSOptions();
		wmsLayerParams.setTransitionEffect(TransitionEffect.RESIZE);

		WMS wmsLayer = new WMS("IDEE", WMS_URL, wmsParams, wmsLayerParams);

		// markers = new Markers("Markers");

		map.addLayers(new Layer[] { wmsLayer, new Google("Google Maps") });
		map.addControl(new LayerSwitcher());
		map.setCenter(DEFAULT_CENTER, DEFAULT_ZOOM_LEVEL);

		panel.add(mapWidget);
		DOM.setStyleAttribute(panel.getElement(), "zIndex", "0");

	}

}
