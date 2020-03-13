/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.contenthandlers.html.links;

/**
 * Stores information about links between web documents.
 */
public class Link {
	
	private final String pUri;
	private final String pHost;
	private final boolean pFollowImmediately;
	private final boolean pActualLink;
	
	/**
	 * Initializes a new instance of this class
	 * with the target of and further information about a link.
	 * 
	 * @param uri
	 * the target URI (URL) of the link; not {@code null}
	 * 
	 * @param host
	 * the host component of the link target;
	 * {@code null} if the host component cannot be extracted
	 * 
	 * @param followImmediately
	 * whether the the link should be followed in any case
	 * (see {@link #followImmediately()})
	 * 
	 * @param actualLink
	 * whether the link is actually present in
	 * the document being linked from (see {@link #isActualLink()})
	 */
	public Link(final String uri, final String host, final boolean followImmediately, final boolean actualLink) {
		pUri = uri;
		pHost = host;
		pFollowImmediately = followImmediately;
		pActualLink = actualLink;
	}
	
	/**
	 * Returns the URI (URL) of this link.
	 * 
	 * @return
	 * the URI (URL) of this link
	 */
	public String getUri() {
		return pUri;
	}
	
	/**
	 * Returns the host component of the URI (URL) of this link.
	 * 
	 * @return
	 * the host component of the URI (URL) of this link
	 */
	public String getHost() {
		return pHost;
	}
	
	/**
	 * Returns whether this link should be followed in any case
	 * or only if the document being linked from
	 * is classified as relevant.
	 * <p>
	 * If the link is to be followed in any case,
	 * it can be staged for retrieval (added to the agenda)
	 * immediately, <i>before</i> performing the
	 * (potentially time-consuming) relevance check.
	 * </p>
	 * 
	 * @return
	 * {@code false} if the the link should only be followed
	 * if the document being linked from is classified as relevant;
	 * {@code true} if the link should be followed anyway
	 */
	public boolean followImmediately() {
		return pFollowImmediately;
	}
	
	/**
	 * Used to distinguish between links actually present
	 * in an HTML document and links derived from that document
	 * by some additional logic.
	 * 
	 * @return
	 * {@code true} if the link is actually present in
	 * the document being linked from;
	 * {@code false} otherwise
	 */
	public boolean isActualLink() {
		return pActualLink;
	}
}
