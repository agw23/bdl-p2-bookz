package edu.mtholyoke.cs341bd.bookz;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * @author jfoley
 */
public class BookzServer extends AbstractHandler {
	Server jettyServer;
	HTMLView view;
	Model model;

	public BookzServer(String baseURL, int port) throws IOException {
		view = new HTMLView(baseURL);
		jettyServer = new Server(port);
		model = new Model();

		// We create a ContextHandler, since it will catch requests for us under
		// a specific path.
		// This is so that we can delegate to Jetty's default ResourceHandler to
		// serve static files, e.g. CSS & images.
		ContextHandler staticCtx = new ContextHandler();
		staticCtx.setContextPath("/static");
		ResourceHandler resources = new ResourceHandler();
		resources.setBaseResource(Resource.newResource("static/"));
		staticCtx.setHandler(resources);

		// This context handler just points to the "handle" method of this
		// class.
		ContextHandler defaultCtx = new ContextHandler();
		defaultCtx.setContextPath("/");
		defaultCtx.setHandler(this);

		// Tell Jetty to use these handlers in the following order:
		ContextHandlerCollection collection = new ContextHandlerCollection();
		collection.addHandler(staticCtx);
		collection.addHandler(defaultCtx);
		jettyServer.setHandler(collection);
	}

	/**
	 * Once everything is set up in the constructor, actually start the server
	 * here:
	 * 
	 * @throws Exception
	 *             if something goes wrong.
	 */
	public void run() throws Exception {
		jettyServer.start();
		jettyServer.join(); // wait for it to finish here! We're using threads behind the scenes; so this keeps the main thread around until something can happen!
	}

	/**
	 * The main callback from Jetty.
	 * 
	 * @param resource
	 *            what is the user asking for from the server?
	 * @param jettyReq
	 *            the same object as the next argument, req, just cast to a
	 *            jetty-specific class (we don't need it).
	 * @param req
	 *            http request object -- has information from the user.
	 * @param resp
	 *            http response object -- where we respond to the user.
	 * @throws IOException
	 *             -- If the user hangs up on us while we're writing back or
	 *             gave us a half-request.
	 * @throws ServletException
	 *             -- If we ask for something that's not there, this might
	 *             happen.
	 */
	@Override
	public void handle(String resource, Request jettyReq, HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException 
	{
		System.out.println(jettyReq);

		String method = req.getMethod();
		String path = req.getPathInfo();

				if ("POST".equals(method)&&"/review".equals(path)) 
				{
					//System.out.println(req.getParameter("book")); 
					model.addToReview(req.getParameter("book"));
					try (PrintWriter txt = resp.getWriter()) {
						view.printReviewPage(model.getReview(), txt); 
					}
				}
		

		if("POST".equals(method) && "/search".equals(path))
		{		
			showResultsPage(req, resp);	
		}

		if ("GET".equals(method)) 
		{
			if("/robots.txt".equals(path)) 
			{
				// We're returning a fake file? Here's why: http://www.robotstxt.org/
				resp.setContentType("text/plain");
				try (PrintWriter txt = resp.getWriter()) 
				{
					txt.println("User-Agent: *");
					txt.println("Disallow: /");
				}
			}
			

			String titleCmd = Util.getAfterIfStartsWith("/title/", path);
			if(titleCmd != null) 
			{
				
				char firstChar = titleCmd.charAt(0);
				view.showBookCollection(this.model.getBooksStartingWith(firstChar), resp);
			}


			// Check for startsWith and substring
			String bookId = Util.getAfterIfStartsWith("/book/", path);
			if(bookId != null) 
			{
				view.showBookPage(this.model.getBook(bookId), resp);
			}

			// Front page!
			if ("/front".equals(path) || "/".equals(path)) 
			{
				view.showFrontPage(this.model, resp);
				return;
			}

		}
	}


	private void showResultsPage(HttpServletRequest req, HttpServletResponse resp) throws IOException 
	{
		Map<String, String[]> parameterMap = req.getParameterMap();
		// if for some reason, we have multiple "message" fields in our form, just put a space between them, see Util.join.
		// Note that message comes from the name="message" parameter in our <input> elements on our form.
		String message = Util.join(parameterMap.get("message"));		    
		String author = Util.join(parameterMap.get("author"));
		
		System.out.println(author);
		System.out.println(message);
		if(!message.equals(""))
		{
			// Good, got new message from form.
			resp.setStatus(HttpServletResponse.SC_ACCEPTED); 
			view.printHTMLResultsPage(model.getBooksStartingWithTitles(message), resp);
			//view.printHTMLResultsPage(model.getBooksStartingWithAuthor(author), resp);
			return;
		}

		else if ( !author.equals(""))
		{
			view.printHTMLResultsPage(model.getBooksStartingWithAuthor(author), resp);
			return;
		}
		// user submitted something weird.
		resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad user.");
	}
}
