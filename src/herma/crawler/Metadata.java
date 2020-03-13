/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler;

import java.time.Instant;

/**
 * Stores meta-data of a retrieved web document.
 */
public class Metadata {
	
	private final String pUrl;
	private final String pHost;
	private final Instant pDownloadTime;
	private final String pMime;
	private final String pTitle;
	
	/**
	 * Initializes a new meta-data instance for a web document.
	 * <p>
	 * None of the parameters of this constructor
	 * is expected to be {@code null} (but this is not checked).
	 * </p>
	 * 
	 * @param url
	 * the URL the document has been retrieved from
	 * 
	 * @param host
	 * the host portion of the URL
	 * the document has been retrieved from
	 * 
	 * @param downloadTime
	 * date and time of the retrieval of the document
	 * 
	 * @param mime
	 * the MIME type of the document
	 * according to the HTTP response header
	 * 
	 * @param title
	 * the title of the document
	 * as extracted by the respective content handler
	 */
	public Metadata(final String url, final String host, final Instant downloadTime, final String mime, final String title) {
		pUrl = url;
		pHost = host;
		pDownloadTime = downloadTime;
		pMime = mime;
		pTitle = title;
	}
	
	/**
	 * Returns the URL the document has been retrieved from.
	 * 
	 * @return
	 * the URL the document has been retrieved from,
	 * as a {@link String}
	 */
	public String getDownloadUrl() {
		return pUrl;
	}
	
	/**
	 * Returns the host portion of the URL
	 * the document has been retrieved from.
	 * 
	 * @return
	 * the host portion of the URL
	 * the document has been retrieved from,
	 * as a {@link String}
	 */
	public String getHost() {
		return pHost;
	}
	
	/**
	 * Returns date and time of the retrieval of the document.
	 * 
	 * @return
	 * date and time of the retrieval of the document,
	 * as an {@link Instant}
	 */
	public Instant getDownloadTime() {
		return pDownloadTime;
	}
	
	/**
	 * Returns the MIME type of the document
	 * (according to the HTTP response header),
	 * such as {@code text/html;charset=utf-8}.
	 * 
	 * @return
	 * the MIME type of the document,
	 * as a {@link String}
	 */
	public String getMime() {
		return pMime;
	}
	
	/**
	 * Returns the title of the document
	 * as extracted by the respective content handler.
	 * 
	 * @return
	 * the title of the document ({@link String})
	 * as extracted by the respective content handler
	 */
	public String getTitle() {
		return pTitle;
	}
	
}
