package at.tectas.buildbox.library.communication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.params.CoreProtocolPNames;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import at.tectas.buildbox.library.communication.Communicator;
import at.tectas.buildbox.library.communication.DownloadStatus;
import at.tectas.buildbox.library.communication.asynccommunicators.BitmapAsyncCommunicator;
import at.tectas.buildbox.library.communication.asynccommunicators.DownloadAsyncCommunicator;
import at.tectas.buildbox.library.communication.asynccommunicators.JSONArrayAsyncCommunicator;
import at.tectas.buildbox.library.communication.asynccommunicators.JSONElementAsyncCommunicator;
import at.tectas.buildbox.library.communication.asynccommunicators.JSONObjectAsyncCommunicator;
import at.tectas.buildbox.library.communication.asynccommunicators.interfaces.IDownloadAsyncCommunicator;
import at.tectas.buildbox.library.communication.callbacks.CallbackType;
import at.tectas.buildbox.library.communication.callbacks.interfaces.ICommunicatorCallback;
import at.tectas.buildbox.library.communication.callbacks.interfaces.IDownloadCancelledCallback;
import at.tectas.buildbox.library.communication.callbacks.interfaces.IDownloadFinishedCallback;
import at.tectas.buildbox.library.communication.callbacks.interfaces.IDownloadProgressCallback;
import at.tectas.buildbox.library.content.items.ParentItem;
import at.tectas.buildbox.library.helpers.PropertyHelper;

public class Communicator {

	public DownloadAsyncCommunicator executeDownloadAsyncCommunicator(
			DownloadPackage pack, IDownloadProgressCallback updateCallback,
			IDownloadFinishedCallback finishedCallback,
			IDownloadCancelledCallback cancelCallback) {

		DownloadAsyncCommunicator communicator = new DownloadAsyncCommunicator(
				this, pack.getKey(), updateCallback, finishedCallback,
				cancelCallback);

		communicator = (DownloadAsyncCommunicator) communicator
				.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, pack);

