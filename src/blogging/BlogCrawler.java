package blogging;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class BlogCrawler {
	private static final int N = 528;
	
	private static WebClient webClient = null;
	
	private static HtmlPage page = null;
	
	private static void init() {
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
		java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);

		webClient = new WebClient(BrowserVersion.INTERNET_EXPLORER_8);
		webClient.setAjaxController(new NicelyResynchronizingAjaxController());
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		webClient.getOptions().setJavaScriptEnabled(true);
		webClient.getOptions().setRedirectEnabled(true);
		webClient.getCookieManager().setCookiesEnabled(true);
		webClient.getOptions().setCssEnabled(true);
		webClient.setCssErrorHandler(new SilentCssErrorHandler());
		
		webClient.waitForBackgroundJavaScript(1000);
	}
	
	private static void getArticle() {
		
		try {
			for (int pageNum = 1; pageNum <= N; pageNum++) {
				if (Mysql.get_article_id(pageNum))
					continue;
				
				page = webClient.getPage("http://woongheelee.com/?page="+pageNum);

				String pageString = page.asText().toString(); 
				String title = pageString.split("방명록")[1].trim().split("\n")[0];
				String text = getText(pageString.split("방명록")[1].trim().split("카카오스토리")[0]);

				Mysql.set_article(pageNum, title, text);
			}
		} catch (FailingHttpStatusCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private static String getText(String post) {
		StringBuffer str = new StringBuffer();
		String[] arr = post.split("\n");
		
		for (int i = 0; i < arr.length; i++) {
			if (arr[i].trim().length() > 1) 
				str.append(arr[i].trim()+"\n");
		}
		
		return str.toString();
	}
	
	private static void closePage() {
		webClient.close();
	}
	
	public static void main(String[] args) {
		Mysql.init_conn();
		
		init();
		getArticle();
		closePage();
	}
}
