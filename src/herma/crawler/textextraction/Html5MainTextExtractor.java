/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.textextraction;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;


public class Html5MainTextExtractor implements HtmlTextExtractor {

	private static final Pattern ONLY_WHITESPACE = Pattern.compile("\\s*");
	
	private static final String[] BLOCK_ELEMENTS = new String[] {
			"header", "nav", "section", "article", "aside", "footer",
			"p", "div",
			"h1", "h2", "h3", "h4", "h5", "h6",
			"ol", "ul", "li",
			"pre", "address", "blockquote", "dl",
			"form", "fieldset",
			"hr", "noscript",
			"table"
		};
	
	private static final HashSet<String> BLOCK_ELEMENTS_SET;
	
	static {
		BLOCK_ELEMENTS_SET = new HashSet<>();
		for (final String blockElement : BLOCK_ELEMENTS)
			BLOCK_ELEMENTS_SET.add(blockElement);
	}
	
	@Override
	public ExtractedText[] extractText(final Document htmlDocument) {
		final Elements mains = htmlDocument.select("main");
		if (mains.size() == 0)
			return null;
		final ArrayList<String> result = new ArrayList<>();
		collectTextBlocks(mains, result);
		if (result.size() == 0)
			return null;
		return new ExtractedText[] {
				new InMemoryExtractedText(TextExtractionMethodInfo.HTML5MAIN, result.toArray(new String[result.size()]))
			};
	}
	
	private static void collectTextBlocks(final Elements mainElements, final ArrayList<String> result) {
		final ArrayDeque<Node> stack = new ArrayDeque<>();
		pushReversed(mainElements, stack);
		final StringBuilder currentBlock = new StringBuilder();
		while (stack.size() > 0) {
			final Node node = stack.pop();
			if (node instanceof TextNode) {
				currentBlock.append(((TextNode) node).text());
				continue;
			}
			if ((currentBlock.length() > 0) && (node instanceof Element) && isBlockElement((Element) node)) {
				if (!ONLY_WHITESPACE.matcher(currentBlock).matches())
					result.add(currentBlock.toString());
				currentBlock.setLength(0);
			}
			pushReversed(node.childNodes(), stack);
		}
		if (currentBlock.length() > 0)
			result.add(currentBlock.toString());
	}
	
	private static <T> void pushReversed(final List<? extends T> list, final ArrayDeque<? super T> stack) {
		final ListIterator<? extends T> it = list.listIterator(list.size());
		while (it.hasPrevious())
			stack.push(it.previous());
	}
	
	private static boolean isBlockElement(final Element element) {
		return BLOCK_ELEMENTS_SET.contains(element.tagName().toLowerCase(Locale.ROOT));
	}
	
}
