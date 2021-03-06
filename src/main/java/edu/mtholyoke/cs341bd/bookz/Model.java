package edu.mtholyoke.cs341bd.bookz;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Model 
{
	Map<String, GutenbergBook> library;

	Map<String, GutenbergBook> bookTitles;
	Map<String, GutenbergBook> authors;
	List<GutenbergBook> searchList;

	List<GutenbergBook> review; 


	public Model() throws IOException {
		// start with an empty hash-map; tell it it's going to be big in advance:
		library = new HashMap<>(40000);

		authors = new HashMap<>(4000);

		review = new ArrayList<>(); 

		// do the hard work:
		DataImport.loadJSONBooks(library);
		searchList = new ArrayList<>();
	}

	public GutenbergBook getBook(String id) {
		return library.get(id);
	}
	
	public List<GutenbergBook> getBooksStartingWith(char firstChar) {
		// TODO, maybe it makes sense to not compute these every time.
		char query = Character.toUpperCase(firstChar);
		List<GutenbergBook> matches = new ArrayList<>(10000); // big
		for (GutenbergBook book : library.values()) 
		{
			char first = Character.toUpperCase(book.title.charAt(0));
			if(first == query) {
				matches.add(book);
			}
		}
		
		return matches;
	}

	public List<GutenbergBook> getBooksStartingWithTitles(String myTitle) 
	{
		// TODO, maybe it makes sense to not compute these every time.
		//char query = Character.toUpperCase(title);
		List<GutenbergBook> matches = new ArrayList<>(10000);// big
		
		for (GutenbergBook book : library.values()) {
			
			String first = book.title.toLowerCase();
			if(first.contains(myTitle.toLowerCase())) 
			{	
				matches.add(book);
			}
		}
			return matches;
	}
	
	public List<GutenbergBook> getList()
	{
		return searchList;
	}
	
	public void addtoSearchList(String str)
	{
		searchList.add(library.get(str));
		System.out.println(searchList);
	}
	
	public List<GutenbergBook> getBooksStartingWithAuthor(String author)
	{
		List<GutenbergBook> matches = new ArrayList<>(20); // big
		for (GutenbergBook book : library.values()) {
			String first;
					
				if (book.creator != null)
				{	
					first = book.creator.toLowerCase();
					if(first.contains(author.toLowerCase())) 
					{	
						matches.add(book);
					}
				}
			
		}
			return matches;
		
	}
	

	public List<GutenbergBook> getRandomBooks(int count) {
		return ReservoirSampler.take(count, library.values());
	}
	
	public void addToReview(String str) {
		review.add(library.get(str));
		System.out.println(review);
	}
	
	public List<GutenbergBook> getReview() {
		return review; 
	}
}
