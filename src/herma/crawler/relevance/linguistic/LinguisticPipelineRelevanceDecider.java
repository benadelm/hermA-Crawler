/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.relevance.linguistic;

import java.util.ArrayList;

import herma.crawler.ErrorHandler;
import herma.crawler.FurtherProcessing;
import herma.crawler.FurtherProcessingItem;
import herma.crawler.WebDocumentProvider;
import herma.crawler.linguisticprocessing.PreParsingPipeline;
import herma.crawler.linguisticprocessing.PreParsingResult;
import herma.crawler.relevance.RelevanceDecider;
import herma.crawler.relevance.RelevanceDecision;
import herma.crawler.textextraction.ExtractedText;
import herma.crawler.util.CleanupUtil;


public class LinguisticPipelineRelevanceDecider implements RelevanceDecider {
	
	private final ErrorHandler pErrorHandler;
	private final PreParsingPipeline pPreParsingPipeline;
	private final AfterPreprocessingRelevanceDecider pAfterPreprocessingRelevanceDecider;
	private final FurtherProcessing pFurtherProcessing;
	
	public LinguisticPipelineRelevanceDecider(final ErrorHandler errorHandler, final PreParsingPipeline preParsingPipeline, final AfterPreprocessingRelevanceDecider afterPreprocessingRelevanceDecider, final FurtherProcessing furtherProcessing) {
		pErrorHandler = errorHandler;
		pPreParsingPipeline = preParsingPipeline;
		pAfterPreprocessingRelevanceDecider = afterPreprocessingRelevanceDecider;
		pFurtherProcessing = furtherProcessing;
	}
	
	@Override
	public boolean isRelevant(final WebDocumentProvider webDocument) {
		final ExtractedText[] extractedTexts = webDocument.extractTexts();
		if (extractedTexts == null) {
			pErrorHandler.handleError(webDocument.getUrl(), "Document text extraction was not possible.");
			return false;
		}
		final PreParsingResult[] preParsingResults;
		final RelevanceDecision[] relevanceDecisions;
		final boolean relevant;
		try {
			try {
				preParsingResults = new PreParsingResult[extractedTexts.length];
				relevanceDecisions = new RelevanceDecision[extractedTexts.length];
			} catch (final Exception e) {
				deleteExtractedTextTempFiles(extractedTexts);
				throw e;
			}
			relevant = isRelevant(extractedTexts, preParsingResults, relevanceDecisions);
		} finally {
			for (final ExtractedText extractedText : extractedTexts)
				cleanupExtractedText(extractedText);
		}
		if (relevant) {
			try {
				final ArrayList<FurtherProcessingItem> furtherProcessingItems = new ArrayList<>(extractedTexts.length);
				for (int i = 0; i < extractedTexts.length; i++)
					furtherProcessingItems.add(new FurtherProcessingItem(preParsingResults[i], relevanceDecisions[i]));
				pFurtherProcessing.startFurtherProcessing(webDocument.createOriginalInfo(), furtherProcessingItems);
				return true; // PreParsingResults must NOT (yet) be disposed (therefore, try-finally would not work)
			} catch (final Exception e) {
				discardPreParsingResults(preParsingResults);
				throw e;
			}
		}
		discardPreParsingResults(preParsingResults);
		return false;
	}
	
	private boolean isRelevant(final ExtractedText[] extractedTexts, final PreParsingResult[] preParsingResults, final RelevanceDecision[] relevanceDecisions) {
		boolean relevant;
		int i;
		try {
			relevant = false;
			i = 0;
		} catch (final Exception e) {
			deleteExtractedTextTempFiles(extractedTexts);
			throw e;
		}
		try {
			while (i < extractedTexts.length) {
				final PreParsingResult preParsingResult = pPreParsingPipeline.apply(extractedTexts[i]);
				try {
					preParsingResults[i] = preParsingResult;
					final RelevanceDecision relevanceDecision = pAfterPreprocessingRelevanceDecider.isRelevant(preParsingResult);
					relevanceDecisions[i] = relevanceDecision;
					relevant = relevant || relevanceDecision.isRelevant();
				} catch (final Exception e) {
					disposePreParsingResult(preParsingResult);
					throw e;
				}
				i++;
			}
			return relevant;
		} catch (final Exception e) {
			for (int j = 0; j < i; j++)
				disposePreParsingResult(preParsingResults[j]);
			for (int j = i; j < extractedTexts.length; j++)
				deleteExtractedTextTempFiles(extractedTexts[j]);
			throw e;
		}
	}
	
	private void deleteExtractedTextTempFiles(final ExtractedText[] extractedTexts) {
		for (final ExtractedText extractedText : extractedTexts)
			deleteExtractedTextTempFiles(extractedText);
	}
	
	private void discardPreParsingResults(final PreParsingResult[] preParsingResults) {
		for (final PreParsingResult preParsingResult : preParsingResults)
			disposePreParsingResult(preParsingResult);
	}
	
	private void deleteExtractedTextTempFiles(final ExtractedText extractedText) {
		CleanupUtil.nothrowCleanup(extractedText::removeTempFiles, "removing temporary files created by text extraction");
	}
	
	private void cleanupExtractedText(final ExtractedText extractedText) {
		CleanupUtil.nothrowCleanup(extractedText::cleanup, "cleaning up text extraction results");
	}
	
	private void disposePreParsingResult(final PreParsingResult preParsingResult) {
		CleanupUtil.nothrowCleanup(preParsingResult::dispose, "disposing a pre-parsing pipeline output (relevance decider)");
	}
	
}
