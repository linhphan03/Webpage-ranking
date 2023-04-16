package Pages;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Page {
	int numLink = 0;
	String pageURL;
	//to store unique page
	Set<Page> pagesLinked;
	Pattern pattern;
	private static final String regex = "\\b((?:https?|ftp|file):"
							            + "//[-a-zA-Z0-9+&@#/%?="
							            + "~_|!:, .;]*[-a-zA-Z0-9+"
							            + "&@#/%=~_|])";
	public static int numPage = 0;
	
	public Page(String pageURL) {
		this.pageURL = pageURL;
		this.pagesLinked = new HashSet<>();
		pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
	}
	
	public void extractLinks() {
		try {
			//jsoup library: extract data from HTML
			URI uri = new URI(this.pageURL);
			Document doc = Jsoup.connect(uri.toString()).get();
			//Reference: https://stackoverflow.com/questions/28660603/how-to-detect-url-to-different-page-also-in-the-same-domain
			//Elements: extract a list of Element 
			Elements htmlLinks = doc.select("a[href~=^[^#]+$]");
			
			for (Element eachHTML : htmlLinks) {
				//get absolute URL only to prevent duplicates
				String link = eachHTML.attr("href");
				//search for pattern of 'pattern' in string
				Matcher matcher = pattern.matcher(link);	
				
				while(matcher.find()) { //.find(): return true if the pattern was found
					Page linkedPage = new Page(link.substring(matcher.start(0), matcher.end(0)));
					if (!isLinkedPageExisted(linkedPage)) {
						numLink++;
						pagesLinked.add(linkedPage);
					}
				}
			}
		}	
		catch (MalformedURLException ex) {
			System.out.println("Invalid URL");
		} 
		catch (IOException ioe) {
			System.out.println("I/O errors: No such file!");
		} catch (URISyntaxException uriE) {
			// TODO Auto-generated catch block
			uriE.printStackTrace();
		}
	}
	
	public boolean isLinkedPageExisted(Page next) throws MalformedURLException {
		URL nextURL = new URL(next.pageURL);
		
		for (Page p : this.pagesLinked) {
			if (nextURL.sameFile(new URL(p.pageURL))) {
				return true;
			}
		}
		return false;
	}
	
	public int getNumLink() {
		return numLink;
	}
	
	public String getPageURL() {
		return pageURL;
	}

	public Set<Page> getPagesLinked() {
		return pagesLinked;
	}

	public static int getNumPage() {
		return numPage;
	}

	public Pattern getPattern() {
		return pattern;
	}

	public static String getRegex() {
		return regex;
	}
	
	public String toString() {
		return pageURL;
	}
}
