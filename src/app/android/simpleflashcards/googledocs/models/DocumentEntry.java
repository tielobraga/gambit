package app.android.simpleflashcards.googledocs.models;


import java.util.Date;
import java.util.List;

import app.android.simpleflashcards.InternetDateTimeFormatter;
import app.android.simpleflashcards.googledocs.DocumentsListUrl;
import app.android.simpleflashcards.googledocs.GoogleDocsException;
import app.android.simpleflashcards.googledocs.KeyUrl;

import com.google.api.client.util.Key;


public class DocumentEntry
{
	private static final String ALTERNATE_SCHEME = "alternate";
	private static final String EDIT_SCHEME = "edit";

	private static final String CATEGORY_KIND = "http://schemas.google.com/g/2005#kind";

	public static enum Type {
		DOCUMENT, SPREADSHEET, PRESENTATION, DRAWING;

		private static final String SCHEME_DOCUMENT = "http://schemas.google.com/docs/2007#document";
		private static final String SCHEME_SPREADSHEET = "http://schemas.google.com/docs/2007#spreadsheet";
		private static final String SCHEME_PRESENTATION = "http://schemas.google.com/docs/2007#presentation";
		private static final String SCHEME_DRAWING = "http://schemas.google.com/docs/2007#drawing";

		public String toString() {
			switch (this) {
				case DOCUMENT:
					return SCHEME_DOCUMENT;

				case SPREADSHEET:
					return SCHEME_SPREADSHEET;

				case PRESENTATION:
					return SCHEME_PRESENTATION;

				case DRAWING:
					return SCHEME_DRAWING;

				default:
					throw new GoogleDocsException("Unknown Google Docs type");
			}
		}

		public static Type fromString(String scheme) {
			if (scheme.equals(SCHEME_DOCUMENT)) {
				return DOCUMENT;
			}

			else if (scheme.equals(SCHEME_DRAWING)) {
				return DRAWING;
			}

			else if (scheme.equals(SCHEME_PRESENTATION)) {
				return PRESENTATION;
			}

			else if (scheme.equals(SCHEME_SPREADSHEET)) {
				return SPREADSHEET;
			}

			else {
				throw new GoogleDocsException("Unknown Google Docs type");
			}
		}
	}

	@Key
	private String id;

	@Key
	private String title;

	@Key("category")
	private CategoriesList categories;

	@Key("link")
	private LinksList links;

	@Key("updated")
	private String lastUpdatedTime;

	public DocumentEntry() {
		categories = new CategoriesList();
		links = new LinksList();
	}

	public DocumentEntry(Type type, String title) {
		this();

		categories.add(new Category(CATEGORY_KIND, type.toString()));
		this.title = title;
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Category> getCategories() {
		return categories;
	}

	public List<Link> getLinks() {
		return links;
	}

	public Date getLastUpdatedTime() {
		return InternetDateTimeFormatter.parse(lastUpdatedTime);
	}

	public Type getType() {
		Category typeCategory = categories.findFirstWithScheme(CATEGORY_KIND);
		return Type.fromString(typeCategory.getTerm());
	}

	public String getSpreadsheetKey() {
		if (getType() != Type.SPREADSHEET) {
			throw new GoogleDocsException("Key can be only determined for a spreadsheet");
		}

		KeyUrl keyUrl = new KeyUrl(links.findFirstWithRel(ALTERNATE_SCHEME).getHref());

		return keyUrl.getKey();
	}

	public DocumentsListUrl getEditUrl() {
		return new DocumentsListUrl(links.findFirstWithRel(EDIT_SCHEME).getHref());
	}

	public String getLinkHref(String linkRel) {
		return links.findFirstWithRel(linkRel).getHref();
	}
}
