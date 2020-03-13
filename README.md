# hermA-Crawler

A web crawler with integrated linguistic processing for thematic crawling and web document collection developed in the research project [hermA](https://www.herma.uni-hamburg.de/en.html).

Starting from given seed URLs, the crawler follows links in HTML and collects documents containing one or more given keyphrases.
Supported document types are currently HTML (`text/html`), PDF (`application/pdf`, `application/x-pdf`) and plain text (`text/plain`).

# System Requirements

The software has been tested with Windows 10 and Linux.
Being written in Java, it should run on any platform Java supports, given that the required external tools run on that platform, too.

# Installation and Setup

## Installation

You will need a Java runtime to run the software. The web crawler has been developed and tested with Java 8, but newer versions may also work.

In addition to the web crawler itself you have to install some external software it will use.

### Third-Party Software

The following third-party software needs to be properly installed:
* [Python 3](https://www.python.org/) (tested with Python 3.8.1)
  * [the Natural Language Toolkit (NLTK)](https://www.nltk.org/)
    * Punkt Tokenizer models: `nltk.download('punkt')` (similar to the description [here](https://www.nltk.org/data.html))
* MongoDB (tested with [MongoDB Community Server](https://www.mongodb.com/download-center/community) 4.2.3)

### External Tools

You will also have to download the following external tools:
* [MarMoT](http://cistern.cis.lmu.de/marmot/) (tested with the release of October 22, 2015)
  * You will also need a model file.
* [Mate](https://code.google.com/archive/p/mate-tools/downloads) (tested with version 3.61)
  * You will need the `.jar` (for version 3.61: `anna-3.61.jar`) as well as models for the lemmatizer and the parser.
* [XpdfTools](https://www.xpdfreader.com/download.html) (tested with version 4.00)
  * You will only need `pdftotext` and `pdfinfo` (from the ‘Xpdf command line tools’).

### The Web Crawler Itself

There is a release of the web crawler with all required Java dependencies packaged into a single runnable `.jar` file.

Alternatively, there is a separate release without these dependencies. If you prefer managing dependencies yourself (for example because you want to use newer versions) or if you want to compile the code yourself (after modifications, for example), you will have to gather the following Java libraries:
* Apache HTTP components
  * [HTTP Core](https://hc.apache.org/httpcomponents-core-ga/index.html) (tested with `httpcore-4.4.6.jar`)
  * [HTTP Client](https://hc.apache.org/httpcomponents-client-ga/index.html) (tested with `httpclient-4.5.3.jar`)
  * [Apache Commons Logging](https://commons.apache.org/proper/commons-logging/) (tested with `commons-logging-1.2.jar`)
* [Jsoup](https://jsoup.org/) (tested with Jsoup 1.10.3)
* [Boilerpipe](https://code.google.com/archive/p/boilerpipe/) (tested with `boilerpipe-1.2.0.jar`)
  * [CyberNeko HTML Parser](http://nekohtml.sourceforge.net/)
  * [Apache Xerxes](http://xerces.apache.org/xerces2-j/) (tested with `xercesImpl-2.11.0.SP5.jar`)
* [MongoDB synchronous driver](https://mongodb.github.io/mongo-java-driver/) (tested with `mongodb-driver-core-3.10.2.jar`, `mongodb-driver-sync-3.10.2.jar` and `bson-3.10.2.jar`)

## Setting up and Running the Crawler

After you have installed the required software and downloaded the external tools:
1. Put the paths to the external tools in the configuration file, as specified there.
2. Create files with seed URLs, keyphrases to search for, and (if needed) blacklisted hosts and put the file paths in the configuration file.
3. Create an output directory for the crawler and set it in the configuration file, as specified there.
4. Set additional paths and other options in the configuration file, if needed.

You can now start the crawler.

**Note:** The web crawler tries to connect with the MongoDB server at port 27017 of the same machine (`localhost:27017`), which is the default. At the moment, this cannot be changed in the configuration file.

## Stopping the Crawler

To stop the crawler, create a file named `stop` (without any file name extension such as `.txt`) directly in the output directory of the crawler, on Linux for example by running `touch stop` in that directory. (There is a script which does that for you, see below.) The running crawler will immediately delete that file. It will continue processing pages that are currently being processed, but it will not start downloading and processing new pages. When all pages have been processed, the crawler will stop.

You can run the script `stop.sh` (on Linux) or `stop.bat` (on Windows) to stop the crawler. There are two possibilities:
* Call the script with the output directory of the crawler as argument, or
* call the script without arguments, but with the output directory as current directory (in a command-line terminal, after navigating there).

## Resuming

If you start the crawler with a database name (key `db`) of a database that already exists and contains a crawling agenda, the crawler will re-use that agenda, effectively continuing where previous crawling stopped.

# Output

The web crawler saves web pages and meta information to the output directory. The files saved to the `original` directory contain exactly the byte sequences received when requesting the respective page; this means in particular that HTML and text files are saved in their original encoding. All other files saved by the crawler are UTF-8 encoded text files.

In detail, the crawler generates the following files:

`urls.txt`

Contains metadata about the saved web pages. Every line in this file corresponds to a saved web page and consists of fields separated by TAB characters. The fields are:
1. full URL of the web page
2. download date and time in ISO 8601 format
3. Content-Type header (MIME type of the data); for HTML (and plain text) this generally contains the encoding, too
4. file name of the file to which the web page has been saved, within the `original` folder
5. title of the page
	 * HTML: contents of the `<title>` element; if the HTML does not contain any `<title>` element, the file name from the URL (without extension) is used
	 * PDF: PDF title as output by Xpdf `pdfinfo`; if `pdfinfo` does not output any title or the title is empty, the file name from the URL (without extension) is used
	 * TXT: file name from the URL (without extension)

---

`files.txt`

Contains metadata about the text extracted from web pages and the linguistic processing of that text. Every line in this file corresponds to a text extraction result from a saved web page and consists of fields separated by TAB characters. The fields are:
1. content of field 4 in `urls.txt` (file name in the `original` folder) of the web page
2. text extraction method (`HTML5Main`, `Canola`, `XPDF pdftotext` or `plain text`)
3. file name of the corresponding text file in `txt/01_Originale`
4. file name of the corresponding text file in `txt/02_Tokenisierung`
5. file name of the corresponding text file in `txt/03_POS_Lemma`
6. file name of the corresponding text file in `txt/04_Parse`

The last four fields should always be identical.

---

`matches.txt`

Contains metadata about the keyphrases matched in text extracted from web pages. Every line in this file corresponds to a matched keyphrase and consists of fields separated by TAB characters. The fields are:
1. name of the file (in `txt/01_Originale`) with the extracted text in which the keyphrase has been found
2. matched keyphrase as it appears in the text; in the case of multi-word phrases, words are separated by a single space (regardless of how they were separated in the original text)
3. absolute frequency of the keyphrase in the file

The found keyphrases appear in arbitrary order.

---

`errors.txt`

Contains (plain text) messages for errors that occurred during crawling.

---

`resume/hosts.txt`

Contains the number of web pages per host with matching keyphrases. Every line in this file corresponds to a hostname and consists of fields separated by TAB characters. The fields are:
1. host (such as `www.bundesaerztekammer.de`)
2. number of web pages under this hostname with matching keyphrases found until a certain point in time

After crawling has terminated, the ‘certain point in time’ is the termination of the crawl. While the crawler is running, this file is updated in fixed intervals (24 hours in the default settings).

---

files in `meta/links-raw`

Contain all links (and redirections) encountered by the web crawler. The file `links-raw.txt` is the most recent one. Approximately every 10000 lines, a new `links-raw.txt` is started and the old one is renamed by appending the current date and time (for example, `links-raw.txt-20200102030405`). Every line in this file corresponds to one link (or redirection) and consists of fields separated by TAB characters. The fields are:
1. link source URL
2. link target URL
3. type of link (`0` for a normal link, `1` for a redirection)
4. link depth (distance of the link target from the seed URLs)

---

files in `meta/processedurls`

Contain all URLs processed by the crawler, that is, for which the crawler has decided whether they contain keyphrases or not. The file `processedurls.txt` is the most recent one. Approximately every 10000 lines, a new `processedurls.txt` is started and the old one is renamed by appending the current date and time (for example, `processedurls.txt-20200102030405`). Every line in this file is one URL.

---

files in `meta/prtne`

The abbreviation ‘prtne’ is supposed to mean ‘potentially relevant, text not extractable’ and these files contain the URLs of web page whose MIME type the crawler cannot (yet) deal with although the contents might be relevant (such as `application/msword`). The file `prtne.txt` is the most recent one. Approximately every 10000 lines, a new `prtne.txt` is started and the old one is renamed by appending the current date (for example, `prtne.txt-20200102030405`). Every line in this file corresponds to one web page and consists of fields separated by TAB characters. The fields are:
1. URL
2. `Content-Type` header

---

files in `meta/unfollowedschemata`

Contain URI schemata which the crawler has encountered but cannot follow (such as `mailto`). The file `unfollowedschemata.txt` is the most recent one. Approximately every 10000 lines, a new `unfollowedschemata.txt` is started and the old one is renamed by appending the current date and time (for example, `unfollowedschemata.txt-20200102030405`). Every line in this file is a URI schema (without the colon).

---

files in `meta/unhandledmime`

Contain MIME types which the crawler has encountered but cannot deal with (such as `application/json`), excluding prtne (see above) MIME types (such as `application/msword`) and MIME types configured to be ignored (such as `image/jpeg`). The file `unhandledmime.txt` is the most recent one. Approximately every 10000 lines, a new `unhandledmime.txt` is started and the old one is renamed by appending the current date and time (for example, `unhandledmime.txt-20200102030405`). Every line in this file is a MIME type.

---

As stated above, files in the `original` folder contain the exact byte sequences of the saved web pages. The subdirectories of `txt` contain corresponding results from linguistic processing:

`01_Originale`

Texts originally extracted from the web pages, with line breaks etc. as returned by the text extraction.

`02_Tokenisierung`

The results of tokenizing the extracted texts, that is, one token (word, punctuation character or other symbol) per line and blank lines as sentence boundaries.

`03_POS_Lemma`

The results of applying POS and morphological tagging as well as lemmatization to the tokenized texts, in CoNLL-2009 format.

`04_Parse`

The results of applying dependency parsing to the tagged and lemmatized texts, in CoNLL-2009 format.

`03a_ParserInput`

Normally, the files in `03_POS_Lemma` are input to the parser. However, if a text contains very long sentences (default settings: more than 430 tokens), the long sentences are removed before parsing. If sentences are removed, the parser input file without the long sentences are stored in this directory.