		return communicator;
	}

	public DownloadAsyncCommunicator executeDownloadAsyncCommunicator(
			DownloadPackage pack,
			Hashtable<CallbackType, IDownloadProgressCallback> updateCallback,
			Hashtable<CallbackType, IDownloadFinishedCallback> finishedCallback,
			Hashtable<CallbackType, IDownloadCancelledCallback> cancelCallback) {
		DownloadAsyncCommunicator communicator = new DownloadAsyncCommunicator(
				this, pack.getKey(), updateCallback, finishedCallback,
				cancelCallback);

		communicator = (DownloadAsyncCommunicator) communicator
				.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, pack);

		return communicator;
	}

	public BitmapAsyncCommunicator executeBitmapAsyncCommunicator(String url) {
		BitmapAsyncCommunicator communicator = new BitmapAsyncCommunicator(this);

		communicator = (BitmapAsyncCommunicator) communicator
				.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, url);

		return communicator;
	}

	public BitmapAsyncCommunicator executeBitmapAsyncCommunicator(String url,
			ImageView view, ICommunicatorCallback callback) {
		BitmapAsyncCommunicator communicator = new BitmapAsyncCommunicator(
				this, view, callback);

		communicator = (BitmapAsyncCommunicator) communicator
				.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, url);

		return communicator;
	}

	public JSONElementAsyncCommunicator executeJSONElementAsyncCommunicator(
			String url) {
		JSONElementAsyncCommunicator communicator = new JSONElementAsyncCommunicator(
				this);

		communicator = (JSONElementAsyncCommunicator) communicator
				.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, url);

		return communicator;
	}

	public JSONElementAsyncCommunicator executeJSONElementAsyncCommunicator(
			String url, ICommunicatorCallback callback) {
		JSONElementAsyncCommunicator communicator = new JSONElementAsyncCommunicator(
				this, callback);

		communicator = (JSONElementAsyncCommunicator) communicator
				.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, url);

		return communicator;
	}

	public JSONElementAsyncCommunicator executeJSONElementAsyncCommunicator(
			String url, ICommunicatorCallback callback, ParentItem parent) {
		JSONElementAsyncCommunicator communicator = new JSONElementAsyncCommunicator(
				this, callback, parent);

		communicator = (JSONElementAsyncCommunicator) communicator
				.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, url);

		return communicator;
	}

	public JSONObjectAsyncCommunicator executeJSONObjectAsyncCommunicator(
			String url) {
		JSONObjectAsyncCommunicator communicator = new JSONObjectAsyncCommunicator(
				this);

		communicator = (JSONObjectAsyncCommunicator) communicator
				.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, url);

		return communicator;
	}

	public JSONObjectAsyncCommunicator executeJSONObjectAsyncCommunicator(
			String url, ICommunicatorCallback callback) {
		JSONObjectAsyncCommunicator communicator = new JSONObjectAsyncCommunicator(
				this, callback);

		communicator = (JSONObjectAsyncCommunicator) communicator
				.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, url);

		return communicator;
	}

	public JSONArrayAsyncCommunicator executeJSONArrayAsyncCommunicator(
			String url) {
		JSONArrayAsyncCommunicator communicator = new JSONArrayAsyncCommunicator(
				this);

		communicator = (JSONArrayAsyncCommunicator) communicator
				.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, url);

		return communicator;
	}

	public JSONArrayAsyncCommunicator executeJSONArrayAsyncCommunicator(
			String url, ICommunicatorCallback callback) {
		JSONArrayAsyncCommunicator communicator = new JSONArrayAsyncCommunicator(
				this, callback);

		communicator = (JSONArrayAsyncCommunicator) communicator
				.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, url);

		return communicator;
	}

	public String getString(String url) throws Exception {
		return getString(url, (Object) null);
	}

	public String getString(String url, JsonElement parameters) {
		return this.getString(url, (Object)parameters);
	}

	public String getString(String url, ArrayList<NameValuePair> parameters)
			throws Exception {
		return this.getString(url, (Object)parameters);
	}

	@SuppressWarnings("unchecked")
	protected String getString(String url, Object parameters) {
		if (url != null) {
			BufferedReader in = null;
			try {
				HttpURLConnection connection = null;
				if (parameters != null) {
					if (parameters instanceof JsonElement) {
					connection = Communicator.getConnection(url, (JsonElement)parameters);
					} else if (parameters instanceof ArrayList<?>) {
						connection = Communicator.getConnection(url, (ArrayList<NameValuePair>)parameters);
					} else {
						connection = Communicator.getConnection(url);
					}
				} else {
					connection = Communicator.getConnection(url);
				}

				if (connection != null) {
					return Communicator.readStream(connection.getInputStream());
				}
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			return null;
		}
	}

	public Bitmap getBitmap(String url, int width) throws IOException {
		Bitmap bitmap = null;
		InputStream in = null;
		BufferedOutputStream out = null;

		try {
			HttpURLConnection connection = Communicator.getConnection(url);

			boolean statusOk = Communicator.checkHttpStatus(connection);

			if (statusOk) {

				in = new BufferedInputStream(connection.getInputStream());

				final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
				out = new BufferedOutputStream(dataStream);

				byte[] buffer = new byte[1024];

				int len, processed = 0;

				while ((len = in.read(buffer)) != -1) {
					out.write(buffer, 0, len);

					processed += len;

					if (processed % 10240 <= 512) {
						out.flush();
					}
				}

				out.flush();

				final byte[] data = dataStream.toByteArray();

				BitmapFactory.Options boundsOptions = new BitmapFactory.Options();

				boundsOptions.inJustDecodeBounds = true;

				BitmapFactory.decodeByteArray(data, 0, data.length,
						boundsOptions);

				BitmapFactory.Options options = new BitmapFactory.Options();

				if (boundsOptions.outWidth != 0 && width != 0) {
					options.inSampleSize = Math
							.round((float) boundsOptions.outWidth
									/ (((float) width / 5) * 4));
				}

				bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,
						options);
			} else {
				throw new Exception("Bad request at: " + url + ". "
						+ "Statuscode: " + connection.getResponseCode());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null)
				in.close();
			if (out != null)
				out.close();
		}

		return bitmap;
	}

	public JsonObject getJsonObject(String url) throws Exception {
		return this.getJsonElement(url).getAsJsonObject();
	}

	public JsonObject getJsonObject(String url, JsonElement element) throws Exception {
		return this.getJsonElement(url, element).getAsJsonObject();
	}

	public JsonObject getJsonObject(String url, ArrayList<NameValuePair> parameters) throws Exception {
		return this.getJsonElement(url, parameters).getAsJsonObject();
	}
	
	public JsonArray getJsonArray(String url) throws Exception {
		return this.getJsonElement(url).getAsJsonArray();
	}

	public JsonArray getJsonArray(String url, JsonElement parameters) throws Exception {
		return this.getJsonElement(url, parameters).getAsJsonArray();
	}
	
	public JsonArray getJsonArray(String url, ArrayList<NameValuePair> parameters) throws Exception {
		return this.getJsonElement(url, parameters).getAsJsonArray();
	}
	
	public JsonElement getJsonElement(String url) throws Exception {
		String respond = this.getString(url);

		JsonParser parser = new JsonParser();

		return parser.parse(respond);
	}

	public JsonElement getJsonElement(String url, JsonElement element) throws Exception {
		String respond = this.getString(url, element);

		JsonParser parser = new JsonParser();

		return parser.parse(respond);
	}

	public JsonElement getJsonElement(String url,
			ArrayList<NameValuePair> parameters) throws Exception {
		String respond = this.getString(url, parameters);
		
		JsonParser parser = new JsonParser();

		return parser.parse(respond);
	}

	public DownloadResponse downloadFileToSd(DownloadPackage pack,
			IDownloadAsyncCommunicator progressHandler) {
		return this.downloadFileToSd(pack, progressHandler, 0, 0);
	}

	public DownloadResponse downloadFileToSd(DownloadPackage pack,
			IDownloadAsyncCommunicator progressHandler, int alreadyDownloaded,
			int retries) {

		if (pack != null) {
			pack.setResponse(new DownloadResponse(pack, DownloadStatus.Pending));

			if (pack.url != null && !pack.url.isEmpty() && retries < 5) {
				InputStream in = null;
				FileOutputStream out = null;

				try {
					HttpURLConnection connection = Communicator.getConnection(
							pack.url, alreadyDownloaded);

					boolean statusOk = Communicator.checkHttpStatus(connection);

					if (statusOk) {

						String responseFilename = Communicator
								.tryGetFilenameFromConnection(connection);

						Boolean ranges = Communicator
								.tryGetAcceptRangesFromConnection(connection);

						long fileSize = connection.getContentLength();

						in = new BufferedInputStream(
								connection.getInputStream());

						MessageDigest md = MessageDigest.getInstance("MD5");

						in = new DigestInputStream(in, md);

						File dir = new File(pack.directory);

						if (!dir.exists()) {
							boolean successfull = dir.mkdirs();

							if (!successfull) {
								throw new IOException(
										"Couldn't create directory: "
												+ dir.getPath());
							}
						}

						pack.setFilename(PropertyHelper
								.stringIsNullOrEmpty(responseFilename) ? (pack
								.getFilename().equals("") ? (UUID.randomUUID()
								.toString() + '.' + pack.type) : pack
								.getFilename()) : responseFilename);

						File file = new File(pack.directory, pack.getFilename());

						if (file.exists()) {
							file.delete();
						}

						out = new FileOutputStream(file, ranges);

						byte[] buffer = new byte[1024];

						int len;

						alreadyDownloaded = ranges ? alreadyDownloaded : 0;

						Integer publishSteps = (int) (fileSize / 100);

						pack.getResponse().progress = (int) ((100d / fileSize) * alreadyDownloaded);
						progressHandler.indirectPublishProgress(pack
								.getResponse());

						while ((len = in.read(buffer)) != -1) {
							out.write(buffer, 0, len);
							alreadyDownloaded += len;

							if (progressHandler.isCancelled()) {
								pack.getResponse().status = DownloadStatus.Aborted;
								return pack.getResponse();
							}

							if (alreadyDownloaded
									% (publishSteps < 1048576 ? 1048576
											: publishSteps) <= 512) {
								out.flush();
								pack.getResponse().progress = (int) ((100d / fileSize) * alreadyDownloaded);
								progressHandler.indirectPublishProgress(pack
										.getResponse());
							}
						}

						pack.getResponse().progress = (int) ((100d / fileSize) * alreadyDownloaded);

						out.flush();
						progressHandler.indirectPublishProgress(pack
								.getResponse());

						out.flush();

						if (alreadyDownloaded != (int) fileSize) {
							if (in != null) {
								in.close();
								in = null;
							}

							if (out != null) {
								out.close();
								out = null;
							}

							try {
								Thread.sleep(1000);
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}

							pack.setResponse(this.downloadFileToSd(pack,
									progressHandler, alreadyDownloaded,
									retries + 1));
						} else {
							if (pack.md5sum != null) {
								byte[] sumBytes = md.digest();

								StringBuffer hexString = new StringBuffer();

								for (int i = 0; i < sumBytes.length; i++) {

									StringBuffer hex = new StringBuffer();

									hex.append(Integer
											.toHexString(0xFF & sumBytes[i]));

									if (hex.length() == 1)
										hexString.append(0);

									hexString.append(hex);
								}

								String sum = hexString.toString();

								if (sum.equals(pack.md5sum))
									pack.getResponse().status = DownloadStatus.Successful;
								else {

									pack.getResponse().status = DownloadStatus.Md5mismatch;
								}
							} else {
								pack.getResponse().status = DownloadStatus.Done;
							}
						}
					} else {
						pack.getResponse().status = DownloadStatus.Broken;
					}
				} catch (Exception e) {
					e.printStackTrace();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					pack.setResponse(this.downloadFileToSd(pack,
							progressHandler, alreadyDownloaded, retries + 1));
				} finally {
					if (in != null)
						try {
							in.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					if (out != null)
						try {
							out.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
				}
			}
			return pack.getResponse();
		}

		return null;
	}

	public static HttpURLConnection getConnection(String urlString,
			ArrayList<NameValuePair> map) throws ClientProtocolException,
			IOException, URISyntaxException {
		HttpURLConnection urlConnection = Communicator
				.getConnectionWithoutConnect(urlString);
		urlConnection.setRequestMethod("POST");
		urlConnection.setDoInput(true);
		urlConnection.setDoOutput(true);
		String query = getQuery(map);
		urlConnection.setFixedLengthStreamingMode(query.getBytes().length);
		urlConnection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		urlConnection.connect();
		OutputStream os = urlConnection.getOutputStream();
		PrintWriter writer = new PrintWriter(
				new OutputStreamWriter(os, "UTF-8"));
		writer.print(query);
		writer.close();
		os.close();

		return urlConnection;
	}

	public static HttpURLConnection getConnection(String urlString,
			JsonElement element) throws IOException {
		HttpURLConnection urlConnection = Communicator
				.getConnectionWithoutConnect(urlString);
		urlConnection.setRequestMethod("POST");
		urlConnection.setDoInput(true);
		urlConnection.setDoOutput(true);
		urlConnection.setRequestProperty("Content-Type", "application/json");
		urlConnection.setRequestProperty("Accept", "application/json");
		urlConnection.connect();
		byte[] params = element.toString().getBytes();
		OutputStream writer = urlConnection.getOutputStream();
		writer.write(params);
		writer.close();

		return urlConnection;
	}

	public static HttpURLConnection getConnection(String urlString)
			throws ClientProtocolException, IOException, URISyntaxException {
		HttpURLConnection urlConnection = Communicator
				.getConnectionWithoutConnect(urlString);

		urlConnection.connect();

		return urlConnection;
	}

	public static HttpURLConnection getConnection(String urlString,
			int alreadyDownloaded) throws ClientProtocolException, IOException,
			URISyntaxException {
		HttpURLConnection urlConnection = Communicator
				.getConnectionWithoutConnect(urlString);

		urlConnection.addRequestProperty("Range", "bytes=" + alreadyDownloaded);

		urlConnection.connect();

		return urlConnection;
	}

	protected static HttpURLConnection getConnectionWithoutConnect(
			String urlString) throws IOException {
		URL url = new URL(urlString);
		HttpURLConnection urlConnection = (HttpURLConnection) url
				.openConnection();
		urlConnection.addRequestProperty(CoreProtocolPNames.USER_AGENT,
				"android");
		urlConnection.addRequestProperty("Cache-Control", "no-cache");

		return urlConnection;
	}

	public static String readStream(InputStream in) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader r = new BufferedReader(new InputStreamReader(in), 1000);

		for (String line = r.readLine(); line != null; line = r.readLine()) {
			sb.append(line).append("\n");
		}

		in.close();

		return sb.toString();
	}

	private static String getQuery(List<NameValuePair> params)
			throws UnsupportedEncodingException {
		StringBuilder result = new StringBuilder();
		boolean first = true;

		for (NameValuePair pair : params) {
			if (first)
				first = false;
			else
				result.append("&");

			result.append(pair.getName());
			result.append("=");
			result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
		}

		return result.toString();
	}

	public static Boolean tryGetAcceptRangesFromConnection(
			HttpURLConnection response) {
		String header = response.getHeaderField("Accept-Ranges");

		if (!PropertyHelper.stringIsNullOrEmpty(header) || header != "none") {
			return true;
		}

		return false;
	}

	public static String tryGetFilenameFromConnection(
			HttpURLConnection connection) {
		String header = connection.getHeaderField("Content-Disposition");

		if (!PropertyHelper.stringIsNullOrEmpty(header) && header.contains(".")) {

			return header.split("filename=")[1].split(";")[0].replace("\"", "")
					.replace("'", "");
		}

		return null;
	}

	public static boolean checkHttpStatus(HttpURLConnection connection)
			throws IOException {
		int status = connection.getResponseCode();

		if (status >= 400) {
			return false;
		}

		return true;
	}
}
