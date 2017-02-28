package edu.mtholyoke.cs341bd.bookz;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

public class HTMLView {

	private String metaURL;

	public HTMLView(String baseURL) {
		this.metaURL = "<base href=\"" + baseURL + "\">";
	}

	/**
	 * HTML top boilerplate; put in a function so that I can use it for all the
	 * pages I come up with.
	 * 
	 * @param html
	 *            where to write to; get this from the HTTP response.
	 * @param title
	 *            the title of the page, since that goes in the header.
	 */
	void printPageStart(PrintWriter html, String title) {
		html.println("<!DOCTYPE html>"); // HTML5
		html.println("<html>");
		html.println("  <head>");
		html.println("    <title>" + title + "</title>");
		html.println("    " + metaURL);
		html.println("    <link type=\"text/css\" rel=\"stylesheet\" href=\"" + getStaticURL("bookz.css") + "\">");
		html.println("  </head>");
		html.println("  <body>");
		html.println("  <a href='/front'><h1 class=\"logo\">"+title+"</h1></a>");
	}

	public String getStaticURL(String resource) {
		return "static/" + resource;
	}

	/**
	 * HTML bottom boilerplate; close all the tags we open in
	 * printPageStart.
	 *
	 * @param html
	 *            where to write to; get this from the HTTP response.
	 */
	void printPageEnd(PrintWriter html) {
		html.println("  </body>");
		html.println("</html>");
	}

	void showFrontPage(Model model, HttpServletResponse resp) throws IOException {
		try (PrintWriter html = resp.getWriter()) {
			printPageStart(html, "Bookz");
			makeResultForm(html);

			html.println("<h3>Browse books by title</h3>");

			for(char letter = 'A'; letter <= 'Z'; letter++) {
				html.println("<a href='/title/"+letter+"' style=\"color:#fff\">"+letter+"</a> ");
			}

			// get 5 random books:
			html.println("<h3>Check out these random books</h3>");
			List<GutenbergBook> randomBooks = model.getRandomBooks(5);
			for (GutenbergBook randomBook : randomBooks) {
				printBookHTML(html, randomBook);
			}
			printPageEnd(html);
		}
	}
	
	public void makeResultForm(PrintWriter html)  
	{		
			//<!-- forms can do POST or GET or even others like PUT as you write inside method -->
			html.println(" <div class= \"form\">");
				html.println(" <form action=\"/search\" method= \"GET\"> ");
				html.println(" <div><input type=\"text\" size=40 name=\"message\" id=\"message\" placeholder=\"Search by Title\"/>");
				//html.println(" <input type = \"submit\" value=\"Search for author/title!\" />");
				html.println(" <div><input type=\"text\" size=40 name=\"author\" id=\"author\" placeholder=\"Search by Author\" />");
				html.println("  <div id =\"button1\"><input type = \"submit\" value=\"Search\" />");
				html.println( "</form>" );
				html.println(" </div>");
				
	}

	void printSearchStart(PrintWriter html, String title) 
	{
		html.println("<!DOCTYPE html>"); // HTML5
		html.println("<html>");
		html.println("  <head>");
		html.println("    <title>" + title + "</title>");
		html.println("    " + metaURL);
		html.println("    <link type=\"text/css\" rel=\"stylesheet\" href=\"" + getStaticURL("bookz.css") + "\">");
		html.println("  </head>");
		html.println("  <body>");
		html.println("  <a href='/front'><h1 class=\"logo\">"+title+"</h1></a>");
		
		html.println("<h2>This is what we found!</h2>");
	}
	 
	
	public void printHTMLResultsPage(List<GutenbergBook> myList, HttpServletResponse resp) throws IOException 
	{	
		try (PrintWriter html = resp.getWriter()) 
		{
			printSearchStart(html, "Bookz");
			makeResultForm(html);
			
			for (GutenbergBook myBooks: myList)
			{
				printBookHTML(html, myBooks);
			}
			
			printPageEnd(html);
		}
	}
		
	private void printBookHTML(PrintWriter html, GutenbergBook book) {
		 
		html.println("<div class='book'>");
		html.println("<a class='none' href='/book/"+book.id+"'>");
		html.println("<div class='title'>"+book.title+"</div>");
		if(book.creator != null) 
		{
			html.println("<div class='creator'>" + book.creator + "</div>");
		}
		html.println("<a href='"+book.getGutenbergURL()+"'>On Project Gutenberg</a>");
		html.println("<form action=\"/review\" method=\"POST\">");
		html.println("<input type=\"hidden\" name=\"book\" value="+book.id+">"); 
		html.println("<input type=\"submit\" value=\"Flag Entry!\" />"); 
		html.println("</form>"); 
		html.println("</div>");
		
	}

	public void showPageNumbers(List<GutenbergBook> theBooks, HttpServletResponse resp, HashMap<String, String> parameters, String url, int listSize) throws IOException {
		try (PrintWriter html = resp.getWriter()) {
			for(int i = 1; i <= theBooks.size()/10; i++) {
				html.println("<a href='"+
			          Util.encodeParametersInURL(parameters, url)+"&page="+i+"' style=\"color:#fff\">"+i+"</a>");
			}
		}
	}
	
	public void showBookPage(GutenbergBook book, HttpServletResponse resp) throws IOException {
		try (PrintWriter html = resp.getWriter()) {
			printPageStart(html, "Bookz");
			printBookHTML(html, book);
			
			printPageEnd(html);
		}
	}

	
	public void showBookCollection(List<GutenbergBook> theBooks, HttpServletResponse resp, HashMap<String, String> parameters, String url, int listSize) throws IOException {
		try (PrintWriter html = resp.getWriter()) 
		{
			printPageStart(html, "Bookz");
			
			if (theBooks.size() < 10) {
				for (int i = 0; i < theBooks.size(); i++) 
					printBookHTML(html, theBooks.get(i));
			}
			else {
				for (int i = listSize*10-10; i < listSize*10; i++) {
					printBookHTML(html, theBooks.get(i));
					}
			}
			showPageNumbers(theBooks, resp, parameters, url, listSize);
			printPageEnd(html);
		}
	}
	
	void printReviewStart(PrintWriter html, String title) {
		html.println("<!DOCTYPE html>"); // HTML5
		html.println("<html>");
		html.println("  <head>");
		html.println("    <title>" + title + "</title>");
		html.println("    " + metaURL);
		html.println("    <link type=\"text/css\" rel=\"stylesheet\" href=\"" + getStaticURL("bookz.css") + "\">");
		html.println("  </head>");
		html.println("  <body>");
		 
		html.println("  <a href='/front'><h1 class=\"logo\">"+title+"</h1></a>");
		html.println("<h2> Flagged books </h2>");
}

private void printReviewBookHTML(PrintWriter html, GutenbergBook book) {
	 
	html.println("<div class='book'>");
	html.println("<a class='none' href='/book/"+book.id+"'>");
	html.println("<div class='title'>"+book.title+"</div>");
	if(book.creator != null) {
		html.println("<div class='creator'>" + book.creator + "</div>");
	}
	html.println("<a href='"+book.getGutenbergURL()+"'>On Project Gutenberg</a>");
	
	// TODO, finish up fields.
	html.println("</a>");
	html.println("</div>");
}

public void printReviewPage(List<GutenbergBook> flaggedBooks, PrintWriter txt) {
	printReviewStart(txt, "Bookz"); 
	
	for (GutenbergBook book : flaggedBooks) {
		printReviewBookHTML(txt, book);
	}
	printPageEnd(txt);
	
}

}
