import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * This is a simple web crawler. It: 1) fetches the content for a given start
 * URL 2) extracts the links from the content. 3) goes on to crawl the extracted
 * links (back to step 1) The crawler stops after 1000 found URLs
 */
public class WebCrawler {
	/**
	 * Attributes:
	 */
	public static int num_found;
	public static int limit;
	public static String urlweb;
	public static TreeSet<String> result = new TreeSet<String>();

	/**
	 * Methods:
	 */

	/**
	 * This method returns a string with all the links of a website (given by
	 * url) separated by the \n character:
	 */
	public static TreeSet<String> getLinksInPage(String url) throws IOException {

		int start_link, start_quote, end_quote, ch;
		String webcontents = "";
		TreeSet<String> links = new TreeSet<String>();
		URL website = null;
		URLConnection yc;
		Reader r;
		InputStream is;
		StringBuilder buf;

		website = new URL(url);
		yc = website.openConnection();
		try {
			is = yc.getInputStream();
			r = new InputStreamReader(is, "UTF-8");
			buf = new StringBuilder();
			while (true) {
				ch = r.read();
				if (ch < 0)
					break;
				buf.append((char) ch);
			}
			webcontents = buf.toString();

			// now webcontents is a large string with the web contents
			if (!webcontents.isEmpty()) {
				while (true) {
					start_link = webcontents.indexOf("<a href=", 0);
					if (start_link != -1) {
						start_quote = webcontents.indexOf("\"", start_link);
						end_quote = webcontents.indexOf("\"", start_quote + 1);
						links.add(webcontents.substring(start_quote + 1,
								end_quote));
						webcontents = webcontents.substring(end_quote + 1,
								webcontents.length() - 1);
					} else
						break;
				}
			}

		} catch (Exception e) {
			// e.printStackTrace();
		}
		return links;
	}

	/**
	 * This method checks if a given url exists by establishing a connection:
	 */
	public static boolean linkIsValid(String string) throws IOException {
		URL website = null;
		HttpURLConnection huc;
		int response;
		boolean value = false;

		try {
			website = new URL(string);
			try {
				huc = (HttpURLConnection) website.openConnection();
				huc.setRequestMethod("HEAD");
				try {
					response = huc.getResponseCode();
					if (response == HttpURLConnection.HTTP_OK) {
						value = true;
					}
				} catch (Exception e) {
					// e.printStackTrace();
					value = false;
				}
			} catch (ClassCastException e) {
				// e.printStackTrace();
				value = false;
			}
		} catch (MalformedURLException e) {
			// e.printStackTrace();
			value = false;
		}
		return value;
	}

	/**
	 * This recursive method saves the links in a set:
	 */
	public static void getLinkList(TreeSet<String> links) throws IOException {
		String value;
		TreeSet<String> list = new TreeSet<String>();
		Iterator<String> it;

		it = links.iterator();
		while (it.hasNext() && num_found < limit) {
			value = it.next();
			if (linkIsValid(value) && !value.equals(urlweb)) {
				if (result.add(value)) {
					num_found++;
					list = getLinksInPage(value);
					getLinkList(list);
				}
			}
		}

	}

	/**
	 * This method sets the limit of links to retrieve:
	 */
	public static void setLimit(int value) {
		limit = value;
	}

	/**
	 * This method sets the source website (starting link):
	 */
	public static void setUrl(String link) {
		urlweb = link;
	}

	/**
	 * Execution
	 */
	public static void main(String args[]) throws IOException {
		Iterator<String> it;
		TreeSet<String> lines = new TreeSet<String>();

		setUrl("http://www.yahoo.com");
		setLimit(40);
		num_found = 0;

		lines = getLinksInPage(urlweb);

		getLinkList(lines);

		it = result.iterator();
		while (it.hasNext())
			System.out.println((String) it.next());

	}

}
